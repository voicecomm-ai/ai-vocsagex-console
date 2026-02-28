package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.impl;

import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphVertexMapper;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphVertexDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GraphVertexServiceImpl implements GraphVertexService {


  @Autowired
  private GraphVertexMapper graphVertexMapper;


  @Override
  public void deleteVertex(GraphVertexDropDO graphVertexDropDO) {
    log.info("【Kg-webserver-db  delete  graph verteies for space:{}】",
        graphVertexDropDO.getSpace());
    try {
      graphVertexMapper.deleteVertex(graphVertexDropDO);
    } catch (Exception e) {
      log.error("【Error delete vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERTEX_NAME_DELETE, e);
    }
  }

  @Override
  public void deleteVertexInfo(String spaceId, String ids) {
    log.info("【Kg-webserver-db  delete  graph All verteies for space:{}】", spaceId);
    try {
      graphVertexMapper.deleteVertexAll(spaceId, ids);
    } catch (Exception e) {
      log.error("【Error delete vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERTEX_NAME_DELETE, e);
    }
  }

  @Override
  public void deleteVertexSingle(String spaceId, String id) {
    log.info("【Kg-webserver-db  delete  graph  vertex for space:{}】", spaceId);
    try {
      graphVertexMapper.deleteVertexSingle(spaceId, id);
    } catch (Exception e) {
      log.error("【Error delete vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERTEX_NAME_DELETE, e);
    }
  }

  @Override
  public void saveEntity(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  save  graph vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      graphVertexMapper.saveEntity(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error save vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_VERTEX, e);
    }
  }

  @Override
  public void saveEntityImport(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  save  graph vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      graphVertexMapper.saveEntityImport(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error save vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_VERTEX, e);
    }
  }

  @Override
  public void saveEntityBath(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  save  graph vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      graphVertexMapper.saveEntityBath(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error save vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_VERTEX, e);
    }
  }

  @Override
  public void updateEntity(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  save  graph vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      graphVertexMapper.updateEntity(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error update vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_VERTEX, e);
    }
  }

  @Override
  public NgVertex getEntity(String entityId, String spaceId) {
    log.info("【Kg-webserver-db  get  graph vertex for space:{}】", spaceId);
    try {
      return graphVertexMapper.getEntity(entityId, spaceId);
    } catch (Exception e) {
      log.error("【Error get vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public String getDocumentName(String spaceId, String edgeName, String value) {
    log.info("【Kg-webserver-db  get  graph vertex for space:{}】", spaceId);
    try {
      return graphVertexMapper.getDocumentName(spaceId, edgeName, value);
    } catch (Exception e) {
      log.error("【Error get vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public List<NgVertex<String>> getEntitySet(Set<String> entitys, String spaceId) {
    log.info("【Kg-webserver-db  get  graph vertexs for space:{}】", spaceId);
    try {

      return graphVertexMapper.getEntitySet(entitys, spaceId);
    } catch (Exception e) {
      log.error("【Error get vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY, e);
    }
  }

  @Override
  public List<NgVertex<String>> getNgvertexs(String spaceId) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", spaceId);
    try {
      return graphVertexMapper.getNgvertexs(spaceId);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public List<NgVertex<String>> getVertexesByTagName(String spaceId, String tagName,
      String entityName) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", spaceId);
    try {
      return graphVertexMapper.getVertexesByTagName(spaceId, tagName, entityName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }


  @Override
  public List<NgVertex<String>> getVertexes(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      if (SpaceConstant.INDEX != graphEntityDO.getCurrent()) {
        graphEntityDO.setCurrent(graphEntityDO.getCurrent() * graphEntityDO.getPageSize());
      }
      graphEntityDO.setEntityName(graphEntityDO.getEntityName()
          .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
      if (StringUtil.isBlank(graphEntityDO.getTagName())) {
        return graphVertexMapper.getAllNgvertexs(graphEntityDO);
      }
      return graphVertexMapper.getAllNgvertexsByName(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }


  @Override
  public List<NgVertex<String>> getSelectNgvertexs(String spaceId, List<String> ids) {
    log.info("【Kg-webserver-db  get select  vertex for space:{}】", spaceId);
    try {
      return graphVertexMapper.getSelectNgvertexs(spaceId, ids);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public int getNumber(String s, String tagName) {
    log.info("【Kg-webserver-db  get   vertex number for space:{}】", s);
    try {
      return graphVertexMapper.getNumber(s, tagName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_NUMBER, e);
    }
  }

  @Override
  public NgVertex<String> getVertexLimit(String spaceId) {
    log.info("【Kg-webserver-db  get   vertex limit 1 for space:{}】", spaceId);

    try {
      return graphVertexMapper.getVertexLimit(spaceId);
    } catch (Exception e) {
      log.error("【Error get limit  vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_NUMBER, e);
    }
  }

  @Override
  public ResultSet getShowStuta(String spaceId) {
    log.info("【Kg-webserver-db  space node info number for space:{}】", spaceId);
    try {
      return graphVertexMapper.getShowStuta(spaceId);
    } catch (Exception e) {
      log.error("【Error get space node info number for space: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SPACE_INFO_ALL, e);
    }
  }

  @Override
  public ResultSet selectLikeEntity(String spaceId, String tagName, String entityName) {
    log.info("【Kg-webserver-db  select like entityName for space:{}】", spaceId);
    try {
      return graphVertexMapper.selectLikeEntity(spaceId, tagName, entityName);
    } catch (Exception e) {
      log.error("【Error get space node info number for space: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SPACE_INFO_ALL, e);
    }
  }

  @Override
  public Integer getEntityTotal(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  select   for space total:{}】", graphEntityDO.getSpaceId());
    try {
      if (StringUtil.isBlank(graphEntityDO.getTagName())) {
        return graphVertexMapper.getEntityTotal(graphEntityDO);
      }
      return graphVertexMapper.getEntityTotalByTagName(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error get select   for space total: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SPACE_INFO_ALL, e);
    }
  }

  @Override
  public Integer getEntityTotal(String spaceId) {
    log.info("【Kg-webserver-db  select   for space total:{}】", spaceId);
    try {
      return graphVertexMapper.getEntityTotalBySpaceId(spaceId);
    } catch (Exception e) {
      log.error("【Error get select   for space total: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SPACE_INFO_ALL, e);
    }
  }

  @Override
  public List<NgVertex<String>> getNgvertexsExport(String s, String entityName) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", s);
    try {
      return graphVertexMapper.getNgvertexsExport(s, entityName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public List<NgVertex<String>> getNgvertexsByName(String s, String tagName, String entityName) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", s);
    try {
      return graphVertexMapper.getNgvertexsByName(s, tagName, entityName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public ResultSet getEntityTotalByProperty(String s, String tagName, String propertyName) {
    try {
      return graphVertexMapper.getEntityTotalByProperty(s, tagName, propertyName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public ResultSet getEntityTotalByAllProperty(String s, String tagName) {
    try {
      return graphVertexMapper.getEntityTotalByAllProperty(s, tagName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public List<NgVertex<String>> getVertexesByTagNameExport(String s, String tagEdgeName,
      String entityName) {
    log.info("【Kg-webserver-db  get all vertex for space:{}】", s);
    try {
      return graphVertexMapper.getVertexesByTagNameExport(s, tagEdgeName, entityName);
    } catch (Exception e) {
      log.error("【Error get All vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_ENTITY_ALL, e);
    }
  }

  @Override
  public void saveEntityFusion(GraphEntityDO graphEntityDO) {
    log.info("【Kg-webserver-db  save  graph vertex for space:{}】", graphEntityDO.getSpaceId());
    try {
      graphVertexMapper.saveEntityFusion(graphEntityDO);
    } catch (Exception e) {
      log.error("【Error save vertex with message: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.SAVE_VERTEX, e);
    }
  }

  @Override
  public Integer executeTheTask(String s) {
    log.info("【执行统计节点数量任务:{}】", s);
    try {
      graphVertexMapper.executeTheTask(s);
      Thread.sleep(500);
      return 1;
    } catch (Exception e) {
      log.error("【执行统计节点数量任务失败: {}】", e.getMessage(), e);
    }
    return 0;
  }

  @Override
  public ResultSet getStatsInfo(String s) {
    log.info("【获取图空间详细信息:{}】", s);
    ResultSet set = null;
    try {
      set = graphVertexMapper.getStatsInfo(s);
    } catch (Exception e) {
      log.error("【获取图空间详细信息: {}】", e.getMessage(), e);
    }

    return set;
  }


}
