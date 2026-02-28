package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphEntityManageService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphVertexDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveEntityDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveTagInfoDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDetailsVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityInfosVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveEntityVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagInfoDatilVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagInfosVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalTagInfosVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgePropertyMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateGraphUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.PropertyValidator;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.UniqueIDGenerator;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author ryc
 * @description
 * @date 2025/9/15 9:36
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphEntityManageServiceImpl implements KnowledgeGraphEntityManageService {

  private final KnowledgeGraphTagEdgeMapper graphTagEdgeMapper;

  private final KnowledgeGraphTagEdgePropertyMapper graphTagEdgePropertyMapper;

  private final GraphVertexService graphVertexService;

//  private final ApiVectorQuantityService apiVectorQuantityService;

  private static final List<String> TYPEINTLIST = Arrays.asList("INT8", "INT16", "INT32", "INT64");

  private final Map<String, TotalTagInfosVO> tagCache = new ConcurrentHashMap<>();


  @Override
  public CommonRespDto<TotalTagInfosVO> getTagInfoList(String spaceId, String tagName) {
    String cacheKey = generateCacheKey(spaceId, tagName);
    // 从缓存中获取标签信息
    TotalTagInfosVO tagInfosVO = tagCache.get(cacheKey);
    if (tagInfosVO != null && tagInfosVO.isIdentity()) {
      // 如果标签数量已经更新，则直接返回并清空缓存
      tagCache.remove(cacheKey);
      return CommonRespDto.success(tagInfosVO);
    }
    // 获取标签
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList = graphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .apply(CharSequenceUtil.isNotBlank(tagName), "tag_name ILIKE {0}",
                "%" + SpecialCharUtil.transfer(tagName) + "%")
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, Integer.valueOf(spaceId))
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX)
            .orderByDesc(BasePo::getCreateTime).orderByDesc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    List<TagInfosVO> tagInfoList = new ArrayList<>();
    knowledgeGraphTagEdgePoList.forEach(tag -> {
      TagInfosVO tagVO = new TagInfosVO();
      BeanUtils.copyProperties(tag, tagVO);
      tagVO.setTagId(Long.valueOf(tag.getTagEdgeId()));
      tagInfoList.add(tagVO);
    });
    // 创建 TotalTagInfosVO 对象并设置标签列表
    tagInfosVO = new TotalTagInfosVO();
    tagInfosVO.setTagInfosList(tagInfoList);
    // 初始设置为 false
    tagInfosVO.setIdentity(false);
    // 将初始数据存储到缓存中
    tagCache.put(cacheKey, tagInfosVO);
    // 异步获取每个标签的数量
    TotalTagInfosVO finalTagInfosVO = tagInfosVO;
    Locale locale = LocaleContextHolder.getLocale();
    CompletableFuture.runAsync(() -> {
      LocaleContextHolder.setLocale(locale);
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      try {
        for (TagInfosVO tagInfo : tagInfoList) {
          String tag = tagInfo.getTagName();
          int number = graphVertexService.getNumber(SpaceConstant.SPACE_NAME_FIX + spaceId, tag);
          tagInfo.setTagNumber(number);
        }
        // 更新缓存中的数据
        finalTagInfosVO.setIdentity(true);
        // 清空缓存，以确保下次请求能够重新获取最新数据
        tagCache.remove(cacheKey);
        // 将更新后的数据存储到缓存中
        tagCache.put(cacheKey, finalTagInfosVO);
        log.info("【所有标签数量异步获取完成】");
      } catch (Exception e) {
        log.error("【获取标签数量失败】", e);
        // 错误处理：可能需要重置 identity 或做其他处理
      }
    });
    return CommonRespDto.success(tagInfosVO);
  }

  @Override
  public CommonRespDto<List<TagPropertyResultVO>> getTagProperties(List<Long> tagIds) {
    List<TagPropertyResultVO> tags = new ArrayList<>();
    if (CollUtil.isNotEmpty(tagIds)) {
      List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = graphTagEdgePropertyMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .in(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagIds)
              .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.INDEX)
              .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
      List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList = graphTagEdgeMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
              .in(KnowledgeGraphTagEdgePo::getTagEdgeId, tagIds)
              .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
      Map<Integer, KnowledgeGraphTagEdgePo> graphTagEdgePoMap = knowledgeGraphTagEdgePoList.stream()
          .collect(Collectors.toMap(KnowledgeGraphTagEdgePo::getTagEdgeId, Function.identity()));
      tagEdgeProperties.stream().forEach(tagEdgeProperty -> {
        TagPropertyResultVO resultVO = new TagPropertyResultVO();
        BeanUtils.copyProperties(tagEdgeProperty, resultVO);
        resultVO.setTagId(Long.valueOf(tagEdgeProperty.getTagEdgeId()));
        resultVO.setDefaultValueAsString(tagEdgeProperty.getDefaultValue());
        KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo = graphTagEdgePoMap.get(
            tagEdgeProperty.getTagEdgeId());
        if (ObjectUtil.isNotNull(knowledgeGraphTagEdgePo)) {
          resultVO.setTagName(knowledgeGraphTagEdgePo.getTagName());
          resultVO.setSpaceId(Long.valueOf(knowledgeGraphTagEdgePo.getSpaceId()));
        }
        tags.add(resultVO);
      });
    }
    return CommonRespDto.success(tags);
  }

  @Override
  public CommonRespDto<PagingRespDto<EntityInfosVO>> getEntities(TagPropertyVO tagPropertyVO) {
    PagingRespDto<EntityInfosVO> pagingRespDto = new PagingRespDto<>();
    pagingRespDto.setCurrent(tagPropertyVO.getCurrent());
    pagingRespDto.setSize(tagPropertyVO.getPageSize());
    try {
      if (!SpaceConstant.PERCENT.equals(tagPropertyVO.getTagName())) {
        List<EntityInfosVO> entityInfosVOS = new ArrayList<>();
        GraphEntityDO graphEntityDO = new GraphEntityDO();
        graphEntityDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + tagPropertyVO.getSpaceId());
        BeanUtils.copyProperties(tagPropertyVO, graphEntityDO);
        List<NgVertex<String>> vertexes = graphVertexService.getVertexes(graphEntityDO);
        vertexes.forEach(v -> {
          EntityInfosVO entityInfosVO = new EntityInfosVO();
          entityInfosVO.setEntityId(v.getVid());
          if (!CollectionUtils.isEmpty(v.getTags())) {
            entityInfosVO.setTagName(
                v.getTags().stream().collect(Collectors.joining(SpaceConstant.TAG_SPLIT)));
          }
          Map<String, Object> properties = v.getProperties();
          for (Map.Entry<String, Object> entry : properties.entrySet()) {
            LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
            entityInfosVO.setEntityName(entryValue.get(SpaceConstant.NAME));
          }
          entityInfosVOS.add(entityInfosVO);
        });
        pagingRespDto.setRecords(entityInfosVOS);
        // 获取所有点
        pagingRespDto.setTotal(graphVertexService.getEntityTotal(graphEntityDO));
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(pagingRespDto);
  }

  @Override
  public CommonRespDto<Boolean> deleteVertex(TagDeleteVO tagDeleteVO) {
    log.info("【Process Kg-webserver-web delete all entity list for space :  {} tag : {}】",
        tagDeleteVO.getSpaceId(), tagDeleteVO.getEntityIds());
    try {
      // 删除图数据库
      GraphVertexDropDO graphVertexDropDO = new GraphVertexDropDO();
      graphVertexDropDO.setSpace(SpaceConstant.SPACE_NAME_FIX + tagDeleteVO.getSpaceId());
      String ids = tagDeleteVO.getEntityIds().stream().map(
          id -> SpaceConstant.DOUBLE_QUOTATION_MARKS + id.toString()
              + SpaceConstant.DOUBLE_QUOTATION_MARKS).collect(Collectors.joining(","));
      graphVertexDropDO.setVids(ids);
      graphVertexService.deleteVertex(graphVertexDropDO);
      //TODO 向量同步
//    apiVectorQuantityService.makeAsyncDelete(new DeleteVectorDTO(graphVertexDropDO.getSpace(), tagDeleteVO.getEntityIds()));
      graphVertexService.executeTheTask(SpaceConstant.SPACE_NAME_FIX + tagDeleteVO.getSpaceId());
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> deleteAllVertex(EntityDropAllVO entityDropAllVO) {
    log.info("【Process Kg-webserver-web delete all entity list for space :  {} tag : {}】",
        entityDropAllVO.getSpaceId(), entityDropAllVO.getTagName());
    try {
      List<NgVertex<String>> ngvertexs;
      GraphVertexDropDO graphVertexDropDO = new GraphVertexDropDO();
      graphVertexDropDO.setSpace(SpaceConstant.SPACE_FIX_NAME + "_" + entityDropAllVO.getSpaceId());
      if (StringUtil.isBlank(entityDropAllVO.getTagName())) {
        ngvertexs = graphVertexService.getNgvertexs(graphVertexDropDO.getSpace());
      } else {
        ngvertexs = graphVertexService.getVertexesByTagName(graphVertexDropDO.getSpace(),
            entityDropAllVO.getTagName(), entityDropAllVO.getEntityName());
      }
      List<String> ids = ngvertexs.stream().map(NgVertex::getVid).collect(Collectors.toList());
      // 删除图数据库
      String result = ids.stream()
          .map(s -> SpaceConstant.DOUBLE_QUOTATION_MARKS + s + SpaceConstant.DOUBLE_QUOTATION_MARKS)
          .collect(Collectors.joining(", "));
      graphVertexService.deleteVertexInfo(graphVertexDropDO.getSpace(), result);
      // 向量同步
//    apiVectorQuantityService.makeAsyncDelete(new DeleteVectorDTO(graphVertexDropDO.getSpace(), ids));
      graphVertexService.executeTheTask(
          SpaceConstant.SPACE_NAME_FIX + entityDropAllVO.getSpaceId());
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<List<SaveEntityVO>> saveEntity(List<SaveEntityVO> saveEntityVO) {
    try {
      if (!CollectionUtils.isEmpty(saveEntityVO)) {
        String id = UniqueIDGenerator.generateUniqueID();
        GraphEntityDO graphEntityDO = new GraphEntityDO();
        graphEntityDO.setSpaceId(
            SpaceConstant.SPACE_NAME_FIX + saveEntityVO.get(SpaceConstant.INDEX).getSpaceId());
        graphEntityDO.setVid(id);
        graphEntityDO.setEntityName(saveEntityVO.get(SpaceConstant.INDEX).getEntityName());
        log.info("【Process Kg-webserver-web save  entity : {} for space :  {} 】",
            graphEntityDO.getEntityName(), graphEntityDO.getSpaceId());
        processEntity(graphEntityDO, saveEntityVO);
        // 新增图数据库
        graphVertexService.saveEntity(graphEntityDO);
        //向量数据库存储
        SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
        saveEntityDTO.setSpaceId(
            SpaceConstant.SPACE_NAME_FIX + saveEntityVO.get(SpaceConstant.INDEX).getSpaceId());
        saveEntityDTO.setEntityId(id);
        saveEntityDTO.setEntityName(saveEntityVO.get(SpaceConstant.INDEX).getEntityName());
        List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
        saveEntityVO.forEach(save -> {
          SaveTagInfoDTO tag = new SaveTagInfoDTO();
          tag.setTagName(save.getTagName());
          tag.setEntityProperties(save.getEntityProperties());
          saveTagInfoDTOS.add(tag);
        });
        saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
        // TODO 向量
//        apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
        graphVertexService.executeTheTask(
            SpaceConstant.SPACE_NAME_FIX + saveEntityVO.get(SpaceConstant.INDEX).getSpaceId());
      }
    } catch (ServiceException e) {
      log.error("调用向量库失败", e);
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【新增节点失败！】", e);
      return CommonRespDto.error(ErrorConstants.SAVE_VERTEX.getMessage());
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<EntityDetailsVO> getEntity(SaveEntityVO saveEntityVO) {
    log.info("【Process Kg-webserver-web get entity info for space :  {} tag : {}】",
        saveEntityVO.getSpaceId(), saveEntityVO.getTagName());
    EntityDetailsVO tmp = new EntityDetailsVO();
    try {
      String space = SpaceConstant.SPACE_FIX_NAME + "_" + saveEntityVO.getSpaceId();
      NgVertex<String> ngVertex = graphVertexService.getEntity(saveEntityVO.getEntityId(), space);
      if (null != ngVertex) {
        List<TagInfoDatilVO> tagInfoDatilVOS = new ArrayList<>();
        BeanUtils.copyProperties(saveEntityVO, tmp);
        tmp.setEntityId(ngVertex.getVid());
        tmp.setSpaceId(saveEntityVO.getSpaceId());
        Map<String, Object> properties = ngVertex.getProperties();
        for (Map.Entry<String, Object> property : properties.entrySet()) {
          TagInfoDatilVO tagInfoDatilVO = new TagInfoDatilVO();
          List<EntityPropertiesVO> entityProperties = new ArrayList<>();
          tagInfoDatilVO.setTagName(property.getKey());
          KnowledgeGraphTagEdgePo tagInfo = graphTagEdgeMapper.selectOne(
              Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
                  .eq(KnowledgeGraphTagEdgePo::getSpaceId, saveEntityVO.getSpaceId())
                  .eq(KnowledgeGraphTagEdgePo::getTagName, property.getKey())
                  .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX), false);
          if (null != tagInfo) {
            tagInfoDatilVO.setTagId(Long.valueOf(tagInfo.getTagEdgeId()));
          }
          LinkedHashMap<String, Object> pro = (LinkedHashMap<String, Object>) property.getValue();
          if (StringUtil.isBlank(tmp.getEntityName())) {
            tmp.setEntityName((String) pro.get(SpaceConstant.NAME));
          }
          if (pro != null && pro.get(SpaceConstant.NAME) != null) {
            // 获取属性信息
            List<KnowledgeGraphTagEdgePropertyPo> edgeProperty = graphTagEdgePropertyMapper.selectList(
                Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
                    .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagInfo.getTagEdgeId())
                    .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.INDEX)
                    .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
            if (!CollectionUtils.isEmpty(edgeProperty)) {
              entityProperties = edgeProperty.stream()
                  .filter(tagEdgeProperty -> pro.containsKey(tagEdgeProperty.getPropertyName()))
                  .map(tagEdgeProperty -> {
                    EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
                    BeanUtils.copyProperties(tagEdgeProperty, entityPropertiesVO);
                    entityPropertiesVO.setDefaultValueAsString(tagEdgeProperty.getDefaultValue());
                    entityPropertiesVO.setPropertyName(tagEdgeProperty.getPropertyName());
                    if (entityPropertiesVO.getPropertyType().equals(SpaceConstant.DATETIME)) {
                      entityPropertiesVO.setPropertyValue(
                          pro.get(tagEdgeProperty.getPropertyName()) != null
                              ? DateGraphUtil.dateProcessTime(
                              pro.get(tagEdgeProperty.getPropertyName()).toString()) : null);
                    } else if (entityPropertiesVO.getPropertyType()
                        .equals(SpaceConstant.TIMESTAMP)) {
                      entityPropertiesVO.setPropertyValue(
                          pro.get(tagEdgeProperty.getPropertyName()) != null
                              ? DateGraphUtil.dateProcessTimeStamp(
                              (Long) pro.get(tagEdgeProperty.getPropertyName())) : null);
                    } else if (pro.get(tagEdgeProperty.getPropertyName()) instanceof Time) {
                      entityPropertiesVO.setPropertyValue(
                          pro.get(tagEdgeProperty.getPropertyName()) != null
                              ? DateGraphUtil.TimeZoneConversion(
                              pro.get(tagEdgeProperty.getPropertyName()).toString()) : null);
                    } else {
                      entityPropertiesVO.setPropertyValue(
                          pro.get(tagEdgeProperty.getPropertyName()) != null ? pro.get(
                              tagEdgeProperty.getPropertyName()).toString() : null);
                      String tempName = entityPropertiesVO.getPropertyName();
                      if (SpaceConstant.FILE_PATH.equals(tempName) && !StringUtil.isBlank(
                          entityPropertiesVO.getPropertyValue())) {
                        String newUrl = entityPropertiesVO.getPropertyValue();
                        entityPropertiesVO.setPropertyValue(newUrl);
                      }
                    }
                    return entityPropertiesVO;
                  }).collect(Collectors.toList());
            }
            tagInfoDatilVO.setEntityProperties(entityProperties);
          }
          tagInfoDatilVOS.add(tagInfoDatilVO);
        }
        tmp.setTagInfoDetailVOS(tagInfoDatilVOS);
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    // 查询本体信息和属性
    return CommonRespDto.success(tmp);
  }

  @Override
  public CommonRespDto<Boolean> updateEntity(EntityDetailsVO entityDetailsVO) {
    // TODO  更新节点名称 ，原有的100关系，向量没有更新
    log.info("【Process Kg-webserver-web   update entity : {} for space :  {} 】",
        entityDetailsVO.getEntityName(), entityDetailsVO.getSpaceId());
    try {
      GraphEntityDO graphEntityDO = new GraphEntityDO();
      graphEntityDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + entityDetailsVO.getSpaceId());
      graphEntityDO.setVid(entityDetailsVO.getEntityId());
      graphEntityDO.setEntityName(entityDetailsVO.getEntityName());
      // processEntity
      processUpdateEntity(graphEntityDO, entityDetailsVO);
      // 删除节点
      graphVertexService.deleteVertexSingle(
          SpaceConstant.SPACE_NAME_FIX + entityDetailsVO.getSpaceId(),
          entityDetailsVO.getEntityId());
      // 新增图数据库
      graphVertexService.saveEntity(graphEntityDO);
      //向量数据库存储
      SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
      saveEntityDTO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + entityDetailsVO.getSpaceId());
      saveEntityDTO.setEntityId(graphEntityDO.getVid());
      saveEntityDTO.setEntityName(entityDetailsVO.getEntityName());
      List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
      entityDetailsVO.getTagInfoDetailVOS().stream().forEach(save -> {
        SaveTagInfoDTO tag = new SaveTagInfoDTO();
        tag.setTagName(save.getTagName());
        tag.setEntityProperties(save.getEntityProperties());
        saveTagInfoDTOS.add(tag);
      });
      saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
      // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<List<EntityLikeInfoVO>> selectLikeEntity(EntityLikeVO entityLikeVO) {
    log.info("【Process Kg-webserver-web get entity like  info for space :  {} tag : {}】",
        entityLikeVO.getSpaceId());
    List<EntityLikeInfoVO> entityLikeInfoVOS = new ArrayList<>();
    try {
      ResultSet resultSet = graphVertexService.selectLikeEntity(
          SpaceConstant.SPACE_FIX_NAME + "_" + entityLikeVO.getSpaceId(), entityLikeVO.getTagName(),
          entityLikeVO.getEntityName()
              .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
      // 确保所需的列都存在
      List<String> columnNames = resultSet.getColumnNames();
      if (columnNames.contains(SpaceConstant.NAME) && columnNames.contains(SpaceConstant.ID)) {
        // 获取各列的值
        List<ValueWrapper> nameValues = resultSet.colValues(SpaceConstant.NAME);
        List<ValueWrapper> ids = resultSet.colValues(SpaceConstant.ID);
        // 假设所有列表的长度相同（这是处理这种情况的关键假设）
        for (int i = SpaceConstant.INDEX; i < nameValues.size(); i++) {
          // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
          String name = nameValues.get(i).asString();
          String id = ids.get(i).asString();
          // 创建一个新的 DataEntry 对象并添加到列表中
          EntityLikeInfoVO entityLikeInfoVO = new EntityLikeInfoVO(name, id);
          entityLikeInfoVOS.add(entityLikeInfoVO);
        }
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(entityLikeInfoVOS);
  }

  @Override
  public String processData(String propertyType, String propertyValue) {
    StringBuilder param = new StringBuilder();
    if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING) || propertyType.equalsIgnoreCase(
        SpaceConstant.STRING)) {
      param.append(JSON.toJSONString(propertyValue.toString())).append(SpaceConstant.TAG_SPACE);
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
//            !PropertyValidator.validatePropertyType(withoutBrackets, value)
    } else if (TYPEINTLIST.contains(propertyType)) {
      boolean result = false;
      try {
        result = PropertyValidator.validatePropertyType(propertyType, propertyValue);
      } catch (Exception e) {
        log.error("【 update knowledge verification information extraction  error】");
        throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
      }
      if (!result) {
        log.error("【The attribute value is out of range :{}】", propertyValue);
        throw new ServiceException(500,
            SpaceConstant.RANGE + propertyValue + SpaceConstant.RANGE_INDEX);
      } else {
        param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
      }
    } else {
      param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
    }
    return param.toString().trim();
  }

  @Override
  public CommonRespDto<Boolean> checkDataUp(Long spaceId) {
    boolean result = checkDataLimit(spaceId);
    return CommonRespDto.success(result);
  }

  @Override
  public boolean checkDataLimit(Long spaceId) {
    return true;
  }

  @Override
  public Integer getTotalDataUpBySpace(Long spaceId) throws UnsupportedEncodingException {
    log.info("【开始获取总的数据上限：{}】", spaceId);
    long sum = 0L;

//    KnowledgeBasePo mapSpace = knowledgeBaseMapper.selectById(spaceId);
//    if (null != mapSpace) {
//      // 获取信息
//      ResultSet set = graphVertexService.getStatsInfo(SpaceConstant.SPACE_NAME_FIX + spaceId);
//      if (null != set) {
//        List<String> columnNames = set.getColumnNames();
//        if (columnNames.contains(SpaceConstant.TYPE) && columnNames.contains(SpaceConstant.TAG_NAME)
//            && columnNames.contains(SpaceConstant.COUNT)) {
//          // 获取各列的值
//          List<ValueWrapper> types = set.colValues(SpaceConstant.TYPE);
//          List<ValueWrapper> names = set.colValues(SpaceConstant.TAG_NAME);
//          List<ValueWrapper> count = set.colValues(SpaceConstant.COUNT);
//          if (types.size() == names.size() && names.size() == types.size()
//              && names.size() == count.size()) {
//            for (int i = SpaceConstant.INDEX; i < types.size(); i++) {
//              if ((types.get(i).asString().equals("Space") && names.get(i).asString()
//                  .equals("vertices")) || (types.get(i).asString().equals("Space") && names.get(i)
//                  .asString().equals("edges"))) {
//                sum += count.get(i).asLong();
//              }
//            }
//          }
//        }
//      }
//    }
    return (int) sum;
  }

  @Override
  public Integer getResourceLimitData(Long spaceId) {
    return 999999999;
  }

  @Override
  public boolean checkDataLimitData(Long spaceId, int i) {
    return true;
  }


  private String generateCacheKey(String spaceId, String tagName) {
    return SpaceConstant.TAGCACHE + spaceId + SpaceConstant.UNDERLINE + tagName;
  }

  private void processEntity(GraphEntityDO graphEntityDO, List<SaveEntityVO> saveEntityVO) {
    StringJoiner tag = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    // 设置名称
    saveEntityVO.forEach(s -> {
      // 获取属性是否设置过过期属性
      log.info("【Web Api get  Tag / Edge  TTl: {}  Ttl field】 ", s.getTagId());

      KnowledgeGraphTagEdgePo mapTagEdge = graphTagEdgeMapper.selectById(s.getTagId());
      if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
          && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX && s.getEntityProperties().stream()
          .map(EntityPropertiesVO::getPropertyName).toList().contains(mapTagEdge.getTtlCol())) {
        Optional<String> filteredValue = s.getEntityProperties().stream()
            .filter(vo -> mapTagEdge.getTtlCol().equals(vo.getPropertyName()))
            .map(EntityPropertiesVO::getPropertyValue).findFirst();
        String result = filteredValue.orElse(null);
        if (null != result && !isNotExpired(result, mapTagEdge.getTtlDuration())) {
          // 已经过期
          log.error("【属性值已经过期，新增失败 :{}】", mapTagEdge.getTtlCol());
          throw ServiceExceptionUtil.exception(ErrorConstants.PERPROTY_EXPIRED);
        }
      }
      StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
      value.add(JSON.toJSONString(s.getEntityName()));
      String tagName = SpaceConstant.QUOTATIONMARK + s.getTagName() + SpaceConstant.QUOTATIONMARK
          + SpaceConstant.FIX_TAG_NAME_FUSION + SpaceConstant.NAME;
      s.getEntityProperties().stream().forEach(property -> {
        key.add(
            SpaceConstant.QUOTATIONMARK + property.getPropertyName() + SpaceConstant.QUOTATIONMARK);
        if (!StringUtil.isBlank(property.getPropertyValue())) {
          String buildString = processData(property.getPropertyType(), property.getPropertyValue());
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

  private boolean isNotExpired(String result, int ttlDuration) {
    LocalDateTime now = LocalDateTime.now();

    // 解析时间字符串
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime resultDateTime = LocalDateTime.parse(result, formatter);

    // 计算 resultDateTime 加上 TTL 持续时间后的时间
    LocalDateTime expiryDateTime = resultDateTime.plus(ttlDuration, ChronoUnit.HOURS);

    // 判断是否不过期
    return resultDateTime.isAfter(now) || expiryDateTime.isAfter(now);

  }

  /**
   * 编辑实体信息处理实体
   *
   * @param graphEntityDO
   * @param entityDetailsVO
   */
  private void processUpdateEntity(GraphEntityDO graphEntityDO, EntityDetailsVO entityDetailsVO) {
    StringJoiner tag = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    if (!graphEntityDO.getEntityName().matches(SpaceConstant.PATTERN_STRING)) {
      log.error("【entity name not matches :{}】", graphEntityDO.getEntityName());
      throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_NAME);
    }
    // 设置名称
    entityDetailsVO.getTagInfoDetailVOS().forEach(s -> {
      // 获取属性是否设置过过期属性
      log.info("【Web Api get  Tag / Edge  TTl: {}  Ttl field】 ", s.getTagId());
      KnowledgeGraphTagEdgePo mapTagEdge = graphTagEdgeMapper.selectById(s.getTagId());
      if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
          && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX && s.getEntityProperties().stream()
          .map(EntityPropertiesVO::getPropertyName).toList().contains(mapTagEdge.getTtlCol())) {
        Optional<String> filteredValue = s.getEntityProperties().stream()
            .filter(vo -> mapTagEdge.getTtlCol().equals(vo.getPropertyName()))
            .map(EntityPropertiesVO::getPropertyValue).findFirst();
        String result = filteredValue.orElse(null);
        if (null != result && !isNotExpired(result, mapTagEdge.getTtlDuration())) {
          // 已经过期
          log.error("【属性值已经过期，更新失败 :{}】", mapTagEdge.getTtlCol());
          throw ServiceExceptionUtil.exception(ErrorConstants.PERPROTY_EXPIRED_UPDATE);
        }
      }
      StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
      value.add(JSON.toJSONString(entityDetailsVO.getEntityName()));
      String tagName = SpaceConstant.QUOTATIONMARK + s.getTagName() + SpaceConstant.QUOTATIONMARK
          + SpaceConstant.FIX_TAG_NAME_FUSION + SpaceConstant.NAME;
      s.getEntityProperties().stream().forEach(property -> {
        key.add(
            SpaceConstant.QUOTATIONMARK + property.getPropertyName() + SpaceConstant.QUOTATIONMARK);
        if (!StringUtil.isBlank(property.getPropertyValue())) {
          String buildString = processData(property.getPropertyType(), property.getPropertyValue());
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
}
