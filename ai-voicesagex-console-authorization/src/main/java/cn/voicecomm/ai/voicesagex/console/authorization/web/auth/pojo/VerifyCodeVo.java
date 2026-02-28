package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class VerifyCodeVo {

  /**
   * 手机号
   */
  private String phone;

  /**
   * 验证码类型
   */
  private Integer smsCodeType;

  /**
   * 验证码
   */
  @NotNull(message = "请输入验证码")
  private String smsCode;
}
