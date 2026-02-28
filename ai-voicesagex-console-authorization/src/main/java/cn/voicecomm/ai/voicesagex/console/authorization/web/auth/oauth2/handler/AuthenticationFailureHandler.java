package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.handler;

import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import java.io.IOException;

@Slf4j
public class AuthenticationFailureHandler
  implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

  /**
   * MappingJackson2HttpMessageConverter 是 Spring 框架提供的一个 HTTP 消息转换器，用于将 HTTP 请求和响应的 JSON 数据与 Java
   * 对象之间进行转换
   */
  private final HttpMessageConverter<Object> accessTokenHttpResponseConverter =
    new MappingJackson2HttpMessageConverter();

  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
    throws IOException, ServletException {
    OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
    ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
    Result<?> result = Result.error(error.getDescription());
    accessTokenHttpResponseConverter.write(result, null, httpResponse);
  }
}
