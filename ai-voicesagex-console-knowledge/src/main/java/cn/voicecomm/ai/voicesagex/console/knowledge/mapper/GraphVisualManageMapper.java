package cn.voicecomm.ai.voicesagex.console.knowledge.mapper;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.ExpansionDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.QueryPathDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.SelectLikeDO;
import com.vesoft.nebula.client.graph.data.ResultSet;
import java.util.List;
import java.util.Set;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.nebula.contrib.ngbatis.proxy.NebulaDaoBasic;
import org.springframework.data.repository.query.Param;

public interface GraphVisualManageMapper extends NebulaDaoBasic<Object, String> {

  List<NgVertex<String>> getRandNumber(@Param("spaceId") String spaceId);


  NgVertex<String> getEntityInfo(@Param("entityId") String entityId,
      @Param("spaceId") String spaceId);


  ResultSet getOneStepsInfo(@Param("entityId") String entityId, @Param("es") String es,
      @Param("spaceId") String spaceId, @Param("step") Integer step);

  ResultSet getOneStepsInfoReverse(@Param("entityId") String entityId, @Param("es") String es,
      @Param("spaceId") String spaceId, @Param("step") Integer step);

  ResultSet getOneStepsInfoForward(@Param("entityId") String entityId, @Param("es") String es,
      @Param("spaceId") String spaceId, @Param("step") Integer step);

  ResultSet getOneStepsInfoVector(@Param("entityId") String entityId, @Param("es") String es,
      @Param("spaceId") String spaceId, @Param("step") Integer step);


  ResultSet getTagNameMap(@Param("ids") Set<String> ids, @Param("spaceId") String spaceId);


  ResultSet selectVertexInfo(@Param("like") SelectLikeDO like);


  NgVertex<String> singleVertexInfo(@Param("spaceId") String spaceId,
      @Param("vertexId") String vertexId);

  List<NgSubgraph<String>> queryFullGraph(@Param("spaceId") String spaceId,
      @Param("vertexId") String vertexId);

  List<NgSubgraph<String>> expansionNode(@Param("ex") ExpansionDO expansionDO);

  List<NgPath<String>> queryPath(@Param("qu") QueryPathDO qu);

  List<NgSubgraph<String>> queryFullGraphNext(@Param("spaceId") String spaceId,
      @Param("vertexId") String vertexId);
}
