package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.config;

import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.JwtClaimConstants;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class JwtTokenCustomizerConfig {

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
    return context -> {
      if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())
        && context.getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
        // Customize headers/claims for access_token
        Optional.ofNullable(context.getPrincipal().getPrincipal())
          .ifPresent(
            principal -> {
              JwtClaimsSet.Builder claims = context.getClaims();
              if (principal instanceof SysUserDetails userDetails) { // 系统用户添加自定义字段

                claims.claim(JwtClaimConstants.USER_ID, userDetails.getId());
                claims.claim(JwtClaimConstants.ACCOUNT, userDetails.getAccount());
                claims.claim(JwtClaimConstants.USERNAME, userDetails.getUsername());
                claims.claim(
                  JwtClaimConstants.DATA_PERMISSION, userDetails.getDataPermission());
                claims.claim(
                  CommonConstants.LOGIN_DEVICE_TYPE_PARAM, userDetails.getDeviceType());

                // 这里存入角色至JWT，解析JWT的角色用于鉴权的位置: ResourceServerConfig#jwtAuthenticationConverter
                var authorities =
                  AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                    .stream()
                    .collect(
                      Collectors.collectingAndThen(
                        Collectors.toSet(), Collections::unmodifiableSet));
                claims.claim(JwtClaimConstants.AUTHORITIES, authorities);
              }
            });
      }
    };
  }
}
