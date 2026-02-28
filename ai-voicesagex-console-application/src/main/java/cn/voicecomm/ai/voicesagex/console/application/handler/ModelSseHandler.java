package cn.voicecomm.ai.voicesagex.console.application.handler;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInstanceConfigDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeResponse;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.ClassificationEnum;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelApiKeyMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelMapper;
import cn.voicecomm.ai.voicesagex.console.util.enums.ResultCodeEnum;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelApiKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author ryc
 * @description
 * @date 2025/7/11 15:55
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class ModelSseHandler {

  private final ModelMapper modelMapper;

  private final ModelApiKeyMapper modelApiKeyMapper;

  /**
   * 预训练模型
   */
  @Value("${algoUrlPrefix}${preTrainedModel.invoke}")
  private String preTrainedModelInvokeUrl;

  private static final ExecutorService executor = new DelegatingSecurityContextExecutorService(
      Executors.newVirtualThreadPerTaskExecutor());

  public SseEmitter invokeWithSse(String secret, ModelInvokeBaseDto modelInvokeBaseDto) {
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);

    emitter.onCompletion(() -> log.info("模型调用--Sse连接已关闭"));
    emitter.onError(callBack -> log.info("模型调用--错误"));
    emitter.onTimeout(() -> log.info("模型调用--超时"));

    // 根据秘钥查询模型信息
    ModelApiKeyPo modelApiKeyPo = modelApiKeyMapper.selectOne(
        Wrappers.<ModelApiKeyPo>lambdaQuery().eq(ModelApiKeyPo::getSecret, secret), false);
    ModelPo modelPo = modelMapper.selectById(modelApiKeyPo.getModelId());
    if (ObjectUtil.isNull(modelPo) || BooleanUtil.isFalse(modelPo.getIsShelf())) {
      try {
        emitter.send(SseEmitter.event().name("error").data("模型已下架"));
      } catch (Exception e) {
        log.error("模型已下架", e);
      } finally {
        emitter.complete();
      }
    }
    // 更新时间
    modelApiKeyPo.setLastUsedTime(LocalDateTime.now());
    modelApiKeyMapper.updateById(modelApiKeyPo);
    Integer modelCategoryType = modelPo.getClassification();
    ModelInstanceConfigDto modelInstanceConfigDto = ModelInstanceConfigDto.builder()
        .modelName(modelPo.getInternalName()).baseUrl(modelPo.getUrl()).apiKey(modelPo.getApiKey()).llmType(
            ClassificationEnum.TEXTGENERATION.getKey().equals(modelCategoryType) ? "chat" : "")
        .contextLength(modelPo.getContextLength()).maxTokenLength(modelPo.getContextLength())
        .isSupportVision(modelPo.getIsSupportVisual())
        .isSupportFunction(modelPo.getIsSupportFunction()).build();
    ModelInvokeParamDto modelInvokeParamDto = ModelInvokeParamDto.builder()
        .modelInstanceType(ClassificationEnum.getValueByKey(modelCategoryType))
        .modelInstanceProvider(modelPo.getLoadingMode()).modelInstanceConfig(modelInstanceConfigDto)
        .modelInputs(modelInvokeBaseDto.getModelInputs())
        .modelParameters(modelInvokeBaseDto.getModelParameters()).build();
    executor.execute(() -> {
      try (HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
        String requestBody = new ObjectMapper().writeValueAsString(modelInvokeParamDto);
        log.info("准备调用流式预训练模型接口，URL: {}，请求体: {}", preTrainedModelInvokeUrl,
            requestBody);
        HttpRequest sseRequest = HttpRequest.newBuilder().uri(URI.create(preTrainedModelInvokeUrl))
            .header("Accept", "text/event-stream")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        // 发起请求
        CompletableFuture<HttpResponse<Stream<String>>> completableFuture = client.sendAsync(
            sseRequest, BodyHandlers.ofLines());
        completableFuture.thenAccept(
            response -> response.body().filter(StrUtil::isNotBlank).forEach(line -> {
              log.info("line数据返回：{}", line);
              if (JSONUtil.isTypeJSON(line)) {
                ModelInvokeResponse modelInvokeResponse = JSONUtil.toBean(line,
                    ModelInvokeResponse.class);
                log.info("返回的code码：{}", modelInvokeResponse.getCode());
                if (ResultCodeEnum.SUCCESS.getCode() != modelInvokeResponse.getCode()) {
                  try {
                    emitter.send(
                        SseEmitter.event().name("error").data(modelInvokeResponse.getMsg()));
                  } catch (Exception e) {
                    log.error("流式预训练模型调用接口返回数据异常信息", e);
                  } finally {
                    emitter.complete();
                  }
                }
                Object data = modelInvokeResponse.getData();
                try {
                  log.info("需要发送消息：{}", JSONUtil.toJsonStr(data));
                  // 发送消息
                  emitter.send(JSONUtil.toJsonStr(data));
                  if (Boolean.TRUE.equals(modelInvokeResponse.getDone())) {
                    emitter.send(SseEmitter.event().name("close").data("连接关闭"));
                    emitter.complete();
                  }
                } catch (Exception e) {
                  if (StrUtil.contains(e.getMessage(), "Broken pipe")) {
                    log.info("客户端或服务端关闭连接！");
                    emitter.complete();
                  } else {
                    log.error("发送sse消息失败！", e);
                    emitter.completeWithError(new RuntimeException(e.getMessage()));
                  }
                }
              } else {
                // 数据异常
                log.error("流式预训练模型调用接口返回数据异常: , 返回数据：{}", line);
                try {
                  emitter.send(
                      SseEmitter.event().name("error").data("流式预训练模型调用接口返回数据异常"));
                } catch (Exception e) {
                  log.error("流式预训练模型调用接口返回数据异常信息异常出错", e);
                } finally {
                  emitter.complete();
                }
              }
            }));
      } catch (Exception e) {
        log.error("流式预训练模型调用接口失败！", e);
        try {
          emitter.send(SseEmitter.event().name("error").data("流式预训练模型调用接口异常"));
        } catch (Exception err) {
          log.error("流式预训练模型调用接口返回数据异常信息异常出错", err);
        } finally {
          emitter.complete();
        }
      }
    });
    return emitter;
  }

}
