package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基迭代事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BaseIterationEvent extends GraphEngineEvent {
    private String iterationId;
    private String iterationNodeId;
    private NodeType iterationNodeType;
    private BaseNode iterationNodeData;
    private String parallelId;
    private String parallelStartNodeId;
    private String parentParallelId;
    private String parentParallelStartNodeId;
    private String parallelModeRunId;

}