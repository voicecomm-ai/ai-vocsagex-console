package cn.voicecomm.ai.voicesagex.console.application.config;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author ryc
 * @description
 * @date 2025/7/9 16:58
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthFilter extends OncePerRequestFilter {

  private final ModelApiKeyService modelApiKeyService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String path = request.getRequestURI();
    if (path.contains("/model/pre-trained/v1/invoke")) {
      String authHeader = request.getHeader("Authorization");
      if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("ApiKey ")) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
        return;
      }
      // 去掉 "ApiKey "
      String apiKey = authHeader.substring(7);
      if (!modelApiKeyService.isValid(apiKey)) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid API Key\"}");
        return;
      }
      // 设置认证上下文
      Authentication auth = new UsernamePasswordAuthenticationToken("apiUser", null,
          List.of(new SimpleGrantedAuthority("ROLE_USER")));
      SecurityContextHolder.getContext().setAuthentication(auth);
      log.info("认证信息: {}, 已认证: {}", auth, auth.isAuthenticated());
    }
    filterChain.doFilter(request, response);
  }
}
