package cn.voicecomm.ai.voicesagex.console.api.enums.user;

import lombok.Getter;

/**
 * ·数据权限类型枚举
 *
 * @author wangf
 * @date 2024/10/22 下午 2:41
 */
@Getter
public enum DataPermissionEnum {

  ALL(0, "超级管理员"),
  DEPARTMENT_AND_SUBORDINATE(1, "本部门（含下级）"),
  DEPARTMENT(2, "本部门"),
  ONLY_SELF(3, "仅本人");

  private Integer key;

  private String permissionDesc;

  DataPermissionEnum(Integer key, String permissionDesc) {
    this.key = key;
    this.permissionDesc = permissionDesc;
  }

}
