package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphVertexDropDO;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Set;
import org.nebula.contrib.ngbatis.models.data.NgVertex;

public interface GraphVertexService {

  void deleteVertex(GraphVertexDropDO graphVertexDropDO);

  void deleteVertexInfo(String spaceId, String ids);

  void deleteVertexSingle(String spaceId, String id);


  void saveEntity(GraphEntityDO graphEntityDO);

  void saveEntityImport(GraphEntityDO graphEntityDO);

  void saveEntityBath(GraphEntityDO graphEntityDO);

  void updateEntity(GraphEntityDO graphEntityDO);


  NgVertex<String> getEntity(String entityId, String spaceId);

  String getDocumentName(String spaceId, String edgeName, String value);

  List<NgVertex<String>> getEntitySet(Set<String> entitys, String spaceId);

  List<NgVertex<String>> getNgvertexs(String spaceId);

  List<NgVertex<String>> getVertexesByTagName(String spaceId, String tagName, String entityName);


  List<NgVertex<String>> getVertexes(GraphEntityDO graphEntityDO);


  List<NgVertex<String>> getSelectNgvertexs(String spaceId, List<String> ids);


  int getNumber(String s, String tagName);


  NgVertex<String> getVertexLimit(String spaceId);

  ResultSet getShowStuta(String spaceId);


  ResultSet selectLikeEntity(String spaceId, String tagName, String entityName);

  Integer getEntityTotal(GraphEntityDO graphEntityDO);

  Integer getEntityTotal(String spaceId);

  List<NgVertex<String>> getNgvertexsExport(String s, String entityName);

  ResultSet getEntityTotalByProperty(String s, String tagName, String propertyName);

  ResultSet getEntityTotalByAllProperty(String s, String tagName);

  List<NgVertex<String>> getNgvertexsByName(String s, String tagName, String entityName);

  List<NgVertex<String>> getVertexesByTagNameExport(String s, String tagEdgeName,
      String entityName);

  void saveEntityFusion(GraphEntityDO graphEntityDO);

  Integer executeTheTask(String s);

  ResultSet getStatsInfo(String s);

//    showStats(String spaceId);


}
