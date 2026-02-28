package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RouteNodeState;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 基节点事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
public class BaseNodeEvent extends GraphEngineEvent {

  private String id;
  private String nodeId;
  private NodeType nodeType;
  private BaseNode nodeData;
  private RouteNodeState routeNodeState;
  private String parallelId;
  private String parallelStartNodeId;
  private String parentParallelId;
  private String parentParallelStartNodeId;
  private String inIterationId;
  private String inLoopId;
  private String nodeVersion = "1";


}