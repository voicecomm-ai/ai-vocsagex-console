package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.TagEdgeEnums;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphTagEdgePropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.TagResultDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Data;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphTagEdgeProperty;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Link;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.NgTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagEdgeIndex;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagType;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphPatternEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphPatternTagMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgePatterManagerMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeManageMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphTagManageMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphTagEdgeManageService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.ResultSetTagEdgeUtil;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.GraphPatternEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.GraphPatternTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.PatterManagerPo;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.utils.ResultSetUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author
 */
@Service
@Slf4j
public class GraphTagEdgeManageServiceImpl implements GraphTagEdgeManageService {


  @Autowired
  private GraphTagManageMapper graphTagManageMapper;

  @Autowired
  private GraphEdgeManageMapper graphEdgeManageMapper;


  @Autowired
  private GraphEdgeMapper graphEdgeMapper;

  @Autowired
  private KnowledgeGraphPatternTagMapper graphPatternTagMapper;

  @Autowired
  private KnowledgeGraphPatternEdgeMapper graphPatternEdgeMapper;


  static private List<String> typeList = Arrays.asList("INT8", "INT16", "INT32", "INT64");
  @Autowired
  private KnowledgePatterManagerMapper knowledgePatterManagerMapper;


  @Override
  public void createTagEdge(GraphCreateTagEdge graphCreateTag) {
    log.info("【Kg-webserver-db  Creating graph tag/edge = {}】", graphCreateTag.getTagName());
    // 判断tag / edge 是否存在
    List<String> tags = getTagEdges(graphCreateTag);
    if (!CollectionUtils.isEmpty(tags) && tags.contains(graphCreateTag.getTagName())) {
      log.error("【kg-webserver-db tag/edge is  exists: {}】", graphCreateTag.getTagName());
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_EDGE_IS_EXISTS);
    }
    try {
      // 校验 tag /edge  name 是否同名
      checkSameName(graphCreateTag);
      //解析数据
      addTagEdgeProperties(graphCreateTag);

      // 创建 Tag/Edge
      createTag(graphCreateTag);

      // 创建索引 默认以实体名称作为index
      createIndexForTagEdge(graphCreateTag);

    } catch (ServiceException se) {
      log.error("【Error creating tag/edge with message: {}】", se.getMessage(), se);
      throw se;
    } catch (Exception e) {
      log.error("【Error creating tag/edge with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_NAME_CREATE, e);
    }
  }

  @Override
  public void insertTagEdgeProperties(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db  add graph tag/edge  properties : {}】",
        graphCreateTagEdge.getTagName());
    // process data
    analyProperties(graphCreateTagEdge);
    try {
      // 创建 Tag/Edge
      addProperties(graphCreateTagEdge);
    } catch (Exception e) {
      log.error("【Error alter tag/edge with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_NAME_ALTER, e);
    }


  }

  @Override
  public void setTtlProperties(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db  set graph tag/edge TTl properties : {}】",
        graphCreateTagEdge.getTagName());
    // 校验 Tag/Edge 是否存在 相关时间字段 校验逻辑放置在mysql中进行校验
    try {
      //先删除默认索引
      dropIndex(graphCreateTagEdge);
      // 添加 Tag/Edge Ttl 属性
      Consumer<GraphCreateTagEdge> createAction =
          graphCreateTagEdge.getType() == SpaceConstant.INDEX ? this::setTtlPropertiesTagMapper
              : this::setTtlPropertiesEdgeMapper;
      log.info("【Kg-webserver-db  set graph tag/edge TTl properties : {} success】",
          graphCreateTagEdge.getTagName());
      // 执行创建操作
      createAction.accept(graphCreateTagEdge);
      // 再将默认索引添加上
      createIndexForTagEdge(graphCreateTagEdge);
    } catch (Exception e) {
      log.error("【Error alter tag/edge with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_NAME_ALTER, e);
    }
  }

