package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基循环事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BaseLoopEvent extends GraphEngineEvent {

  // Getters and Setters
  private String loopId;
  private String loopNodeId;
  private NodeType loopNodeType;
  private BaseNode loopNodeData;
  private String parallelId;
  private String parallelStartNodeId;
  private String parentParallelId;
  private String parentParallelStartNodeId;
  private String parallelModeRunId;

}