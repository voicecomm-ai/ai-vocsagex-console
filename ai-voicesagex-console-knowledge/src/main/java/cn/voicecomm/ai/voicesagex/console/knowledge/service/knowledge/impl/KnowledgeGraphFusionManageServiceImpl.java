package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphEntityManageService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphFusionManageService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion.AffirmFusionVO;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion.FusionResultVO;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphVertexDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.NgEdgePropertyInfo;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateGraphUtil;
import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.client.graph.data.DateTimeWrapper;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.TimeWrapper;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.internal.StringUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName KnowledgeGraphFusionManageServiceImpl
 * @Author wangyang
 * @Date 2026/1/5
 */

@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphFusionManageServiceImpl implements KnowledgeGraphFusionManageService {

  private final KnowledgeGraphTagEdgeService knowledgeGraphTagEdgeService;

  private final GraphEdgeMapper graphEdgeMapper;

  private final KnowledgeGraphEntityManageService knowledgeGraphEntityManageService;

  private final GraphVertexService graphVertexService;

  private final GraphEdgeService graphEdgeService;


  @Override
  public CommonRespDto<Boolean> affirmFusion(AffirmFusionVO affirmFusionVO)
      throws UnsupportedEncodingException {
    log.info("【Graph Knowledge Fusion start for space: {}】", affirmFusionVO.getSpaceId());
    // 查询所有关系保存
    // 新建所有关系，需要判断是否已经存在这条关系，如果已经存在，不新建
    try {
      List<NgEdgePropertyInfo> ngEdgePropertyInfos = new ArrayList<>();
      log.info("【get all edge info for space :】", affirmFusionVO.getSpaceId());
      List<String> vertexs = affirmFusionVO.getVertexIds();
      if (!CollectionUtils.isEmpty(vertexs)) {
        StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
        vertexs.forEach(v -> joiner.add(
            SpaceConstant.DOUBLE_QUOTATION_MARKS + v + SpaceConstant.DOUBLE_QUOTATION_MARKS));
        // 查看该图空间下是否有edge
        List<KnowledgeGraphTagEdgeDto> mapTagEdges = knowledgeGraphTagEdgeService.getTagInfos(
            Long.valueOf(affirmFusionVO.getSpaceId()));

        if (!CollectionUtils.isEmpty(mapTagEdges)) {
          ResultSet resultSet = graphEdgeMapper.getAllNgEdge(
              SpaceConstant.SPACE_NAME_FIX + affirmFusionVO.getSpaceId(), joiner.toString());
          processEdge(resultSet, ngEdgePropertyInfos, affirmFusionVO.getVertexIds(),
              affirmFusionVO.getFusionResultVO().getVertexId());
          // 融合节点
          log.info("【start  fusion vertex info : {}】",
              affirmFusionVO.getFusionResultVO().getVertexName());
          affirmFusionEntityInfo(affirmFusionVO);

          if (!CollectionUtils.isEmpty(ngEdgePropertyInfos)) {
            // 新增融合节点后的关系
            log.info("【The converged node relationship is added】");
            saveFusionEdge(ngEdgePropertyInfos, affirmFusionVO.getFusionResultVO().getVertexId(),
                affirmFusionVO.getSpaceId().toString());
          }

        } else {
          log.info("【start  fusion vertex info : {}】",
              affirmFusionVO.getFusionResultVO().getVertexName());
          affirmFusionEntityInfo(affirmFusionVO);
        }
        // 删除节点 以及所属关系
        log.info("【Delete a node and its owning relationship】");
        deleteFusionVertex(affirmFusionVO.getVertexIds(), affirmFusionVO.getSpaceId().toString());
      }

      graphVertexService.executeTheTask(SpaceConstant.SPACE_NAME_FIX +
          affirmFusionVO.getSpaceId());
    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【Knowledge fusion failure: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.AFFIRMFUSION, e);
    }

    log.info("【The entity: {} information is merged successfully】");
    return CommonRespDto.success(Boolean.TRUE);
  }

  /**
   * 删除节点和所属关系
   *
   * @param vertexIds
   * @param spaceId
   */
  private void deleteFusionVertex(List<String> vertexIds, String spaceId) {
    // 删除图数据库
    GraphVertexDropDO graphVertexDropDO = new GraphVertexDropDO();
    graphVertexDropDO.setSpace(SpaceConstant.SPACE_NAME_FIX + spaceId);
    String ids = vertexIds.stream()
        .map(id -> SpaceConstant.DOUBLE_QUOTATION_MARKS + id.toString()
            + SpaceConstant.DOUBLE_QUOTATION_MARKS)
        .collect(Collectors.joining(SpaceConstant.TAG_SPLIT));
    graphVertexDropDO.setVids(ids);
    log.info("【The system starts to delete node :{} information and relationships】", ids);
    graphVertexService.deleteVertex(graphVertexDropDO);
    log.info("【The system success to delete node :{} information and relationships】", ids);

    // 向量同步
//    apiVectorQuantityService.makeAsyncDelete(new DeleteVectorDTO(SpaceConstant.SPACE_NAME_FIX + spaceId,vertexIds));
  }


  /**
   * 新增融合节点关系数据
   *
   * @param ngEdgePropertyInfos
   * @param
   */
  private void saveFusionEdge(List<NgEdgePropertyInfo> ngEdgePropertyInfos, String vertexId,
      String spaceId) throws UnsupportedEncodingException {
    if (!CollectionUtils.isEmpty(ngEdgePropertyInfos)) {
      for (NgEdgePropertyInfo ng : ngEdgePropertyInfos) {
        log.info("【Insert relational data in sequence： {}】", ng.getEdgeName());
        GraphRelationDO graphRelationDO = new GraphRelationDO();
        graphRelationDO.setSubjectId(
            ng.getStatus() == SpaceConstant.INDEX ? vertexId : ng.getSrcId());
        graphRelationDO.setObjectId(
            ng.getStatus() == SpaceConstant.REPLICA_FACTOR ? vertexId : ng.getDstId());
        graphRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + spaceId);
        graphRelationDO.setEdgeName(ng.getEdgeName());
        graphRelationDO.setRank(SpaceConstant.RANK + ng.getRank());
        // 解析关系数据
        processSaveRelation(graphRelationDO, ng.getProperties(), spaceId);
        // 新增图数据库
        graphEdgeService.saveRelationFusion(graphRelationDO, SpaceConstant.INDEX);

        // 向量新增
        //向量数据库存储
//        saveRelationVector(graphRelationDO, spaceId);

        log.info("【Insert relational:{} data in sequence success】 ", graphRelationDO.getEdgeName());
      }
    }

  }


  /**
   * INSERT EDGE `${re.edgeName}`  (${re.entityProperties})  VALUES
   * "${re.subjectId}"->"${re.objectId}":(${re.entityValue});
   *
   * @param graphEntityDO
   * @param
   */
  private void processSaveRelation(GraphRelationDO graphEntityDO,
      Map<String, ValueWrapper> properties, String spaceId) throws UnsupportedEncodingException {
    StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner match = new StringJoiner(" AND ");
    // 设置属性名属性值
    for (Map.Entry<String, ValueWrapper> propertyName : properties.entrySet()) {
      key.add(SpaceConstant.QUOTATIONMARK + propertyName.getKey() + SpaceConstant.QUOTATIONMARK);
      // 获取属性类型
      String type = getEdgeType(propertyName.getKey(), graphEntityDO.getEdgeName(), spaceId);
      String buildString = processData(type, propertyName.getValue());
      value.add(buildString);
      match.add(
          "e." + SpaceConstant.QUOTATIONMARK + propertyName.getKey() + SpaceConstant.QUOTATIONMARK
              + (buildString.equals("NULL") ? " is " : "==")
              + buildString);
    }
    graphEntityDO.setEntityProperties(key.toString());
    graphEntityDO.setEntityValue(value.toString());
    graphEntityDO.setAttributeMatch(match.toString());

  }


  private String processData(String propertyType, ValueWrapper propertyValue)
      throws UnsupportedEncodingException {
    StringBuilder param = new StringBuilder();
    if (!propertyValue.isNull()) {
      if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING) || propertyType.equalsIgnoreCase(
          SpaceConstant.STRING)) {
        param.append(JSON.toJSONString(propertyValue.asString()));
      } else if (propertyType.equalsIgnoreCase(SpaceConstant.DOUBLE)
          || propertyType.equalsIgnoreCase(SpaceConstant.FLOAT)) {
        param.append(
                !propertyValue.isNull() ? Double.parseDouble(String.valueOf(propertyValue.asDouble()))
                    : SpaceConstant.NULL)
            .append(SpaceConstant.TAG_SPACE);
      } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATE)) {
        // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD"格式的字符串
        String dateValue =
            !propertyValue.isNull() ? "date(\"" + propertyValue.asDate().toString() + "\")"
                : SpaceConstant.NULL;
        param.append(dateValue)
            .append(SpaceConstant.TAG_SPACE);
      } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIME)) {
        // 注意：这里假设time只包含小时、分钟和秒，不包括毫秒
        // 你可能需要调整以适应你的具体需求
        String timeValue =
            !propertyValue.isNull() ? "time(\"" + transformTime(propertyValue.asTime()).toString()
                + "\")" : SpaceConstant.NULL;
        param.append(timeValue)
            .append(SpaceConstant.TAG_SPACE);
      } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATETIME)) {
        // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD HH:mm:ss"格式的字符串
        // 我们需要添加毫秒（假设为0）以符合某些数据库或系统的datetime格式
        String datetimeValue =
            !propertyValue.isNull() ? "datetime(\"" + DateGraphUtil.dateProcessTime(
                transformDateTime(propertyValue.asDateTime()).toString()) + ".000000\")"
                : SpaceConstant.NULL;
        param.append(datetimeValue)
            .append(SpaceConstant.TAG_SPACE);
      } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIMESTAMP)) {
        String datetimeValue =
            !propertyValue.isNull() ? "timestamp(\"" + DateGraphUtil.dateProcessTimeStamp(
                propertyValue.asLong()) + ".000000\")" : SpaceConstant.NULL;
        param.append(datetimeValue)
            .append(SpaceConstant.TAG_SPACE);
// 输出最终构建的字符串（如果需要）

      } else if (propertyType.equalsIgnoreCase(SpaceConstant.BOOL)) {
        String bool =
            !propertyValue.isNull() ? propertyValue.asBoolean() ? SpaceConstant.TRUE_STRING
                : SpaceConstant.FALSE_STRING : SpaceConstant.NULL;
        param.append(bool);
      } else {
        param.append(!propertyValue.isNull() ? Integer.valueOf(propertyValue.toString())
            : SpaceConstant.NULL);
      }
      return param.toString().trim();
    } else {
      return propertyValue.asNull().toString();
    }
  }

  private static Object transformDateTime(DateTimeWrapper dateTime) {
    DateTime localDateTime = dateTime.getLocalDateTime();

    int month = localDateTime.getMonth() - 1;
    GregorianCalendar calendar = new GregorianCalendar(
        localDateTime.getYear(),
        month,
        localDateTime.getDay(),
        localDateTime.getHour(),
        localDateTime.getMinute(),
        localDateTime.getSec()
    );

    calendar.set(Calendar.MILLISECOND, Math.floorDiv(localDateTime.getMicrosec(), 1000));

    return calendar.getTime();
  }

  private static Object transformTime(TimeWrapper time) {
    return new java.sql.Time(time.getHour(), time.getMinute(), time.getSecond());
  }

  /**
   * 获取属性类型
   *
   * @param key
   * @param edgeName
   * @param spaceId
   * @return
   */
  private String getEdgeType(String key, String edgeName, String spaceId) {
    log.info("【获取tag属性值 {} ,for space:{}】", edgeName, spaceId);
    KnowledgeGraphTagEdgeDto mapTagEdge = knowledgeGraphTagEdgeService.getEdgeInfo(
        Long.valueOf(spaceId), edgeName);
    if (null != mapTagEdge) {
      // 获取属性值
      KnowledgeGraphTagEdgePropertyDto tagEdgeProperty = knowledgeGraphTagEdgeService.getTagEdgeProperty(
          Long.valueOf(mapTagEdge.getTagEdgeId()), SpaceConstant.REPLICA_FACTOR, key);
      if (null != tagEdgeProperty && !StringUtil.isBlank(tagEdgeProperty.getPropertyType())) {
        return tagEdgeProperty.getPropertyType();
      }
    }
    return "";

  }


  /**
   * 融合节点信息
   *
   * @param affirmFusionVO
   */
  private void affirmFusionEntityInfo(AffirmFusionVO affirmFusionVO) {
    GraphEntityDO graphEntityDO = new GraphEntityDO();
    graphEntityDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + affirmFusionVO.getSpaceId());
    graphEntityDO.setVid(affirmFusionVO.getFusionResultVO().getVertexId());
    processEntity(graphEntityDO, affirmFusionVO.getFusionResultVO());
    // 新增图数据库
    graphVertexService.saveEntityFusion(graphEntityDO);
    log.info("【The new financial node is successfully added】");

    // 向量存储
