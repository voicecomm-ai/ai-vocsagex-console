package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.UserConstant;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.service.SysUserDetailsService;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.OAuth2AuthenticationProviderUtils;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.OAuth2EndpointUtils;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.CollectionUtils;

import static cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants.LOGIN_TYPE_APP;
import static cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants.LOGIN_TYPE_PAINTING;
import static cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants.LOGIN_TYPE_PC;

import java.security.Principal;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RefreshScope
public class PasswordAuthenticationProvider implements AuthenticationProvider {

  @Value("${voicesagex.console.systemType:0}")
  private Integer systemType;

  private static String en_msg = "Incorrect user/pass";

  private static String en_disabled_msg = "Account disabled";

  private static final String ERROR_URI =
    "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
  private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE =
    new OAuth2TokenType(OidcParameterNames.ID_TOKEN);
  private final OAuth2AuthorizationService authorizationService;
  private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
  private final SysUserDetailsService sysUserDetailsService;
  private final RedissonClient redissonClient;
  private final PasswordEncoder passwordEncoder;

  public PasswordAuthenticationProvider(
    OAuth2AuthorizationService authorizationService,
    OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
    SysUserDetailsService sysUserDetailsService,
    RedissonClient redissonClient, PasswordEncoder passwordEncoder) {
    this.sysUserDetailsService = sysUserDetailsService;
    this.redissonClient = redissonClient;
    this.passwordEncoder = passwordEncoder;
    Assert.notNull(authorizationService, "authorizationService cannot be null");
    Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
    this.authorizationService = authorizationService;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    PasswordAuthenticationToken passwordAuthenticationToken =
      (PasswordAuthenticationToken) authentication;
    OAuth2ClientAuthenticationToken clientPrincipal =
      OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(
        passwordAuthenticationToken);
    RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

    // 验证客户端是否支持授权类型(grant_type=password)
    if (!registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
    }

    Map<String, Object> additionalParameters =
      passwordAuthenticationToken.getAdditionalParameters();

    // 生成用户名密码身份验证令牌
    String account = (String) additionalParameters.get(UserConstant.ACCOUNT);
    String password = (String) additionalParameters.get(UserConstant.PASSWORD);

    Authentication usernamePasswordAuthentication = null;
    String accountPasswordResetError = "账号/密码错误";
    String accountPasswordError = "账号/密码错误";
    String disabledError = "账号已禁用";
    log.info("systemType:{}", systemType);
    if (Objects.equals(systemType, 1)) {
      accountPasswordResetError = en_msg;
      accountPasswordError = en_msg;
      disabledError = en_disabled_msg;
    }
    try {
      // 密码解密
      password = RSAUtils.decryptByPrivateKey(password, RSAUtils.getPrivateKey());
    } catch (Exception e) {
      log.error("密码解密失败！");
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.ACCESS_DENIED, accountPasswordResetError);
    }

