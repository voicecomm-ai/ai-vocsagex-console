package cn.voicecomm.ai.voicesagex.console.api.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangf
 * @date 2025/5/19 下午 4:34
 */
@Getter
@AllArgsConstructor
public enum ApplicationStatusEnum {

  DELETED("delete", "删除"),

  DRAFT("draft", "草稿"),

  PUBLISHED("published", "已发布"),

  EXPERIENCE("experience", "上架"),

  ;
  private final String key;

  private final String desc;

  public static String getDescByKey(String key) {
    for (ApplicationStatusEnum enumItem : ApplicationStatusEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }
}
