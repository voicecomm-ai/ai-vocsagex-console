package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;


import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVisualService;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.VisualResultHandler;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphVisualManageMapper;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.ExpansionDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.QueryPathDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.SelectLikeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.VertexInfoDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfoVector;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import lombok.extern.slf4j.Slf4j;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GraphVisualServiceImpl implements GraphVisualService {


  @Autowired
  private GraphVisualManageMapper graphVisualManageMapper;

  @Autowired
  private VisualResultHandler visualResultHandler;

  @Override
  public List<VertexInfoDO> selectVertexInfo(SelectLikeDO selectLikeVO) {
    log.info("【Fuzzy inquiry of complex vertex info：{}】", selectLikeVO.getVertexName());
    try {

      List<VertexInfoDO> vertexInfoDOS = new ArrayList<>();
      if (!selectLikeVO.getVertexName().equals(SpaceConstant.ALL_RELATION)) {
        ResultSet resultSet = graphVisualManageMapper.selectVertexInfo(selectLikeVO);
        List<String> columnNames = resultSet.getColumnNames();
        if (columnNames.contains(SpaceConstant.NAME) && columnNames.contains(SpaceConstant.ID)
            && columnNames.contains(SpaceConstant.TAGLIST)) {
          // 获取各列的值
          List<ValueWrapper> idList = resultSet.colValues(SpaceConstant.ID);
          List<ValueWrapper> names = resultSet.colValues(SpaceConstant.NAME);
          List<ValueWrapper> tags = resultSet.colValues(SpaceConstant.TAGLIST);
          if (idList.size() == tags.size() && names.size() == idList.size()
              && names.size() == tags.size()) {
            for (int i = SpaceConstant.INDEX; i < idList.size(); i++) {
              List<String> tagList = new ArrayList<>();
              // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
              String dst = idList.get(i).asString();
              String name = names.get(i).asString();
              ArrayList<ValueWrapper> valueWrappers = tags.get(i).asList();
              valueWrappers.stream().forEach(valueWrapper -> {
                try {
                  tagList.add(valueWrapper.asString());
                } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
                }
              });
              VertexInfoDO vertexInfoDO = new VertexInfoDO(name, dst, tagList);
              vertexInfoDOS.add(vertexInfoDO);
            }
          }
        }
      }
      return vertexInfoDOS;

    } catch (Exception e) {
      log.error("【Error Fuzzy inquiry of complex vertex info: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_LIKE_VERTEX, e);
    }
  }

  @Override
  public List<NgVertex<String>> getRandNumber(String spaceId) {
    log.info("【kg-web-db rand get vertex ：{}】", spaceId);
    try {
      return graphVisualManageMapper.getRandNumber(spaceId);
    } catch (Exception e) {
      log.error("【Error delete vrand get vertex: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_RAND_VERTEX, e);
    }
  }

  @Override
  public NgVertex<String> getEntityInfo(String entityId, String spaceId) {
    log.info("【kg-web-db  get vertex info ：{}】", entityId);
    try {
      return graphVisualManageMapper.getEntityInfo(entityId, spaceId);
    } catch (Exception e) {
      log.error("【Error  get vertex: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERTEX, e);
    }
  }

  @Override
  public List<VisualInfo> getOneStepsInfo(String entityId, List<String> mapTagEdges, String spaceId,
      Integer step) {
    log.info("【kg-web-db  get vertex for one steps info ：{}】", entityId);
    try {
      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
      mapTagEdges.stream().forEach(m -> {
        joiner.add(SpaceConstant.QUOTATIONMARK + m + SpaceConstant.QUOTATIONMARK);
      });
      ResultSet oneStepsInfo = graphVisualManageMapper.getOneStepsInfo(entityId, joiner.toString(),
          spaceId, step);
      return visualResultHandler.handle(oneStepsInfo);
    } catch (Exception e) {
      log.error("【Error  get vertex for one steps: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERTEX, e);
    }
  }

  @Override
  public List<VisualInfo> getOneStepsInfoSemantic(String entityId, List<String> mapTagEdges,
      String spaceId, Integer step, Boolean direction) {
    log.info("【kg-web-db  get vertex for one steps info ：{}】", entityId);
    try {
      ResultSet oneStepsInfo = null;
      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
      mapTagEdges.stream().forEach(m -> {
        joiner.add(SpaceConstant.QUOTATIONMARK + m + SpaceConstant.QUOTATIONMARK);
      });
      if (direction) {
        oneStepsInfo = graphVisualManageMapper.getOneStepsInfoReverse(entityId, joiner.toString(),
            spaceId, step);
      } else {
        oneStepsInfo = graphVisualManageMapper.getOneStepsInfoForward(entityId, joiner.toString(),
            spaceId, step);
      }
      return visualResultHandler.handle(oneStepsInfo);
    } catch (Exception e) {
      log.error("【Error  get vertex for one steps: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERTEX, e);
    }
  }

  @Override
  public List<VisualInfoVector> getOneStepsInfoVector(String entityId, List<String> mapTagEdges,
      String spaceId, Integer step) {
    log.info("【kg-web-db  get vertex for one steps info ：{}】", entityId);
    try {
      StringJoiner joiner = new StringJoiner(SpaceConstant.TAG_SPLIT);
      mapTagEdges.stream().forEach(m -> {
        joiner.add(SpaceConstant.QUOTATIONMARK + m + SpaceConstant.QUOTATIONMARK);
      });
      ResultSet oneStepsInfo = graphVisualManageMapper.getOneStepsInfoVector(entityId,
          joiner.toString(), spaceId, step);
      return visualResultHandler.handleVector(oneStepsInfo);
    } catch (Exception e) {
      log.error("【Error  get vertex for one steps: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERTEX, e);
    }
  }

  @Override
  public Map<String, Map<String, List<String>>> getTagNameMap(Set<String> ids, String spaceId)
      throws UnsupportedEncodingException {
    log.info("【kg-web-db  get vertex for tag name info ：{}】", spaceId);
    try {
      Map<String, Map<String, List<String>>> listMap = new HashMap<>();
      ResultSet tagNames = graphVisualManageMapper.getTagNameMap(ids, spaceId);
      List<String> columnNames = tagNames.getColumnNames();
      if (columnNames.contains(SpaceConstant.ID) && columnNames.contains(SpaceConstant.TAGLIST)
          && columnNames.contains(SpaceConstant.NAME)) {
        // 获取各列的值
        List<ValueWrapper> idList = tagNames.colValues(SpaceConstant.ID);
        List<ValueWrapper> tags = tagNames.colValues(SpaceConstant.TAGLIST);
        List<ValueWrapper> names = tagNames.colValues(SpaceConstant.NAME);
        if (idList.size() == tags.size() && tags.size() == names.size()) {
          for (int i = SpaceConstant.INDEX; i < idList.size(); i++) {
            Map<String, List<String>> map = new HashMap<>();
            List<String> tagList = new ArrayList<>();
            // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
            String dst = idList.get(i).asString();
            String name = names.get(i).asString();

            ArrayList<ValueWrapper> valueWrappers = tags.get(i).asList();
            valueWrappers.stream().forEach(valueWrapper -> {
              try {
                tagList.add(valueWrapper.asString());
              } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
              }
            });
            // 创建一个新的 DataEntry 对象并添加到列表中
            map.put(name, tagList);
            listMap.put(dst, map);
          }
        }
      }
      return listMap;
    } catch (Exception e) {
      log.error("【Error  get vertex: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERTEX_TAG, e);
    }
  }

  @Override
  public NgVertex<String> singleVertexInfo(String spaceId, String vertexId) {
    log.info("【kg-web-db  get vertex for id name info ：{}】", spaceId);
    return graphVisualManageMapper.singleVertexInfo(spaceId, vertexId);
  }

  @Override
  public List<NgSubgraph<String>> queryFullGraph(String spaceId, String vertexId) {
    log.info("【kg-web-db  get from  vertex:{}  to subgraph】", vertexId);
    try {
      return graphVisualManageMapper.queryFullGraph(spaceId, vertexId);
    } catch (Exception e) {
      log.error("【Error  get vertex to subgraph: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SUBGRAPH, e);
    }
  }

  @Override
  public List<NgSubgraph<String>> queryFullGraphNext(String spaceId, String vertexId) {
    log.info("【kg-web-db  get from  vertex:{}  to subgraph】", vertexId);
    try {
      return graphVisualManageMapper.queryFullGraphNext(spaceId, vertexId);
    } catch (Exception e) {
      log.error("【Error  get vertex to subgraph: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SUBGRAPH, e);
    }
  }


  @Override
  public List<NgSubgraph<String>> expansionNode(ExpansionDO expansionDO) {
    log.info("【kg-web-db  expansionNode from  vertex:{}  to subgraph】", expansionDO.getSpaceId());
    try {
      return graphVisualManageMapper.expansionNode(expansionDO);
    } catch (Exception e) {
      log.error("【Error  expansionNode vertex to subgraph: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SUBGRAPH, e);
    }
  }

  @Override
  public List<NgPath<String>> queryPath(QueryPathDO queryPathDO) {
    log.info("【kg-web-db  query p ath from  vertex:{}  to path】", queryPathDO.getSpaceId());
    try {
      return graphVisualManageMapper.queryPath(queryPathDO);
    } catch (Exception e) {
      log.error("【Error  expansionNode vertex to subgraph: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.QUEREY_PATH, e);
    }
  }

}
