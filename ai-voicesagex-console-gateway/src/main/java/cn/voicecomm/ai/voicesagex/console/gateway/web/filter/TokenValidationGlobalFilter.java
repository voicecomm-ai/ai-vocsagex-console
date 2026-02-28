package cn.voicecomm.ai.voicesagex.console.gateway.web.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.RegisteredPayload;
import cn.voicecomm.ai.voicesagex.console.gateway.web.constant.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.util.enums.ResultCodeEnum;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author gml
 * @date 2024/5/31 10:08
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenValidationGlobalFilter implements GlobalFilter, Ordered {

  private final RedissonClient redissonClient;

  private static final String BEARER_PREFIX = "Bearer ";

  private static final String USER_ID = "user_id";

  private final ObjectMapper objectMapper;

  /**
   * 白名单列表
   */
  private static final List<String> AUTH_WHITE_LIST = new ArrayList<>();
  private static final List<String> NO_AUTH_WHITE_LIST = new ArrayList<>();

  static {
    // 认证服务器请求全部放行
    AUTH_WHITE_LIST.add("/auth/");
    // 小程序列表放行
    // websocket请求放行
    AUTH_WHITE_LIST.add("/websocket/**");
    // swagger放行
    AUTH_WHITE_LIST.add("/v3/api-docs");
    //回调接口放行
    AUTH_WHITE_LIST.add("/extractionAbutmentManage/**");
    NO_AUTH_WHITE_LIST.add("/api/**");

    NO_AUTH_WHITE_LIST.add("/model/download/callback");
    NO_AUTH_WHITE_LIST.add("/model/preTrainModel/callback");
    NO_AUTH_WHITE_LIST.add("/trainModel/train/callback");
    NO_AUTH_WHITE_LIST.add("/trainModel/deploy/callback");
    NO_AUTH_WHITE_LIST.add("/finetuneModel/finetune/callback");
    NO_AUTH_WHITE_LIST.add("/finetuneModel/deploy/callback");
    NO_AUTH_WHITE_LIST.add("/evalModel/eval/callback");

    NO_AUTH_WHITE_LIST.add("/applicationExperience/workflow/getWorkflowParams");
    NO_AUTH_WHITE_LIST.add("/applicationExperience/workflow/getWorkflowParamsByAppId");
    NO_AUTH_WHITE_LIST.add("/workflow/publishRun");
    NO_AUTH_WHITE_LIST.add("/workflow/publishRunUrl");
    NO_AUTH_WHITE_LIST.add("/workflow/workflowRunDetail");
    NO_AUTH_WHITE_LIST.add("/workflow/nodeExecutions");
    NO_AUTH_WHITE_LIST.add("/applicationExperience/nodeExecutions");
    NO_AUTH_WHITE_LIST.add("/uploadFile/upload");

    NO_AUTH_WHITE_LIST.add("/agentLongTermMemoryUrl/**");

  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();

    // 获取请求URL
    String path = request.getURI().getPath();
    log.info("path:{}", path);
    if (isWhiteList(path)) {
      // 认证服务器请求跟websocket请求放行
      log.info("path in whitelist");
      return chain.filter(exchange);
    }
    if (isNoAuthWhiteList(path)) {
      ServerHttpRequest newRequest = exchange.getRequest().mutate()
          .headers(h -> h.remove(HttpHeaders.AUTHORIZATION))
          .build();

      log.info("path in noAuthWhitelist");

      return chain.filter(
          exchange.mutate().request(newRequest).build()
      );
    }

    String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StrUtil.isBlank(authorization) || !StrUtil.startWithIgnoreCase(authorization,
        BEARER_PREFIX)) {
      log.info("authorization is blank or not Bearer");
      return chain.filter(exchange);
    }
    // 响应错误信息
    Result result = null;
    try {
      String token = authorization.substring(BEARER_PREFIX.length());
      JWSObject jwsObject = JWSObject.parse(token);
      Map<String, Object> jsonObject = jwsObject.getPayload().toJSONObject();
      String jti = (String) jsonObject.get(RegisteredPayload.JWT_ID);
      Long userId = (Long) jsonObject.get(USER_ID);
      Object loginDeviceObj = jsonObject.get(RedisConstants.LOGIN_DEVICE_TYPE_PARAM);
      String loginDeviceType = RedisConstants.LOGIN_TYPE_PC;
      if (ObjectUtil.isNotNull(loginDeviceObj)) {
        loginDeviceType = loginDeviceObj.toString();
      }
      String tokenFormat = String.format(RedisConstants.TOKEN_WHITELIST_PREFIX, userId,
          loginDeviceType, jti);
      boolean exists = redissonClient.getBucket(tokenFormat).isExists();
      if (Boolean.FALSE.equals(exists)) {
        log.error("redis中不存在白名单：{}", tokenFormat);
        result = Result.of(ResultCodeEnum.AUTHENTICATED_PAST);
      }
    } catch (ParseException e) {
      log.error(ResultCodeEnum.INVALID_ACCESS_TOKEN.getMsg(), e);
      result = Result.of(ResultCodeEnum.INVALID_ACCESS_TOKEN);
    }

    if (Objects.isNull(result)) {
      // 如果令牌存在，则通过
      return chain.filter(exchange);
    }

    // 转换响应消息内容对象为字节
    byte[] bits = new byte[0];
    try {
      bits = objectMapper.writeValueAsBytes(result);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    DataBuffer buffer = response.bufferFactory().wrap(bits);
    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    // 返回响应对象
    return response.writeWith(Mono.just(buffer));
  }

  @Override
  public int getOrder() {
    return -100;
  }

  /**
   * 是否在白名单内
   *
   * @param url
   * @return
   */
  private static boolean isWhiteList(String url) {
    int i = StrUtil.ordinalIndexOf(url, "/", 3);
    if (i == -1) {
      return false;
    }
    String targetUrl = url.substring(i);
    for (String key : AUTH_WHITE_LIST) {
      if (key.contains("**")) {
        key = key.replace("/**", "");
        if (targetUrl.startsWith(key)) {
          return true;
        }
      } else if (targetUrl.startsWith(key)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isNoAuthWhiteList(String url) {
    int i = StrUtil.ordinalIndexOf(url, "/", 3);
    if (i == -1) {
      return false;
    }
    String targetUrl = url.substring(i);
    for (String key : NO_AUTH_WHITE_LIST) {
      if (key.contains("**")) {
        key = key.replace("/**", "");
        if (targetUrl.startsWith(key)) {
          return true;
        }
      } else if (targetUrl.startsWith(key)) {
        return true;
      }
    }
    return false;
  }
}
