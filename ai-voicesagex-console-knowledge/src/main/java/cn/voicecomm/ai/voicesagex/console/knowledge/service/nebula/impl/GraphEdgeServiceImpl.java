package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEdgeDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationSingleDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.LimitEdgeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Ralation;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphVertexMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GraphEdgeServiceImpl implements GraphEdgeService {


  @Autowired
  private GraphEdgeMapper graphEdgeMapper;

  @Autowired
  private GraphVertexMapper graphVertexMapper;

  @Override
  public void deleteRelation(GraphEdgeDropDO graphEdgeDropDO) {
    log.info("【Kg-webserver-db  drop  graph retional  properties : {}】",
        graphEdgeDropDO.getSpaceId());

    try {
      // drop 操作
      graphEdgeDropDO.getRalations().stream().forEach(g -> {
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setSpaceId(graphEdgeDropDO.getSpaceId());
        graphEdge.setRank(SpaceConstant.RANK + g.getRank());
        analyProperties(graphEdge, g);
        graphEdgeMapper.dropRelation(graphEdge);
      });

    } catch (Exception e) {
      log.error("【Error alter tag/edge with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.RELATION_NAME_DELETE, e);
    }
  }

  @Override
  public void saveSubject(GraphRelationEntityDO graphRelationEntityDO) {
    log.info("【Kg-webserver-db create entity  for relation  : {}】",
        graphRelationEntityDO.getTagName());
    try {
      graphVertexMapper.saveSubject(graphRelationEntityDO);
    } catch (Exception e) {
      log.error("【Error create entity for relation mast have property: {}】",
          graphRelationEntityDO.getTagName());
      throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_RELATEION, e);
    }
  }


  @Override
  public void saveObject(GraphRelationEntityDO graphRelationEntityDO) {
    log.info("【Kg-webserver-db create entity  for relation to Object   : {}】",
        graphRelationEntityDO.getTagName());
    try {
      graphVertexMapper.saveObject(graphRelationEntityDO);
    } catch (Exception e) {
      log.error("【Error create entity for relation mast have property: {}】",
          graphRelationEntityDO.getTagName());
      throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_RELATEION, e);
    }
  }


  @Override
  public void saveRelation(GraphRelationDO graphRelationDO, int type) {
    log.info("【Kg-webserver-db create relation  for relation to Object   : {}】",
        graphRelationDO.getEdgeName());
    try {
      if (SpaceConstant.INDEX == type) {
        // 查询本条关系是否存在
        ResultSet resultSet = graphEdgeMapper.getEdgeInfoExcel(graphRelationDO);
        if (!resultSet.getRows().isEmpty()) {
          if (StringUtil.isBlank(graphRelationDO.getEntityProperties())) {
            // 确保所需的列都存在
            List<String> columnNames = resultSet.getColumnNames();
            if (columnNames.contains(SpaceConstant.E)) {
              // 获取各列的值
              List<ValueWrapper> es = resultSet.colValues(SpaceConstant.E);
              // 假设所有列表的长度相同（这是处理这种情况的关键假设）
              for (int i = SpaceConstant.INDEX; i < es.size(); i++) {
                boolean flag = true;
                // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
                HashMap<String, ValueWrapper> name = es.get(i).asMap();
                for (Map.Entry<String, ValueWrapper> entry : name.entrySet()) {
                  if (!entry.getValue().isNull()) {
                    flag = false;
                  }
                }
                if (flag) {
                  log.error("【Error save relation have one info: {}】",
                      graphRelationDO.getEdgeName());
                  throw ServiceExceptionUtil.exception(ErrorConstants.EXISTS_RELATEION);
                }
              }
            }
          } else {
            log.error("【Error save relation have one info: {}】", graphRelationDO.getEdgeName());
            throw ServiceExceptionUtil.exception(ErrorConstants.EXISTS_RELATEION);
          }
        }
        graphRelationDO.setRank(SpaceConstant.RANK + System.currentTimeMillis());
      }

      graphEdgeMapper.saveRelation(graphRelationDO);
//                graphEdgeMapper.saveRelation(graphRelationDO);

    } catch (ServiceException se) {

      log.error("【Error  save relation have one info: {}】", graphRelationDO.getEdgeName());
      throw se;
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphRelationDO.getEdgeName());
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_RELATEION, e);
    }
  }

  @Override
  public void saveRelationExcel(GraphRelationDO graphRelationDO) {
    log.info("【Kg-webserver-db create relation  for relation to Object   : {}】",
        graphRelationDO.getEdgeName());
    try {
      graphEdgeMapper.saveRelationBatch(graphRelationDO);
    } catch (ServiceException se) {

      log.error("【Error  save relation have one info: {}】", graphRelationDO.getEdgeName());
      throw se;
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphRelationDO.getEdgeName());
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_RELATEION, e);
    }
  }

  @Override
  public List<NgVertex<String>> getObjectInfo(GraphEntityRelationDO graphEntityRelationDO) {
    log.info("【Kg-webserver-db get entity  for relation to Object   : {}】",
        graphEntityRelationDO.getObjectName());
    try {
      return graphEdgeMapper.getObjects(graphEntityRelationDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphEntityRelationDO.getObjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public NgVertex<String> getObjectInfoSingle(GraphEntityRelationSingleDO graphEntityRelationDO) {
    log.info("【Kg-webserver-db get entity  for relation to Object   : {}】",
        graphEntityRelationDO.getObjectName());
    try {
      return graphEdgeMapper.getObjectsSingle(graphEntityRelationDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphEntityRelationDO.getObjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public List<NgVertex<String>> getSubjectInfo(GraphEntityRelationDO graphEntityRelationDO) {
    log.info("【Kg-webserver-db get entity  for relation to subject   : {}】",
        graphEntityRelationDO.getSubjectName());
    try {
      return graphEdgeMapper.getSubjects(graphEntityRelationDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphEntityRelationDO.getSubjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public NgVertex<String> getSubjectInfoSingle(GraphEntityRelationSingleDO graphEntityRelationDO) {
    log.info("【Kg-webserver-db get entity  for relation to subject   : {}】",
        graphEntityRelationDO.getSubjectName());
    try {
      return graphEdgeMapper.getSubjectsSingle(graphEntityRelationDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphEntityRelationDO.getSubjectName());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public List<NgEdge<String>> getAllEdge(GraphEdgeDropDO graphEdgeDropDO) {
    log.info("【Kg-webserver-db get all edge  for relation to subject   : {}】",
        graphEdgeDropDO.getEdgeName());
    try {
      return graphEdgeMapper.getAllEdge(graphEdgeDropDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphEdgeDropDO.getEdgeName());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE, e);
    }
  }

  @Override
  public List<NgEdge<String>> getVertexesLimit(LimitEdgeDO limitEdgeDO) {
    log.info("【Kg-webserver-db get limit edge  for relation to subject   : {}】",
        limitEdgeDO.getSpaceId());
    try {
      if (SpaceConstant.INDEX != limitEdgeDO.getCurrent()) {
        limitEdgeDO.setCurrent(limitEdgeDO.getCurrent() * limitEdgeDO.getPageSize());
      }
      limitEdgeDO.setSubjectName(limitEdgeDO.getSubjectName()
          .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
      limitEdgeDO.setObjectName(limitEdgeDO.getObjectName()
          .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));

      if (!StringUtil.isBlank(limitEdgeDO.getSubjectTagName())) {
        limitEdgeDO.setSubjectTagName(SpaceConstant.QUOTATIONMARK + limitEdgeDO.getSubjectTagName()
            + SpaceConstant.QUOTATIONMARK);
      }

      if (!StringUtil.isBlank(limitEdgeDO.getObjectTagName())) {
        limitEdgeDO.setObjectTagName(SpaceConstant.QUOTATIONMARK + limitEdgeDO.getObjectTagName()
            + SpaceConstant.QUOTATIONMARK);
      }

      if (!StringUtil.isBlank(limitEdgeDO.getEdgeName())) {
        return graphEdgeMapper.getVertexesLimitByEdgeByName(limitEdgeDO);
      }
      return graphEdgeMapper.getVertexesLimit(limitEdgeDO);

    } catch (Exception e) {
      log.error("【Error Kg-webserver-db get limit edge  for relation to subject: {}】",
          limitEdgeDO.getSpaceId());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE, e);
    }
  }


  @Override
  public List<NgVertex<String>> getList(String spaceId, String tagName) {
    log.info("【Kg-webserver-db get all entity  for relation to subject   : {}】", tagName);
    try {
      return graphEdgeMapper.getAllEntity(spaceId, tagName);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】", tagName);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public Long getNumber(String edgeName, String spaceId) {
    log.info("【Kg-webserver-db get  total  for relation to subject   : {}】", edgeName);
    try {
      return graphEdgeMapper.getNumber(spaceId, edgeName);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】", edgeName);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_RELATION_NUMBER, e);
    }
  }

  @Override
  public int getAllNumber(String spaceId) {
    log.info("【Kg-webserver-db get  total  for relation to subject   : {}】", spaceId);
    try {
      List<NgEdge<String>> allNumber = graphEdgeMapper.getAllNumber(spaceId);
      return allNumber.size();
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】", spaceId);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public NgEdge<String> getEdgeInfo(GraphRelationDO graphRelationDO) {
    log.info("【Kg-webserver-db get  total  for relation to subject   : {}】",
        graphRelationDO.getSpaceId());
    try {
      return graphEdgeMapper.getEdgeInfo(graphRelationDO);

    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphRelationDO.getSpaceId());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }

  }

  @Override
  public void updateRelation(GraphRelationDO graphRelationDO) {
    log.info("【Kg-webserver-db update  relation  for relation to subject   : {}】",
        graphRelationDO.getSpaceId());
    try {
      graphEdgeMapper.updateRelation(graphRelationDO);
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphRelationDO.getSpaceId());
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_ENTITY_ALL, e);
    }
  }

  @Override
  public Integer getSumEdge(LimitEdgeDO limitEdgeDO) {
    log.info("【Kg-webserver-db get  total  for relation to subject   : {}】",
        limitEdgeDO.getSpaceId());
    try {

      return graphEdgeMapper.getSumEdge(limitEdgeDO);

    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          limitEdgeDO.getSpaceId());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE, e);
    }
  }

  @Override
  public Integer getSumEdgeByEdge(LimitEdgeDO limitEdgeDO) {
    log.info("【Kg-webserver-db get  total  for relation to subject   : {}】",
        limitEdgeDO.getSpaceId());
    try {

      return graphEdgeMapper.getSumEdgeByEdge(limitEdgeDO);

    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          limitEdgeDO.getSpaceId());
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE, e);
    }
  }

  @Override
  public List<NgEdge<String>> getExportAllData(String spaceId, String tagEdgeName,
      String entityName, String subjectTagName, String objectTagName) {
    log.info("【Kg-webserver-db get export data relation to subject   : {}】", spaceId);
    try {
      if (!StringUtil.isBlank(tagEdgeName)) {
        return graphEdgeMapper.getExportAllDataByEdgeName(spaceId, tagEdgeName, entityName,
            subjectTagName, objectTagName);
      }
      return graphEdgeMapper.getExportAllData(spaceId, entityName, subjectTagName, objectTagName);

    } catch (Exception e) {
      log.error("【Error Kg-webserver-db get export data relation: {}】", spaceId);
      throw ServiceExceptionUtil.exception(ErrorConstants.EXPORT_GET_EDGE, e);
    }
  }

  @Override
  public ResultSet getEntityTotalByProperty(String s, String edgeName, String propertyName) {
    try {
      return graphEdgeMapper.getEntityTotalByProperty(s, edgeName, propertyName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.EXPORT_GET_EDGE, e);
    }
  }

  @Override
  public void saveRelationFusion(GraphRelationDO graphRelationDO, int index) {
    log.info("【Kg-webserver-db create relation  for fusion relation to Object   : {}】",
        graphRelationDO.getEdgeName());
    try {
      // 查询本条关系是否存在
      ResultSet resultSet = graphEdgeMapper.getEdgeInfoExcel(graphRelationDO);
      boolean relation = true;
      if (!resultSet.getRows().isEmpty()) {
        if (StringUtil.isBlank(graphRelationDO.getEntityProperties())) {
          // 确保所需的列都存在
          List<String> columnNames = resultSet.getColumnNames();
          if (columnNames.contains(SpaceConstant.E)) {
            // 获取各列的值
            List<ValueWrapper> es = resultSet.colValues(SpaceConstant.E);
            // 假设所有列表的长度相同（这是处理这种情况的关键假设）
            for (int i = SpaceConstant.INDEX; i < es.size(); i++) {
              boolean flag = true;
              // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
              HashMap<String, ValueWrapper> name = es.get(i).asMap();
              for (Map.Entry<String, ValueWrapper> entry : name.entrySet()) {
                if (entry.getKey().equals(SpaceConstant.NAME)) {
                  continue;
                }
                if (!entry.getValue().isNull()) {
                  flag = false;
                }
              }
              if (!flag) {
                relation = false;
              }
            }
          }
        }
        if (!relation) {
          graphEdgeMapper.saveRelation(graphRelationDO);
        }
      } else {
        graphEdgeMapper.saveRelation(graphRelationDO);
      }
    } catch (ServiceException se) {

      log.error("【Error  save relation have one info: {}】", graphRelationDO.getEdgeName());
      throw se;
    } catch (Exception e) {
      log.error("【Error save relation for relation mast have property: {}】",
          graphRelationDO.getEdgeName());
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_RELATEION, e);
    }

  }


  @Override
  public List<NgVertex<String>> getEntityInfo(String s, String key, String tag) {
    log.info("【Kg-webserver-db get entity  for entity name  : {}】", s);
    try {
      return graphEdgeMapper.getEntityInfo(s, key, tag);

    } catch (Exception e) {
      log.error("【Error Kg-webserver-db get entity  for entity name: {}】", s);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public Integer getEdgeTotal(String s) {
    log.info("【Kg-webserver-db get edge total for space: {}】", s);
    try {
      return graphEdgeMapper.getEdgeTotal(s);
    } catch (Exception e) {
      log.error("【Error get edge total for space: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE, e);
    }
  }


  /**
   * 解析数据
   *
   * @param
   */
  private void analyProperties(GraphEdge graphEdge, Ralation ralation) {
    log.info("【Kg-webserver-db  drop  graph retional  process properties : {}】", ralation);

    String properties = "\"" + ralation.getSourceId() + "\"" + SpaceConstant.DIRECTION + "\""
        + ralation.getObjectId() + "\"";
    graphEdge.setEdgeName(ralation.getEdgeName());
    graphEdge.setProperties(properties);
  }
}
