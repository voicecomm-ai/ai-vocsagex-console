package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

@Getter
public enum SystemVariableKey {

  /**
   * 查询内容
   */
  QUERY("query"),

  /**
   * 文件列表
   */
  FILES("files"),

  /**
   * 对话ID
   */
  CONVERSATION_ID("conversation_id"),

  /**
   * 用户ID
   */
  USER_ID("user_id"),

  /**
   * 对话计数
   */
  DIALOGUE_COUNT("dialogue_count"),

  /**
   * 应用ID
   */
  APP_ID("app_id"),

  /**
   * 工作流ID
   */
  WORKFLOW_ID("workflow_id"),

  /**
   * 工作流执行ID
   */
  WORKFLOW_EXECUTION_ID("workflow_run_id");

  private final String value;

  SystemVariableKey(String value) {
    this.value = value;
  }
}
