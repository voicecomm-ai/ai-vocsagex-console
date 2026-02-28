package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import static cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil.containsSpecialCharacters;
import static com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.EdgePatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.GraphPatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertySaveReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.TagPatternDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.PropertyType;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.TagEdgeEnum.TypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphTagEdgePropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.TagResultDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Data;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphTagEdgeProperty;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Link;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagType;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeGraphTagEdgeConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeGraphTagEdgePropertyConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphPatternEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphPatternTagMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgePropertyMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgePatterManagerMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphTagEdgeManageService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.GraphPatternEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.GraphPatternTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.PatterManagerPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 图知识库本体关系接口实现类
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphTagEdgeServiceImpl extends
    ServiceImpl<KnowledgeGraphTagEdgeMapper, KnowledgeGraphTagEdgePo> implements
    KnowledgeGraphTagEdgeService {

  private final KnowledgeGraphTagEdgeConverter graphTagEdgeConverter;

  private final KnowledgeGraphTagEdgePropertyConverter graphTagEdgePropertyConverter;

  private final KnowledgeGraphTagEdgePropertyMapper graphTagEdgePropertyMapper;

  private final GraphTagEdgeManageService graphTagEdgeManageService;

  private final GraphVertexService graphVertexService;

  private final GraphEdgeService graphEdgeService;

  private final KnowledgeGraphPatternEdgeMapper graphPatternEdgeMapper;

  private final KnowledgeGraphPatternTagMapper graphPatternTagMapper;

  private final KnowledgePatterManagerMapper graphPatterManagerMapper;


  private static final List<String> TYPELIST = Arrays.asList("INT8", "INT16", "INT32", "INT64",
      "FIXED_STRING", "FLOAT");
  private static final List<String> TYPEINTLIST = Arrays.asList("INT8", "INT16", "INT32", "INT64");
  private final KnowledgeBaseService knowledgeBaseService;

  @Override
  public CommonRespDto<List<KnowledgeGraphTagEdgeDto>> getList(
      KnowledgeGraphTagEdgePageReq pageReq) {
    log.info("获取Tag/Edge列表请求参数：{}", JSON.toJSONString(pageReq, WriteMapNullValue));
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList = baseMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .apply(StrUtil.isNotBlank(pageReq.getTagName()), "tag_name ILIKE {0}",
                "%" + SpecialCharUtil.transfer(pageReq.getTagName()) + "%")
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, pageReq.getSpaceId())
            .eq(ObjectUtil.isNotNull(pageReq.getType()), KnowledgeGraphTagEdgePo::getType,
                pageReq.getType()).orderByDesc(BasePo::getCreateTime)
            .orderByDesc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    return CommonRespDto.success(
        graphTagEdgeConverter.poListToDtoList(knowledgeGraphTagEdgePoList));
  }

  @Override
  public CommonRespDto<PagingRespDto<KnowledgeGraphTagEdgePropertyDto>> getTagEdgeInfo(
      KnowledgeGraphTagEdgePropertyReq tagEdgePropertyReq) {
    log.info("获取属性列表请求参数：{}", JSON.toJSONString(tagEdgePropertyReq, WriteMapNullValue));
    Page<KnowledgeGraphTagEdgePropertyPo> page = Page.of(tagEdgePropertyReq.getCurrent(),
        tagEdgePropertyReq.getSize());
    LambdaQueryWrapper<KnowledgeGraphTagEdgePropertyPo> lambdaQueryWrapper = Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
        .apply(StrUtil.isNotBlank(tagEdgePropertyReq.getPropertyName()), "property_name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(tagEdgePropertyReq.getPropertyName()) + "%")
        .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgePropertyReq.getTagEdgeId())
        .orderByDesc(BasePo::getCreateTime)
        .orderByDesc(KnowledgeGraphTagEdgePropertyPo::getPropertyId);
    PagingRespDto<KnowledgeGraphTagEdgePropertyDto> pagingRespDto = graphTagEdgePropertyConverter.pagePoToDto(
        graphTagEdgePropertyMapper.selectPage(page, lambdaQueryWrapper));
    List<KnowledgeGraphTagEdgePropertyDto> tagEdgePropertyDtoList = pagingRespDto.getRecords();
    tagEdgePropertyDtoList.forEach(
        propertyDto -> propertyDto.setTagName(tagEdgePropertyReq.getTagName()));
    return CommonRespDto.success(pagingRespDto);
  }

  @Override
  public CommonRespDto<Integer> save(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    log.info("新增Tag/Edge请求参数：{}",
        JSON.toJSONString(knowledgeGraphTagEdgeDto, WriteMapNullValue));
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = graphTagEdgeConverter.dtoToPo(
        knowledgeGraphTagEdgeDto);
    try {
      // 图数据库使用
      GraphCreateTagEdge graphCreateTag = new GraphCreateTagEdge();
      BeanUtils.copyProperties(knowledgeGraphTagEdgeDto, graphCreateTag);
      graphCreateTag.setSpace(
          SpaceConstant.SPACE_FIX_NAME + "_" + knowledgeGraphTagEdgeDto.getSpaceId());
      graphTagEdgeManageService.createTagEdge(graphCreateTag);
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    baseMapper.insert(knowledgeGraphTagEdgePo);
    return CommonRespDto.success(knowledgeGraphTagEdgePo.getTagEdgeId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    log.info("删除Tag/Edge请求参数：{}",
        JSON.toJSONString(knowledgeGraphTagEdgeDto, WriteMapNullValue));
    try {
      // 图数据库操作
      GraphCreateTagEdge graphCreateTagEdge = new GraphCreateTagEdge();
      BeanUtils.copyProperties(knowledgeGraphTagEdgeDto, graphCreateTagEdge);
      graphCreateTagEdge.setSpace(
          SpaceConstant.SPACE_FIX_NAME + "_" + knowledgeGraphTagEdgeDto.getSpaceId());
      checkEntityData(knowledgeGraphTagEdgeDto.getSpaceId(), knowledgeGraphTagEdgeDto.getType(),
          knowledgeGraphTagEdgeDto.getTagName());
      // 删除 Tag / Edge 对应 的索引
      graphTagEdgeManageService.dropIndex(graphCreateTagEdge);
      graphTagEdgeManageService.dropTagEdge(graphCreateTagEdge);
      graphVertexService.executeTheTask(
          SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId());
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    baseMapper.deleteById(knowledgeGraphTagEdgeDto.getTagEdgeId());
    graphTagEdgePropertyMapper.delete(Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
        .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId,
            knowledgeGraphTagEdgeDto.getTagEdgeId()));
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<KnowledgeGraphTagEdgeDto> getTtlCol(KnowledgeGraphTagEdgeReq tagEdgeReq) {
    log.info("获取Tag/Edge过期时间请求参数：{}", JSON.toJSONString(tagEdgeReq, WriteMapNullValue));
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectById(
        tagEdgeReq.getTagEdgeId());
    if (ObjectUtil.isNull(knowledgeGraphTagEdgePo)) {
      return CommonRespDto.error("数据不存在");
    }
    return CommonRespDto.success(graphTagEdgeConverter.poToDto(knowledgeGraphTagEdgePo));
  }

  @Override
  public CommonRespDto<Boolean> createTtlCol(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    log.info("新增Tag/Edge过期时间请求参数：{}",
        JSON.toJSONString(knowledgeGraphTagEdgeDto, WriteMapNullValue));
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = graphTagEdgeConverter.dtoToPo(
        knowledgeGraphTagEdgeDto);
    try {
      GraphCreateTagEdge graphCreateTagEdge = new GraphCreateTagEdge();
      BeanUtils.copyProperties(knowledgeGraphTagEdgeDto, graphCreateTagEdge);
      // time from h to s
      graphCreateTagEdge.setTtlDuration(graphCreateTagEdge.getTtlDuration() * SpaceConstant.SECOND);
      graphCreateTagEdge.setSpace(
          SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId());
      // 执行图数据库
      graphTagEdgeManageService.setTtlProperties(graphCreateTagEdge);
      Thread.sleep(SpaceConstant.WAIT);
      if (SpaceConstant.INDEX == knowledgeGraphTagEdgeDto.getType()) {
        graphTagEdgeManageService.rebuildTagIndex(
            SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId(),
            knowledgeGraphTagEdgeDto.getTagName());
      } else {
        graphTagEdgeManageService.rebuildEdgeIndex(
            SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId(),
            knowledgeGraphTagEdgeDto.getTagName());
      }
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    baseMapper.updateById(knowledgeGraphTagEdgePo);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> dropTtlCol(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    log.info("清空Tag/EdgeTtl时间请求参数：{}",
        JSON.toJSONString(knowledgeGraphTagEdgeDto, WriteMapNullValue));
    try {
      GraphTagEdgePropertyDO graphCreateTagEdge = new GraphTagEdgePropertyDO();
      BeanUtils.copyProperties(knowledgeGraphTagEdgeDto, graphCreateTagEdge);
      graphCreateTagEdge.setSpace(
          SpaceConstant.SPACE_FIX_NAME + "_" + knowledgeGraphTagEdgeDto.getSpaceId());
      // 删除图数据库
      graphTagEdgeManageService.dropTagEdgeTtl(graphCreateTagEdge);
      Thread.sleep(SpaceConstant.WAIT);
      if (SpaceConstant.INDEX == knowledgeGraphTagEdgeDto.getType()) {
        graphTagEdgeManageService.rebuildTagIndex(
            SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId(),
            knowledgeGraphTagEdgeDto.getTagName());
      } else {
        graphTagEdgeManageService.rebuildEdgeIndex(
            SpaceConstant.SPACE_NAME_FIX + knowledgeGraphTagEdgeDto.getSpaceId(),
            knowledgeGraphTagEdgeDto.getTagName());
      }
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    baseMapper.update(Wrappers.<KnowledgeGraphTagEdgePo>lambdaUpdate()
        .set(KnowledgeGraphTagEdgePo::getTtlCol, StrUtil.EMPTY)
        .set(KnowledgeGraphTagEdgePo::getTtlDuration, 0)
        .eq(KnowledgeGraphTagEdgePo::getTagEdgeId, knowledgeGraphTagEdgeDto.getTagEdgeId()));
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<List<String>> getAllTtlField(KnowledgeGraphTagEdgeReq tagEdgeReq) {
    log.info("获取Tag/Edge下Ttl属性字段列表请求参数：{}",
        JSON.toJSONString(tagEdgeReq, WriteMapNullValue));
    return CommonRespDto.success(graphTagEdgePropertyMapper.selectList(
            Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
                .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgeReq.getTagEdgeId())
                .eq(KnowledgeGraphTagEdgePropertyPo::getPropertyType, "TIMESTAMP")).stream()
        .map(KnowledgeGraphTagEdgePropertyPo::getPropertyName).collect(Collectors.toList()));
  }

  @Override
  public CommonRespDto<Integer> createTagEdgeProperties(
      KnowledgeGraphTagEdgePropertySaveReq propertySaveReq) {
    log.info("新增属性请求参数：{}", JSON.toJSONString(propertySaveReq, WriteMapNullValue));
    // 属性名查重
    TagType tagType = propertySaveReq.getTagTypes();
    // 处理前端遗漏的逻辑
    if (tagType.getPropertyType().equalsIgnoreCase(SpaceConstant.FIX_STRING)
        && tagType.getTagRequired().equals(SpaceConstant.ENGDE)) {
      tagType.setDefaultValue("");
    }
    Long selectCount = graphTagEdgePropertyMapper.selectCountByTagEdgeIdAndNameWithoutDelete(
        propertySaveReq.getTagEdgeId(), tagType.getPropertyName());
    if (selectCount > 0) {
      return CommonRespDto.error(ErrorConstants.SAME_NAME_PROPERTIES_IS_EXISTS.getMessage());
    }
    // 校验 属性名称是否符合
    if (!containsSpecialCharacters(propertySaveReq.getTagTypes().getPropertyName())) {
      log.error("【Error alter tag/edge properties Name  with exists: {}】",
          propertySaveReq.getTagTypes().getPropertyName());
      return CommonRespDto.error(ErrorConstants.SAME_NAME_PROPERTIES.getMessage());
    }
    if (SpaceConstant.NAME.equals(tagType.getPropertyName())) {
      return CommonRespDto.error(ErrorConstants.SAME_NAME.getMessage());
    }
    KnowledgeGraphTagEdgePropertyPo graphTagEdgePropertyPo = graphTagEdgePropertyConverter.tagTypeToPo(
        tagType);
    graphTagEdgePropertyPo.setTagEdgeId(propertySaveReq.getTagEdgeId());
    graphTagEdgePropertyPo.setType(propertySaveReq.getType());
    graphTagEdgePropertyPo.setDefaultValue(tagType.getDefaultValue());
    try {
      GraphCreateTagEdge graphCreateTagEdge = new GraphCreateTagEdge();
      BeanUtils.copyProperties(propertySaveReq, graphCreateTagEdge);
      graphCreateTagEdge.setSpace(
          SpaceConstant.SPACE_FIX_NAME + "_" + propertySaveReq.getSpaceId());
      graphTagEdgeManageService.insertTagEdgeProperties(graphCreateTagEdge);
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    graphTagEdgePropertyMapper.insert(graphTagEdgePropertyPo);
    return CommonRespDto.success(graphTagEdgePropertyPo.getPropertyId());
  }

  @Override
  public CommonRespDto<Void> updateProperty(
      KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto) {
    log.info("更新属性请求参数：{}",
        JSON.toJSONString(knowledgeGraphTagEdgePropertyDto, WriteMapNullValue));
    KnowledgeGraphTagEdgePropertyPo knowledgeGraphTagEdgePropertyPo = graphTagEdgePropertyMapper.selectById(
        knowledgeGraphTagEdgePropertyDto.getPropertyId());
    if (ObjectUtil.isNull(knowledgeGraphTagEdgePropertyPo)) {
      return CommonRespDto.error("属性不存在");
    }
    CommonRespDto<Void> accordRespDto = accordType(knowledgeGraphTagEdgePropertyDto,
        knowledgeGraphTagEdgePropertyPo);
    if (!accordRespDto.isOk()) {
      return accordRespDto;
    }
    try {
      // 图数据修改
      GraphTagEdgePropertyDO graphCreateTagEdge = new GraphTagEdgePropertyDO();
      BeanUtils.copyProperties(knowledgeGraphTagEdgePropertyDto, graphCreateTagEdge);
      graphCreateTagEdge.setSpace(
          SpaceConstant.SPACE_FIX_NAME + "_" + knowledgeGraphTagEdgePropertyDto.getSpaceId());
      //假设builder是一个StringBuilder实例
      StringBuilder builder = new StringBuilder();
      // 提取公共的字符串拼接部分
      String propertyName = knowledgeGraphTagEdgePropertyDto.getPropertyName();
      String propertyType = knowledgeGraphTagEdgePropertyDto.getPropertyType();
      // 初始化额外的字符串拼接部分
      String extraPart = "";
      if (SpaceConstant.FIX_STRING.equalsIgnoreCase(propertyType)) {
        extraPart = SpaceConstant.FIX_TAG_NAME_START + knowledgeGraphTagEdgePropertyDto.getExtra()
            + SpaceConstant.FIX_TAG_NAME_SUX;
      }
      // 判断是否需要添加额外的字段（如 NOT_NULL、EDGE_DEFAULT、默认值等）
      if (knowledgeGraphTagEdgePropertyDto.getTagRequired() == SpaceConstant.INDEX) {
        String value = processData(knowledgeGraphTagEdgePropertyDto.getPropertyType(),
            knowledgeGraphTagEdgePropertyDto.getDefaultValue());
        builder.append("`").append(propertyName).append("`").append(SpaceConstant.TAG_SPACE)
            .append(propertyType).append(extraPart)
            // 如果propertyType是FIX_STRING，则添加额外的部分
            .append(SpaceConstant.TAG_SPACE).append(SpaceConstant.TAG_NOT_NULL)
            .append(SpaceConstant.TAG_SPACE).append(SpaceConstant.TAG_EDGE_DEFAULT).append(value);
      } else {
        builder.append("`").append(propertyName).append("`").append(SpaceConstant.TAG_SPACE)
            .append(propertyType).append(SpaceConstant.TAG_SPACE)
            // 如果propertyType是FIX_STRING，则添加额外的部分
            .append(extraPart);
      }
      graphCreateTagEdge.setProperties(builder.toString());
      graphTagEdgeManageService.updateGraphProperty(graphCreateTagEdge);
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }

    graphTagEdgePropertyMapper.updateById(
        graphTagEdgePropertyConverter.dtoToPo(knowledgeGraphTagEdgePropertyDto));
    return CommonRespDto.success();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> dropTagEdgeProperty(
      KnowledgeGraphTagEdgePropertyDeleteReq propertyDeleteReq) {
    log.info("清空属性请求参数：{}", JSON.toJSONString(propertyDeleteReq, WriteMapNullValue));
    List<KnowledgeGraphTagEdgePropertyDto> propertyDtoList = propertyDeleteReq.getPropertyVOS();
    try {
      List<String> properties = propertyDtoList.stream()
          .map(KnowledgeGraphTagEdgePropertyDto::getPropertyName).collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(properties)) {
        StringJoiner stringJoiner = new StringJoiner(",");
        StringJoiner edgeJoiner = new StringJoiner(",");
        ResultSet resultSet = null;
        for (String s : properties) {
          stringJoiner.add(SpaceConstant.PROPERTY_NAME + SpaceConstant.QUOTATIONMARK
              + propertyDeleteReq.getTagName() + SpaceConstant.QUOTATIONMARK + SpaceConstant.VERTEX
              + SpaceConstant.QUOTATIONMARK + s + SpaceConstant.QUOTATIONMARK + SpaceConstant.AS
              + SpaceConstant.QUOTATIONMARK + s.toUpperCase() + SpaceConstant.QUOTATIONMARK);
          edgeJoiner.add(SpaceConstant.PROPERTY_NAME_EDGE + SpaceConstant.QUOTATIONMARK + s
              + SpaceConstant.QUOTATIONMARK + SpaceConstant.AS + SpaceConstant.QUOTATIONMARK
              + s.toUpperCase() + SpaceConstant.QUOTATIONMARK);
        }

        if (SpaceConstant.INDEX == propertyDeleteReq.getType()) {
          resultSet = graphVertexService.getEntityTotalByProperty(
              SpaceConstant.SPACE_FIX_NAME + "_" + propertyDeleteReq.getSpaceId(),
              propertyDeleteReq.getTagName(), stringJoiner.toString());

        } else {
          resultSet = graphEdgeService.getEntityTotalByProperty(
              SpaceConstant.SPACE_FIX_NAME + "_" + propertyDeleteReq.getSpaceId(),
              propertyDeleteReq.getTagName(), edgeJoiner.toString());
        }
        if (null != resultSet) {
          for (String s : properties) {
            List<ValueWrapper> idList = resultSet.colValues(s.toUpperCase());
            for (ValueWrapper v : idList) {
              if (!v.isNull()) {
                log.error("【kg-webserver-db error delete tag : {}】",
                    propertyDeleteReq.getTagEdgeId());
                return CommonRespDto.error(ErrorConstants.DELTE_PRPPERTY_ERROR.getMessage());
              }
            }
          }
        }
      }
      GraphTagEdgeProperty tagEdgeProperty = new GraphTagEdgeProperty();
      BeanUtils.copyProperties(propertyDeleteReq, tagEdgeProperty);
      tagEdgeProperty.setSpace(SpaceConstant.SPACE_FIX_NAME + "_" + propertyDeleteReq.getSpaceId());
      for (KnowledgeGraphTagEdgePropertyDto propertyVO : propertyDeleteReq.getPropertyVOS()) {
        tagEdgeProperty.setPropertyName(propertyVO.getPropertyName());
        tagEdgeProperty.setPropertyId(Long.valueOf(propertyVO.getPropertyId()));
        graphTagEdgeManageService.dropTagEdgeProperty(tagEdgeProperty);
      }
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    List<Integer> propertyIds = propertyDtoList.stream()
        .map(KnowledgeGraphTagEdgePropertyDto::getPropertyId).collect(Collectors.toList());
    if (CollUtil.isNotEmpty(propertyIds)) {
      graphTagEdgePropertyMapper.delete(Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
          .in(KnowledgeGraphTagEdgePropertyPo::getPropertyId, propertyIds));
      baseMapper.update(Wrappers.<KnowledgeGraphTagEdgePo>lambdaUpdate()
          .set(KnowledgeGraphTagEdgePo::getTtlCol, StrUtil.EMPTY)
          .set(KnowledgeGraphTagEdgePo::getTtlDuration, 0)
          .eq(KnowledgeGraphTagEdgePo::getTagEdgeId, propertyDeleteReq.getTagEdgeId()));
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> dropAllTagEdgeProperty(
      KnowledgeGraphTagEdgeDeleteReq graphTagEdgeDeleteReq) {
    log.info("清空所有属性请求参数：{}",
        JSON.toJSONString(graphTagEdgeDeleteReq, WriteMapNullValue));
    LambdaQueryWrapper<KnowledgeGraphTagEdgePropertyPo> lambdaQueryWrapper = Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
        .apply(StrUtil.isNotBlank(graphTagEdgeDeleteReq.getPropertyName()),
            "property_name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(graphTagEdgeDeleteReq.getPropertyName()) + "%")
        .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, graphTagEdgeDeleteReq.getTagEdgeId());
    List<KnowledgeGraphTagEdgePropertyPo> graphTagEdgePropertyPoList = graphTagEdgePropertyMapper.selectList(
        lambdaQueryWrapper);
    GraphTagEdgeProperty tagEdgeProperty = new GraphTagEdgeProperty();
    BeanUtils.copyProperties(graphTagEdgeDeleteReq, tagEdgeProperty);
    tagEdgeProperty.setSpace(
        SpaceConstant.SPACE_FIX_NAME + "_" + graphTagEdgeDeleteReq.getSpaceId());
    try {
      if (SpaceConstant.INDEX == graphTagEdgeDeleteReq.getType()) {
        ResultSet resultSet = graphVertexService.getEntityTotalByAllProperty(
            tagEdgeProperty.getSpace(), graphTagEdgeDeleteReq.getTagName());
        if (null != resultSet) {
          List<ValueWrapper> idList = resultSet.colValues(SpaceConstant.PRO);
          List<ValueWrapper> tags = resultSet.colValues(SpaceConstant.T);
          if (idList.size() == tags.size()) {
            for (int i = SpaceConstant.INDEX; i < idList.size(); i++) {
              Map<String, ValueWrapper> stringValueWrapperHashMap = idList.get(i).asMap();
              List<ValueWrapper> tagsValue = tags.get(i).asList();
              if (null != stringValueWrapperHashMap
                  && tagsValue.size() == SpaceConstant.REPLICA_FACTOR) {
                for (Map.Entry<String, ValueWrapper> entry : stringValueWrapperHashMap.entrySet()) {
                  if (entry.getKey().contains(graphTagEdgeDeleteReq.getPropertyName())
                      && !SpaceConstant.NAME.equals(entry.getKey()) && !entry.getValue().isNull()) {
                    log.error("【kg-webserver-db error delete tag : {}】",
                        graphTagEdgeDeleteReq.getTagEdgeId());
                    throw ServiceExceptionUtil.exception(ErrorConstants.DELTE_PRPPERTY_ERROR);
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("调用图数据库服务异常：", e);
      return CommonRespDto.error(e.getMessage());
    }
    graphTagEdgePropertyMapper.delete(lambdaQueryWrapper);
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectById(
        graphTagEdgeDeleteReq.getTagEdgeId());
    if (CollUtil.isNotEmpty(graphTagEdgePropertyPoList)) {
      for (KnowledgeGraphTagEdgePropertyPo property : graphTagEdgePropertyPoList) {
        // 进行图数据库删除
        tagEdgeProperty.setPropertyName(property.getPropertyName());
        tagEdgeProperty.setPropertyId(Long.valueOf(property.getPropertyId()));
        graphTagEdgeManageService.dropTagEdgeProperty(tagEdgeProperty);
      }
      Set<String> propertyNameSet = graphTagEdgePropertyPoList.stream()
          .map(KnowledgeGraphTagEdgePropertyPo::getPropertyName).collect(Collectors.toSet());
      if (propertyNameSet.contains(knowledgeGraphTagEdgePo.getTtlCol())) {
        baseMapper.update(Wrappers.<KnowledgeGraphTagEdgePo>lambdaUpdate()
            .set(KnowledgeGraphTagEdgePo::getTtlCol, StrUtil.EMPTY)
            .set(KnowledgeGraphTagEdgePo::getTtlDuration, 0)
            .eq(KnowledgeGraphTagEdgePo::getTagEdgeId, graphTagEdgeDeleteReq.getTagEdgeId()));
      }
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> deleteTagEdgeByKnowledgeId(Integer spaceId) {
    log.info("删除本体请求参数knowledgeId：{}", spaceId);
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList = baseMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId));
    if (CollUtil.isNotEmpty(knowledgeGraphTagEdgePoList)) {
      baseMapper.delete(Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
          .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId));
      List<Integer> tagEdgeIds = knowledgeGraphTagEdgePoList.stream()
          .map(KnowledgeGraphTagEdgePo::getTagEdgeId).collect(Collectors.toList());
      for (int i = 0; i < tagEdgeIds.size(); i += SpaceConstant.BATCH_SIZE) {
        int end = Math.min(i + SpaceConstant.BATCH_SIZE, tagEdgeIds.size());
        List<Integer> subList = tagEdgeIds.subList(i, end);
        graphTagEdgePropertyMapper.delete(Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .in(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, subList));
      }
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Boolean> updateGraphPattern(Integer spaceId) {
    log.info("更新模型可视化请求参数knowledgeBaseId：{}", spaceId);
    TagResultDO tagResultDO = graphTagEdgeManageService.showPattern(
        SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
// 存储到mysql
    graphTagEdgeManageService.saveGraph(tagResultDO, spaceId);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<TagPatternDto> getGraphPatternTag(Integer spaceId, String tagName) {
    log.info("模型可视化获取本体请求参数knowledgeBaseId：{}，tagName：{}", spaceId, tagName);
    TagPatternDto tagPatternDto = new TagPatternDto();
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectOne(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, tagName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, TypeEnum.TAG.getKey()), false);
    if (ObjectUtil.isNotNull(knowledgeGraphTagEdgePo)) {
      BeanUtils.copyProperties(knowledgeGraphTagEdgePo, tagPatternDto);
      List<KnowledgeGraphTagEdgePropertyPo> tagEdgePropertyPoList = graphTagEdgePropertyMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId,
                  knowledgeGraphTagEdgePo.getTagEdgeId()));
      tagPatternDto.setPatternProperties(
          graphTagEdgePropertyConverter.poListToDtoList(tagEdgePropertyPoList));
    }
    return CommonRespDto.success(tagPatternDto);
  }

  @Override
  public CommonRespDto<EdgePatternDto> getGraphPatternEdge(Integer spaceId, String edgeName) {
    log.info("模型可视化获取关系请求参数knowledgeBaseId：{}，edgeName：{}", spaceId, edgeName);
    EdgePatternDto edgePatternDto = new EdgePatternDto();
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectOne(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, edgeName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, TypeEnum.EDGE.getKey()), false);
    if (ObjectUtil.isNotNull(knowledgeGraphTagEdgePo)) {
      BeanUtils.copyProperties(knowledgeGraphTagEdgePo, edgePatternDto);
      List<KnowledgeGraphTagEdgePropertyPo> tagEdgePropertyPoList = graphTagEdgePropertyMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId,
                  knowledgeGraphTagEdgePo.getTagEdgeId()));
      edgePatternDto.setPatternProperties(
          graphTagEdgePropertyConverter.poListToDtoList(tagEdgePropertyPoList));
      edgePatternDto.setEdgeName(edgeName);
    }
    return CommonRespDto.success(edgePatternDto);
  }

  @Override
  public CommonRespDto<GraphPatternDto> getGraphPattern(Integer spaceId) {
    log.info("模型可视化请求参数knowledgeBaseId：{}", spaceId);
    GraphPatternDto graphPattern = new GraphPatternDto();
    Set<Link> edges = new HashSet<>();
    Set<Data> nodes = new HashSet<>();

    // 查找本体数据
    List<GraphPatternTagPo> mapTagEdges = graphPatternTagMapper.selectList(
        Wrappers.<GraphPatternTagPo>lambdaQuery()
            .eq(GraphPatternTagPo::getSpaceId, spaceId));
    // 解析数据 本体
    mapTagEdges.stream().forEach(tag -> {
      Data data = new Data();

      data.setId(tag.getTagName());

      nodes.add(data);
    });
    graphPattern.setNodes(nodes);

    //获取边的数据
    List<GraphPatternEdgePo> graphPatternEdges = graphPatternEdgeMapper.selectList(
        Wrappers.<GraphPatternEdgePo>lambdaQuery()
            .eq(GraphPatternEdgePo::getSpaceId, spaceId));

    // 解析边
    graphPatternEdges.stream().forEach(edge -> {
      Link link = new Link(edge.getSource(), edge.getTarget(), edge.getValue());
      ;

      edges.add(link);
    });

    graphPattern.setEdges(edges);

    // 获取上次更新时间
    PatterManagerPo patterManager = graphPatterManagerMapper.selectOne(
        Wrappers.<PatterManagerPo>lambdaQuery()
            .eq(PatterManagerPo::getSpaceId, spaceId));
    if (null != patterManager) {
      graphPattern.setUpdatedTime(DateUtil.date(patterManager.getUpdateTime()));
    }

    return CommonRespDto.success(graphPattern);
  }

  @Override
  public KnowledgeGraphTagEdgeDto getTagInfo(Long spaceId, String tagName) {
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectOne(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getTagName, tagName)
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX), false);
    return graphTagEdgeConverter.poToDto(knowledgeGraphTagEdgePo);
  }

  @Override
  public KnowledgeGraphTagEdgeDto getEdgeInfo(Long spaceId, String tagName) {
    KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectOne(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getTagName, tagName)
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.REPLICA_FACTOR), false);
    return graphTagEdgeConverter.poToDto(knowledgeGraphTagEdgePo);
  }

  @Override
  public List<KnowledgeGraphTagEdgeDto> getTagInfos(Long spaceId) {
    log.info("【get all edges data in to mysql for space: {} 】", spaceId);
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePo = baseMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.REPLICA_FACTOR)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    return graphTagEdgeConverter.poListToDtoList(knowledgeGraphTagEdgePo);
  }

  @Override
  public List<KnowledgeGraphTagEdgeDto> getEdgeInfos(Long spaceId) {
    log.info("【get all Tags data in to mysql for space: {} 】", spaceId);
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePo = baseMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    return graphTagEdgeConverter.poListToDtoList(knowledgeGraphTagEdgePo);
  }

  @Override
  public KnowledgeGraphTagEdgePropertyDto getTagEdgeProperty(Long tagEdgeId, int type,
      String properyName) {
    log.info("【Mysql get All  Tag / Edge  property :{}  field info from  mysql】", tagEdgeId);
    KnowledgeGraphTagEdgePropertyPo knowledgeGraphTagEdgePropertyPo = graphTagEdgePropertyMapper.selectOne(
        Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgeId)
            .eq(KnowledgeGraphTagEdgePropertyPo::getType, type)
            .eq(KnowledgeGraphTagEdgePropertyPo::getPropertyName, properyName), false);
    return graphTagEdgePropertyConverter.poToDto(knowledgeGraphTagEdgePropertyPo);
  }

  @Override
  public List<KnowledgeGraphTagEdgePropertyDto> getEdgeProperty(Long tagId, int type) {
    log.info("【Mysql get All  Tag / Edge  property :{}  field info from  mysql】", tagId);
    List<KnowledgeGraphTagEdgePropertyPo> graphTagEdgePropertyPoList = graphTagEdgePropertyMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagId)
            .eq(KnowledgeGraphTagEdgePropertyPo::getType, type)
            .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
    return graphTagEdgePropertyConverter.poListToDtoList(graphTagEdgePropertyPoList);
  }

  /**
   * 校验字段
   *
   * @param graphTagEdgePropertyDto
   * @param tagEdgePropertyPo
   */
  private CommonRespDto<Void> accordType(KnowledgeGraphTagEdgePropertyDto graphTagEdgePropertyDto,
      KnowledgeGraphTagEdgePropertyPo tagEdgePropertyPo) {
    if (!graphTagEdgePropertyDto.getPropertyType()
        .equalsIgnoreCase(tagEdgePropertyPo.getPropertyType())
        || tagEdgePropertyPo.getPropertyType().equals(SpaceConstant.FIX_STRING)) {
      if (TYPELIST.contains(tagEdgePropertyPo.getPropertyType())) {
        switch (tagEdgePropertyPo.getPropertyType()) {
          case SpaceConstant.FIX_STRING:
            if (!graphTagEdgePropertyDto.getPropertyType().equalsIgnoreCase(SpaceConstant.STRING)) {
              if (!(graphTagEdgePropertyDto.getPropertyType()
                  .equalsIgnoreCase(SpaceConstant.FIX_STRING)
                  && Integer.valueOf(graphTagEdgePropertyDto.getExtra()) >= Integer.valueOf(
                  tagEdgePropertyPo.getExtra()))) {
                if (!StringUtil.isBlank(graphTagEdgePropertyDto.getExtra())
                    && graphTagEdgePropertyDto.getPropertyType()
                    .equalsIgnoreCase(SpaceConstant.FIX_STRING)
                    && Integer.valueOf(graphTagEdgePropertyDto.getExtra()) < Integer.valueOf(
                    tagEdgePropertyPo.getExtra())) {
                  log.error("【kg-webserver-db update Tag Edge Property error : {}】",
                      graphTagEdgePropertyDto.getPropertyName());
                  return CommonRespDto.error(
                      ErrorConstants.UPDATE_TAG_EDGE_INT_PROPERTY.getMessage());
                } else if (!StringUtil.isBlank(graphTagEdgePropertyDto.getExtra())) {
                  log.error("【kg-webserver-db update Tag Edge Property error : {}】",
                      graphTagEdgePropertyDto.getPropertyName());
                  return CommonRespDto.error(
                      ErrorConstants.UPDATE_TAG_EDGE_STRING_PROPERTY.getMessage());
                } else {
                  return CommonRespDto.error(
                      ErrorConstants.UPDATE_TAG_EDGE_STRING_PROPERTY.getMessage());
                }
              }
            }
            break;
          case SpaceConstant.FLOAT:
            if (!graphTagEdgePropertyDto.getPropertyType().equalsIgnoreCase(SpaceConstant.DOUBLE)) {
              log.error("【kg-webserver-db update Tag Edge Property error : {}】",
                  graphTagEdgePropertyDto.getPropertyType());
              return CommonRespDto.error(
                  ErrorConstants.UPDATE_TAG_EDGE_DOUBLE_PROPERTY.getMessage());
            }
            break;
          default:
            if (TYPEINTLIST.contains(tagEdgePropertyPo.getPropertyType())) {
              try {
                if ((PropertyType.getStatus(tagEdgePropertyPo.getPropertyType())
                    > PropertyType.getStatus(graphTagEdgePropertyDto.getPropertyType()))) {
                  log.error("【kg-webserver-db update Tag Edge Property error : {}】",
                      graphTagEdgePropertyDto.getPropertyName());
                  return CommonRespDto.error(
                      ErrorConstants.UPDATE_TAG_EDGE_INT_PROPERTY.getMessage());
                }
              } catch (Exception e) {
                return CommonRespDto.error(ErrorConstants.UPDATE_TAG_EDGE_INT.getMessage());
              }
            } else {
              log.error("【kg-webserver-db update Tag Edge Property error : {}】",
                  graphTagEdgePropertyDto.getPropertyName());
              return CommonRespDto.error(ErrorConstants.UPDATE_TAG_EDGE_PROPERTY.getMessage());
            }
            break;
        }
      } else {
        log.error("【kg-webserver-db update Tag Edge Property error : {}】",
            graphTagEdgePropertyDto.getPropertyName());
        return CommonRespDto.error(ErrorConstants.UPDATE_TAG_EDGE_PROPERTY.getMessage());
      }
    } else if (tagEdgePropertyPo.getPropertyType().equals(SpaceConstant.TIMESTAMP)) {
      KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = baseMapper.selectById(
          graphTagEdgePropertyDto.getTagEdgeId());
      // 查看是否设置过存活时间
      if (knowledgeGraphTagEdgePo.getTtlCol().equals(graphTagEdgePropertyDto.getPropertyName())) {
        return CommonRespDto.error(ErrorConstants.UPDATE_TAG_TTL_PROPERTY.getMessage());
      }
    }
    return CommonRespDto.success();
  }

  /**
   * 校验数据
   *
   * @param space
   * @param type
   * @param tagName
   */
  private void checkEntityData(Integer space, int type, String tagName) {
    if (SpaceConstant.INDEX == type) {
      if (graphVertexService.getNumber(SpaceConstant.SPACE_NAME_FIX + space, tagName)
          > SpaceConstant.INDEX) {
        log.error("【Error Drop space :{} All Tag/edge info】 ", space);
        throw ServiceExceptionUtil.exception(ErrorConstants.DROP_ALL_DATA);
      }
    } else {
      if (graphEdgeService.getNumber(tagName, SpaceConstant.SPACE_NAME_FIX + space).intValue()
          > SpaceConstant.INDEX) {
        log.error("【Error Drop space :{} All Tag/edge info】 ", space);
        throw ServiceExceptionUtil.exception(ErrorConstants.DROP_ALL_EDGE_DATA);
      }
    }
  }

  private String processData(String propertyType, String propertyValue) {
    StringBuilder param = new StringBuilder();
    if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING) || propertyType.equalsIgnoreCase(
        SpaceConstant.STRING)) {
      param.append("\"").append(propertyValue).append("\"").append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DOUBLE) || propertyType.equalsIgnoreCase(
        SpaceConstant.FLOAT)) {
      double doubleValue = Double.parseDouble(propertyValue); // 尝试解析为double
      String formatValue = String.format("%.15f", doubleValue);
      param.append(formatValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATE)) {
      // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD"格式的字符串
      String dateValue = "date(\"" + propertyValue + "\")";
      param.append(dateValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIME)) {
      // 注意：这里假设time只包含小时、分钟和秒，不包括毫秒
      // 你可能需要调整以适应你的具体需求
      String timeValue = "time(\"" + propertyValue + "\")";
      param.append(timeValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATETIME)) {
      // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD HH:mm:ss"格式的字符串
      // 我们需要添加毫秒（假设为0）以符合某些数据库或系统的datetime格式
      String datetimeValue = "datetime(\"" + propertyValue + ".000000\")";
      param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIMESTAMP)) {
      String datetimeValue = "timestamp(\"" + propertyValue + ".000000\")";
      param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
      // 输出最终构建的字符串（如果需要）
    } else {
      param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
    }
    return param.toString().trim();
  }

}