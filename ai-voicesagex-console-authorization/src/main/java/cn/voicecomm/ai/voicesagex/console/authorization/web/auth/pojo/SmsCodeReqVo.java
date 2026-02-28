package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SmsCodeReqVo {

  /**
   * 手机号
   */
  @NotNull(message = "手机号不能为空")
  private String phone;

  /**
   * 短信验证码类型 1 注册，2 更换密码，3 更换手机号，4 注销账号，5 忘记密码，6 新手机
   */
  private Integer smsCodeType;
}