  private void setTtlPropertiesEdgeMapper(GraphCreateTagEdge graphCreateTagEdge) {
    graphEdgeManageMapper.setTtl(graphCreateTagEdge);
  }

  private void setTtlPropertiesTagMapper(GraphCreateTagEdge graphCreateTagEdge) {
    graphTagManageMapper.setTtl(graphCreateTagEdge);
  }

  private void addTagEdgeProperties(GraphCreateTagEdge graphCreateTag) {
    StringBuilder builder = new StringBuilder();
    // 固定插入一个名称 和类型 保证后期创建实体时使用
    builder.append(SpaceConstant.FIX_TAG_NAME).append(SpaceConstant.FIX_TAG_NAME_SUX);
    graphCreateTag.setProperties(builder.toString());
    log.info("【Concatenation create statement ：{} 】", builder);
  }

  /**
   * 检测是否同名
   *
   * @param graphCreateTag
   */
  private void checkSameName(GraphCreateTagEdge graphCreateTag) {
    GraphCreateTagEdge graphCreateTagEdgeTemp = new GraphCreateTagEdge();
    graphCreateTagEdgeTemp.setSpace(graphCreateTag.getSpace());
    if (graphCreateTag.getType() == SpaceConstant.INDEX) {
      graphCreateTagEdgeTemp.setType(SpaceConstant.ENGDE);
    } else {
      graphCreateTagEdgeTemp.setType(SpaceConstant.INDEX);
    }
    List<String> reuslts = getTagEdges(graphCreateTagEdgeTemp);
    if (!CollectionUtils.isEmpty(reuslts) && reuslts.contains(graphCreateTag.getTagName())) {
      log.error("【The graph space already has the same name tag/edge  : {} 】",
          graphCreateTag.getTagName());
      throw ServiceExceptionUtil.exception(ErrorConstants.SAME_NAME_EDGE_IS_EXISTS);
    }

  }

  /**
   * // 创建索引 默认以实体名称作为index
   *
   * @param graphCreateTag
   */
  private void createIndexForTagEdge(GraphCreateTagEdge graphCreateTag) {
    TagEdgeIndex tagEdgeIndex = new TagEdgeIndex();
    tagEdgeIndex.setSpace(graphCreateTag.getSpace());
    tagEdgeIndex.setTagEdgeName(graphCreateTag.getTagName());
    tagEdgeIndex.setIndex(graphCreateTag.getTagName() + SpaceConstant.FIX_INDEX_NAME);
    log.info("【Kg-webserver-db  Creating default index  = {} for tag/edge : {}】",
        SpaceConstant.FIX_INDEX_NAME, graphCreateTag.getTagName());
    if (graphCreateTag.getType() == SpaceConstant.INDEX) {
      graphTagManageMapper.createIndexDefault(tagEdgeIndex);
    } else {
      graphEdgeManageMapper.createIndexDefault(tagEdgeIndex);
    }
  }

