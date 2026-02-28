package cn.voicecomm.ai.voicesagex.console.knowledge.handle;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.GraphCreateSpace;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphSpaceManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ryc
 * @description
 * @date 2025/9/10 17:35
 */
@Component
public class KnowledgeBaseHandler {

  @Autowired
  private GraphSpaceManageService graphSpaceManageService;

  public void createNebulaSpace(String space) {
    // 拼接 space 并调用 graphSpaceManageService
    graphSpaceManageService.createSpace(GraphCreateSpace.builder().space(space).build());
  }

}
