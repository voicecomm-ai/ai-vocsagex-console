package cn.voicecomm.ai.voicesagex.console.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ·判断是否枚举
 *
 * @author wangfan
 * @date 2023/3/31 15:02
 */
@Getter
@AllArgsConstructor
public enum WhetherEnum {

  /**
   * 否
   */
  FALSE(0, "否"),

  /**
   * 是
   */
  TURE(1, "是");

  private final Integer key;

  private final String desc;

  public static String getDescByKey(Integer key) {
    for (WhetherEnum enumItem : WhetherEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }

}