    // 根据账号获取会员信息
    SysUserDetails sysUserDetails = sysUserDetailsService.loadUserByAccount(account);
    if (Objects.isNull(sysUserDetails)) {
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.ACCESS_DENIED, accountPasswordError);
    }

    if (Objects.equals(sysUserDetails.getStatus(), 1)) {
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.ACCESS_DENIED, disabledError);
    }

    // 校验密码
    String currentPassword = sysUserDetails.getPassword();
    boolean matches = passwordEncoder.matches(password, currentPassword);
    if (!matches) {
      log.info("密码校验不通过");
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.ACCESS_DENIED, accountPasswordResetError);
    }
    Object loginDeviceObj = additionalParameters.get(CommonConstants.LOGIN_DEVICE_TYPE_PARAM);
    String loginDeviceType = LOGIN_TYPE_PC;
    if (ObjectUtil.isNotNull(loginDeviceObj)) {
      loginDeviceType = loginDeviceObj.toString();
    }
    sysUserDetails.setDeviceType(loginDeviceType);

    usernamePasswordAuthentication =
      new UsernamePasswordAuthenticationToken(sysUserDetails, null);

    // 验证申请访问范围(Scope)
    Set<String> authorizedScopes = registeredClient.getScopes();
    Set<String> requestedScopes = passwordAuthenticationToken.getScopes();
    if (!CollectionUtils.isEmpty(requestedScopes)) {
      Set<String> unauthorizedScopes =
        requestedScopes.stream()
          .filter(requestedScope -> !registeredClient.getScopes().contains(requestedScope))
          .collect(Collectors.toSet());
      if (!CollectionUtils.isEmpty(unauthorizedScopes)) {
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
      }
      authorizedScopes = new LinkedHashSet<>(requestedScopes);
    }

    // 访问令牌(Access Token) 构造器
    DefaultOAuth2TokenContext.Builder tokenContextBuilder =
      DefaultOAuth2TokenContext.builder()
        .registeredClient(registeredClient)
        .principal(usernamePasswordAuthentication) // 身份验证成功的认证信息(用户名、权限等信息)
        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
        .authorizedScopes(authorizedScopes)
        .authorizationGrantType(AuthorizationGrantType.PASSWORD) // 授权方式
        .authorizationGrant(passwordAuthenticationToken) // 授权具体对象
      ;

    // 生成访问令牌(Access Token)
    OAuth2TokenContext tokenContext =
      tokenContextBuilder.tokenType((OAuth2TokenType.ACCESS_TOKEN)).build();
    OAuth2Token generatedAccessToken = tokenGenerator.generate(tokenContext);
    if (generatedAccessToken == null) {
      OAuth2Error error =
        new OAuth2Error(
          OAuth2ErrorCodes.SERVER_ERROR,
          "The token generator failed to generate the access token.",
          ERROR_URI);
      throw new OAuth2AuthenticationException(error);
    }

    OAuth2AccessToken accessToken =
      new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        generatedAccessToken.getTokenValue(),
        generatedAccessToken.getIssuedAt(),
        generatedAccessToken.getExpiresAt(),
        tokenContext.getAuthorizedScopes());

    OAuth2Authorization.Builder authorizationBuilder =
      OAuth2Authorization.withRegisteredClient(registeredClient)
        .principalName(usernamePasswordAuthentication.getName())
        .authorizationGrantType(AuthorizationGrantType.PASSWORD)
        .authorizedScopes(authorizedScopes)
        .attribute(Principal.class.getName(), usernamePasswordAuthentication); // attribute 字段
    if (generatedAccessToken instanceof ClaimAccessor) {
      authorizationBuilder.token(
        accessToken,
        (metadata) ->
          metadata.put(
            OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
            ((ClaimAccessor) generatedAccessToken).getClaims()));
    } else {
      authorizationBuilder.accessToken(accessToken);
    }

    String jti = (String) ((Jwt) generatedAccessToken).getClaims()
      .get(OAuth2TokenIntrospectionClaimNames.JTI);

    // 生成刷新令牌(Refresh Token)
    OAuth2RefreshToken refreshToken = null;
    if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
      &&
      // Do not issue refresh token to public client
      !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

      tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
      OAuth2Token generatedRefreshToken = tokenGenerator.generate(tokenContext);
      if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
        OAuth2Error error =
          new OAuth2Error(
            OAuth2ErrorCodes.SERVER_ERROR,
            "The token generator failed to generate the refresh token.",
            ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }

      refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
      authorizationBuilder.refreshToken(refreshToken);
    }

    // 持久化令牌发放记录到数据库
    OAuth2Authorization authorization = authorizationBuilder.build();
    authorizationService.save(authorization);

    // 单用户登录踢出之前登录的用户
    sysUserDetailsService.deleteLoginUser(sysUserDetails.getId());
    // 缓存白名单(同步token过期时间)
    redissonClient.getBucket(
        String.format(RedisConstants.TOKEN_WHITELIST, sysUserDetails.getId(), loginDeviceType, jti))
      .set(1, Duration.ofHours(
        CommonConstants.ACCESS_TOKEN_TIME_TO_LIVE));

    return new OAuth2AccessTokenAuthenticationToken(
      registeredClient, clientPrincipal, accessToken, refreshToken, Collections.emptyMap());
  }

  /**
   * 判断传入的 authentication 类型是否与当前认证提供者(AuthenticationProvider)相匹配--模板方法
   *
   * <p>ProviderManager#authenticate 遍历 providers 找到支持对应认证请求的 provider-迭代器模式
   */
  @Override
  public boolean supports(Class<?> authentication) {
    return PasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  public static int convertDeviceTypeToNumber(String loginDeviceType) {
    // 0 运营后台 1 拍照app 2 绘画app 3 综合机app
    return switch (loginDeviceType) {
      case LOGIN_TYPE_APP -> 1;
      case LOGIN_TYPE_PAINTING -> 2;
      default -> 0;
    };
  }
}
