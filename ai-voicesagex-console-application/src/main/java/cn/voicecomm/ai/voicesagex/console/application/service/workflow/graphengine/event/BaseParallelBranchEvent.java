package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基并行分支事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BaseParallelBranchEvent extends GraphEngineEvent {
    private String parallelId;
    private String parallelStartNodeId;
    private String parentParallelId;
    private String parentParallelStartNodeId;
    private String inIterationId;
    private String inLoopId;

}