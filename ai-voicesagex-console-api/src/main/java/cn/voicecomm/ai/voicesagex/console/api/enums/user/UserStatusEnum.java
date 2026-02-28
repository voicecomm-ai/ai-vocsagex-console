package cn.voicecomm.ai.voicesagex.console.api.enums.user;

import lombok.Getter;

/**
 * @author gml
 * @date 2024/5/30 10:40
 */
@Getter
public enum UserStatusEnum {

  NORMAL((byte) 0, "正常"),
  DISABLED((byte) 1, "禁用"),
  DELETED((byte) 2, "删除"),
  ;
  private final byte code;

  private final String desc;

  UserStatusEnum(byte code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
