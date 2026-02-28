package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

@Getter
public enum DataType {
  FILE("file"),

  TEXT("text");

  private final String value;

  DataType(String value) {
    this.value = value;
  }
}
