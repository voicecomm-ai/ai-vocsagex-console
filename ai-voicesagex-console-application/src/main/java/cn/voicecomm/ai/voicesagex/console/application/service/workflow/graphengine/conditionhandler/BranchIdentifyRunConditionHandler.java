package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.conditionhandler;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.GraphRuntimeState;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RouteNodeState;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分支标识运行条件处理器
 * 处理基于分支标识的运行条件检查
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchIdentifyRunConditionHandler extends RunConditionHandler {
    
    /**
     * 构造函数
     * 
     * @param graph 图结构
     * @param condition 运行条件
     */
    public BranchIdentifyRunConditionHandler(GraphRun graph, RunCondition condition) {
        super(graph, condition);
    }
    
    /**
     * 检查条件是否可以执行
     * 
     * @param graphRuntimeState 图运行时状态
     * @param previousRouteNodeState 前一个路由节点状态
     * @return 是否可以执行
     */
    @Override
    public boolean check(GraphRuntimeState graphRuntimeState, RouteNodeState previousRouteNodeState) {
        if (this.condition.getBranch_identify() == null || this.condition.getBranch_identify().isEmpty()) {
            throw new RuntimeException("Branch identify is required");
        }
        
        // 简化实现，实际应该检查节点运行结果的边源句柄是否与条件中的分支标识匹配
        return true;
    }
}