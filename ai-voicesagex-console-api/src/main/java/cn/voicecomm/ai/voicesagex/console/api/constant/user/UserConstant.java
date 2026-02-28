package cn.voicecomm.ai.voicesagex.console.api.constant.user;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author gml
 * @date 2024/5/30 10:40
 */
@NoArgsConstructor(access = PRIVATE)
public abstract class UserConstant {

  public static final byte[] STATUS = {0, 1, 2, 3, 4};

  public static final String ACCOUNT = "account";

  public static final String PASSWORD = "password";

  public static final String ACCOUNT_MANAGE = "accountManage";

  public static final String NO_ACCOUNT_PERMISSION = "未拥有账号列表相关权限";
}
