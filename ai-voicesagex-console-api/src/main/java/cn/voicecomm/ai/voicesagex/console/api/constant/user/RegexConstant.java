package cn.voicecomm.ai.voicesagex.console.api.constant.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

/**
 * @author gml
 * @date 2024/5/30 10:40
 */
@NoArgsConstructor(access = PRIVATE)
public abstract class RegexConstant {

  /**
   * 账号正则表达式：数字或者英文字符，长度不超过50
   */
  public static final String ACCOUNT_REGEX = "^.{1,50}$";

  /**
   * 手机号正则表达式：11位数字
   */
  public static final String PHONE_REGEX =
      "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

  /**
   * 密码正则表达式：6-20位，包含大小写字母、数字
   */
  public static final String PASSWORD_REGEX = "^(?!.*\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,20}";

  // 邮箱正则表达式模式
  public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
}
