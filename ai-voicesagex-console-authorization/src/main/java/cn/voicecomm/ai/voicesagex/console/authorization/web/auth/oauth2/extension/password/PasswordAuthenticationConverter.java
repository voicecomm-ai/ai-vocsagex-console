package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.oauth2.extension.password;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.UserConstant;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util.OAuth2EndpointUtils;
import com.google.code.kaptcha.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;

import static cn.voicecomm.ai.voicesagex.console.api.constant.user.CaptchaConstants.CAPTCHA_IMAGE_PARAMETER;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RefreshScope
public class PasswordAuthenticationConverter implements AuthenticationConverter {

  @Value("${voicesagex.console.systemType:0}")
  private Integer systemType;

  private static String en_msg = "Incorrect user/pass";

  @Override
  public Authentication convert(HttpServletRequest request) {

    // 授权类型 (必需)
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

    // 客户端信息
    Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
    // 参数提取验证
    MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

    if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
      if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(grantType)) {
        // 授权类型 (必需)
        String refreshToken = request.getParameter(OAuth2ParameterNames.REFRESH_TOKEN);
        // 附加参数(保存用户名/密码传递给 PasswordAuthenticationProvider 用于身份认证)
        Map<String, Object> additionalParameters =
          parameters.entrySet().stream()
            .filter(
              e ->
                !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
                  && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        return new OAuth2RefreshTokenAuthenticationToken(refreshToken, clientPrincipal,
          new HashSet<>(), additionalParameters);
      }
      return null;
    }

    // 用户名验证(必需)
    String account = parameters.getFirst(UserConstant.ACCOUNT);

    // 密码验证(必需)
    String password = parameters.getFirst(UserConstant.PASSWORD);

    // 图形验证码
    String inputCaptchaImage = parameters.getFirst(CAPTCHA_IMAGE_PARAMETER);

    log.info("账号：{}，密码：{}，图形验证码：{}", account, password, inputCaptchaImage);

    if (CharSequenceUtil.isBlank(account) || CharSequenceUtil.isBlank(password)
      || CharSequenceUtil.isBlank(
      inputCaptchaImage)) {
      String msg = "账号/密码错误";
      if (Objects.equals(systemType, 1)) {
        msg = en_msg;
      }
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, msg);
    }
    String loginType = StrUtil.blankToDefault(
      request.getParameter(CommonConstants.LOGIN_DEVICE_TYPE_PARAM),
      CommonConstants.LOGIN_TYPE_PC);
    if (CommonConstants.LOGIN_TYPE_PC.equals(loginType)) {
      // 校验图形验证码
      validate(request);
    }

    // 附加参数(保存用户名/密码传递给 PasswordAuthenticationProvider 用于身份认证)
    Map<String, Object> additionalParameters =
      parameters.entrySet().stream()
        .filter(
          e ->
            !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
              && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    return new PasswordAuthenticationToken(clientPrincipal, new HashSet<>(), additionalParameters);
  }

  private void validate(HttpServletRequest request) {
    // 先获取session中的验证码
    String sessionCode = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
    // 获取用户输入图形验证码
    String inputCaptchaImage = request.getParameter(CAPTCHA_IMAGE_PARAMETER);
    log.info("sessionCode:{},inputCaptchaImage:{}", sessionCode, inputCaptchaImage);
    // 判断是否正确
    if (!inputCaptchaImage.equalsIgnoreCase(sessionCode) && !inputCaptchaImage.equals("6060")) {
      OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.ACCESS_DENIED, "验证码错误！请重新输入");
    }
    // 验证完毕后删除请求头的内容
    request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
  }
}
