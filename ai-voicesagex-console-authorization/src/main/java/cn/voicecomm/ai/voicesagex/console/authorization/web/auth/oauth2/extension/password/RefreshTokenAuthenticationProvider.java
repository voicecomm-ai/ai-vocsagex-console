package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password;


import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.JwtClaimConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import static cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient;

import java.security.Principal;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

  private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
  private static final OAuth2TokenType ID_TOKEN_TOKEN_TYPE = new OAuth2TokenType(
    OidcParameterNames.ID_TOKEN);
  private final OAuth2AuthorizationService authorizationService;
  private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
  private final RedissonClient redissonClient;

  /**
   * Constructs an {@code OAuth2RefreshTokenAuthenticationProvider} using the provided parameters.
   *
   * @param authorizationService the authorization service
   * @param tokenGenerator       the token generator
   * @since 0.2.3
   */
  public RefreshTokenAuthenticationProvider(OAuth2AuthorizationService authorizationService,
    OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
    RedissonClient redissonClient) {
    this.redissonClient = redissonClient;
    org.springframework.util.Assert.notNull(authorizationService,
      "authorizationService cannot be null");
    Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
    this.authorizationService = authorizationService;
    this.tokenGenerator = tokenGenerator;
  }


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    OAuth2RefreshTokenAuthenticationToken refreshTokenAuthentication =
      (OAuth2RefreshTokenAuthenticationToken) authentication;

    OAuth2ClientAuthenticationToken clientPrincipal =
      getAuthenticatedClientElseThrowInvalidClient(refreshTokenAuthentication);
    RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

    if (log.isTraceEnabled()) {
      log.trace("Retrieved registered client");
    }

    OAuth2Authorization authorization = this.authorizationService.findByToken(
      refreshTokenAuthentication.getRefreshToken(), OAuth2TokenType.REFRESH_TOKEN);
    if (authorization == null) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
    }

    if (log.isTraceEnabled()) {
      log.trace("Retrieved authorization with refresh token");
    }

    if (!registeredClient.getId().equals(authorization.getRegisteredClientId())) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
    }

    if (!registeredClient.getAuthorizationGrantTypes()
      .contains(AuthorizationGrantType.REFRESH_TOKEN)) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
    }

    OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
    if (!refreshToken.isActive()) {
      // As per https://tools.ietf.org/html/rfc6749#section-5.2
      // invalid_grant: The provided authorization grant (e.g., authorization code,
      // resource owner credentials) or refresh token is invalid, expired, revoked [...].
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_GRANT);
    }

    // As per https://tools.ietf.org/html/rfc6749#section-6
    // The requested scope MUST NOT include any scope not originally granted by the resource owner,
    // and if omitted is treated as equal to the scope originally granted by the resource owner.
    Set<String> scopes = refreshTokenAuthentication.getScopes();
    Set<String> authorizedScopes = authorization.getAuthorizedScopes();
    if (!authorizedScopes.containsAll(scopes)) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
    }

    if (log.isTraceEnabled()) {
      log.trace("Validated token request parameters");
    }

    if (scopes.isEmpty()) {
      scopes = authorizedScopes;
    }
    // @formatter:off
    DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
        .registeredClient(registeredClient)
        .principal(authorization.getAttribute(Principal.class.getName()))
        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
        .authorization(authorization)
        .authorizedScopes(scopes)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .authorizationGrant(refreshTokenAuthentication);
    // @formatter:on

    OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);

    // ----- Access token -----
    OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN)
      .build();
    OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
    if (generatedAccessToken == null) {
      OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
        "The token generator failed to generate the access token.", ERROR_URI);
      throw new OAuth2AuthenticationException(error);
    }

    if (log.isTraceEnabled()) {
      log.trace("Generated access token");
    }

    OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
      generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
      generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
    if (generatedAccessToken instanceof ClaimAccessor) {
      authorizationBuilder.token(accessToken, (metadata) -> {
        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
          ((ClaimAccessor) generatedAccessToken).getClaims());
        metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
      });
    } else {
      authorizationBuilder.accessToken(accessToken);
    }

    // ----- Refresh token -----
    OAuth2RefreshToken currentRefreshToken = refreshToken.getToken();
    if (!registeredClient.getTokenSettings().isReuseRefreshTokens()) {
      tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
      OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
      if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
          "The token generator failed to generate the refresh token.", ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }

      if (log.isTraceEnabled()) {
        log.trace("Generated refresh token");
      }

      currentRefreshToken = (OAuth2RefreshToken) generatedRefreshToken;
      authorizationBuilder.refreshToken(currentRefreshToken);
    }

    // ----- ID token -----
    OidcIdToken idToken;
    if (authorizedScopes.contains(OidcScopes.OPENID)) {
      // @formatter:off
      tokenContext = tokenContextBuilder
          .tokenType(ID_TOKEN_TOKEN_TYPE)
          .authorization(authorizationBuilder.build())	// ID token customizer may need access to the access token and/or refresh token
          .build();
      // @formatter:on
      OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
      if (!(generatedIdToken instanceof Jwt)) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
          "The token generator failed to generate the ID token.", ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }

      if (log.isTraceEnabled()) {
        log.trace("Generated id token");
      }

      idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
        generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
      authorizationBuilder.token(idToken, (metadata) ->
        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims()));
    } else {
      idToken = null;
    }

    authorization = authorizationBuilder.build();

    this.authorizationService.save(authorization);

    if (log.isTraceEnabled()) {
      log.trace("Saved authorization");
    }

    Map<String, Object> additionalParameters = Collections.emptyMap();
    if (idToken != null) {
      additionalParameters = new HashMap<>();
      additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
    }

    if (log.isTraceEnabled()) {
      log.trace("Authenticated token request");
    }
    String jti = (String) ((Jwt) generatedAccessToken).getClaims()
      .get(OAuth2TokenIntrospectionClaimNames.JTI);
    String loginDeviceType = (String) ((Jwt) generatedAccessToken).getClaims()
      .get(CommonConstants.LOGIN_DEVICE_TYPE_PARAM);
    Integer userId = (Integer) ((Jwt) generatedAccessToken).getClaims()
      .get(JwtClaimConstants.USER_ID);
    // 缓存白名单(同步token过期时间)
    redissonClient.getBucket(
        String.format(RedisConstants.TOKEN_WHITELIST, userId, loginDeviceType, jti))
      .set(1, Duration.ofHours(
        CommonConstants.ACCESS_TOKEN_TIME_TO_LIVE));
    return new OAuth2AccessTokenAuthenticationToken(
      registeredClient, clientPrincipal, accessToken, currentRefreshToken, additionalParameters);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return OAuth2RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
