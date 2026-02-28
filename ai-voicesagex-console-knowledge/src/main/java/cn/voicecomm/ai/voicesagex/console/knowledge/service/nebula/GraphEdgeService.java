package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;


import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEdgeDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationSingleDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.LimitEdgeDO;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;


public interface GraphEdgeService {


  void deleteRelation(GraphEdgeDropDO graphEdgeDropDO);


  void saveSubject(GraphRelationEntityDO graphRelationEntityDO);


  void saveObject(GraphRelationEntityDO graphRelationEntityDO);


  void saveRelation(GraphRelationDO graphRelationDO, int type);

  void saveRelationExcel(GraphRelationDO graphRelationDO);


  List<NgVertex<String>> getObjectInfo(GraphEntityRelationDO graphEntityRelationDO);

  NgVertex<String> getObjectInfoSingle(GraphEntityRelationSingleDO graphEntityRelationDO);

  List<NgVertex<String>> getSubjectInfo(GraphEntityRelationDO graphEntityRelationDO);

  NgVertex<String> getSubjectInfoSingle(GraphEntityRelationSingleDO graphEntityRelationDO);


  List<NgEdge<String>> getAllEdge(GraphEdgeDropDO graphEdgeDropDO);

  List<NgEdge<String>> getVertexesLimit(LimitEdgeDO limitEdgeDO);


  List<NgVertex<String>> getList(String spaceId, String tagName);


  Long getNumber(String edgeName, String spaceId);

  int getAllNumber(String spaceId);

  NgEdge<String> getEdgeInfo(GraphRelationDO graphRelationDO);


  void updateRelation(GraphRelationDO graphRelationDO);


  Integer getSumEdge(LimitEdgeDO limitEdgeDO);

  Integer getSumEdgeByEdge(LimitEdgeDO limitEdgeDO);

  List<NgEdge<String>> getExportAllData(String spaceId, String tagEdgeName, String entityName,
      String subjectTagName, String objectTagName);

  ResultSet getEntityTotalByProperty(String s, String edgeName, String propertyName);

  void saveRelationFusion(GraphRelationDO graphRelationDO, int index);

  List<NgVertex<String>> getEntityInfo(String s, String key, String tag);

  Integer getEdgeTotal(String s);
}
