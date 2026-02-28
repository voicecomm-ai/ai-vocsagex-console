package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LogoutSuccessHandler logoutSuccessHandler;

  /**
   * Spring Security 安全过滤器链配置
   *
   * @param http 安全配置
   * @return 安全过滤器链
   */
  @Bean
  @Order(0)
  SecurityFilterChain defaultSecurityFilterChain(
    HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
    http.authorizeHttpRequests(
        authorize ->
          authorize
            .requestMatchers("/auth/**", "/logout", "/")
            .permitAll()
            .anyRequest()
            .authenticated())
      .csrf(AbstractHttpConfigurer::disable)
      .formLogin(Customizer.withDefaults())
      .logout(
        logout ->
          logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler) // 自定义登出成功处理器
            .invalidateHttpSession(true)
            .clearAuthentication(true));

    return http.build();
  }
}
