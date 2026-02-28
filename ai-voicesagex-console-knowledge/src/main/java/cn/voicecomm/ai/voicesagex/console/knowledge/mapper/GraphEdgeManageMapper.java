package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphTagEdgePropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphTagEdgeProperty;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagEdgeIndex;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphEdgeManageMapper extends NebulaDaoBasic<GraphCreateTagEdge, String> {

  void createEdge(@Param("edge") GraphCreateTagEdge edge);

  void createEdgeDocument(@Param("edge") String edge, @Param("spaceId") String spaceId);

  ResultSet getAllEdges(@Param("edge") GraphCreateTagEdge edge);

  void dropEdge(@Param("edge") GraphCreateTagEdge edge);

  void dropIndex(@Param("edge") GraphCreateTagEdge edge);

  ResultSet getEdgeInfo(@Param("edge") GraphCreateTagEdge edge);

  void createIndexDefault(@Param("index") TagEdgeIndex index);

  void alterEdge(@Param("edge") GraphCreateTagEdge edge);


  void setTtl(@Param("edge") GraphCreateTagEdge edge);


  void dropTagEdgeProperty(@Param("edge") GraphTagEdgeProperty edge);

  void updateTagEdgeProperty(@Param("edge") GraphTagEdgePropertyDO edge);


  void dropTagEdgeTtl(@Param("edge") GraphTagEdgePropertyDO tag);


  ResultSet getEdgesPattern(@Param("spaceId") String spaceId);


  List<NgEdge<String>> getEdgeForTag(@Param("spaceId") String spaceId, @Param("edge") String edge);

  String getTagForId(@Param("spaceId") String spaceId, @Param("id") String id);


}