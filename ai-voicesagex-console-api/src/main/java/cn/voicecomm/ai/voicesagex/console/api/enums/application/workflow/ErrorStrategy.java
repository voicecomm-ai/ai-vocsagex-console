package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/8/21 15:29
 */
@Getter
public enum ErrorStrategy {
  FAIL_BRANCH("fail-branch"),
  DEFAULT_VALUE("default-value");

  private final String value;

  ErrorStrategy(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
