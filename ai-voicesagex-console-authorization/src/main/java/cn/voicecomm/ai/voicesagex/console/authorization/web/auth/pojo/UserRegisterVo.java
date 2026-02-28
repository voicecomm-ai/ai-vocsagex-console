package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class UserRegisterVo {

  @NotNull(message = "请输入手机号")
  private String phone;

  @NotNull(message = "请输入验证码")
  private String smsCode;
}
