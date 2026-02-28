package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import lombok.Getter;

@Getter
public enum FileType {
  CUSTOM("custom"),
  IMAGE("image"),
  VIDEO("video"),
  AUDIO("audio"),
  DOCUMENT("document");

  private final String value;

  FileType(String value) {
    this.value = value;
  }

  public static FileType fromValue(String value) {
    for (FileType type : FileType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    return null;
  }
}
