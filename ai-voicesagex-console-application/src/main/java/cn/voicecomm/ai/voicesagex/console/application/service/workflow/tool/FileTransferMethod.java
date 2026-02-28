package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import lombok.Getter;

@Getter
public enum FileTransferMethod {
  LOCAL_FILE("local_file"),
  REMOTE_URL("remote_url"),
  TOOL_FILE("tool_file");

  private final String value;

  FileTransferMethod(String value) {
    this.value = value;
  }

  public static FileTransferMethod fromValue(String value) {
    for (FileTransferMethod method : FileTransferMethod.values()) {
      if (method.value.equalsIgnoreCase(value)) {
        return method;
      }
    }
    return null;
  }

}
