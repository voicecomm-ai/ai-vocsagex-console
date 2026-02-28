package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEdgeDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationSingleDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.LimitEdgeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.BatchVerificationRelation;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphEdge;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphEdgeMapper extends NebulaDaoBasic<GraphEdge, String> {

  void dropRelation(@Param("re") GraphEdge re);

  void saveRelation(@Param("re") GraphRelationDO re);


  void saveRelationBatch(@Param("re") GraphRelationDO re);

  void batchSaveRelation(@Param("re") BatchVerificationRelation re);


  List<NgVertex<String>> getObjects(@Param("tx") GraphEntityRelationDO tx);

  List<NgVertex<String>> getSubjects(@Param("tx") GraphEntityRelationDO tx);

  List<NgVertex<String>> getAllEntity(@Param("spaceId") String spaceId,
      @Param("tagName") String tagName);


  List<NgEdge<String>> getAllEdge(@Param("er") GraphEdgeDropDO er);


  List<NgEdge<String>> getVertexesLimit(@Param("er") LimitEdgeDO er);

  List<NgEdge<String>> getVertexesLimitByEdgeByName(@Param("er") LimitEdgeDO er);

  List<NgEdge<String>> getVertexesLimitByEdge(@Param("er") LimitEdgeDO er);


  Long getNumber(@Param("spaceId") String spaceId, @Param("edgeName") String edgeName);

  List<NgEdge<String>> getAllNumber(@Param("spaceId") String spaceId);

  List<NgEdge<String>> getEdges(@Param("spaceId") String spaceId,
      @Param("edgeName") String edgeName, @Param("entity") String entity);

  NgEdge<String> getEdgeInfo(@Param("re") GraphRelationDO re);

  ResultSet getEdgeInfoExcel(@Param("re") GraphRelationDO re);

  NgEdge<String> getEdgeInfoDocument(@Param("re") GraphRelationDO re);


  void updateRelation(@Param("re") GraphRelationDO re);


  Integer getSumEdge(@Param("re") LimitEdgeDO re);

  Integer getSumEdgeByEdge(@Param("re") LimitEdgeDO limitEdgeDO);

  List<NgEdge<String>> getExportAllData(@Param("spaceId") String spaceId,
      @Param("entityName") String entityName, @Param("subjectTagName") String subjectTagName,
      @Param("objectTagName") String objectTagName);

  List<NgEdge<String>> getExportAllDataByEdgeName(@Param("spaceId") String spaceId,
      @Param("tagEdgeName") String tagEdgeName, @Param("entityName") String entityName,
      @Param("subjectTagName") String subjectTagName, @Param("objectTagName") String objectTagName);

  ResultSet getEntityTotalByProperty(@Param("spaceId") String spaceId,
      @Param("edgeName") String edgeName, @Param("propertyName") String propertyName);

  NgVertex<String> getObjectsSingle(@Param("tx") GraphEntityRelationSingleDO tx);

  NgVertex<String> getSubjectsSingle(@Param("tx") GraphEntityRelationSingleDO tx);

  List<NgVertex<String>> getEntityInfo(@Param("name") String name, @Param("spaceId") String spaceId,
      @Param("tag") String tag);

  ResultSet screenTag(@Param("spaceId") String spaceId, @Param("edgeName") String edgeName);

  ResultSet screenTagModel(@Param("spaceId") String spaceId, @Param("edgeName") String edgeName);

  ResultSet screenTagObject(@Param("spaceId") String spaceId, @Param("edgeName") String edgeName);

  void saveRelationNoRank(@Param("re") GraphRelationDO graphRelationDO);

  Integer getEdgeTotal(@Param("spaceId") String spaceId);

  ResultSet getAllNgEdge(@Param("spaceId") String spaceId, @Param("entityId") String entityId);
}
