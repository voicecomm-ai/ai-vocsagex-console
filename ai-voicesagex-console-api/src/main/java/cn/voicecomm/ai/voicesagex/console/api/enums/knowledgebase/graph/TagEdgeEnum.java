package cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型枚举
 *
 * @author ryc
 * @date 2025/6/4
 */
@Getter
@AllArgsConstructor
public enum TagEdgeEnum {
  ;

  @Getter
  @AllArgsConstructor
  public enum TypeEnum {

    /**
     * 本体关系
     */
    TAG(0, "本体"),

    EDGE(1, "关系"),
    ;

    private final Integer key;

    private final String desc;

    public static String getDescByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem.getDesc();
        }
      }
      return "";
    }

    public static TypeEnum getEnumByKey(Integer key) {
      for (TypeEnum enumItem : TypeEnum.values()) {
        if (enumItem.getKey().equals(key)) {
          return enumItem;
        }
      }
      return null;
    }
  }

}
