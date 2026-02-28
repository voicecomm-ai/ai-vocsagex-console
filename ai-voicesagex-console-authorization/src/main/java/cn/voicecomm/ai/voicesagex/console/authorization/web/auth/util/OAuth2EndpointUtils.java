package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class OAuth2EndpointUtils {

  public OAuth2EndpointUtils() {
  }

  public static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
    Map<String, String[]> parameterMap = request.getParameterMap();
    MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
    parameterMap.forEach(
      (key, values) -> {
        for (String value : values) {
          parameters.add(key, value);
        }
      });
    return parameters;
  }

  public static void throwError(String errorCode, String msg) {
    OAuth2Error error = new OAuth2Error(errorCode, msg, "");
    throw new OAuth2AuthenticationException(error);
  }
}
