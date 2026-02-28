package cn.voicecomm.ai.voicesagex.console.api.constant.user;

/**
 * @author gml
 * @date 2024/6/13 16:35
 */
public class RedisConstants {

  /**
   * 认证信息存储前缀
   */
  public static final String SECURITY_CONTEXT_PREFIX_KEY = "security_context:";

  /**
   * JWT 密钥对(包含公钥和私钥)
   */
  public static final String JWK_SET_KEY = "console_backend_jwk_set";

  /**
   * 白名单TOKEN Key
   */
  public static final String TOKEN_WHITELIST = "token:whitelist:%s:%s:%s";

  /**
   * 白名单TOKEN Key 前缀
   */
  public static final String TOKEN_WHITELIST_PREFIX = "token:whitelist:";
}
