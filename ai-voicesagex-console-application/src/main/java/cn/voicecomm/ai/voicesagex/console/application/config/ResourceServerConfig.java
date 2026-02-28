package cn.voicecomm.ai.voicesagex.console.application.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
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
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      HandlerMappingIntrospector introspector, ApiKeyAuthFilter apiKeyAuthFilter) throws Exception {
    http.authorizeHttpRequests((requests) -> {
      requests.requestMatchers("/websocket/**", "/model/download/callback",
          "/model/preTrainModel/callback", "/trainModel/train/callback",
          "/trainModel/deploy/callback", "/finetuneModel/finetune/callback",
          "/evalModel/eval/callback", "/finetuneModel/deploy/callback", "/api/**",
          "/agentLongTermMemoryUrl/**", "/applicationExperience/workflow/getWorkflowParams",
          "/workflow/publishRun", "/workflow/publishRunUrl", "/workflow/nodeExecutions",
          "/workflow/workflowRunDetail", "/applicationExperience/workflow/getWorkflowParamsByAppId",
          "/extractionAbutmentManage/parsingFile", "/extractionAbutmentManage/extractTriad",
          "/applicationExperience/nodeExecutions", "/uploadFile/upload").permitAll();
      requests.anyRequest().authenticated();
    }).csrf(AbstractHttpConfigurer::disable);

    // 配置认证失败处理和访问拒绝处理，用于记录日志
    http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
        (request, response, authException) -> {
          log.warn("授权验证失败: {}", authException.getMessage());
          log.warn("授权失败请求URI: {}, 请求方法: {}, 远程地址: {}", request.getRequestURI(),
              request.getMethod(), request.getRemoteAddr());
          log.warn("授权失败请求Authorization: {}",request.getHeader("Authorization"));
          if (authException.getCause() != null) {
            log.warn("异常原因: {}", authException.getCause().getClass().getSimpleName() + ": "
                + authException.getCause().getMessage());
          }
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json;charset=UTF-8");
          String jsonResponse = """
              {"error":"Unauthorized","message":"%s"}
              """.formatted(authException.getMessage());
          response.getWriter().write(jsonResponse);
        }).accessDeniedHandler((request, response, accessDeniedException) -> {
      log.warn("访问被拒绝: {}", accessDeniedException.getMessage());
      log.warn("访问拒绝请求URI: {}, 请求方法: {}, 远程地址: {}", request.getRequestURI(),
          request.getMethod(), request.getRemoteAddr());
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      String jsonResponse = """
          {"error":"Forbidden","message":"%s"}
          """.formatted(accessDeniedException.getMessage());
      response.getWriter().write(jsonResponse);
    }));

    // 添加 API Key 认证过滤器（放在 JWT 过滤器之前执行）
    http.addFilterBefore(apiKeyAuthFilter, BearerTokenAuthenticationFilter.class);
    http.oauth2ResourceServer(
        resourceServerConfigurer -> resourceServerConfigurer.jwt(Customizer.withDefaults()));
    return http.build();
  }

}
