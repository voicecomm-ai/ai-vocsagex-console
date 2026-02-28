package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.ExpansionDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.QueryPathDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.SelectLikeDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.visual.VertexInfoDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.VisualInfoVector;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nebula.contrib.ngbatis.models.data.NgPath;
import org.nebula.contrib.ngbatis.models.data.NgSubgraph;
import org.nebula.contrib.ngbatis.models.data.NgVertex;


public interface GraphVisualService {


  List<VertexInfoDO> selectVertexInfo(SelectLikeDO selectLikeVO);

  /**
   * 随机获取点
   *
   * @param spaceId
   * @return
   */
  List<NgVertex<String>> getRandNumber(String spaceId);

  /**
   * entityId获取entity节点消息 通过
   *
   * @param entityId
   * @return
   */
  NgVertex<String> getEntityInfo(String entityId, String spaceId);


  /**
   * 获取一跳三元组信息
   *
   * @param entityId
   * @param mapTagEdges
   * @param spaceId
   * @return
   */
  List<VisualInfo> getOneStepsInfo(String entityId, List<String> mapTagEdges, String spaceId,
      Integer step);

  List<VisualInfoVector> getOneStepsInfoVector(String entityId, List<String> mapTagEdges,
      String spaceId, Integer step);

  List<VisualInfo> getOneStepsInfoSemantic(String entityId, List<String> mapTagEdges,
      String spaceId, Integer step, Boolean direction);


  /**
   * 获取tagName
   *
   * @param ids
   * @param spaceId
   * @return
   */
  Map<String, Map<String, List<String>>> getTagNameMap(Set<String> ids, String spaceId)
      throws UnsupportedEncodingException;


  NgVertex<String> singleVertexInfo(String spaceId, String vertexId);


  List<NgSubgraph<String>> queryFullGraph(String spaceId, String vertexId);

  List<NgSubgraph<String>> queryFullGraphNext(String spaceId, String vertexId);


  List<NgSubgraph<String>> expansionNode(ExpansionDO expansionDO);


  List<NgPath<String>> queryPath(QueryPathDO queryPathDO);
}
