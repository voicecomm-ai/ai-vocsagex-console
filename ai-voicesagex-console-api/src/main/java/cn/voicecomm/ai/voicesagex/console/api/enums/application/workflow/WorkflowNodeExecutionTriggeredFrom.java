package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点执行触发来源枚举 表示工作流中某个节点执行的触发来源。
 *
 * @author: gaox
 * @date: 2025/8/4 15:17
 */
@Getter
@AllArgsConstructor
public enum WorkflowNodeExecutionTriggeredFrom {

  /**
   * 单步执行：节点被单独触发运行（例如在调试模式下单节点运行）
   */
  SINGLE_STEP("single-step"),

  /**
   * 工作流运行：节点作为整个工作流执行流程的一部分被触发
   */
  WORKFLOW_RUN("workflow-run");

  /**
   * 枚举对应的字符串值，用于序列化和持久化
   */
  private final String value;
}
