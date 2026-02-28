package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * RunCompletedEvent - 节点运行完成事件
 */
@Data
@SuperBuilder
public class NodeRunCompletedEvent{
  private NodeRunResult runResult;

}
