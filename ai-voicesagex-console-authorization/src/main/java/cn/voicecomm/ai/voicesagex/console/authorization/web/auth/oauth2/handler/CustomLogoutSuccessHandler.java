package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.RegisteredPayload;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

  private final OAuth2AuthorizationService authorizationService;

  private final ObjectMapper objectMapper;

  private final RedissonClient redissonClient;

  @Override
  public void onLogoutSuccess(
    HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, ServletException {
    // 获取令牌并删除
    String token = request.getHeader("Authorization").replace("Bearer ", "");
    OAuth2Authorization authorization =
      authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
    if (authorization != null) {
      authorizationService.remove(authorization);
      // 删除白名单
      String jti;
      Long userId;
      try {
        JWSObject jwsObject = JWSObject.parse(token);
        Map<String, Object> jsonObject = jwsObject.getPayload().toJSONObject();
        jti = (String) jsonObject.get(RegisteredPayload.JWT_ID);
        userId = (Long) jsonObject.get("user_id");
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
      String loginType = StrUtil.blankToDefault(
        request.getParameter(CommonConstants.LOGIN_DEVICE_TYPE_PARAM),
        CommonConstants.LOGIN_TYPE_PC);
      redissonClient
        .getKeys()
        .delete(
          String.format(RedisConstants.TOKEN_WHITELIST, userId, loginType, jti));
    }
    response.setContentType("application/json;charset=UTF-8");
    // 登出只返回成功
    response.getWriter().write(objectMapper.writeValueAsString(Result.success()));
  }
}
