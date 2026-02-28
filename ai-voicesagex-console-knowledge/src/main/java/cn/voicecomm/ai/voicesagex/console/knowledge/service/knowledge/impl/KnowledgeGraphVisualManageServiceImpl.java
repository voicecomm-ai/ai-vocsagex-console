package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import static com.alibaba.fastjson2.JSONWriter.Feature.WriteMapNullValue;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphVisualManageService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.ExpansionDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.QueryPathDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.SelectLikeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.VertexInfoDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.PropertyRelationDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.enums.DirectionEnums;
import cn.voicecomm.ai.voicesagex.console.api.nebula.enums.FindEnums;
import cn.voicecomm.ai.voicesagex.console.api.nebula.enums.QueryTypeEnums;
import cn.voicecomm.ai.voicesagex.console.api.nebula.enums.StateEnum;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.CenterVertexVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.EdgeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.EdgePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExpansionInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExpansionVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExtendVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.FullGraphVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GetCenterNodeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GraphVisualVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GraphVisualnfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.NodeInfoVo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.QueryFullGraphVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.QueryPathVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.RouteListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.RouteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SelectLikeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SingleEdgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SingleVertexVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SubVertexVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.TagPropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexEdgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexPropertiesVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexTagInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VisualmanagerVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphVisualManageMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVisualService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateGraphUtil;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphVisualManagePo;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphVisualManageServiceImpl extends
    ServiceImpl<KnowledgeGraphVisualManageMapper, KnowledgeGraphVisualManagePo> implements
    KnowledgeGraphVisualManageService {

  private final GraphVisualService graphVisualService;

  private final KnowledgeGraphTagEdgeService knowledgeGraphTagEdgeService;

  private final GraphEdgeService graphEdgeService;

  private final GraphVertexService graphVertexService;

  private final GraphEdgeMapper graphEdgeMapper;

  @Override
  public CommonRespDto<NodeInfoVo> getNodeNumber(GraphVisualnfoVO graphVisualnfoVO) {
    log.info("【Count the number of nodes in the graph space ： {}】", graphVisualnfoVO.getSpaceId());
    // 获取所有tag
    NodeInfoVo nodeInfoVo = new NodeInfoVo();
    // 获取所有tag和 edge
    List<KnowledgeGraphTagEdgeDto> tags = knowledgeGraphTagEdgeService.getEdgeInfos(
        graphVisualnfoVO.getSpaceId());
    List<KnowledgeGraphTagEdgeDto> edges = knowledgeGraphTagEdgeService.getTagInfos(
        graphVisualnfoVO.getSpaceId());
    nodeInfoVo.setNodeNum(tags.stream().mapToInt(tag -> graphVertexService.getNumber(
        SpaceConstant.SPACE_NAME_FIX + graphVisualnfoVO.getSpaceId(), tag.getTagName())).sum());
    nodeInfoVo.setEdgeNum(edges.stream().mapToInt(edge -> Math.toIntExact(
        graphEdgeService.getNumber(edge.getTagName(),
            SpaceConstant.SPACE_NAME_FIX + graphVisualnfoVO.getSpaceId()))).sum());
    // 依次获取节点数量
    return CommonRespDto.success(nodeInfoVo);
  }

  @Override
  public CommonRespDto<List<VertexInfoVO>> selectVertexInfo(SelectLikeVO selectLikeVO) {
    log.info("【Fuzzy search node details for space:  {}】", selectLikeVO.getSpaceId());
    List<VertexInfoVO> vertexInfoVOS = new ArrayList<>();
    if (!StringUtil.isBlank(selectLikeVO.getVertexName())) {
      SelectLikeDO selectLikeDO = new SelectLikeDO();
      selectLikeDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + selectLikeVO.getSpaceId());
      selectLikeDO.setVertexName(selectLikeVO.getVertexName());
      List<VertexInfoDO> vertexInfoDOS = graphVisualService.selectVertexInfo(selectLikeDO);
      vertexInfoDOS.stream().forEach(vertexInfoDO -> {
        VertexInfoVO vo = new VertexInfoVO(vertexInfoDO.getVertexName(), vertexInfoDO.getVertexId(),
            vertexInfoDO.getVertexTagName());
        vertexInfoVOS.add(vo);
      });
    }
    return CommonRespDto.success(vertexInfoVOS);
  }

  @Override
  public CommonRespDto<GraphVisualVO> getGraphVisual(GraphVisualnfoVO graphVisualnfoVO) {
    // 查询中心节点，如果未设置过中心节点，随机5个散点返回
    log.info("【get Graph visual  for space:  {}】", graphVisualnfoVO.getSpaceId());
    GraphVisualVO graphVisualVO = new GraphVisualVO();
    List<VertexEdgeVO> vertexEdgeVOS = new ArrayList<>();
    HashSet<String> ids = new HashSet<>();
    try {

      KnowledgeGraphVisualManagePo visualmanager = null;
      List<KnowledgeGraphVisualManagePo> knowledgeGraphVisualManagePoList = baseMapper.selectList(
          Wrappers.<KnowledgeGraphVisualManagePo>lambdaQuery()
              .eq(KnowledgeGraphVisualManagePo::getSpaceId, graphVisualnfoVO.getSpaceId()));
      if (CollUtil.isNotEmpty(knowledgeGraphVisualManagePoList)) {
        visualmanager = knowledgeGraphVisualManagePoList.getFirst();
      }
      if (null != visualmanager && !existVertex(visualmanager)) {
        // 以中心为基准获取一条的所有节点和边的关系
        // 获取所有edge
        List<KnowledgeGraphTagEdgeDto> mapTagEdges = knowledgeGraphTagEdgeService.getTagInfos(
            graphVisualnfoVO.getSpaceId());
        NgVertex<String> center = graphVertexService.getEntity(visualmanager.getEntityId(),
            SpaceConstant.SPACE_FIX_NAME + "_" + graphVisualnfoVO.getSpaceId());
        if (!CollectionUtils.isEmpty(mapTagEdges)) {
          // 获取从中心点一跳的所有节点信息
          List<VisualInfo> visualInfos = graphVisualService.getOneStepsInfo(
              visualmanager.getEntityId(),
              mapTagEdges.stream().map(KnowledgeGraphTagEdgeDto::getTagName).toList(),
              SpaceConstant.SPACE_FIX_NAME + "_" + graphVisualnfoVO.getSpaceId(),
              SpaceConstant.REPLICA_FACTOR);
          if (!CollectionUtils.isEmpty(visualInfos)) {
            visualInfos.stream().forEach(visualInfo -> {
              VertexEdgeVO vo = new VertexEdgeVO();
              BeanUtils.copyProperties(visualInfo, vo);
              ids.add(visualInfo.getObjectId());
              ids.add(visualInfo.getSubjectId());
              vertexEdgeVOS.add(vo);
            });

            ids.add(String.valueOf(visualmanager.getEntityId()));
            // 通过节点id获取 tagname
            Map<String, Map<String, List<String>>> tagNameMap = graphVisualService.getTagNameMap(
                ids, SpaceConstant.SPACE_FIX_NAME + "_" + graphVisualnfoVO.getSpaceId());

            vertexEdgeVOS.stream().forEach(vertexEdgeVO -> {
              Map<String, List<String>> subMap = tagNameMap.get(vertexEdgeVO.getSubjectId());
              Map<String, List<String>> obMap = tagNameMap.get(vertexEdgeVO.getObjectId());
              if (!CollectionUtils.isEmpty(subMap)) {
                subMap.entrySet().forEach((entry) -> {
                  vertexEdgeVO.setSubjectName(entry.getKey());
                  vertexEdgeVO.setSubjectTagName(entry.getValue());
                });
              }
              if (!CollectionUtils.isEmpty(obMap)) {
                obMap.entrySet().forEach((entry) -> {
                  vertexEdgeVO.setObjectName(entry.getKey());
                  vertexEdgeVO.setObjectTagName(entry.getValue());
                });
              }
            });
          } else {
            if (null != center) {
              VertexEdgeVO vo = new VertexEdgeVO();
              vo.setSubjectId(center.getVid());
              vo.setSubjectTagName(center.getTags());
              setVertexName(center, vo);
              vertexEdgeVOS.add(vo);
            }
          }
        } else {
          // 没有对应的关联关系边，返回中心节点消息
          if (null != center) {
            VertexEdgeVO vo = new VertexEdgeVO();
            vo.setSubjectId(center.getVid());
            vo.setSubjectTagName(center.getTags());
            setVertexName(center, vo);
            vertexEdgeVOS.add(vo);
          }
        }
      } else {
        // 随机返回5个点
        List<NgVertex<String>> vertices = graphVisualService.getRandNumber(
            SpaceConstant.SPACE_FIX_NAME + "_" + graphVisualnfoVO.getSpaceId());
        if (!CollectionUtils.isEmpty(vertices)) {
          vertices.stream().forEach(v -> {
            VertexEdgeVO vo = new VertexEdgeVO();
            vo.setSubjectId(v.getVid());
            vo.setSubjectTagName(v.getTags());
            setVertexName(v, vo);
            vertexEdgeVOS.add(vo);
          });
        }
      }
      graphVisualVO.setVertexVOList(vertexEdgeVOS);
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(graphVisualVO);
  }

  private boolean existVertex(KnowledgeGraphVisualManagePo visualmanager) {
    NgVertex ngVertex = graphVertexService.getEntity(visualmanager.getEntityId(),
        SpaceConstant.SPACE_FIX_NAME + "_" + visualmanager.getSpaceId());
    return null == ngVertex;
  }


  @Override
  public CommonRespDto<Boolean> setCenterNode(CenterVertexVO centerVertexVO) {
    log.info("【Ease of Access Center,Isizinda :  {}】",
        JSON.toJSONString(centerVertexVO, WriteMapNullValue));
    if (centerVertexVO != null) {
      // 执行更新操作
      baseMapper.delete(Wrappers.<KnowledgeGraphVisualManagePo>lambdaQuery()
          .eq(KnowledgeGraphVisualManagePo::getSpaceId, centerVertexVO.getSpaceId()));
      if (!StringUtil.isBlank(centerVertexVO.getVertexName())) {
        KnowledgeGraphVisualManagePo visualmanager = KnowledgeGraphVisualManagePo.builder()
            //时间戳作为id
            .spaceId(Math.toIntExact(centerVertexVO.getSpaceId()))
            .entityId(centerVertexVO.getVertexId()).centreVertex(centerVertexVO.getVertexName())
            .build();
        return CommonRespDto.success(baseMapper.insert(visualmanager) > SpaceConstant.INDEX);
      } else {
        return CommonRespDto.success(Boolean.TRUE);
      }
    } else {
      return CommonRespDto.success(Boolean.FALSE);
    }
  }

  @Override
  public CommonRespDto<List<String>> getEdgeList(GraphVisualnfoVO graphVisualnfoVO) {
    log.info("【Get all edge information for spaceId:  {}】", graphVisualnfoVO.getSpaceId());
    return CommonRespDto.success(
        knowledgeGraphTagEdgeService.getTagInfos(graphVisualnfoVO.getSpaceId()).stream()
            .map(KnowledgeGraphTagEdgeDto::getTagName).toList());
  }

  @Override
  public CommonRespDto<VertexTagInfoVO> singleVertexInfo(SingleVertexVO singleVertexVO) {
    log.info("【Obtain node information and ontology information for spaceId:  {}】",
        singleVertexVO.getSpaceId());
    VertexTagInfoVO vo = new VertexTagInfoVO();
    try {
      NgVertex<String> ngVertex = graphVisualService.singleVertexInfo(
          SpaceConstant.SPACE_NAME_FIX + singleVertexVO.getSpaceId(), singleVertexVO.getVertexId());
      if (null != ngVertex) {
        vo.setVertexId(ngVertex.getVid());
        List<String> tags = ngVertex.getTags();
        Map<String, Object> properties = ngVertex.getProperties();
        List<TagPropertyVO> tagPropertyVOS = new ArrayList<>();
        tags.stream().forEach(tag -> {
          TagPropertyVO tagPropertyVO = new TagPropertyVO();
          LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>) properties.get(
              tag);
          // 获取所有的键并进行排序
          List<String> sortedKeys = new ArrayList<>(tagMap.keySet());
          Collections.sort(sortedKeys);  // 按字母顺序排序
          // 使用排序后的键创建新的 LinkedHashMap
          LinkedHashMap<String, Object> sortedTagMap = new LinkedHashMap<>();
          for (String key : sortedKeys) {
            sortedTagMap.put(key, tagMap.get(key));
          }
          tagPropertyVO.setTagName(tag);
          List<VertexPropertiesVO> vertexPropertiesVOS = new ArrayList<>();
          for (Map.Entry<String, Object> entry : sortedTagMap.entrySet()) {
            VertexPropertiesVO vertexPropertiesVO = new VertexPropertiesVO();
            if (!SpaceConstant.NAME.equals(entry.getKey())) {
              // 从数据库查询属性类型
              vertexPropertiesVO.setPropertyName(entry.getKey());
              KnowledgeGraphTagEdgePropertyDto tagEdgeProperty = getEntityPropertyType(
                  singleVertexVO.getSpaceId(), tag, entry.getKey());
              if (null != tagEdgeProperty) {
                vertexPropertiesVO.setPropertyType(tagEdgeProperty.getPropertyType());
                vertexPropertiesVO.setTagRequired(tagEdgeProperty.getTagRequired());
                vertexPropertiesVO.setExtra(tagEdgeProperty.getExtra());
              }
              if (null == entry.getValue()) {
                vertexPropertiesVO.setPropertyValue("");
              } else {
                if (entry.getValue() instanceof Date) {
                  if (entry.getValue() instanceof Time) {
                    vertexPropertiesVO.setPropertyValue(
                        DateGraphUtil.TimeZoneConversion(entry.getValue().toString()));
                  } else if (entry.getValue().toString().contains(SpaceConstant.COLON)) {
                    vertexPropertiesVO.setPropertyValue(
                        DateGraphUtil.dateProcessTime(entry.getValue().toString()));
                  } else {
                    vertexPropertiesVO.setPropertyValue(entry.getValue().toString());
                  }
                } else if (entry.getValue() instanceof Long
                    && entry.getValue().toString().length() == SpaceConstant.PARTITION_NUM) {
                  vertexPropertiesVO.setPropertyValue(
                      DateGraphUtil.dateProcessTimeStamp(((Long) entry.getValue()).longValue()));
                } else {
                  if (SpaceConstant.FILE_PATH.equals(entry.getKey())) {
//                  String newUrl = entry.getValue().toString().replace(url, "");
//                  if (newUrl.contains("http") || newUrl.contains("https")) {
//                    vertexPropertiesVO.setPropertyValue(newUrl);
//
//                  } else {
//                    vertexPropertiesVO.setPropertyValue(
//                        SpaceConstant.HTTP + ip + port + SpaceConstant.PREFIX + newUrl);
//                  }
                  } else {
                    vertexPropertiesVO.setPropertyValue(entry.getValue().toString());
                  }
                }
              }
              vertexPropertiesVOS.add(vertexPropertiesVO);
            }
          }
          vo.setVertexName(sortedTagMap.get(SpaceConstant.NAME).toString());
          tagPropertyVO.setVertexPropertiesVOS(vertexPropertiesVOS);
          tagPropertyVOS.add(tagPropertyVO);
        });
        vo.setTagName(tagPropertyVOS);
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(vo);
  }

  private KnowledgeGraphTagEdgePropertyDto getEntityPropertyType(Long spaceId, String tag,
      String key) {
    log.info("【获取tag属性值{}】", tag);
    KnowledgeGraphTagEdgeDto mapTagEdge = knowledgeGraphTagEdgeService.getTagInfo(spaceId, tag);
    if (null != mapTagEdge) {
      // 获取属性值
      KnowledgeGraphTagEdgePropertyDto tagEdgeProperty = knowledgeGraphTagEdgeService.getTagEdgeProperty(
          Long.valueOf(mapTagEdge.getTagEdgeId()), SpaceConstant.INDEX, key);
      if (null != tagEdgeProperty && !StringUtil.isBlank(tagEdgeProperty.getPropertyType())) {
        return tagEdgeProperty;
      }
    }
    return null;
  }

  @Override
  public CommonRespDto<EdgeInfoVO> singleEdgeInfo(SingleEdgeVO singleEdgeVO) {
    log.info("【get edge data  for spaceId:  {}】", singleEdgeVO.getSpaceId());
    EdgeInfoVO edgeInfoVO = new EdgeInfoVO();
    List<EdgePropertyVO> edgePropertyVOList = new ArrayList<>();
    GraphRelationDO graphRelationDO = new GraphRelationDO();
    BeanUtils.copyProperties(singleEdgeVO, graphRelationDO);
    graphRelationDO.setSubjectId(singleEdgeVO.getSubjectId());
    graphRelationDO.setObjectId(singleEdgeVO.getObjectId());
    graphRelationDO.setRank(SpaceConstant.RANK + singleEdgeVO.getRank());
    Map<String, PropertyRelationDTO> propertyType = new HashMap<>();
    graphRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + singleEdgeVO.getSpaceId());
    // 从数据库拿取边的id
    KnowledgeGraphTagEdgeDto tagInfos = knowledgeGraphTagEdgeService.getEdgeInfo(
        singleEdgeVO.getSpaceId(), singleEdgeVO.getEdgeName());
    // 获取边的信息
    NgEdge<String> ngEdge = graphEdgeMapper.getEdgeInfo(graphRelationDO);
    if (null != ngEdge && null != tagInfos) {
      edgeInfoVO.setEdgeName(ngEdge.getEdgeName());
      LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>) ngEdge.getProperties();
      List<KnowledgeGraphTagEdgePropertyDto> edgeProperty = knowledgeGraphTagEdgeService.getEdgeProperty(
          Long.valueOf(tagInfos.getTagEdgeId()), SpaceConstant.REPLICA_FACTOR);
      edgeProperty.stream().forEach(edge -> {
        propertyType.put(edge.getPropertyName(),
            new PropertyRelationDTO(edge.getPropertyType(), edge.getExtra()));
      });
      for (Map.Entry<String, Object> entry : tagMap.entrySet()) {
        if (!SpaceConstant.NAME.equals(entry.getKey())) {
          EdgePropertyVO edgePropertyVO = new EdgePropertyVO();
          edgePropertyVO.setPropertyName(entry.getKey());
          String type = propertyType.get(edgePropertyVO.getPropertyName()).getPropertyType();
          edgePropertyVO.setPropertyType(type);
          edgePropertyVO.setExtra(propertyType.get(edgePropertyVO.getPropertyName()).getExtra());
          if (!StringUtil.isBlank(type)) {
            if (type.equals(SpaceConstant.DATETIME)) {
              edgePropertyVO.setPropertyValue(
                  entry.getValue() != null && !StringUtil.isBlank(entry.getValue().toString())
                      ? DateGraphUtil.dateProcessTime(entry.getValue().toString()) : "");
            } else if (type.equals(SpaceConstant.TIMESTAMP)) {
              // 获取边属性，拿取边的类型
              edgePropertyVO.setPropertyValue(
                  entry.getValue() != null && !StringUtil.isBlank(entry.getValue().toString())
                      ? DateGraphUtil.dateProcessTimeStamp(
                      Long.valueOf(entry.getValue().toString())) : "");
            } else if (entry.getValue() instanceof Time) {
              edgePropertyVO.setPropertyValue(
                  entry.getValue() != null ? DateGraphUtil.TimeZoneConversion(
                      entry.getValue().toString()) : null);
            } else {
              edgePropertyVO.setPropertyValue(
                  entry.getValue() != null ? entry.getValue().toString() : "");
            }
          }
          edgePropertyVOList.add(edgePropertyVO);
        }
      }
    }
    edgeInfoVO.setEdgePropertyVOList(edgePropertyVOList);
    return CommonRespDto.success(edgeInfoVO);
  }

  @Override
  public CommonRespDto<QueryFullGraphVO> queryFullGraph(FullGraphVO fullGraphVO) {
    log.info("【get from  vertex:{}  to subgraph  for spaceId:  {}】", fullGraphVO.getStartId(),
        fullGraphVO.getSpaceId());
    List<VertexEdgeVO> vertexVOList = new ArrayList<>();
    List<VertexEdgeVO> vertexVOTmp = new ArrayList<>();
    Map<String, SubVertexVO> subMap = new HashMap<>();
    Set<String> vertexSet = new HashSet<>();
    try {
      List<NgSubgraph<String>> subgraph = graphVisualService.queryFullGraph(
          SpaceConstant.SPACE_NAME_FIX + fullGraphVO.getSpaceId(), fullGraphVO.getStartId());
      // 解析点和边信息
      for (NgSubgraph<String> ngSubgraph : subgraph) {
        if (ngSubgraph.getVertexes().get(SpaceConstant.INDEX).getVid()
            .equals(String.valueOf(fullGraphVO.getStartId()))) {
          List<NgEdge<String>> edges = ngSubgraph.getEdges();
          if (!CollectionUtils.isEmpty(edges)) {
            edges.stream().forEach(e -> {
              if (!CollectionUtils.isEmpty(fullGraphVO.getEdges())) {
                String edgeName = e.getEdgeName();
                if (fullGraphVO.getEdges().contains(e.getEdgeName()) && e.getSrcID()
                    .equals(fullGraphVO.getStartId()) || e.getDstID()
                    .equals(fullGraphVO.getStartId())) {
                  VertexEdgeVO vo = new VertexEdgeVO(e.getSrcID(), e.getDstID(), edgeName,
                      e.getRank());
                  vertexSet.add(e.getDstID());
                  vertexSet.add(e.getSrcID());
                  vertexVOList.add(vo);
                }
              } else {
                String edgeName = e.getEdgeName();
                VertexEdgeVO vo = new VertexEdgeVO(e.getSrcID(), e.getDstID(), edgeName,
                    e.getRank());
                vertexSet.add(e.getDstID());
                vertexSet.add(e.getSrcID());
                vertexVOList.add(vo);
              }
            });
          } else {
            List<NgVertex<String>> ngVertices = ngSubgraph.getVertexes();
            ngVertices.forEach(ng -> {
              NgVertex<String> ngVertex = graphVertexService.getEntity(ng.getVid(),
                  SpaceConstant.SPACE_FIX_NAME + "_" + fullGraphVO.getSpaceId());
              if (null != ngVertex) {
                VertexEdgeVO vo = new VertexEdgeVO();
                vo.setSubjectId(ngVertex.getVid());
                vo.setSubjectTagName(ngVertex.getTags());
                Map<String, Object> properties = ngVertex.getProperties();
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                  LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
                  vo.setSubjectName(entryValue.get(SpaceConstant.NAME));
                }
                vertexVOList.add(vo);
              }
            });
          }
        }
      }
      if (!CollectionUtils.isEmpty(vertexSet)) {
        // 查询点的详细信息
        List<NgVertex<String>> ngvertexs = graphVertexService.getSelectNgvertexs(
            SpaceConstant.SPACE_FIX_NAME + "_" + fullGraphVO.getSpaceId(),
            vertexSet.stream().toList());
        ngvertexs.stream().forEach(ng -> {
          SubVertexVO subVertexVO = new SubVertexVO();
          subVertexVO.setVertexTagName(ng.getTags());
          Map<String, Object> properties = ng.getProperties();
          for (Map.Entry<String, Object> entry : properties.entrySet()) {
            LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
            subVertexVO.setVertexName(entryValue.get(SpaceConstant.NAME));
          }
          subMap.put(ng.getVid(), subVertexVO);
        });

        // vertexVOList 填充属性
        for (VertexEdgeVO vertexEdgeVO : vertexVOList) {
          boolean flag = true;
          SubVertexVO subVertexVO = subMap.get(String.valueOf(vertexEdgeVO.getSubjectId()));
          if (null != subVertexVO) {
            vertexEdgeVO.setSubjectName(subVertexVO.getVertexName());
            vertexEdgeVO.setSubjectTagName(subVertexVO.getVertexTagName());
          } else {
            flag = false;
          }
          SubVertexVO subVertexVObject = subMap.get(String.valueOf(vertexEdgeVO.getObjectId()));
          if (null != subVertexVObject) {
            vertexEdgeVO.setObjectName(subVertexVObject.getVertexName());
            vertexEdgeVO.setObjectTagName(subVertexVObject.getVertexTagName());
          } else {
            flag = false;
          }
          if (flag) {
            vertexVOTmp.add(vertexEdgeVO);
          }
        }
      } else {
        if (!CollectionUtils.isEmpty(vertexVOList)) {
          vertexVOTmp.add(vertexVOList.get(0));
        }
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(new QueryFullGraphVO(vertexVOTmp));
  }

  @Override
  public CommonRespDto<ExpansionInfoVO> expansionNode(ExpansionVO expansionVO) {
    log.info("【expasion node   for spaceId:  {}】", expansionVO.getVertexInfoVOList(),
        expansionVO.getSpaceId());
    List<VertexEdgeVO> vertexVOList = new ArrayList<>();
    try {
      ExpansionDO expansionDO = new ExpansionDO();
      BeanUtils.copyProperties(expansionVO, expansionDO);
      Map<String, SubVertexVO> subMap = new HashMap<>();
      expansionDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + expansionVO.getSpaceId());
      expansionDO.setDirection(DirectionEnums.getPathByCode(expansionVO.getDirection()));
      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
      Set<String> vertexSet = new HashSet<>();
      StringJoiner edgeInfos = new StringJoiner(SpaceConstant.TAG_SPLIT);
      expansionVO.getVertexInfoVOList().stream().forEach(e -> {
        joiner.add(SpaceConstant.DOUBLE_QUOTATION_MARKS + e.getVertexId()
            + SpaceConstant.DOUBLE_QUOTATION_MARKS);
      });
      expansionDO.setVertexInfoVOList(joiner.toString());
      expansionVO.getEdgeNameList().stream().forEach(edge -> {
        edgeInfos.add(SpaceConstant.QUOTATIONMARK + edge + SpaceConstant.QUOTATIONMARK);
      });
      expansionDO.setEdgeNameList(edgeInfos.toString());
      List<NgSubgraph<String>> subgraph = graphVisualService.expansionNode(expansionDO);

      if (!CollectionUtils.isEmpty(subgraph)) {
        // 解析点和边信息
        NgSubgraph<String> ngSubgraph = subgraph.get(SpaceConstant.INDEX);
        List<NgEdge<String>> edges = ngSubgraph.getEdges();
        edges.stream().forEach(e -> {
          VertexEdgeVO vo = new VertexEdgeVO(e.getSrcID(), e.getDstID(), e.getEdgeName(),
              e.getRank());
          vertexSet.add(e.getDstID());
          vertexSet.add(e.getSrcID());
          vertexVOList.add(vo);
        });

        if (!CollectionUtils.isEmpty(vertexSet)) {
          // 查询点的详细信息
          List<NgVertex<String>> ngvertexs = graphVertexService.getSelectNgvertexs(
              SpaceConstant.SPACE_FIX_NAME + "_" + expansionVO.getSpaceId(),
              vertexSet.stream().toList());
          ngvertexs.stream().forEach(ng -> {
            SubVertexVO subVertexVO = new SubVertexVO();
            subVertexVO.setVertexTagName(ng.getTags());
            Map<String, Object> properties = ng.getProperties();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
              LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
              subVertexVO.setVertexName(entryValue.get(SpaceConstant.NAME));
            }
            subMap.put(ng.getVid(), subVertexVO);
          });

          // vertexVOList 填充属性
          vertexVOList.stream().forEach(vertexEdgeVO -> {
            SubVertexVO subVertexVO = subMap.get(String.valueOf(vertexEdgeVO.getSubjectId()));
            if (null != subVertexVO) {
              vertexEdgeVO.setSubjectName(subVertexVO.getVertexName());
              vertexEdgeVO.setSubjectTagName(subVertexVO.getVertexTagName());
            }
            SubVertexVO subVertexVObject = subMap.get(String.valueOf(vertexEdgeVO.getObjectId()));
            if (null != subVertexVObject) {
              vertexEdgeVO.setObjectName(subVertexVObject.getVertexName());
              vertexEdgeVO.setObjectTagName(subVertexVObject.getVertexTagName());
            }
          });
        }
      }
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(new ExpansionInfoVO(vertexVOList));
  }

  @Override
  public CommonRespDto<RouteListVO> queryPath(QueryPathVO queryPathVO) {
    log.info("【query path info  for space:  {}】", queryPathVO.getSpaceId());
    RouteListVO routeListVO = new RouteListVO();
    QueryPathDO queryPathDO = new QueryPathDO();
    List<RouteVO> routeVOS = new ArrayList<>();
    try {
      queryPathDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + queryPathVO.getSpaceId());
      queryPathDO.setDirection(FindEnums.find(queryPathVO.getDirection()));
      queryPathDO.setQueryType(QueryTypeEnums.getPathByCode(queryPathVO.getQueryType()));
      StringJoiner joiner = new StringJoiner(",");
      queryPathVO.getEdgeNameList().stream().forEach(edge -> {
        joiner.add("`" + edge + "`");
      });
      queryPathDO.setEdgeNameList(joiner.toString());
      queryPathDO.setStartId(queryPathVO.getStartId());
      queryPathDO.setEndId(queryPathVO.getEndId());
      queryPathDO.setStepInterval(queryPathVO.getStepInterval());
      List<VertexEdgeVO> vertexEdge = new ArrayList<>();
      List<NgPath<String>> ngPaths = graphVisualService.queryPath(queryPathDO);
      if (!CollectionUtils.isEmpty(ngPaths)) {
        routeListVO.setTotal(ngPaths.size());
        ngPaths.stream().forEach(n -> {
          RouteVO routeVO = new RouteVO();
          routeVO.setHopCount(n.getRelationships().size());
          List<NgPath.Relationship<String>> relations = n.getRelationships();
          List<VertexEdgeVO> vertexEdgeVOS = new ArrayList<>();
          for (NgPath.Relationship<String> ng : relations) {
            VertexEdgeVO vo = new VertexEdgeVO();
            vo.setEdgeName(ng.getEdgeName());
            vo.setRank(ng.getRank());
            vo.setSubjectId(ng.getSrcID());
            NgVertex subjectNgVertex = graphVertexService.getEntity(vo.getSubjectId(),
                queryPathDO.getSpaceId());
            vo.setSubjectTagName(subjectNgVertex.getTags());
            LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) subjectNgVertex.getProperties()
                .get(vo.getSubjectTagName().get(SpaceConstant.INDEX));
            if (properties != null && properties.get(SpaceConstant.NAME) != null) {
              vo.setSubjectName((String) properties.get(SpaceConstant.NAME));
            }
            vo.setObjectId(ng.getDstID());
            NgVertex objectNgVertex = graphVertexService.getEntity(vo.getObjectId(),
                queryPathDO.getSpaceId());
            vo.setObjectTagName(objectNgVertex.getTags());
            LinkedHashMap<String, Object> objectProperties = (LinkedHashMap<String, Object>) objectNgVertex.getProperties()
                .get(vo.getObjectTagName().get(SpaceConstant.INDEX));
            if (objectProperties != null && objectProperties.get(SpaceConstant.NAME) != null) {
              vo.setObjectName((String) objectProperties.get(SpaceConstant.NAME));
            }
            vertexEdgeVOS.add(vo);
          }
          routeVO.setVertexVOList(vertexEdgeVOS);
          vertexEdge.addAll(vertexEdgeVOS);
          routeVOS.add(routeVO);
        });
        routeListVO.setRouteVOList(routeVOS);
      }
      routeListVO.setVertexEdge(vertexEdge);
    } catch (Exception e) {
      log.error("调用图库异常", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(routeListVO);
  }

  @Override
  public CommonRespDto<Integer> statisticalState(GraphVisualnfoVO graphVisualnfoVO) {
    log.info("【Statistical graph visualization status:  {}】", graphVisualnfoVO.getSpaceId());
    if (CollectionUtils.isEmpty(
        knowledgeGraphTagEdgeService.getEdgeInfos(graphVisualnfoVO.getSpaceId()))) {
      return CommonRespDto.success(StateEnum.MODEL.getStatus());
    } else if (!countNumber(graphVisualnfoVO)) {
      return CommonRespDto.success(StateEnum.VISUAL.getStatus());
    }
    return CommonRespDto.success(StateEnum.KNOWLEDGE.getStatus());
  }

  @Override
  public CommonRespDto<VisualmanagerVO> getCenterNode(GetCenterNodeVO getCenterNodeVO) {
    log.info("【Obtain information about the central node:{}】", getCenterNodeVO.getSpaceId());
    VisualmanagerVO visualmanagerVO = new VisualmanagerVO();
    try {
      KnowledgeGraphVisualManagePo visualmanager = baseMapper.selectOne(
          Wrappers.<KnowledgeGraphVisualManagePo>lambdaQuery()
              .eq(KnowledgeGraphVisualManagePo::getSpaceId, getCenterNodeVO.getSpaceId()), false);
      if (null != visualmanager) {
        visualmanagerVO.setVertexName(visualmanager.getCentreVertex());
        visualmanagerVO.setVertexId(visualmanager.getEntityId());
      }
      return CommonRespDto.success(visualmanagerVO);
    } catch (Exception e) {
      log.error("【Error Obtain information about the central node: {}】", e.getMessage(), e);
      return CommonRespDto.error(ErrorConstants.GET_ENTITY_NODE_INFO.getMessage());
    }
  }

  @Override
  public CommonRespDto<Set<ExtendVO>> extendOrNot(FullGraphVO fullGraphVO) {
    log.info("【Check whether the node  :{} can be extended  for spaceId:  {}】",
        fullGraphVO.getStartId(), fullGraphVO.getSpaceId());
    Set<ExtendVO> extendVOS = new HashSet<>();
    try {
      List<NgSubgraph<String>> subgraph = graphVisualService.queryFullGraph(
          SpaceConstant.SPACE_NAME_FIX + fullGraphVO.getSpaceId(), fullGraphVO.getStartId());
      if (!CollectionUtils.isEmpty(subgraph)) {
        NgSubgraph<String> ngSubgraph = subgraph.get(SpaceConstant.INDEX);
        if (!ngSubgraph.getEdges().isEmpty()) {
          // Use a map to count edges by their names
          Map<String, Integer> edgeCountMap = new HashMap<>();

          for (NgEdge<String> n : ngSubgraph.getEdges()) {
            // Check if the source ID matches the start ID
            if (n.getSrcID().toString().equals(fullGraphVO.getStartId()) || n.getDstID().toString()
                .equals(fullGraphVO.getStartId())) {
              String edgeName = n.getEdgeName(); // Get the edge name
              // Increment the count for this edge name
              edgeCountMap.put(edgeName, edgeCountMap.getOrDefault(edgeName, 0) + 1);
            }
          }

          // Create ExtendVO objects for each edge name and its count
          for (Map.Entry<String, Integer> entry : edgeCountMap.entrySet()) {
            ExtendVO extendVO = new ExtendVO();
            extendVO.setExtendEdge(entry.getKey());
            extendVO.setEdgeExtendNumber(entry.getValue()); // Set the edge count
            extendVOS.add(extendVO); // Add to the set
          }
        }
      }
      return CommonRespDto.success(extendVOS);
    } catch (Exception e) {
      log.error("【查看节点是否可扩展失败: {}】", e.getMessage(), e);
      return CommonRespDto.error(ErrorConstants.GET_EXTEND_NODE_INFO.getMessage());
    }
  }


  private boolean countNumber(GraphVisualnfoVO graphVisualnfoVO) {
    NgVertex<String> vertex = graphVertexService.getVertexLimit(
        SpaceConstant.SPACE_NAME_FIX + graphVisualnfoVO.getSpaceId());
    if (null != vertex) {
      return false;
    }
    return true;

  }

  /**
   * 设置节名称
   *
   * @param v
   */
  private void setVertexName(NgVertex<String> v, VertexEdgeVO vo) {
    Map<String, Object> properties = v.getProperties();
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
      vo.setSubjectName(entryValue.get(SpaceConstant.NAME));
    }
  }

}
