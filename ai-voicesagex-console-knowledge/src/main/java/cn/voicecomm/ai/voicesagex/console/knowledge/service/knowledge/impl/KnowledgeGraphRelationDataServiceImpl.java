package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphEntityManageService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphRelationDataService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEdgeDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.LimitEdgeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Ralation;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveEntityDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveRelationDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveTagInfoDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeInfosVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityTagVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.RelationDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveRelationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ScreenTagVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SubjectInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalEdgeVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgePropertyMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateGraphUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.MD5Util;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.PropertyValidator;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.UniqueIDGenerator;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.Entity;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
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
public class KnowledgeGraphRelationDataServiceImpl implements KnowledgeGraphRelationDataService {

  private final KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper;

  private final KnowledgeGraphTagEdgePropertyMapper knowledgeGraphTagEdgePropertyMapper;

  private final KnowledgeGraphEntityManageService knowledgeGraphEntityManageService;

  private final GraphEdgeService graphEdgeService;

  private final GraphVertexService graphVertexService;

  private final GraphEdgeMapper graphEdgeMapper;

//  private final ApiVectorQuantityService apiVectorQuantityService;

  private static final List<String> TYPEINTLIST = Arrays.asList("INT8", "INT16", "INT32", "INT64");


  @Override
  public CommonRespDto<TotalEdgeVO> getTagInfoList(String spaceId, String edgeName) {
    String cacheKey = generateCacheKey(spaceId, edgeName);
    // 从缓存中获取边信息
    TotalEdgeVO totalEdgeVO = edgeCache.get(cacheKey);
    // 如果缓存存在且数量已更新，则返回缓存数据并清空缓存
    if (totalEdgeVO != null && totalEdgeVO.isIdentity()) {
      // 清空缓存
      edgeCache.remove(cacheKey);
      return CommonRespDto.success(totalEdgeVO);
    }
    // 获取标签
    List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .apply(CharSequenceUtil.isNotBlank(edgeName), "tag_name ILIKE {0}",
                "%" + SpecialCharUtil.transfer(edgeName) + "%")
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, Integer.valueOf(spaceId))
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.ENGDE)
            .orderByDesc(BasePo::getCreateTime).orderByDesc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    List<EdgeInfosVO> edgeInfosVOS = new ArrayList<>();
    knowledgeGraphTagEdgePoList.forEach(tag -> {
      EdgeInfosVO edgeInfosVO = new EdgeInfosVO();
      BeanUtils.copyProperties(tag, edgeInfosVO);
      edgeInfosVO.setEdgeId(Long.valueOf(tag.getTagEdgeId()));
      edgeInfosVO.setEdgeName(tag.getTagName());
      // 将初始数量设为 0
      edgeInfosVO.setTagNumber(0);
      edgeInfosVOS.add(edgeInfosVO);
    });
    // 创建 TotalEdgeVO 对象并设置边信息列表
    totalEdgeVO = new TotalEdgeVO();
    totalEdgeVO.setEdgeInfosVOList(edgeInfosVOS);
    // 初始设置为 false
    totalEdgeVO.setIdentity(false);
    // 将初始数据存储到缓存中
    edgeCache.put(cacheKey, totalEdgeVO);
    // 异步获取每个边的数量
    TotalEdgeVO finalTotalEdgeVO = totalEdgeVO;
    Locale locale = LocaleContextHolder.getLocale();
    CompletableFuture.runAsync(() -> {
      try {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        LocaleContextHolder.setLocale(locale);
        for (EdgeInfosVO edgeInfosVO : edgeInfosVOS) {
          String tag = edgeInfosVO.getEdgeName();
          Long number = graphEdgeService.getNumber(tag, SpaceConstant.SPACE_NAME_FIX + spaceId);
          edgeInfosVO.setTagNumber(number.intValue());
        }
        // 更新缓存中的数据
        finalTotalEdgeVO.setIdentity(true);
        // 清空缓存，以确保下次请求能够重新获取最新数据
        edgeCache.remove(cacheKey);
        // 将更新后的数据存储到缓存中
        edgeCache.put(cacheKey, finalTotalEdgeVO);
        log.info("【所有边数量异步获取完成】");
      } catch (Exception e) {
        log.error("【获取边数量失败】", e);
      }
    });
    // 返回初始数据
    return CommonRespDto.success(totalEdgeVO);
  }

  @Override
  public CommonRespDto<List<EdgePropertyResultVO>> getEdgeProperties(Long edgeId) {
    log.info("【Mysql get All  Tag / Edge Properties info from  mysql for ids:{}】", edgeId);
    List<EdgePropertyResultVO> tagPropertyResultVOS = new ArrayList<>();
    // 执行查询
    List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = knowledgeGraphTagEdgePropertyMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, edgeId)
            .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.ENGDE)
            .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
    tagEdgeProperties.forEach(tagEdgeProperty -> {
      EdgePropertyResultVO resultVO = new EdgePropertyResultVO();
      BeanUtils.copyProperties(tagEdgeProperty, resultVO);
      resultVO.setEdgeId(Long.valueOf(tagEdgeProperty.getTagEdgeId()));
      resultVO.setDefaultValueAsString(tagEdgeProperty.getDefaultValue());
      KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
          tagEdgeProperty.getTagEdgeId());
      resultVO.setEdgeName(mapTagEdge.getTagName());
      resultVO.setSpaceId(Long.valueOf(mapTagEdge.getSpaceId()));
      tagPropertyResultVOS.add(resultVO);
    });
    return CommonRespDto.success(tagPropertyResultVOS);
  }

  @Override
  public CommonRespDto<PagingRespDto<EdgeListVO>> getEntities(EdgePropertyVO edgePropertyVO) {
    log.info("【Process Kg-webserver-web get all relation list for space :  {} tag : {}】",
        edgePropertyVO.getSpaceId(), edgePropertyVO.getEdgeId());

    PagingRespDto<EdgeListVO> page = new PagingRespDto<>();
    page.setCurrent(edgePropertyVO.getCurrent());
    page.setSize(edgePropertyVO.getPageSize());
    if (SpaceConstant.INDEX == edgePropertyVO.getCurrent()) {
      page.setCurrent(SpaceConstant.INDEX);
    }
    try {
      List<EdgeListVO> edgeListVOS = new ArrayList<>();
      LimitEdgeDO limitEdgeDO = new LimitEdgeDO();
      Map<String, EntityTagVO> entityMap = new HashMap<>();
      Set<String> entityList = new HashSet<>();
      limitEdgeDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + edgePropertyVO.getSpaceId());
      BeanUtils.copyProperties(edgePropertyVO, limitEdgeDO);
      List<NgEdge<String>> vertexes = graphEdgeService.getVertexesLimit(limitEdgeDO);
      vertexes.forEach(v -> {
        EdgeListVO edgeListVO = new EdgeListVO();
        edgeListVO.setEdgeId(edgePropertyVO.getEdgeId());
        edgeListVO.setSpaceId(edgePropertyVO.getSpaceId());
        edgeListVO.setSubjectId(v.getSrcID());
        edgeListVO.setRank(v.getRank());
        edgeListVO.setObjectId(v.getDstID());
        edgeListVO.setEdgeName(v.getEdgeName());
        // 查询本体id
        entityList.add(String.valueOf(edgeListVO.getSubjectId()));
        entityList.add(String.valueOf(edgeListVO.getObjectId()));
        edgeListVOS.add(edgeListVO);
      });

      if (!CollectionUtils.isEmpty(vertexes)) {
        // 查询数据
        Map<String, Integer> tagEdgeMap = knowledgeGraphTagEdgeMapper.selectList(
            Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
                .eq(KnowledgeGraphTagEdgePo::getSpaceId, edgePropertyVO.getSpaceId())
                .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX)
                .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId)).stream().collect(
            Collectors.toMap(KnowledgeGraphTagEdgePo::getTagName,
                KnowledgeGraphTagEdgePo::getTagEdgeId, (key1, key2) -> key1));
        // 解析查询数据
        processEntityData(entityList, limitEdgeDO.getSpaceId(), entityMap);
        // 设置 entityName 和tagName
        edgeListVOS.forEach(v -> {
          EntityTagVO subjuctEntity = entityMap.get(v.getSubjectId());
          List<Long> tagId = new ArrayList<>();
          List<Long> objectTagId = new ArrayList<>();
          String subjectTagNameTmp = subjuctEntity.getTagName();
          if (null != subjuctEntity) {
            v.setSubjectTagName(subjuctEntity.getTagName());
            v.setSubjectName(subjuctEntity.getEntityName());
            if (!StringUtil.isBlank(v.getSubjectTagName())) {
              for (String s : subjectTagNameTmp.split(SpaceConstant.TAG_SPLIT_INDEX)) {
                tagId.add(Long.valueOf(tagEdgeMap.getOrDefault(s, 0)));
              }
              v.setSubjectTagId(tagId);
            }

          }
          EntityTagVO objectEntity = entityMap.get(v.getObjectId());
          if (null != objectEntity) {
            String objectTagTmp = objectEntity.getTagName();
            v.setObjectTagName(objectEntity.getTagName());
            v.setObjectName(objectEntity.getEntityName());
            if (!StringUtil.isBlank(v.getObjectTagName())) {
              for (String s : objectTagTmp.split(SpaceConstant.TAG_SPLIT_INDEX)) {
                objectTagId.add(Long.valueOf(tagEdgeMap.getOrDefault(s, 0)));
              }
              v.setObjectTagId(objectTagId);
            }
          }
        });
        page.setRecords(edgeListVOS);
        //获取所有点
        if (StringUtil.isBlank(edgePropertyVO.getEdgeName())) {
          page.setTotal((graphEdgeService.getSumEdge(limitEdgeDO)));
        } else {
          page.setTotal(graphEdgeService.getSumEdgeByEdge(limitEdgeDO));
        }
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(page);
  }

  @Override
  public CommonRespDto<Boolean> deleteRelateions(EdgeDeleteVO edgeDeleteVO) {
    log.info("【Process Kg-webserver-web delete all Relateion list for space :  {} vid : {}】",
        edgeDeleteVO.getSpaceId(), edgeDeleteVO.getRalationVOS());
    // 删除图数据库
    try {
      GraphEdgeDropDO graphEdgeDropDO = new GraphEdgeDropDO();
      BeanUtils.copyProperties(edgeDeleteVO, graphEdgeDropDO);
      graphEdgeDropDO.setRalations(edgeDeleteVO.getRalationVOS());
      graphEdgeDropDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + edgeDeleteVO.getSpaceId());
      graphEdgeService.deleteRelation(graphEdgeDropDO);
      List<String> vids = new ArrayList<>();
      edgeDeleteVO.getRalationVOS().forEach(
          v -> vids.add(MD5Util.generateMD5(v.getSourceId() + v.getEdgeName() + v.getObjectId())));
      // TODO 向量同步
//    apiVectorQuantityService.makeAsyncDeleteRelationEntity(new DeleteVectorDTO(graphEdgeDropDO.getSpaceId(), vids));
      edgeCache.clear(); // 清空缓存
      graphVertexService.executeTheTask(SpaceConstant.SPACE_NAME_FIX + edgeDeleteVO.getSpaceId());
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> deleteAllRelations(RelationDropAllVO relationDropAllVO) {
    log.info("【Process Kg-webserver-web delete all relation list for space :  {} tag : {}】",
        relationDropAllVO.getSpaceId());
    // 使用QueryWrapper构造查询条件，并指定排序规则
    List<Ralation> ralations = new ArrayList<>();
    // 从图数据库获取所有
    GraphEdgeDropDO graphEdgeDropDO = new GraphEdgeDropDO();
    graphEdgeDropDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + relationDropAllVO.getSpaceId());
    graphEdgeDropDO.setEdgeName(relationDropAllVO.getEdgeName());
    graphEdgeDropDO.setSubjectName(relationDropAllVO.getSubjectName());
    graphEdgeDropDO.setSubjectTagName(relationDropAllVO.getSubjectTagName());
    graphEdgeDropDO.setObjectTagName(relationDropAllVO.getObjectTagName());
    try {
      List<String> vids = new ArrayList<>();
      if (!StringUtil.isBlank(graphEdgeDropDO.getEdgeName())) {
        // 从图数据库获取本体下所有实体
        List<NgEdge<String>> ngEdges = graphEdgeService.getAllEdge(graphEdgeDropDO);
        ngEdges.stream().forEach(ngEdge -> {
          Ralation ralation = new Ralation();
          ralation.setSourceId(ngEdge.getSrcID());
          ralation.setRank(ngEdge.getRank());
          ralation.setObjectId(ngEdge.getDstID());
          ralation.setEdgeName(graphEdgeDropDO.getEdgeName());
          ralations.add(ralation);
          vids.add(MD5Util.generateMD5(
              ralation.getSourceId() + ralation.getEdgeName() + ralation.getObjectId()));
        });
        // 删除图数据库
        graphEdgeDropDO.setRalations(ralations);
        graphEdgeService.deleteRelation(graphEdgeDropDO);
        // TODO 向量
//      apiVectorQuantityService.makeAsyncDeleteRelationEntity(new DeleteVectorDTO(graphEdgeDropDO.getSpaceId(), vids));
      } else {
        // 获取所有tag
        List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
            Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
                .eq(KnowledgeGraphTagEdgePo::getSpaceId, relationDropAllVO.getSpaceId())
                .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.ENGDE)
                .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
        if (!CollectionUtils.isEmpty(mapTagEdges)) {
          mapTagEdges.forEach(mapTagEdge -> {
            graphEdgeDropDO.setEdgeName(mapTagEdge.getTagName());
            List<NgEdge<String>> tmp = graphEdgeService.getAllEdge(graphEdgeDropDO);
            tmp.forEach(ngEdge -> {
              Ralation ralation = new Ralation();
              ralation.setSourceId(ngEdge.getSrcID());
              ralation.setObjectId(ngEdge.getDstID());
              ralation.setEdgeName(mapTagEdge.getTagName());
              ralations.add(ralation);
              vids.add(MD5Util.generateMD5(
                  ralation.getSourceId() + ralation.getEdgeName() + ralation.getObjectId()));
            });
            // 删除图数据库
            graphEdgeDropDO.setRalations(ralations);
            graphEdgeService.deleteRelation(graphEdgeDropDO);
            //TODO 向量同步
//          apiVectorQuantityService.makeAsyncDeleteRelationEntity(new DeleteVectorDTO(graphEdgeDropDO.getSpaceId(), vids));
          });
        }
      }
      edgeCache.clear(); // 清空缓存
      graphVertexService.executeTheTask(
          SpaceConstant.SPACE_NAME_FIX + relationDropAllVO.getSpaceId());
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<List<SubjectInfoVO>> getEntity(String spaceId) {
    // 使用QueryWrapper构造查询条件，并指定排序规则
    List<SubjectInfoVO> subjectInfoVOS = new ArrayList<>();
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, Integer.valueOf(spaceId))
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.INDEX)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    mapTagEdges.forEach(tag -> {
      SubjectInfoVO subjectInfoVO = new SubjectInfoVO();
      BeanUtils.copyProperties(tag, subjectInfoVO);
      subjectInfoVO.setTagId(Long.valueOf(tag.getTagEdgeId()));
      subjectInfoVOS.add(subjectInfoVO);
    });
    return CommonRespDto.success(subjectInfoVOS);
  }

  @Override
  public CommonRespDto<List<SaveRelationVO>> saveRelation(List<SaveRelationVO> saveRelationVO) {
    if (!CollectionUtils.isEmpty(saveRelationVO)) {
      log.info("【Process Kg-webserver-web save all relation list for space :  {}】",
          saveRelationVO.getFirst().getSpaceId());
      GraphRelationDO graphRelationDO = new GraphRelationDO();
      BeanUtils.copyProperties(saveRelationVO, graphRelationDO);
      graphRelationDO.setSpaceId(
          SpaceConstant.SPACE_FIX_NAME + "_" + saveRelationVO.getFirst().getSpaceId());
      try {
        // 判断实体是否已经创建过
        // 通过图数据库进行判断
        for (SaveRelationVO save : saveRelationVO) {
          if (save.getSubjectTagName().equals(save.getObjectTagName()) && save.getSubjectName()
              .equals(save.getObjectName())) {
            log.error("【entity name not matches :{}】", save.getSubjectName());
            return CommonRespDto.error(ErrorConstants.ENTITY_SAVE_NAME.getMessage());
          }
          KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
              save.getEdgeId());
          if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
              && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX && save.getEntityProperties()
              .stream().map(EntityPropertiesVO::getPropertyName).toList()
              .contains(mapTagEdge.getTtlCol())) {
            Optional<String> filteredValue = save.getEntityProperties().stream()
                .filter(vo -> mapTagEdge.getTtlCol().equals(vo.getPropertyName()))
                .map(EntityPropertiesVO::getPropertyValue).findFirst();
            String result = filteredValue.orElse(null);
            if (null != result && !isNotExpired(result, mapTagEdge.getTtlDuration())) {
              // 已经过期
              log.error("【属性值已经过期，新增失败 :{}】", mapTagEdge.getTtlCol());
              return CommonRespDto.error(ErrorConstants.PERPROTY_EXPIRED.getMessage());
            }
          }
          if (StringUtil.isBlank(save.getSubjectId())) {
            List<Entity> subjects = getSubjectInfo(save);
            checkSubject(save, graphRelationDO, subjects);
          } else {
            graphRelationDO.setSubjectId(save.getSubjectId());
          }

          if (StringUtil.isBlank(save.getObjectId())) {
            List<Entity> objects = getObjectInfo(save);
            // 检查主体 和客体是否已经创建
            checkObject(save, graphRelationDO, objects);
          } else {
            graphRelationDO.setObjectId(save.getObjectId());
          }
          graphRelationDO.setEdgeName(save.getEdgeName());
          graphRelationDO.setRank(SpaceConstant.RANK + save.getRank());
          // 解析关系数据
          processSaveRelation(graphRelationDO, save);
          // 校验数据是否达到上限
          boolean check = knowledgeGraphEntityManageService.checkDataLimit(save.getSpaceId());
          if (!check) {
            log.error("【空间数据已达上限，新增失败！】");
            return CommonRespDto.error(ErrorConstants.RESOURCE_DATA_LIMIT_ERROR.getMessage());
          }
          // 新增图数据库
          graphEdgeService.saveRelation(graphRelationDO, SpaceConstant.INDEX);
          graphVertexService.executeTheTask(SpaceConstant.SPACE_NAME_FIX + save.getSpaceId());
          // TODO 向量新增
          SaveRelationDTO saveRelationDTO = new SaveRelationDTO();
          saveRelationDTO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + save.getSpaceId());
          saveRelationDTO.setSubjectId(graphRelationDO.getSubjectId());
          saveRelationDTO.setRank(
              graphRelationDO.getRank().split(SpaceConstant.RANK)[SpaceConstant.REPLICA_FACTOR]);
          saveRelationDTO.setSubjectName(save.getSubjectName());
          saveRelationDTO.setObjectId(graphRelationDO.getObjectId());
          saveRelationDTO.setObjectName(save.getObjectName());
          saveRelationDTO.setEdgeName(save.getEdgeName());
          if (!CollectionUtils.isEmpty(save.getEntityProperties())) {
            saveRelationDTO.setEntityProperties(save.getEntityProperties());
          }
//        apiVectorQuantityService.makeAsyncCreateRelation(saveRelationDTO);
        }
      } catch (Exception e) {
        log.error("调用图库异常", e);
        return CommonRespDto.error(e.getMessage());
      }
    }
    return CommonRespDto.success(saveRelationVO);
  }

  @Override
  public CommonRespDto<List<EdgeInfoVO>> getEdges(String spaceId) {
    List<EdgeInfoVO> edgeInfosVOS = new ArrayList<>();
    List<KnowledgeGraphTagEdgePo> tagEdgeProperties = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, Integer.valueOf(spaceId))
            .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.ENGDE)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    tagEdgeProperties.stream().forEach(mapTagEdge -> {
      EdgeInfoVO edgeInfosVO = new EdgeInfoVO();
      edgeInfosVO.setEdgeName(mapTagEdge.getTagName());
      edgeInfosVO.setEdgeId(Long.valueOf(mapTagEdge.getTagEdgeId()));
      edgeInfosVOS.add(edgeInfosVO);
    });
    return CommonRespDto.success(edgeInfosVOS);
  }

  @Override
  public CommonRespDto<SaveRelationVO> getRelation(SaveRelationVO saveRelationVO) {
    log.info("【Process Kg-webserver-web get  relation  list for space :  {}】",
        saveRelationVO.getSpaceId());
    String edgeName = saveRelationVO.getEdgeName();
    String space = SpaceConstant.SPACE_FIX_NAME + "_" + saveRelationVO.getSpaceId();
    SaveRelationVO tmp = new SaveRelationVO();
    List<EntityPropertiesVO> entityProperties = new ArrayList<>();
    BeanUtils.copyProperties(saveRelationVO, tmp);
    tmp.setSpaceId(saveRelationVO.getSpaceId());
    GraphRelationDO graphRelationDO = new GraphRelationDO();
    BeanUtils.copyProperties(saveRelationVO, graphRelationDO);
    graphRelationDO.setSubjectId(saveRelationVO.getSubjectId());
    graphRelationDO.setObjectId(saveRelationVO.getObjectId());
    graphRelationDO.setRank(SpaceConstant.RANK + saveRelationVO.getRank());
    graphRelationDO.setSpaceId(space);
    Map<String, KnowledgeGraphTagEdgePropertyPo> propertyType = new HashMap<>();
    try {
      // 从数据库拿取边的id
      List<KnowledgeGraphTagEdgePo> tagInfos = knowledgeGraphTagEdgeMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePo::getSpaceId, saveRelationVO.getSpaceId())
              .eq(KnowledgeGraphTagEdgePo::getType, SpaceConstant.ENGDE)
              .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
      NgEdge<String> ngEdge = graphEdgeMapper.getEdgeInfo(graphRelationDO);
      if (null != ngEdge && CollUtil.isNotEmpty(tagInfos)) {
        tmp.setEdgeName(edgeName);
        LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>) ngEdge.getProperties();
        Integer tagEdgeId = tagInfos.stream()
            .filter(m -> m.getTagName().equals(saveRelationVO.getEdgeName()))
            .collect(Collectors.toList()).getFirst().getTagEdgeId();
        List<KnowledgeGraphTagEdgePropertyPo> edgeProperty = knowledgeGraphTagEdgePropertyMapper.selectList(
            Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
                .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgeId)
                .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.REPLICA_FACTOR)
                .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
        edgeProperty.forEach(edge -> propertyType.put(edge.getPropertyName(), edge));
        for (Map.Entry<String, Object> entry : tagMap.entrySet()) {
          if (!SpaceConstant.NAME.equals(entry.getKey())) {
            EntityPropertiesVO edgePropertyVO = new EntityPropertiesVO();
            edgePropertyVO.setPropertyName(entry.getKey());
            KnowledgeGraphTagEdgePropertyPo property = propertyType.get(
                edgePropertyVO.getPropertyName());
            if (null != property) {
              BeanUtils.copyProperties(property, edgePropertyVO);
              if (property.getPropertyType().equals(SpaceConstant.DATETIME)) {
                edgePropertyVO.setPropertyValue(
                    entry.getValue() != null ? DateGraphUtil.dateProcessTime(
                        entry.getValue().toString()) : "");
              } else if (property.getPropertyType().equals(SpaceConstant.TIMESTAMP)) {
                // 获取边属性，拿取边的类型
                edgePropertyVO.setPropertyValue(
                    entry.getValue() != null ? DateGraphUtil.dateProcessTimeStamp(
                        Long.valueOf(entry.getValue().toString())) : "");

              } else if (entry.getValue() instanceof Time) {
                edgePropertyVO.setPropertyValue(
                    entry.getValue() != null ? DateGraphUtil.TimeZoneConversion(
                        entry.getValue().toString()) : null);

              } else {
                edgePropertyVO.setPropertyValue(
                    entry.getValue() != null ? entry.getValue().toString() : "");
              }
              entityProperties.add(edgePropertyVO);
            }
          }
        }
      }
      tmp.setEntityProperties(entityProperties);
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(tmp);
  }

  @Override
  public CommonRespDto<Boolean> updateRelation(SaveRelationVO saveRelationVO) {
    log.info("【Process Kg-webserver-web   update relation : {} for space :  {} 】",
        saveRelationVO.getObjectTagId(), saveRelationVO.getSpaceId());
    try {
      GraphRelationDO graphRelationDO = new GraphRelationDO();
      BeanUtils.copyProperties(saveRelationVO, graphRelationDO);
      graphRelationDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + saveRelationVO.getSpaceId());
      graphRelationDO.setEdgeName(saveRelationVO.getEdgeName());
      graphRelationDO.setRank(SpaceConstant.RANK + saveRelationVO.getRank());
      KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
          saveRelationVO.getEdgeId());
      if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
          && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX
          && saveRelationVO.getEntityProperties().stream().map(EntityPropertiesVO::getPropertyName)
          .toList().contains(mapTagEdge.getTtlCol())) {
        Optional<String> filteredValue = saveRelationVO.getEntityProperties().stream()
            .filter(vo -> mapTagEdge.getTtlCol().equals(vo.getPropertyName()))
            .map(EntityPropertiesVO::getPropertyValue).findFirst();
        String result = filteredValue.orElse(null);
        if (null != result && !isNotExpired(result, mapTagEdge.getTtlDuration())) {
          // 已经过期
          log.error("【属性值已经过期，更新失败 :{}】", mapTagEdge.getTtlCol());
          throw ServiceExceptionUtil.exception(ErrorConstants.PERPROTY_EXPIRED_UPDATE);
        }
      }
      processSaveRelation(graphRelationDO, saveRelationVO);
      graphEdgeService.saveRelation(graphRelationDO, SpaceConstant.REPLICA_FACTOR);
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Set<String>> screenTag(ScreenTagVO screenTagVO) {
    log.info("【获取指定关系：{} 下 本体列表】", screenTagVO.getEdgeName());
    Set<String> tags = new HashSet<>();
    ResultSet resultSet = null;
    try {
      if (screenTagVO.getIsSubject()) {
        resultSet = graphEdgeMapper.screenTag(
            SpaceConstant.SPACE_NAME_FIX + screenTagVO.getSpaceId(), screenTagVO.getEdgeName());
      } else {
        resultSet = graphEdgeMapper.screenTagObject(
            SpaceConstant.SPACE_NAME_FIX + screenTagVO.getSpaceId(), screenTagVO.getEdgeName());
      }
      // 确保所需的列都存在
      if (null != resultSet) {
        List<String> columnNames = resultSet.getColumnNames();
        if (columnNames.contains(SpaceConstant.T)) {
          // 获取各列的值
          List<ValueWrapper> typeValues = resultSet.colValues(SpaceConstant.T);

          // 假设所有列表的长度相同（这是处理这种情况的关键假设）
          if (!typeValues.isEmpty()) {
            for (int i = SpaceConstant.INDEX; i < typeValues.size(); i++) {
              // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
              List<ValueWrapper> result = typeValues.get(i).asList();
              for (ValueWrapper valueWrapper : result) {
                tags.add(valueWrapper.asString());
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("【获取指定关系：{} 下 本体列表失败】", screenTagVO.getEdgeName(), e);
      return CommonRespDto.error(ErrorConstants.SCREEN_TAG_ERROR.getMessage());
    }
    return CommonRespDto.success(tags);
  }

  private String generateCacheKey(String spaceId, String edgeName) {
    return SpaceConstant.EDGECACHE + spaceId + SpaceConstant.UNDERLINE + edgeName;
  }

  /**
   * 解析实体数据
   *
   * @param entityList
   * @param spaceId
   * @param entityMap
   */
  private void processEntityData(Set<String> entityList, String spaceId,
      Map<String, EntityTagVO> entityMap) {
    List<NgVertex<String>> ngVertex = graphVertexService.getEntitySet(entityList, spaceId);
    if (!CollectionUtils.isEmpty(ngVertex)) {
      for (NgVertex<String> vertex : ngVertex) {
        EntityTagVO entityTagVO = new EntityTagVO();
        entityTagVO.setTagName(
            vertex.getTags().stream().collect(Collectors.joining(SpaceConstant.TAG_SPLIT_INDEX)));
        Map<String, Object> properties = vertex.getProperties();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
          LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
          entityTagVO.setEntityName(entryValue.get(SpaceConstant.NAME));
        }
        entityMap.put(vertex.getVid(), entityTagVO);
      }
    }
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

  private List<Entity> getSubjectInfo(SaveRelationVO saveRelationVO) {
    log.info("【Process Kg-webserver-web check have entity :  {} 】", saveRelationVO.getEdgeId(),
        saveRelationVO.getSpaceId());
    List<Entity> entities = new ArrayList<>();
    GraphEntityRelationDO graphEntityRelationDO = new GraphEntityRelationDO();
    graphEntityRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + saveRelationVO.getSpaceId());
    graphEntityRelationDO.setSubjectName(saveRelationVO.getSubjectName());
    graphEntityRelationDO.setSubjectTagName(saveRelationVO.getSubjectTagName());
    List<NgVertex<String>> objectInfos = graphEdgeService.getSubjectInfo(graphEntityRelationDO);
    objectInfos.stream().forEach(o -> {
      Entity entity = new Entity();
      entity.setEntityId(o.getVid());
      entity.setTagName(o.getTags().get(SpaceConstant.INDEX));
      LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) o.getProperties()
          .get(entity.getTagName());
      if (null != properties && null != properties.get(SpaceConstant.NAME)) {
        entity.setEntityName((String) properties.get(SpaceConstant.NAME));
      }
      entities.add(entity);
    });
    return entities;
  }

  private void checkSubject(SaveRelationVO saveRelationVO, GraphRelationDO graphRelationDO,
      List<Entity> subject) {
    String id = UniqueIDGenerator.generateUniqueID();
    // 判断subject是否有值，如果没有新建实体 ，如果有多条 也同样走新建,如果只用一条则使用
    if (CollUtil.isNotEmpty(subject)) {
      if (subject.size() > 1) {
        // 新建
        saveEntityForSubject(saveRelationVO, graphRelationDO, id);
        graphRelationDO.setSubjectId(id);
      } else {
        graphRelationDO.setSubjectId(subject.get(SpaceConstant.INDEX).getEntityId());
      }
    } else {
      // 新建
      saveEntityForSubject(saveRelationVO, graphRelationDO, id);
      graphRelationDO.setSubjectId(id);

    }
  }

  private void saveEntityForSubject(SaveRelationVO saveRelationVO, GraphRelationDO graphRelationDO,
      String id) {
    log.info("【Process Kg-webserver-web save  Entity : {} for space :  {} to Relation : {} 】",
        saveRelationVO.getEdgeId(), saveRelationVO.getSpaceId(), saveRelationVO.getEdgeId());
    GraphRelationEntityDO graphRelationEntityDO = new GraphRelationEntityDO();
    graphRelationEntityDO.setVid(id);
    graphRelationEntityDO.setTagName(saveRelationVO.getSubjectTagName());
    graphRelationEntityDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + saveRelationVO.getSpaceId());
    if (!saveRelationVO.getSubjectName().matches(SpaceConstant.PATTERN_STRING)) {
      log.error("【entity name not matches :{}】", saveRelationVO.getSubjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_NAME);
    }
    KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
        saveRelationVO.getSubjectTagId());
    List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = knowledgeGraphTagEdgePropertyMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, saveRelationVO.getSubjectTagId())
            .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.INDEX)
            .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
    if (null != mapTagEdge && !CollectionUtils.isEmpty(tagEdgeProperties)) {
      if (!StringUtil.isBlank(mapTagEdge.getTtlCol())
          && SpaceConstant.INDEX != mapTagEdge.getTtlDuration()) {
        for (KnowledgeGraphTagEdgePropertyPo property : tagEdgeProperties) {
          if (property.getPropertyName().equals(mapTagEdge.getTtlCol()) && !StringUtil.isBlank(
              property.getDefaultValue()) && property.getTagRequired() == SpaceConstant.INDEX) {
            if (!isNotExpired(property.getDefaultValue(), mapTagEdge.getTtlDuration())) {
              // 已经过期
              log.error("【属性值已经过期，新增失败 :{}】", mapTagEdge.getTtlCol());
              throw ServiceExceptionUtil.exception(ErrorConstants.SUBJECT_PERPROTY_EXPIRED);
            }
          }
        }
      }
    }
    graphRelationEntityDO.setName("\'" + saveRelationVO.getSubjectName()
        .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE) + "\'");
    boolean check = knowledgeGraphEntityManageService.checkDataLimit(saveRelationVO.getSpaceId());
    if (!check) {
      log.error("【空间数据已达上限，新增失败！】");
      throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_LIMIT_ERROR);
    }
    graphEdgeService.saveSubject(graphRelationEntityDO);
    // 存储向量数据库
    saveVector(saveRelationVO, id);
  }

  private void saveVector(SaveRelationVO saveRelationVO, String id) {
    //向量数据库存储
    SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
    saveEntityDTO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + saveRelationVO.getSpaceId());
    saveEntityDTO.setEntityId(id);
    saveEntityDTO.setEntityName(saveRelationVO.getSubjectName());
    List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
    SaveTagInfoDTO tag = new SaveTagInfoDTO();
    tag.setTagName(saveRelationVO.getSubjectTagName());
    saveTagInfoDTOS.add(tag);
    saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
    // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
  }

  private void saveVectorObject(SaveRelationVO saveRelationVO, String id) {
    //向量数据库存储
    SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
    saveEntityDTO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + saveRelationVO.getSpaceId());
    saveEntityDTO.setEntityId(id);
    saveEntityDTO.setEntityName(saveRelationVO.getObjectName());
    List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
    SaveTagInfoDTO tag = new SaveTagInfoDTO();
    tag.setTagName(saveRelationVO.getObjectTagName());
    saveTagInfoDTOS.add(tag);
    saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
    // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
  }

  private List<Entity> getObjectInfo(SaveRelationVO saveRelationVO) {
    log.info("【Process Kg-webserver-web check have entity :  {} 】", saveRelationVO.getEdgeId(),
        saveRelationVO.getSpaceId());
    List<Entity> entities = new ArrayList<>();
    GraphEntityRelationDO graphEntityRelationDO = new GraphEntityRelationDO();
    graphEntityRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + saveRelationVO.getSpaceId());
    graphEntityRelationDO.setObjectName(saveRelationVO.getObjectName());
    graphEntityRelationDO.setObjectTagName(saveRelationVO.getObjectTagName());
    List<NgVertex<String>> objectInfos = graphEdgeService.getObjectInfo(graphEntityRelationDO);
    objectInfos.stream().forEach(o -> {
      Entity entity = new Entity();
      entity.setEntityId(o.getVid());
      entity.setTagName(o.getTags().get(SpaceConstant.INDEX));
      LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) o.getProperties()
          .get(entity.getTagName());
      if (null != properties && null != properties.get(SpaceConstant.NAME)) {
        entity.setEntityName((String) properties.get(SpaceConstant.NAME));
      }
      entities.add(entity);
    });
    return entities;
  }

  private void checkObject(SaveRelationVO saveRelationVO, GraphRelationDO graphRelationDO,
      List<Entity> objects) {
    String id = UniqueIDGenerator.generateUniqueID();
    // 判断subject是否有值，如果没有新建实体 ，如果有多条 也同样走新建,如果只用一条则使用
    if (!org.springframework.util.CollectionUtils.isEmpty(objects)) {
      if (objects.size() > 1) {
        // 新建
        saveEntityForObject(saveRelationVO, graphRelationDO, id);
        graphRelationDO.setObjectId(id);
        // mysql记录
      } else {
        graphRelationDO.setObjectId(objects.get(SpaceConstant.INDEX).getEntityId());
      }
    } else {
      // 新建
      saveEntityForObject(saveRelationVO, graphRelationDO, id);
      graphRelationDO.setObjectId(id);

    }
  }

  private void saveEntityForObject(SaveRelationVO saveRelationVO, GraphRelationDO graphRelationDO,
      String id) {
    log.info("【Process Kg-webserver-web save  Entity : {} for space :  {} to Relation : {} 】",
        saveRelationVO.getEdgeId(), saveRelationVO.getSpaceId(), saveRelationVO.getEdgeId());
    GraphRelationEntityDO graphRelationEntityDO = new GraphRelationEntityDO();
    graphRelationEntityDO.setVid(id);
    graphRelationEntityDO.setTagName(saveRelationVO.getObjectTagName());
    graphRelationEntityDO.setSpaceId(
        SpaceConstant.SPACE_FIX_NAME + "_" + saveRelationVO.getSpaceId());
    if (!saveRelationVO.getObjectName().matches(SpaceConstant.PATTERN_STRING)) {
      log.error("【entity name not matches :{}】", saveRelationVO.getObjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_NAME);
    }
    KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
        saveRelationVO.getObjectTagId());
    List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = knowledgeGraphTagEdgePropertyMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, saveRelationVO.getObjectTagId())
            .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.INDEX)
            .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
    if (null != mapTagEdge && !CollectionUtils.isEmpty(tagEdgeProperties)) {
      if (!StringUtil.isBlank(mapTagEdge.getTtlCol())
          && SpaceConstant.INDEX != mapTagEdge.getTtlDuration()) {
        for (KnowledgeGraphTagEdgePropertyPo property : tagEdgeProperties) {
          if (property.getPropertyName().equals(mapTagEdge.getTtlCol()) && !StringUtil.isBlank(
              property.getDefaultValue()) && property.getTagRequired() == SpaceConstant.INDEX) {
            if (!isNotExpired(property.getDefaultValue(), mapTagEdge.getTtlDuration())) {
              // 已经过期
              log.error("【属性值已经过期，新增失败 :{}】", mapTagEdge.getTtlCol());
              throw ServiceExceptionUtil.exception(ErrorConstants.OBJECT_PERPROTY_EXPIRED);
            }
          }
        }
      }
    }
    graphRelationEntityDO.setName("\'" + saveRelationVO.getObjectName()
        .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE) + "\'");
    boolean check = knowledgeGraphEntityManageService.checkDataLimit(saveRelationVO.getSpaceId());
    if (!check) {
      log.error("【空间数据已达上限，新增失败！】");
      throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_LIMIT_ERROR);
    }
    graphEdgeService.saveObject(graphRelationEntityDO);

    // 存储向量数据库
    saveVectorObject(saveRelationVO, id);
  }


  private void processSaveRelation(GraphRelationDO graphEntityDO, SaveRelationVO saveEntityVO) {
    StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner match = new StringJoiner(" AND ");

    // 获取属性是否设置过过期属性
    log.info("【Web Api get  Tag / Edge  TTl: {}  Ttl field】 ", saveEntityVO.getEdgeId());

    saveEntityVO.getEntityProperties().forEach(s -> {
//      if (SpaceConstant.STRING.equals(s.getPropertyType()) && SpaceConstant.INDEX == s.getTagRequired()
//          && !SpaceConstant.TEXT.equals(s.getExtra()) && !StringUtil.isBlank(s.getPropertyValue())) {
//        // 文件迁移
//        URL urlPath = null;
//        try {
//          urlPath = new URL(s.getPropertyValue());
//          String path = urlPath.getPath();
//          movePdfFile(path, graphEntityDO.getSpaceId());
//          String newUrl = s.getPropertyValue().replace(url, "").replace(SpaceConstant.TMP, SpaceConstant.TARGET);
//          s.setPropertyValue(newUrl);
//        } catch (MalformedURLException e) {
//          log.error("【Move temporary failure: {}】", e.getMessage(), e);
//          throw ServiceExceptionUtil.exception(ErrorConstants.MOVE_FILE_ERROR, e);
//        }
//      }
      key.add(SpaceConstant.QUOTATIONMARK + s.getPropertyName() + SpaceConstant.QUOTATIONMARK);
      String buildString = processData(s.getPropertyType(), s.getPropertyValue());
      value.add(buildString);
      match.add(
          "e." + SpaceConstant.QUOTATIONMARK + s.getPropertyName() + SpaceConstant.QUOTATIONMARK
              + "==" + buildString);
    });
    graphEntityDO.setEntityProperties(key.toString());
    graphEntityDO.setEntityValue(value.toString());
    graphEntityDO.setAttributeMatch(match.toString());
  }

  private String processData(String propertyType, String propertyValue) {
    StringBuilder param = new StringBuilder();
    if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING) || propertyType.equalsIgnoreCase(
        SpaceConstant.STRING)) {
      param.append("\"").append(propertyValue).append("\"").append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DOUBLE) || propertyType.equalsIgnoreCase(
        SpaceConstant.FLOAT)) {
      double doubleValue = Double.parseDouble(propertyValue); // 尝试解析为double
//            String formatValue = String.format("%.15f", doubleValue);
      param.append(doubleValue).append(SpaceConstant.TAG_SPACE);
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
        throw new RuntimeException(SpaceConstant.RANGE + propertyValue + SpaceConstant.RANGE_INDEX);
      } else {
        param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
      }
    } else {
      param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
    }
    return param.toString().trim();
  }
}