  @Override
  public void dropTagEdge(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db drop space : {}  All Tag/edge info 】",
        graphCreateTagEdge.getSpace());
    try {
      if (graphCreateTagEdge.getType() == SpaceConstant.INDEX) {
        graphTagManageMapper.dropTag(graphCreateTagEdge);
      } else {
        graphEdgeManageMapper.dropEdge(graphCreateTagEdge);
      }
    } catch (Exception e) {
      log.error("【Description Failed to switch drop space:{} tag error : {}】",
          graphCreateTagEdge.getSpace(), e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.DROP_TAG);
    }
  }

  @Override
  public void dropIndex(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db drop space : {}  All Tag/edge Index  info ：{} 】",
        graphCreateTagEdge.getSpace(), graphCreateTagEdge.getTagName());
    graphCreateTagEdge.setIndex(graphCreateTagEdge.getTagName() + SpaceConstant.FIX_INDEX_NAME);
    try {
      if (graphCreateTagEdge.getType() == SpaceConstant.INDEX) {
        graphTagManageMapper.dropIndex(graphCreateTagEdge);
      } else {
        graphEdgeManageMapper.dropIndex(graphCreateTagEdge);
      }
    } catch (Exception e) {
      log.error("【Description Failed to switch drop spacee Tag/Edg Index :{} tag error : {}】",
          graphCreateTagEdge.getSpace(), e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.DROP_TAG);
    }
  }

  @Override
  public List<NgTagEdge> getTagEdgeInfo(GraphCreateTagEdge graphCreateTagEdge, int pageSize,
      int currentPage) {
    log.info("【Kg-webserver-db get Tag/Edge = {} info 】", graphCreateTagEdge.getTagName());
    // 校验 Tag/egde 是否存在
    exitsTagEdge(graphCreateTagEdge);
    try {
      ResultSet result =
          graphCreateTagEdge.getType() == SpaceConstant.INDEX ? graphTagManageMapper.getTagInfo(
              graphCreateTagEdge) : graphEdgeManageMapper.getEdgeInfo(graphCreateTagEdge);
      // 分页获取Tage edge 结果
      log.info("【Kg-webserver-db in  db  Tag/Edge : {} info 】", result);
      return ResultSetTagEdgeUtil.paginate(ResultSetTagEdgeUtil.createNgTagList(result), pageSize,
          currentPage);
    } catch (Exception e) {
      log.error("【Get Tag/Edge = {} info  Error : {}】", graphCreateTagEdge.getTagName(),
          e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_NAME_CHECK);
    }
  }

  /**
   * 获取所有Tag / Edge 列表
   *
   * @param graphCreateTagEdge
   * @return
   */
  @Override
  public List<String> getTagEdges(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db get All  Tag/Edge for  space : {}】", graphCreateTagEdge.getSpace());
    List<String> tagEdges = new ArrayList<>();
    try {
      ResultSet result =
          graphCreateTagEdge.getType() == SpaceConstant.INDEX ? graphTagManageMapper.getAllTags(
              graphCreateTagEdge) : graphEdgeManageMapper.getAllEdges(graphCreateTagEdge);
      if (result.rowsSize() != 0) {
        List<String> columnNames = result.getColumnNames();
        String firstCol = columnNames.get(SpaceConstant.INDEX);
        List<ValueWrapper> valueWrappers = result.colValues(firstCol);
        valueWrappers.forEach((i) -> {
          tagEdges.add(ResultSetUtil.getValue(i));
        });
      }
      return tagEdges;
    } catch (Exception e) {
      log.error("【Kg-webserver-db get All  Tag/Edge = {} error:{}】",
          graphCreateTagEdge.getTagName(), e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ALL_TAGEDGE);
    }
  }

  @Override
  public void dropTagEdgeProperty(GraphTagEdgeProperty tagEdgeProperty) {
    log.info("【Kg-webserver-db drop space : {}  All Tag/edge :  {}  Property   info ：{} 】",
        tagEdgeProperty.getSpace(), tagEdgeProperty.getTagName(),
        tagEdgeProperty.getPropertyName());
    try {
      if (tagEdgeProperty.getType() == SpaceConstant.INDEX) {
        graphTagManageMapper.dropTagEdgeProperty(tagEdgeProperty);
      } else {
        graphEdgeManageMapper.dropTagEdgeProperty(tagEdgeProperty);
      }
    } catch (Exception e) {
      log.error("【Description Failed to switch drop spacee Tag/Edg Property :{} tag error : {}】",
          tagEdgeProperty.getPropertyName(), e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.DROP_TAG_PROPERTY);
    }
  }

  @Override
  public void updateGraphProperty(GraphTagEdgePropertyDO tagEdgeProperty) {
    log.info("【Kg-webserver-db update space : {}  All Tag/edge :  {}  Property   info ：{} 】",
        tagEdgeProperty.getSpace(), tagEdgeProperty.getTagName(),
        tagEdgeProperty.getPropertyName());
    try {
      if (tagEdgeProperty.getType() == SpaceConstant.INDEX) {
        graphTagManageMapper.updateTagEdgeProperty(tagEdgeProperty);
      } else {
        graphEdgeManageMapper.updateTagEdgeProperty(tagEdgeProperty);
      }
    } catch (Exception e) {
      log.error("【update  space Tag/Edg Property :{} tag error : {}】",
          tagEdgeProperty.getPropertyName(), e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_TAG_PROPERTY);
    }
  }

  @Override
  public void dropTagEdgeTtl(GraphTagEdgePropertyDO graphTagEdgePropertyDO) {
    log.info("【Kg-webserver-db update space : {}  All Tag/edge :  {}  Property   info ：{} 】",
        graphTagEdgePropertyDO.getSpace(), graphTagEdgePropertyDO.getTagName(),
        graphTagEdgePropertyDO.getPropertyName());
    GraphCreateTagEdge graphCreateTagEdge = new GraphCreateTagEdge();
    BeanUtils.copyProperties(graphTagEdgePropertyDO, graphCreateTagEdge);
    try {
      //先删除默认索引
      dropIndex(graphCreateTagEdge);
      if (graphCreateTagEdge.getType() == SpaceConstant.INDEX) {
        graphTagManageMapper.dropTagEdgeTtl(graphTagEdgePropertyDO);
      } else {
        graphEdgeManageMapper.dropTagEdgeTtl(graphTagEdgePropertyDO);
      }
      // 再将默认索引添加上
      createIndexForTagEdge(graphCreateTagEdge);
    } catch (Exception e) {
      log.error("【drop  space Tag/Edg Ttl :{} tag error : {}】", graphCreateTagEdge.getTtlCol(),
          e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.DROP_TAG_EDGE_TTL);
    }
  }


  private void exitsTagEdge(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Kg-webserver-db  Tag/Edge = {} info is exists 】", graphCreateTagEdge.getTagName());
    List<String> tagEdges = getTagEdges(graphCreateTagEdge);
    if (!tagEdges.contains(graphCreateTagEdge.getTagName())) {
      log.error("Get Tag/Edge = {} info not exists】", graphCreateTagEdge.getTagName());
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_EDGE_EXISTS);
    }
  }

  private void createTag(GraphCreateTagEdge graphCreateTag) {
    log.info("【Start create tag/edge in type is : {}  】 ", graphCreateTag.getType());
    Consumer<GraphCreateTagEdge> createAction =
        graphCreateTag.getType() == SpaceConstant.INDEX ? this::createTagWithTagMapper
            : this::createTagWithEdgeMapper;
    // 执行创建操作
    createAction.accept(graphCreateTag);
  }


  /**
   * add  properties  for  Tag/Edge
   *
   * @param graphCreateTag
   */
  private void addProperties(GraphCreateTagEdge graphCreateTag) {
    log.info("【Start add tag/edge properties in type is : {}  】 ", graphCreateTag.getType());
    Consumer<GraphCreateTagEdge> createAction =
        graphCreateTag.getType() == SpaceConstant.INDEX ? this::addPropertiesTagWithTagMapper
            : this::addPropertiesTagWithEdgeMapper;
    // 执行创建操作
    createAction.accept(graphCreateTag);
  }

  private void createTagWithTagMapper(GraphCreateTagEdge graphCreateTag) {
    graphTagManageMapper.createTag(graphCreateTag);
  }

  private void createTagWithEdgeMapper(GraphCreateTagEdge graphCreateTag) {
    graphEdgeManageMapper.createEdge(graphCreateTag);
  }


  private void addPropertiesTagWithTagMapper(GraphCreateTagEdge graphCreateTag) {
    graphTagManageMapper.alterTag(graphCreateTag);
  }

  private void addPropertiesTagWithEdgeMapper(GraphCreateTagEdge graphCreateTag) {
    graphEdgeManageMapper.alterEdge(graphCreateTag);
  }

  // 解析属性参数
  // 以 (name string , age int) 格式
  private GraphCreateTagEdge analyProperties(GraphCreateTagEdge graphCreateTagEdge) {
    log.info("【Web Api Creating  process space tag/edge = {}】", graphCreateTagEdge.getTagName());
    try {
      if (graphCreateTagEdge.getTagTypes() != null) {
        StringBuilder builder = new StringBuilder();
        TagType tagTypes = graphCreateTagEdge.getTagTypes();
        String propertyType = tagTypes.getPropertyType();
        tagTypes.setPropertyName("`" + tagTypes.getPropertyName() + "`");

        if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING)) {
          // 注意：这里修改了tagTypes的状态，如果你不想修改原始对象，请注释或删除这行代码
          TagType type = new TagType();
          BeanUtils.copyProperties(tagTypes, type);
          type.setPropertyType(
              SpaceConstant.FIX_STRING + SpaceConstant.FIX_TAG_NAME_START + tagTypes.getExtra()
                  + SpaceConstant.FIX_TAG_NAME_SUX);
          // 拼接字段是否必填和类型
          builder.append(tagTypes.getPropertyName()).append(SpaceConstant.TAG_SPACE)
              .append(type.getPropertyType()).append(SpaceConstant.TAG_SPACE).append(
                  tagTypes.getTagRequired() == SpaceConstant.INDEX ? SpaceConstant.TAG_NOT_NULL
                      : SpaceConstant.TAG_NULL).append(SpaceConstant.TAG_EDGE_DEFAULT).append("\"")
              .append(type.getDefaultValueAsString()).append("\"").append(SpaceConstant.TAG_SPACE);
        } else {

          // 拼接字段是否必填和类型
          builder.append(tagTypes.getPropertyName()).append(SpaceConstant.TAG_SPACE)
              .append(propertyType).append(SpaceConstant.TAG_SPACE).append(
                  tagTypes.getTagRequired() == SpaceConstant.INDEX ? SpaceConstant.TAG_NOT_NULL
                      : SpaceConstant.TAG_NULL);

          if (tagTypes.getTagRequired() == SpaceConstant.INDEX) {
            if (typeList.contains(propertyType)) {
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT)
                  .append(tagTypes.getDefaultValueAsString()).append(SpaceConstant.TAG_SPACE);
            } else if (propertyType.equalsIgnoreCase(SpaceConstant.DOUBLE)
                || propertyType.equalsIgnoreCase(SpaceConstant.FLOAT)) {
              double doubleValue = Double.parseDouble(
                  (String) tagTypes.getDefaultValueAsString()); // 尝试解析为double
              String formatValue = String.format("%.15f", doubleValue);
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append(formatValue)
                  .append(SpaceConstant.TAG_SPACE);
            } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATE)) {
              // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD"格式的字符串
              String dateValue = "date(\"" + tagTypes.getDefaultValueAsString() + "\")";
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append(dateValue)
                  .append(SpaceConstant.TAG_SPACE);
            } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIME)) {
              // 注意：这里假设time只包含小时、分钟和秒，不包括毫秒
              // 你可能需要调整以适应你的具体需求
              String timeValue = "time(\"" + tagTypes.getDefaultValueAsString() + "\")";
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append(timeValue)
                  .append(SpaceConstant.TAG_SPACE);
            } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATETIME)) {
              // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD HH:mm:ss"格式的字符串
              // 我们需要添加毫秒（假设为0）以符合某些数据库或系统的datetime格式
              String datetimeValue =
                  "datetime(\"" + tagTypes.getDefaultValueAsString() + ".000000\")";
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append(datetimeValue)
                  .append(SpaceConstant.TAG_SPACE);
            } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIMESTAMP)) {
              String datetimeValue =
                  "timestamp(\"" + tagTypes.getDefaultValueAsString() + ".000000\")";
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append(datetimeValue)
                  .append(SpaceConstant.TAG_SPACE);

            } else if (propertyType.equalsIgnoreCase(SpaceConstant.BOOL)) {
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT)
                  .append(tagTypes.getDefaultValueAsString()).append(SpaceConstant.TAG_SPACE);
            } else {
              builder.append(SpaceConstant.TAG_EDGE_DEFAULT).append("\"")
                  .append(tagTypes.getDefaultValue()).append("\"").append(SpaceConstant.TAG_SPACE);
            }
          }
        }
        // 输出最终构建的字符串（如果需要）
        String finalString = builder.toString().trim();
        graphCreateTagEdge.setProperties(finalString);
        log.info("【Concatenation create statement ：{} 】", builder);
      }
    } catch (Exception e) {
      log.error("【Web api process Tage/Edge data error : {}】", e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.TAG_NAME_CHECK);
    }
    log.info("【Web Db Creating  process space tag/edge：{}  success】", graphCreateTagEdge);
    return graphCreateTagEdge;

  }


  @Override
  public TagResultDO showPattern(String spaceId) {
    log.info("【Get Graph Pattern for space : {}】", spaceId);
    Set<String> tags = new HashSet<>();
    Set<String> allTags = new HashSet<>();
    Set<Data> datas = new HashSet<>();
    Map<String, String> replacedMap = new HashMap<>();
    TagResultDO tagResultDO = new TagResultDO();

    try {
      // Get all edges
      ResultSet edgesSet = graphEdgeManageMapper.getEdgesPattern(spaceId);
      List<String> edges = new ArrayList<>();

      // 获取所有edge
      if (edgesSet.rowsSize() != 0) {
        List<String> columnNames = edgesSet.getColumnNames();
        String firstCol = columnNames.get(SpaceConstant.INDEX);
        List<ValueWrapper> valueWrappers = edgesSet.colValues(firstCol);

        for (ValueWrapper wrapper : valueWrappers) {
          edges.add(ResultSetUtil.getValue(wrapper));
        }
      }

      // 获取所有edge 对应的 点边关系
      for (String edge : edges) {
        screenTag(edge, replacedMap, spaceId, tags);
      }

      // Get all tags
      ResultSet resultSet = graphTagManageMapper.getPatternAllTags(spaceId);
      if (resultSet != null) {
        int size = resultSet.rowsSize();
        for (int i = SpaceConstant.INDEX; i < size; i++) {
          ResultSet.Record row = resultSet.rowValues(i);
          if (row != null) {
            allTags.add(row.get(TagEdgeEnums.INDEX_VALUE_ONE.getStatus()).asString());
          }
        }
      }

      // 排查已经有关系的点
      allTags.removeAll(tags);
      for (String tag : allTags) {
        List<NgVertex<String>> ngEdges = graphTagManageMapper.getVertex(spaceId, tag);

        if (!CollectionUtils.isEmpty(ngEdges)) {
          tags.add(tag);
        }
      }

      tags.forEach(tag -> {
        Data data = new Data();
        data.setId(tag);
        datas.add(data);
      });

      tagResultDO.setDatas(datas);
      tagResultDO.setSrcMap(replacedMap);

      return tagResultDO;
    } catch (Exception e) {
      log.error("【Get graph pattern for spaceId : {}  error : {} 】", spaceId, e.getMessage());
      throw ServiceExceptionUtil.exception(ErrorConstants.GRAPH_PATTERN);
    }


  }

  public Map<String, String> screenTag(String edge, Map<String, String> replacedMap, String spaceId,
      Set<String> tags) {
    try {
      ResultSet resultSet = graphEdgeMapper.screenTagModel(spaceId, edge);
      // 确保所需的列都存在
      if (null != resultSet) {
        // 获取各列的值
        List<ValueWrapper> t = resultSet.colValues(SpaceConstant.T);
        List<ValueWrapper> t1 = resultSet.colValues(SpaceConstant.T1);
        // 假设所有列表的长度相同（这是处理这种情况的关键假设）
        if (t.size() == t1.size()) {

          for (int i = SpaceConstant.INDEX; i < t.size(); i++) {
            StringJoiner joiner = new StringJoiner(",");
            StringJoiner joiner1 = new StringJoiner(",");
            // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
            ArrayList<ValueWrapper> src = t.get(i).asList();
            ArrayList<ValueWrapper> dst = t1.get(i).asList();
            for (ValueWrapper s : src) {
              joiner.add(s.asString());
            }
            for (ValueWrapper d : dst) {
              joiner1.add(d.asString());
            }

            replacedMap.put(
                joiner.toString() + SpaceConstant.SPILE + joiner1.toString() + SpaceConstant.SPILE
                    + edge, edge);
            tags.add(joiner.toString());
            tags.add(joiner1.toString());
          }
        }
      }
    } catch (Exception e) {
      throw ServiceExceptionUtil.exception(ErrorConstants.SCREEN_TAG_ERROR);
    }

    return replacedMap;
  }


  @Override
  public Integer startInit(String spaceId) {
    log.info("【start init SUBMIT JOB STATS】", spaceId);
    try {
      return graphTagManageMapper.startInit(spaceId);
    } catch (Exception e) {
      log.error("【The graph space info   = {} error : {}】", spaceId);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_JOB_CHECK);
    }
  }


  @Override
  public void flushJob(String spaceId) {
    log.info("【Web flush space for job ：{}】 ", spaceId);
    try {
      graphTagManageMapper.flushJob(spaceId);
    } catch (Exception e) {
      log.error("【Error Web flush space for job ： {}】", spaceId);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_JOB_FLUSH);
    }

  }

  @Override
  public boolean showJobInfo(Integer id, String spaceId) {
    log.info("【Web get job  space for job ：{}】 ", spaceId);
    try {
      ResultSet resultSet = graphTagManageMapper.showJobInfo(id, spaceId);
      List<String> columnNames = resultSet.getColumnNames();
      if (columnNames.contains(SpaceConstant.JOB) && columnNames.contains(SpaceConstant.STATUS)
          && columnNames.contains(SpaceConstant.STOP_TIME)) {
        List<ValueWrapper> jobValues = resultSet.colValues(SpaceConstant.JOB);
        List<ValueWrapper> statusValues = resultSet.colValues(SpaceConstant.STATUS);
        List<ValueWrapper> stopTimeValues = resultSet.colValues(SpaceConstant.STOP_TIME);
        for (int i = SpaceConstant.INDEX; i < jobValues.size(); i++) {
          // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
          Long job = (jobValues.get(i).asLong());
          if (job.intValue() == id) {
            String status = statusValues.get(i).asString();
            DateTime dateTime = stopTimeValues.get(i).asDateTime().getLocalDateTime();
            if (status.equals(SpaceConstant.FINISHED) && DateUtil.dateTimeComparison(dateTime)) {
              return true;
            } else {
              //停止任务
              graphTagManageMapper.stopJob(id, spaceId);
              return true;
            }
          }
        }
      }

    } catch (Exception e) {
      log.error("【Error get job space for job ： {}】", spaceId);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_JOB_INFO);
    }

    return false;
  }

  @Override
  public void rebuildTagIndex(String s, String tagName) {
    log.info("【Index rebuild info  ：{}】 ", s);
    try {
      graphTagManageMapper.rebuildTagIndex(s, tagName + SpaceConstant.FIX_INDEX_NAME);
    } catch (Exception e) {
      log.error("【Error Index rebuild info ： {}】", s, e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_INDEX_TAG_REBUILD);
    }
  }

  @Override
  public void rebuildEdgeIndex(String s, String edgeName) {
    log.info("【Index edge rebuild info  ：{}】 ", s);
    try {
      graphTagManageMapper.rebuildEdgeIndex(s, edgeName + SpaceConstant.FIX_INDEX_NAME);
    } catch (Exception e) {
      log.error("【Error Index edge rebuild info ： {}】", s, e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SPACE_INDEX_EDGE_REBUILD);
    }
  }

  @Override
  public void saveGraph(TagResultDO graphPattern, Integer spaceId) {
    log.info("【save graph pattern data in to mysql 】");
    // 解析数据存储
    Set<Data> datas = graphPattern.getDatas();
    Map<String, String> srcMap = graphPattern.getSrcMap();
    Set<Link> links = new HashSet<>();
    List<GraphPatternTagPo> graphPatternTags = new ArrayList<>();
    List<GraphPatternEdgePo> graphPatternEdges = new ArrayList<>();

    // 清除原有数据
    graphPatternTagMapper.delete(
        Wrappers.<GraphPatternTagPo>lambdaQuery().eq(GraphPatternTagPo::getSpaceId, spaceId));

    graphPatternEdgeMapper.delete(
        Wrappers.<GraphPatternEdgePo>lambdaQuery().eq(GraphPatternEdgePo::getSpaceId, spaceId));
    // 解析数据
    srcMap.forEach((key, value) -> {
      String[] split = key.split("-");
      Link link = new Link();
      link.setSource(split[SpaceConstant.INDEX]);
      link.setTarget(split[SpaceConstant.REPLICA_FACTOR]);
      link.setValue(value);
      links.add(link);
    });

    datas.stream().forEach(node -> {
      GraphPatternTagPo tag = new GraphPatternTagPo();
      tag.setSpaceId(spaceId);
      tag.setTagName(node.getId());
      graphPatternTags.add(tag);
    });

    links.stream().forEach(link -> {
      GraphPatternEdgePo edge = new GraphPatternEdgePo();
      edge.setSpaceId(spaceId);
      edge.setSource(link.getSource());
      edge.setTarget(link.getTarget());
      edge.setValue(link.getValue());
      graphPatternEdges.add(edge);
    });

    // 批量存储可视化数据
    if (!CollectionUtils.isEmpty(graphPatternTags)) {
      MybatisPlusUtil<GraphPatternTagPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          graphPatternTagMapper,
          GraphPatternTagPo.class);
      mybatisPlusUtil.saveBatch(graphPatternTags, graphPatternTags.size());
    }

    // 批量存储可视化数据
    if (!CollectionUtils.isEmpty(graphPatternEdges)) {
      MybatisPlusUtil<GraphPatternEdgePo> mybatisPlusUtil = new MybatisPlusUtil<>(
          graphPatternEdgeMapper,
          GraphPatternEdgePo.class);
      mybatisPlusUtil.saveBatch(graphPatternEdges, graphPatternEdges.size());
    }

    // 更新可视化数据状态
    log.info("【Mysql update  space: {}  pattern data  info  in db 】", spaceId);

    // 执行更新操作
    int result = knowledgePatterManagerMapper.update(Wrappers.<PatterManagerPo>lambdaUpdate()
        .set(PatterManagerPo::getIsFlush, SpaceConstant.REPLICA_FACTOR)
        .set(PatterManagerPo::getUpdateTime, new Date())
        .eq(PatterManagerPo::getSpaceId, spaceId));
  }

  public boolean isCommaSeparated(String input) {
    // 去除字符串两端的空白字符
    String trimmedInput = input.trim();

    // 如果字符串为空或没有逗号，则不是逗号分隔的
    if (trimmedInput.isEmpty() || !trimmedInput.contains(",")) {
      return false;
    }

    // 使用逗号分割字符串，并检查分割后的数组长度是否大于1
    String[] parts = trimmedInput.split(",");
    return parts.length > 1;
  }


}
