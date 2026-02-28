package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphTagEdgePropertyDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.TagResultDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateTagEdge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphTagEdgeProperty;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.NgTagEdge;
import java.util.List;

/**
 * Tag / Edge Manage
 */
public interface GraphTagEdgeManageService {

  /**
   * 创建 Tag / Edge
   *
   * @param
   */
  void createTagEdge(GraphCreateTagEdge graphCreateTagEdge);


  /**
   * 添加 Tag / Edge 属性
   */

  void insertTagEdgeProperties(GraphCreateTagEdge graphCreateTagEdge);


  /**
   * 设置 Ttl 属性
   */
  void setTtlProperties(GraphCreateTagEdge graphCreateTagEdge);

  /**
   * 清空 Tag / Edge
   *
   * @param
   */
  void dropTagEdge(GraphCreateTagEdge graphCreateTagEdge);


  /**
   * 清空索引
   */
  void dropIndex(GraphCreateTagEdge graphCreateTagEdge);

  /**
   * 获取 Tag / Edge 列表信息
   *
   * @param
   * @return
   */

  List<NgTagEdge> getTagEdgeInfo(GraphCreateTagEdge graphCreateTagEdge, int pageSize,
      int currentPage);

  /**
   * 获取所有Tag / Edge 列表
   *
   * @param graphCreateTagEdge
   * @return
   */

  List<String> getTagEdges(GraphCreateTagEdge graphCreateTagEdge);


  /**
   * 修改 Tag / Edge 名称
   *
   * @param
   */
  void dropTagEdgeProperty(GraphTagEdgeProperty tagEdgeProperty);

  /**
   * 更新属性
   *
   * @param
   */
  void updateGraphProperty(GraphTagEdgePropertyDO graphCreateTagEdge);

  /**
   * 删除 TTl
   *
   * @param
   */
  void dropTagEdgeTtl(GraphTagEdgePropertyDO graphCreateTagEdge);


  /**
   * 模型可视化接口
   *
   * @param spaceId
   */
  TagResultDO showPattern(String spaceId);

  /**
   * init
   *
   * @param spaceId
   */
  Integer startInit(String spaceId);


  /**
   * 刷新job
   *
   * @param spaceId
   */
  void flushJob(String spaceId);

  boolean showJobInfo(Integer id, String spaceId);

  void rebuildTagIndex(String s, String tagName);

  void rebuildEdgeIndex(String s, String tagName);

  void saveGraph(TagResultDO tagResultDO, Integer spaceId);
}
