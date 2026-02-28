package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.conditionhandler;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.GraphRuntimeState;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities.RouteNodeState;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 运行条件处理器抽象基类 定义了运行条件检查的基本接口和通用属性
 */
@Data
@NoArgsConstructor
public abstract class RunConditionHandler {

  /**
   * 初始化参数
   */
//    protected GraphInitParams initParams;
  public RunConditionHandler(GraphRun graph, RunCondition condition) {
    this.graph = graph;
    this.condition = condition;
  }

  /**
   * 图结构
   */
  protected GraphRun graph;

  /**
   * 运行条件
   */
  protected RunCondition condition;


  /**
   * 检查条件是否可以执行
   *
   * @param graphRuntimeState      图运行时状态
   * @param previousRouteNodeState 前一个路由节点状态
   * @return 是否可以执行
   */
  public abstract boolean check(GraphRuntimeState graphRuntimeState,
      RouteNodeState previousRouteNodeState);
}