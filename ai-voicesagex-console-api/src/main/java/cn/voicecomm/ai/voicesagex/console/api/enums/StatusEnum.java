package cn.voicecomm.ai.voicesagex.console.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ·状态枚举
 *
 * @author wangf
 * @date 2025/2/18 下午 2:00
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

  /**
   * 否
   */
  DISABLE(0, "禁用"),

  /**
   * 是
   */
  ENABLE(1, "启用");

  private final Integer key;

  private final String desc;

  public static String getDescByKey(Integer key) {
    for (StatusEnum enumItem : StatusEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }

}
