package cn.voicecomm.ai.voicesagex.console.api.enums.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleDataPermissionEnum {

  DEPARTMENT_AND_SUB(1, "本部门（含下级）"),

  ONLY_DEPARTMENT(2, "本部门"),

  ONLY_SELF(3, "仅本人");

  private final Integer key;

  private final String desc;

  public static String getDescByKey(Integer key) {
    for (RoleDataPermissionEnum enumItem : RoleDataPermissionEnum.values()) {
      if (enumItem.getKey().equals(key)) {
        return enumItem.getDesc();
      }
    }
    return "";
  }

}
