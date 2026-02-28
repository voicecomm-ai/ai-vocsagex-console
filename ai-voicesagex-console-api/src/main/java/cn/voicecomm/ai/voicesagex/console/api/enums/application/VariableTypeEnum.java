package cn.voicecomm.ai.voicesagex.console.api.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangf
 * @date 2025/5/19 下午 4:34
 */
@Getter
@AllArgsConstructor
public enum VariableTypeEnum {

  TEXT("text", "文本"),

  PARAGRAPH("paragraph", "段落"),

  SELECT("select", "下拉选择"),

  NUMBER("number", "数字"),

  ;
  private final String key;

  private final String desc;

  public static String getDescByKey(String key) {
    for (VariableTypeEnum enumItem : VariableTypeEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }
}
