package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * State of a node or edge during workflow execution.
 *
 * @author gaox
 */
@Getter
public enum NodeState {

  /**
   * 未知状态
   */
  UNKNOWN("unknown"),

  /**
   * 已执行状态
   */
  TAKEN("taken"),

  /**
   * 已跳过状态
   */
  SKIPPED("skipped");

  private final String value;

  NodeState(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
