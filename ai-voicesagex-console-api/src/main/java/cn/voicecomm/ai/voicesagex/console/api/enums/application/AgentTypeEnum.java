package cn.voicecomm.ai.voicesagex.console.api.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangf
 * @date 2025/5/19 下午 4:34
 */
@Getter
@AllArgsConstructor
public enum AgentTypeEnum {

  // agent类型  single单个，multiple多个
  SINGLE("single", "单个"),

  MULTIPLE("multiple", "多个"),
  ;
  private final String key;

  private final String desc;

  public static String getDescByKey(String key) {
    for (AgentTypeEnum enumItem : AgentTypeEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }
}
