package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件属性枚举
 */
@Getter
@AllArgsConstructor
public enum FileAttribute {
  /**
   * 文件类型
   */
  TYPE("type"),

  /**
   * 文件大小
   */
  SIZE("size"),

  /**
   * 文件名
   */
  NAME("name"),

  /**
   * MIME类型
   */
  MIME_TYPE("mime_type"),

  /**
   * 传输方法
   */
  TRANSFER_METHOD("transfer_method"),

  /**
   * 文件URL
   */
  URL("url"),

  /**
   * 文件扩展名
   */
  EXTENSION("extension"),

  /**
   * 关联ID
   */
  RELATED_ID("related_id");

  private final String value;

  public static boolean contains(String value) {
    return Arrays.stream(values())
        .anyMatch(attr -> attr.value.equals(value));
  }
  public static FileAttribute fromValue(String value) {
    return Arrays.stream(values())
        .filter(attr -> attr.value.equals(value))
        .findFirst()
        .orElse(null);
  }
}
