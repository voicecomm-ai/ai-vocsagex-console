package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

/**
 * @author: gaox
 * @date: 2025/7/31 14:41
 */

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 节点执行状态枚举
 *
 * 表示工作流中某个节点的当前执行状态。
 */
@Getter
@AllArgsConstructor
public enum WorkflowNodeExecutionStatus {

  /**
   * 执行中：节点正在运行
   */
  RUNNING("running"),

  /**
   * 成功：节点已成功完成执行
   */
  SUCCEEDED("succeeded"),

  /**
   * 失败：节点执行过程中发生错误
   */
  FAILED("failed"),

  /**
   * 异常：节点执行出现未捕获的异常
   */
  EXCEPTION("exception"),

  /**
   * 重试中：节点正在进行自动重试
   */
  RETRY("retry");

  private final String value;
}