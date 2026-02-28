package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/8/21 15:30
 */
@Getter
public enum FailBranchSourceHandle {

  FAILED("fail-branch"),
  SUCCESS("success-branch");

  private final String value;

  FailBranchSourceHandle(String value) {
    this.value = value;
  }
}
