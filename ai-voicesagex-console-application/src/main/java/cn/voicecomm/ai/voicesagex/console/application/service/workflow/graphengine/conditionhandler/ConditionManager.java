package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.conditionhandler;


import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;

/**
 * 条件管理器
 * 负责根据运行条件类型获取对应的条件处理器
 */
public class ConditionManager {
    
    /**
     * 获取条件处理器
     * 根据运行条件的类型返回相应的条件处理器实例
     * 
     * @param initParams 初始化参数
     * @param graph 图结构
     * @param runCondition 运行条件
     * @return 条件处理器
     */
    public static RunConditionHandler getConditionHandler(
//            GraphInitParams initParams,
            GraphRun graph,
            RunCondition runCondition) {
        
        if ("branch_identify".equals(runCondition.getType())) {
            return new BranchIdentifyRunConditionHandler(graph, runCondition);
        } else {
            return new ConditionRunConditionHandlerHandler(graph, runCondition);
        }
    }
}