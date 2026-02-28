package cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import java.util.List;
import java.util.Map;

/**
 * Space Manage
 */
public interface GraphSpaceManageService {

  /**
   * 创建图空间
   *
   * @param space
   */
  void createSpace(GraphCreateSpace space);


  /**
   * 获取图空间详细信息
   *
   * @return
   */
  List<String> detailSpace();


  /**
   * 清空图空间
   *
   * @param spaceName
   */
  void dropSpace(String spaceName);


  /**
   * 获取图空间详细信息
   *
   * @param spaceName
   * @return
   */
  Map<String, Object> descSpace(String spaceName);

  void createGraphPattern(Integer id);


}
