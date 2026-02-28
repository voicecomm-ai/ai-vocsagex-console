package cn.voicecomm.ai.voicesagex.console.api.enums.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wangf
 * @date 2025/5/19 下午 4:34
 */
@Getter
@AllArgsConstructor
public enum ApplicationTypeEnum {

  AGENT("agent", "智能体"),

  WORKFLOW("workflow", "工作流应用"),

  AGENT_ARRANGEMENT("agent_arrangement", "智能体编排应用"),

  ;
  private final String key;

  private final String desc;

  public static String getDescByKey(String key) {
    for (ApplicationTypeEnum enumItem : ApplicationTypeEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }
}
