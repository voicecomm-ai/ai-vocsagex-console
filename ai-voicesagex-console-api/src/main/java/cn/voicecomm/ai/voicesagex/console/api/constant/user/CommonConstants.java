package cn.voicecomm.ai.voicesagex.console.api.constant.user;

/**
 * @author gml
 * @date 2024/6/14 13:10
 */
public class CommonConstants {

  /**
   * token存活时间
   */
  public static final Long ACCESS_TOKEN_TIME_TO_LIVE = 12L;

  public static final Long REFRESH_TOKEN_TIME_TO_LIVE = 30L;

  public static final long TEN_YEAR_HOURS = 87600L;

  public static final Long WECHAT_ACCESS_TOKEN_TIME_TO_LIVE = TEN_YEAR_HOURS;

  public static final Long WECHAT_REFRESH_TOKEN_TIME_TO_LIVE = TEN_YEAR_HOURS;

  public static final String LOGIN_DEVICE_TYPE_PARAM = "deviceType";

  public static final String LOGIN_TYPE_APP = "app";

  public static final String LOGIN_TYPE_PC = "pc";

  public static final String LOGIN_TYPE_PAINTING = "painting";

  public static final String LOGIN_TYPE_WECHAT = "wechat";

  public static final String PASSWORD_UPDATE_LOGOUT = "密码已修改！请重新登录";

  public static final String ACCOUNT_DISABLE_LOGOUT = "账号已禁用，无法使用";
}
