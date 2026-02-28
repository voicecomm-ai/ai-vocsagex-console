package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/11/19 13:47
 */
@Getter
public enum InputType {

  /**
   * 变量类型
   */
  VARIABLE("variable"),

  /**
   * 常量类型
   */
  CONSTANT("constant");

  private final String value;

  InputType(String value) {
    this.value = value;
  }

}
