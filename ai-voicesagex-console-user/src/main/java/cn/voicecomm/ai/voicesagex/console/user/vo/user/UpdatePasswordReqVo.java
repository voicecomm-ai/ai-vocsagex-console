package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordReqVo {

  /**
   * id
   */
  @NotNull(message = "id不能为空")
  private Integer id;

  /**
   * 旧密码
   */
  private String oldPassword;

  /**
   * 新密码
   */
  @NotNull(message = "新密码")
  private String firstPassword;

  /**
   * 确认密码
   */
  @NotNull(message = "确认密码")
  private String secondPassword;
}
