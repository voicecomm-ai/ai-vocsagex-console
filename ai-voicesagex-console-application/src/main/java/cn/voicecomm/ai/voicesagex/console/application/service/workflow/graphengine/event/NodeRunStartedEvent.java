package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeRunStartedEvent extends BaseNodeEvent {

  private String predecessorNodeId;
  private String parallelModeRunId;
//    private AgentNodeStrategyInit agentStrategy;


}