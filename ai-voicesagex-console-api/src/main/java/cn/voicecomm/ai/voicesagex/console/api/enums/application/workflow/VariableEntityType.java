package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.Getter;

@Getter
public enum VariableEntityType {
  TEXT_INPUT("text-input"),
  SELECT("select"),
  PARAGRAPH("paragraph"),
  NUMBER("number"),
  EXTERNAL_DATA_TOOL("external_data_tool"),
  FILE("file"),
  FILE_LIST("file-list");

  private final String value;

  VariableEntityType(String value) {
    this.value = value;
  }

  public static VariableEntityType fromValue(String value) {
    for (VariableEntityType type : VariableEntityType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown VariableEntityType: " + value);
  }
}
