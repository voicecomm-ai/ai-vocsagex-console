package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改密码
 */
@Data
public class UpdatePasswordVo {

  /**
   * 手机号
   */
  @NotNull(message = "手机号不能为空")
  private String phone;

  /**
   * 第一次输入的密码
   */
  @NotNull(message = "第一次输入的密码不能为空")
  String firstPassword;
  /**
   * 第二次输入的密码
   */
  @NotNull(message = "第二次输入的密码不能为空")
  String secondPassword;
}
