package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.DocumentSpaceTagDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphTagEdgePropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphTagEdgeProperty;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagEdgeIndex;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Set;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphTagManageMapper extends NebulaDaoBasic<GraphCreateTagEdge, String> {

  void createTag(@Param("tag") GraphCreateTagEdge tag);

  ResultSet getAllTags(@Param("tag") GraphCreateTagEdge tag);

  void dropTag(@Param("tag") GraphCreateTagEdge tag);

  void dropIndex(@Param("tag") GraphCreateTagEdge tag);

  ResultSet getTagInfo(@Param("tag") GraphCreateTagEdge tag);


  void createIndexDefault(@Param("index") TagEdgeIndex index);


  void alterTag(@Param("tag") GraphCreateTagEdge tag);

  void setTtl(@Param("tag") GraphCreateTagEdge tag);


  void dropTagEdgeProperty(@Param("tag") GraphTagEdgeProperty tag);

  void updateTagEdgeProperty(@Param("tag") GraphTagEdgePropertyDO tag);

  void dropTagEdgeTtl(@Param("tag") GraphTagEdgePropertyDO tag);

  ResultSet getPatternAllTags(@Param("spaceId") String spaceId);


  ResultSet getTagForId(@Param("spaceId") String spaceId, @Param("id") Set<String> id);


  List<NgVertex<String>> getVertex(@Param("spaceId") String spaceId, @Param("tag") String tag);


  Integer startInit(@Param("spaceId") String spaceId);

  void flushJob(@Param("spaceId") String spaceId);

  ResultSet showJobInfo(@Param("id") Integer id, @Param("spaceId") String spaceId);

  void stopJob(@Param("id") Integer id, @Param("spaceId") String spaceId);

  void createDocumentTag(@Param("doc") DocumentSpaceTagDO doc);

  void rebuildTagIndex(@Param("spaceId") String spaceId, @Param("tagName") String tagName);

  void rebuildEdgeIndex(@Param("spaceId") String spaceId, @Param("edgeName") String edgeName);
}