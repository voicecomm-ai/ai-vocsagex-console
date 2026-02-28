package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.config;

import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password.PasswordAuthenticationConverter;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password.PasswordAuthenticationProvider;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password.RefreshTokenAuthenticationProvider;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.handler.AuthenticationFailureHandler;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.handler.AuthenticationSuccessHandler;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.jackson.SysUserMixin;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.service.SysUserDetailsService;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServerConfig {

  private final OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer;

  private final RedissonClient redissonClient;

  private final SysUserDetailsService sysUserDetailsService;

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Value("${voicesagexConsole.loginUrl}")
  private String loginUrl;

  /**
   * 授权服务器端点配置
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain authorizationServerSecurityFilterChain(
    HttpSecurity http,
    OAuth2AuthorizationService authorizationService,
    OAuth2TokenGenerator<?> tokenGenerator)
    throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)

      // 自定义授权模式转换器(Converter)
      .tokenEndpoint(
        tokenEndpoint ->
          tokenEndpoint
            .accessTokenRequestConverters(
              authenticationConverters -> // <1>
                // 自定义授权模式转换器(Converter)
                authenticationConverters.add(
                  new PasswordAuthenticationConverter()
                ))
            .authenticationProviders(
              authenticationProviders -> {
                // 自定义授权模式提供者(Provider)
                authenticationProviders.add(
                  new PasswordAuthenticationProvider(
                    authorizationService,
                    tokenGenerator,
                    sysUserDetailsService,
                    redissonClient,
                    passwordEncoder()));

                for (int i = 0; i < authenticationProviders.size(); i++) {
                  AuthenticationProvider authenticationProvider =
                    authenticationProviders.get(i);
                  if (authenticationProvider
                    instanceof OAuth2RefreshTokenAuthenticationProvider) {
                    authenticationProviders.set(
                      i,
                      new RefreshTokenAuthenticationProvider(
                        authorizationService, tokenGenerator, redissonClient));
                  }
                }
              })
            .accessTokenResponseHandler(new AuthenticationSuccessHandler()) // 自定义成功响应
            .errorResponseHandler(new AuthenticationFailureHandler()) // 自定义失败响应
      );

    http.exceptionHandling(
        (exceptions) ->
          exceptions.defaultAuthenticationEntryPointFor(
            new LoginUrlAuthenticationEntryPoint(loginUrl),
            new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
      .oauth2ResourceServer(
        oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
      .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

  /**
   * JWK（JWT密钥对）源
   */
  @Bean // <5>
  @SneakyThrows
  public JWKSource<SecurityContext> jwkSource() {

    // 尝试从Redis中获取JWKSet(JWT密钥对，包含非对称加密的公钥和私钥)
    RBucket<String> bucket = redissonClient.getBucket(RedisConstants.JWK_SET_KEY);
    if (bucket.isExists()) {
      // 如果存在，解析JWKSet并返回
      JWKSet jwkSet = JWKSet.parse(bucket.get());
      return new ImmutableJWKSet<>(jwkSet);
    } else {
      // 如果Redis中不存在JWKSet，生成新的JWKSet
      KeyPair keyPair = generateRsaKey();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

      // 构建RSAKey
      RSAKey rsaKey =
        new RSAKey.Builder(publicKey)
          .privateKey(privateKey)
          .keyID(UUID.randomUUID().toString())
          .build();

      // 构建JWKSet
      JWKSet jwkSet = new JWKSet(rsaKey);

      // 将JWKSet存储在Redis中
      redissonClient.getBucket(RedisConstants.JWK_SET_KEY).set(jwkSet.toString(Boolean.FALSE));
      return new ImmutableJWKSet<>(jwkSet);
    }
  }

  /**
   * 生成RSA密钥对
   */
  private static KeyPair generateRsaKey() { // <6>
    KeyPair keyPair;
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      keyPair = keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return keyPair;
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {

    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }

  /**
   * 授权服务器配置(令牌签发者、获取令牌等端点)
   */
  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  /**
   * 配置密码解析器,使用BCrypt的方式对密码进行加密和验证
   *
   * @return BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    JdbcRegisteredClientRepository registeredClientRepository =
      new JdbcRegisteredClientRepository(jdbcTemplate);

    // 初始化 OAuth2 客户端
    initClient(registeredClientRepository);

    return registeredClientRepository;
  }

  @Bean
  public OAuth2AuthorizationService authorizationService(
    JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    // 创建基于JDBC的OAuth2授权服务。这个服务使用JdbcTemplate和客户端仓库来存储和检索OAuth2授权数据。
    JdbcOAuth2AuthorizationService service =
      new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);

    // 创建并配置用于处理数据库中OAuth2授权数据的行映射器。
    JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper =
      new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
    rowMapper.setLobHandler(new DefaultLobHandler());
    ObjectMapper objectMapper = new ObjectMapper();
    ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
    List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
    objectMapper.registerModules(securityModules);
    objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    // You will need to write the Mixin for your class so Jackson can marshall it.

    // 添加自定义Mixin，用于序列化/反序列化特定的类。
    // Mixin类需要自行实现，以便Jackson可以处理这些类的序列化。
    objectMapper.addMixIn(SysUserDetails.class, SysUserMixin.class);
    objectMapper.addMixIn(Long.class, Object.class);

    // 将配置好的ObjectMapper设置到行映射器中。
    rowMapper.setObjectMapper(objectMapper);

    // 将自定义的行映射器设置到授权服务中。
    service.setAuthorizationRowMapper(rowMapper);

    return service;
  }

  @Bean
  public OAuth2AuthorizationConsentService authorizationConsentService(
    JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    // Will be used by the ConsentController
    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
  }

  @Bean
  OAuth2TokenGenerator<?> tokenGenerator(JWKSource<SecurityContext> jwkSource) {
    JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
    jwtGenerator.setJwtCustomizer(jwtCustomizer);

    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
      jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  /**
   * 初始化创建客户端
   */
  private void initClient(JdbcRegisteredClientRepository registeredClientRepository) {

    String clientId = "voicesagex-console";
    String clientSecret = "123456";
    String clientName = "AI中台";

    /*
     如果使用明文，客户端认证时会自动升级加密方式，换句话说直接修改客户端密码，所以直接使用 bcrypt 加密避免不必要的麻烦
     官方ISSUE： https://github.com/spring-projects/spring-authorization-server/issues/1099
    */
    String encodeSecret = passwordEncoder().encode(clientSecret);

    RegisteredClient registeredMallAdminClient =
      registeredClientRepository.findByClientId(clientId);
    String id =
      registeredMallAdminClient != null
        ? registeredMallAdminClient.getId()
        : UUID.randomUUID().toString();

    RegisteredClient mallAppClient =
      RegisteredClient.withId(id)
        .clientId(clientId)
        .clientSecret(encodeSecret)
        .clientName(clientName)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .authorizationGrantType(AuthorizationGrantType.PASSWORD) // 密码模式
        .redirectUri(loginUrl)
        .tokenSettings(
          TokenSettings.builder()
            .accessTokenTimeToLive(
              Duration.ofHours(CommonConstants.ACCESS_TOKEN_TIME_TO_LIVE))
            .refreshTokenTimeToLive(
              Duration.ofDays(CommonConstants.REFRESH_TOKEN_TIME_TO_LIVE))
            .build())
        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
        .build();
    registeredClientRepository.save(mallAppClient);
  }
}
