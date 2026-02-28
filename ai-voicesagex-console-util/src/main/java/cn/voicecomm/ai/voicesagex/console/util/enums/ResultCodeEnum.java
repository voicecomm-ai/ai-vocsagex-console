package cn.voicecomm.ai.voicesagex.console.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jiwh
 * @date 2024/5/30 14:17
 */
@Getter
@AllArgsConstructor
public enum ResultCodeEnum {
  SUCCESS(1000, "操作成功"),

  FAILED(2000, "操作失败"),

  /**
   * 登录失败
   */
  LOGIN_FAILURE(2003, "账号或密码错误"),
  /**
   * 操作失败
   */
  PARAM_ERROR(2004, "参数错误"),
  /**
   * 未认证
   */
  UNAUTHENTICATED(2005, "缺少认证信息"),
  /**
   * 认证失败
   */
  AUTHENTICATED_FAILURE(2006, "认证失败"),
  /**
   * 账号不可用
   */
  ACCOUNT_DISABLED(2007, "账号不可用"),
  /**
   * 账号被锁定
   */
  ACCOUNT_LOCKED(2008, "账号已被锁定"),
  /**
   * 账号已过期
   */
  ACCOUNT_EXPIRED(2009, "账号已过期"),
  /**
   * 密码已过期
   */
  CREDENTIALS_EXPIRED(2010, "密码已过期"),
  /**
   * 认证过期
   */
  AUTHENTICATED_PAST(2011, "您的身份已过期, 请重新认证!"),
  /**
   * 刷新令牌错误
   */
  REFRESH_TOKEN_FAILURE(2012, "刷新令牌错误"),
  /**
   * 无效令牌
   */
  INVALID_ACCESS_TOKEN(2013, "无效的令牌"),
  /**
   * 图形验证码错误
   */
  IMAGE_CAPTCHA_VALIDATE_ERROR(2014, "验证码错误"),
  /**
   * 短信验证码错误
   */
  SMS_CODE_VALIDATE_ERROR(2015, "验证码错误"),
  /**
   * 管理员ip限制
   */
  ADMIN_LIMIT_IP(2016, "管理员已开启IP登录限制，当前IP无法登陆该账户"),

  REFRESH_TOKEN(1100, "刷新令牌"),
  /**
   * 登录图片验证码
   */
  IMAGECHECKCODE_SUCCESS(1000, "登录验证码操作成功"),
  /**
   * 登录图片验证码
   */
  IMAGECHECKCODE_FAILURE(2000, "登录验证码操作失败"),

  /**
   * 3000 开头 为项目中验证的错误code
   */
  /**
   * 完成配置中 超出限制
   */
  PROJECT_CONFIG_FAIL(3001, "超出限制");


  private final int code;

  private final String msg;
}
