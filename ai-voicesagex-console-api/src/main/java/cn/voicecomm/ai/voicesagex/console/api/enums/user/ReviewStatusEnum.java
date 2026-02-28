package cn.voicecomm.ai.voicesagex.console.api.enums.user;

import lombok.Getter;

/**
 * @author gml
 * @date 2024/5/30 10:40
 */
@Getter
public enum ReviewStatusEnum {

  WAITING(0, "待审核"),
  SUCCESS(1, "审核通过"),
  FAILED(2, "审核驳回"),
  ;
  private final int code;

  private final String desc;

  ReviewStatusEnum(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static String getDescByKey(Integer key) {
    for (ReviewStatusEnum enumItem : ReviewStatusEnum.values()) {
      if (enumItem.getCode() == key) {
        return enumItem.getDesc();
      }
    }
    return "";
  }
}
