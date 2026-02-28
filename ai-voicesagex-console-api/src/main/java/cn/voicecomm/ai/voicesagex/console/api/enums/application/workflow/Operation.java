package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/11/19 13:45
 */
@Getter
public enum Operation {

  /**
   * 覆写
   */
  OVER_WRITE("over-write"),

  /**
   * 清除
   */
  CLEAR("clear"),

  /**
   * 追加
   */
  APPEND("append"),

  /**
   * 扩展
   */
  EXTEND("extend"),

  /**
   * 设置
   */
  SET("set"),

  /**
   * 加法
   */
  ADD("+="),

  /**
   * 减法
   */
  SUBTRACT("-="),

  /**
   * 乘法
   */
  MULTIPLY("*="),

  /**
   * 除法
   */
  DIVIDE("/="),

  /**
   * 移除第一个元素
   */
  REMOVE_FIRST("remove-first"),

  /**
   * 移除最后一个元素
   */
  REMOVE_LAST("remove-last");

  private final String value;

  Operation(String value) {
    this.value = value;
  }
  // 根据value值获取type
  public static Operation getByValue(String value) {
    for (Operation operation : Operation.values()) {
      if (operation.value.equals(value)) {
        return operation;
      }
    }
    return null;
  }
}
