package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.conditionhandler;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.GraphRuntimeState;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RouteNodeState;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ConditionProcessor;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ProcessConditionsResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 条件运行条件处理器 处理基于条件表达式的运行条件检查
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionRunConditionHandlerHandler extends RunConditionHandler {

  /**
   * 构造函数
   *
   * @param graph     图结构
   * @param condition 运行条件
   */
  public ConditionRunConditionHandlerHandler(GraphRun graph, RunCondition condition) {
    super(graph, condition);
  }

  /**
   * 检查条件是否可以执行
   *
   * @param graphRuntimeState      图运行时状态
   * @param previousRouteNodeState 前一个路由节点状态
   * @return 是否可以执行
   */
  @Override
  public boolean check(GraphRuntimeState graphRuntimeState, RouteNodeState previousRouteNodeState) {
    if (this.condition.getConditions() == null || this.condition.getConditions().isEmpty()) {
      return true;
    }

    // 处理条件
    ConditionProcessor conditionProcessor = new ConditionProcessor();
    ProcessConditionsResult result = conditionProcessor.processConditions(
        graphRuntimeState.getVariablePool(),
        this.condition.getConditions(),
        "and" // 默认使用and操作符
    );

    return result.finalResult();
  }
}