//    saveVector(affirmFusionVO);
  }

  /**
   * INSERT VERTEX `t2` (name, age) ,`t1` (name, age)  VALUES "11":("n1", 12,"n1", 12);
   *
   * @param graphEntityDO
   * @param fusionResultVO
   */
  private void processEntity(GraphEntityDO graphEntityDO, FusionResultVO fusionResultVO) {
    StringJoiner tag = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    if (!LocaleContextHolder.getLocale().toString().equals("ar_EG")) {
      if (!fusionResultVO.getVertexName().matches(SpaceConstant.PATTERN_STRING)) {
        log.error("【entity name not matches :{}】", fusionResultVO.getVertexName());
        throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_NAME);
      }
    }
    // 设置名称

    fusionResultVO.getTagNameInfo().forEach(s -> {
      StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
      value.add(JSON.toJSONString(fusionResultVO.getVertexName()));
      String tagName = SpaceConstant.QUOTATIONMARK + s.getTagName() + SpaceConstant.QUOTATIONMARK
          + SpaceConstant.FIX_TAG_NAME_FUSION + SpaceConstant.NAME;
      s.getVertexPropertiesVOS().stream().forEach(property -> {
        key.add(
            SpaceConstant.QUOTATIONMARK + property.getPropertyName() + SpaceConstant.QUOTATIONMARK);
        if (!StringUtil.isBlank(property.getPropertyValue())) {
          String buildString = knowledgeGraphEntityManageService.processData(
              property.getPropertyType(),
              property.getPropertyValue());
          value.add(buildString);
        } else {
          String buildString = SpaceConstant.NULL;
          value.add(buildString);
        }
      });
      tag.add(tagName + SpaceConstant.TAG_SPLIT + key.toString()
          + SpaceConstant.FIX_TAG_NAME_SUX_FUSION);

    });
    graphEntityDO.setEntityProperties(tag.toString());
    graphEntityDO.setEntityValue(value.toString());
  }


  /**
   * 解析边信息
   *
   * @param resultSet
   * @param ngEdgePropertyInfos
   * @param
   * @throws UnsupportedEncodingException
   */
  private void processEdge(ResultSet resultSet, List<NgEdgePropertyInfo> ngEdgePropertyInfos,
      List<String> vertexIds, String vertexId) throws UnsupportedEncodingException {
    log.info("【Graph Knowledge Fusion process Edge info 】");
    if (null != resultSet && resultSet.getRows().size() > SpaceConstant.INDEX) {
      List<String> columnNames = resultSet.getColumnNames();
      if (columnNames.contains(SpaceConstant.SRC) && columnNames.contains(SpaceConstant.DST)
          && columnNames.contains(SpaceConstant.EDGENAMEINFO) &&
          columnNames.contains(SpaceConstant.PROPERTY)) {
        int srcIndex = columnNames.indexOf(SpaceConstant.SRC);
        int dstIndex = columnNames.indexOf(SpaceConstant.DST);
        int edgeNameIndex = columnNames.indexOf(SpaceConstant.EDGENAMEINFO);
        int propertyIndex = columnNames.indexOf(SpaceConstant.PROPERTY);
        int rank = columnNames.indexOf(SpaceConstant.RANK_NAME);

        // 假设所有列表的长度相同（这是处理这种情况的关键假设）
        for (int i = SpaceConstant.INDEX; i < resultSet.getRows().size(); i++) {
          ResultSet.Record record = resultSet.rowValues(i);
          List<ValueWrapper> values = record.values();
          String src = values.get(srcIndex).asString();
          String dst = values.get(dstIndex).asString();
          String edgeName = values.get(edgeNameIndex).asString();
          Long rankName = values.get(rank).asLong();
          Map<String, ValueWrapper> property = values.get(propertyIndex).asMap();
          if (!(vertexIds.contains(src) && vertexIds.contains(dst))) {
            if (vertexIds.contains(src)) {
              if (!dst.equals(vertexId)) {
                ngEdgePropertyInfos.add(
                    new NgEdgePropertyInfo(src, dst, edgeName, property, SpaceConstant.INDEX,
                        rankName));
              }
            } else {
              if (!src.equals(vertexId)) {
                ngEdgePropertyInfos.add(new NgEdgePropertyInfo(src, dst, edgeName, property,
                    SpaceConstant.REPLICA_FACTOR, rankName));
              }
            }
          }


        }
      }
    }
  }
}


