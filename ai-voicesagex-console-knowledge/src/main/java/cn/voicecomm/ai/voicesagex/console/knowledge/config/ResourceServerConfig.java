package cn.voicecomm.ai.voicesagex.console.knowledge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * 资源服务器配置
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class ResourceServerConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {

    http.authorizeHttpRequests(
            (requests) -> {
              requests.requestMatchers("/extractionAbutmentManage/**")
                  .permitAll();
              requests.anyRequest().authenticated();
            })
        .csrf(AbstractHttpConfigurer::disable);
    http.oauth2ResourceServer(
        resourceServerConfigurer -> resourceServerConfigurer.jwt(Customizer.withDefaults()));
    return http.build();
  }
}
