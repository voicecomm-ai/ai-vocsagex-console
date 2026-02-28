package cn.voicecomm.ai.voicesagex.console.application.controller;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.CodeRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.CodeResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.CodeWebRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.JsonSchemaRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.JsonSchemaResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.JsonSchemaWebRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.OptimizePromptRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.OptimizePromptResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.OptimizePromptResponse.OptimizedPromptData;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.OptimizePromptWebRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 内容生成
 *
 * @author wangf
 * @date 2025/6/4 下午 4:21
 */
@RestController
@RequestMapping("/prompt")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PromptGenerateController {

  @DubboReference
  public ModelService modelService;

  /**
   * 提示词接口路径
   */
  @Value("${algoUrlPrefix}${prompt.url}")
  private String promptGenerateUrl;

  @Value("${algoUrlPrefix}${prompt.jsonSchemaUrl}")
  private String jsonSchemaUrl;

  @Value("${algoUrlPrefix}${prompt.codeUrl}")
  private String codeUrl;

  private final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  private static final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();


  /**
   * 提示词生成
   *
   * @param request request
   * @return 返回包含操作结果的封装对象
   */
  @PostMapping(value = "/promptGenerate", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.TEXT_EVENT_STREAM_VALUE})
  public Object promptGenerate(@RequestBody @Validated OptimizePromptWebRequest request) {
    if (Boolean.TRUE.equals(request.getStream())) {
      log.info("提示词生成，sessionId：{}", request.getSseConnectId());
      return promptGenerateStream(request);
    }
    log.info("提示词非流式生成，sessionId：{}", request.getSseConnectId());
    return promptGenerateSync(request);
  }

  public SseEmitter promptGenerateStream(OptimizePromptWebRequest request) {
    String sseConnectId = request.getSseConnectId();
    // 创建SseEmitter对象
    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    if (emitterMap.containsKey(sseConnectId)) {
      try {
        emitter.send(SseEmitter.event().name("error").id(sseConnectId).data("无效的sse连接Id"));
      } catch (Exception e) {
        log.error("发送无效的sse连接Id信息异常出错", e);
      } finally {
        emitter.complete();
      }
      return emitter;
    }
    emitterMap.put(sseConnectId, emitter);
    emitter.onCompletion(() -> {
      log.info("提示词生成--Sse连接已关闭，sessionId：{}", sseConnectId);
      emitterMap.remove(sseConnectId);
    });
    emitter.onError(callBack -> {
      log.info("提示词生成--错误，sessionId：{}", sseConnectId);
      emitterMap.remove(sseConnectId);
    });
    emitter.onTimeout(() -> {
      log.info("提示词生成--超时，sessionId：{}", sseConnectId);
      emitterMap.remove(sseConnectId);
    });
    log.info("开始处理提示词生成请求，modelId: {}, prompt: {}", request.getModelId(),
        request.getPrompt());

    executor.execute(() -> {
      OptimizePromptRequest promptRequest = new OptimizePromptRequest().setPrompt(request.getPrompt())
          .setInstruction(request.getInstruction()).setStream(request.getStream());
      CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(request.getModelId());
      if (!commonRespDto.isOk() || commonRespDto.getData() == null) {
        log.warn("未找到对应的模型信息，modelId: {}", request.getModelId());
        try {
          emitter.send(SseEmitter.event().name("error").id(sseConnectId).data("请选择模型"));
        } catch (Exception e) {
          log.error("发送无效的模型ID信息异常出错", e);
        } finally {
          emitter.complete();
        }
        return;
      }
      if (BooleanUtil.isFalse(commonRespDto.getData().getIsShelf())) {
        log.warn("模型已下架，模型信息，modelId: {}", request.getModelId());
        try {
          emitter.send(SseEmitter.event().name("error").id(sseConnectId).data("模型已下架"));
        } catch (Exception e) {
          log.error("发送模型已下架信息异常出错", e);
        } finally {
          emitter.complete();
        }
        return;
      }
      ModelDto modelDto = commonRespDto.getData();
      JSONObject jsonObject = buildPromptRequest(modelDto);
      promptRequest.setModel_instance_config(jsonObject);
      final String requestBody = JSONUtil.toJsonStr(promptRequest);
      log.info("准备调用提示词生成接口，session id：{}, URL: {}，请求体: {}", sseConnectId,
          promptGenerateUrl, requestBody);
      try (HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
        HttpRequest sseRequest = HttpRequest.newBuilder().uri(URI.create(promptGenerateUrl))
            .header("Accept", "text/event-stream")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        // 发起请求
        CompletableFuture<HttpResponse<Stream<String>>> completableFuture = client.sendAsync(
            sseRequest, BodyHandlers.ofLines());
        completableFuture.thenAccept(
            response -> response.body().filter(StrUtil::isNotBlank).forEach(line -> {
              if (!emitterMap.containsKey(sseConnectId)) {
                return;
              }
              if (line.startsWith("data:")) {
                log.info("提示词生成接口返回数据：sessionId：{},data:{}", sseConnectId, line);
                OptimizePromptResponse optimizePromptResponse = JSONUtil.toBean(line.substring(5),
                    OptimizePromptResponse.class);
                OptimizedPromptData data = optimizePromptResponse.getData();
                try {
                  // 发送消息
                  emitter.send(SseEmitter.event().name("message").data(JSONUtil.toJsonStr(data)));
                  if (Boolean.TRUE.equals(optimizePromptResponse.getDone())) {
                    emitter.send(SseEmitter.event().name("close").data("连接关闭"));
                    emitter.complete();
                  }
                } catch (Exception e) {
                  if (StrUtil.contains(e.getMessage(), "Broken pipe")) {
                    log.info("客户端或服务端关闭连接！sessionId：{}", sseConnectId);
                    emitter.complete();
                  } else {
                    log.error("发送sse消息失败！sessionId：{}", sseConnectId, e);
                    emitter.completeWithError(new RuntimeException(e.getMessage()));

                  }
                }
              } else {
                // 数据异常
                log.error("提示词生成接口返回数据异常: sessionId：{}, 返回数据：{}", sseConnectId,
                    line);
                try {
                  emitter.send(SseEmitter.event().name("error").id(sseConnectId)
                      .data("提示词生成接口返回数据异常"));
                } catch (Exception e) {
                  log.error("发送提示词生成接口返回数据异常信息异常出错", e);
                } finally {
                  emitter.complete();
                }
              }
            }));
      } catch (Exception e) {
        log.error("请求提示词生成接口失败！sessionId：{}", sseConnectId, e);
      }
    });
    return emitter;
  }

  /**
   * 构建请求
   *
   * @param modelDto modelDto
   */
  public static JSONObject buildPromptRequest(ModelDto modelDto) {

    log.info("获取到应用模型信息，modelName: {}", modelDto.getName());
    JSONObject jsonObject = JSONUtil.createObj();
    jsonObject.putOnce("llm_type", "chat");
    jsonObject.putOnce("model_name", modelDto.getInternalName());
    jsonObject.putOnce("base_url", modelDto.getUrl());
    jsonObject.putOnce("apikey", modelDto.getApiKey());
    jsonObject.putOnce("context_length", modelDto.getContextLength());
    jsonObject.putOnce("max_token_length", modelDto.getTokenMax());
    jsonObject.putOnce("is_support_vision", modelDto.getIsSupportFunction());
    jsonObject.putOnce("is_support_function", modelDto.getIsSupportVisual());
    return jsonObject;
  }

  private ModelDto validateAndFetchModel(Integer modelId) {
    CommonRespDto<Boolean> available = modelService.isAvailable(modelId);
    if (!available.isOk()) {
      log.warn("模型不可用，modelId: {}", modelId);
      return null;
    }
    CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(modelId);
    if (!commonRespDto.isOk() || commonRespDto.getData() == null) {
      log.warn("未找到对应模型信息，modelId: {}", modelId);
      return null;
    }
    ModelDto modelDto = commonRespDto.getData();
    if (BooleanUtil.isFalse(modelDto.getIsShelf())) {
      log.warn("模型已下架，modelId: {}", modelId);
      return null;
    }
    return modelDto;
  }

  public Result<OptimizePromptResponse> promptGenerateSync(OptimizePromptWebRequest request) {
    String sseConnectId = request.getSseConnectId();

    ModelDto modelDto = validateAndFetchModel(request.getModelId());
    if (modelDto == null) {
      return Result.error("模型不可用或不存在", null);
    }
    OptimizePromptRequest promptRequest = new OptimizePromptRequest().setPrompt(request.getPrompt())
        .setInstruction(request.getInstruction()).setStream(request.getStream());
    JSONObject jsonObject = buildPromptRequest(modelDto);
    promptRequest.setModel_instance_config(jsonObject);

    final String requestBody = JSONUtil.toJsonStr(promptRequest);
    log.info("准备调用非流式提示词生成接口，session id：{}, URL: {}，请求体: {}", sseConnectId,
        promptGenerateUrl, requestBody);
    try (cn.hutool.http.HttpResponse response = HttpUtil.createPost(promptGenerateUrl)
        .body(requestBody).execute()) {
      String body = response.body();
      OptimizePromptResponse bean = JSONUtil.toBean(body, OptimizePromptResponse.class);
      log.info("调用非流式提示词生成接口成功，sessionId: {}, 返回数据: {}", sseConnectId, body);
      return Result.success(bean);
    } catch (Exception e) {
      log.error("调用非流式提示词生成接口失败，sessionId: {}", sseConnectId, e);
      return Result.error("请求失败", null);
    }
  }

  /**
   * jsonSchema生成
   *
   * @param request request
   * @return Result
   */
  @PostMapping("/jsonSchemaGenerate")
  public Result<JsonSchemaResponse> jsonSchemaGenerate(
      @RequestBody @Validated JsonSchemaWebRequest request) {
    String sseConnectId = request.getSseConnectId();
    ModelDto modelDto = validateAndFetchModel(request.getModelId());
    if (modelDto == null) {
      return Result.error("模型不可用或不存在", null);
    }
    JsonSchemaRequest promptRequest = new JsonSchemaRequest().setDescription(
        request.getDescription()).setModel_parameters(JSONUtil.createObj());
    JSONObject jsonObject = buildPromptRequest(modelDto);
    promptRequest.setModel_instance_config(jsonObject);
    final String requestBody = JSONUtil.toJsonStr(promptRequest);
    log.info("准备调用jsonSchema生成接口，session id：{}, URL: {}，请求体: {}", sseConnectId,
        promptGenerateUrl, requestBody);
    try (cn.hutool.http.HttpResponse response = HttpUtil.createPost(jsonSchemaUrl).body(requestBody)
        .execute()) {
      String body = response.body();
      JsonSchemaResponse bean = JSONUtil.toBean(body, JsonSchemaResponse.class);
      log.info("调用jsonSchema生成接口成功，sessionId: {}, 返回数据: {}", sseConnectId, body);
      return Result.success(bean);
    } catch (Exception e) {
      log.error("调用jsonSchema生成接口失败，sessionId: {}", sseConnectId, e);
      return Result.error("请求失败", null);
    }
  }


  /**
   * code生成
   *
   * @param request request
   * @return Result
   */
  @PostMapping("/codeGenerate")
  public Result<CodeResponse> codeGenerate(@RequestBody @Validated CodeWebRequest request) {
    String sseConnectId = request.getSseConnectId();
    ModelDto modelDto = validateAndFetchModel(request.getModelId());
    if (modelDto == null) {
      return Result.error("模型不可用或不存在", null);
    }
    CodeRequest promptRequest = new CodeRequest().setInstruction(request.getInstruction())
        .setLanguage(request.getLanguage()).setStream(false)
        .setModel_parameters(JSONUtil.createObj());
    JSONObject jsonObject = buildPromptRequest(modelDto);
    promptRequest.setModel_instance_config(jsonObject);
    final String requestBody = JSONUtil.toJsonStr(promptRequest);
    log.info("准备调用code生成接口，session id：{}, URL: {}，请求体: {}", sseConnectId, codeUrl,
        requestBody);
    try (cn.hutool.http.HttpResponse response = HttpUtil.createPost(codeUrl).body(requestBody)
        .execute()) {
      String body = response.body();
      CodeResponse bean = JSONUtil.toBean(body, CodeResponse.class);
      log.info("调用code生成接口成功，sessionId: {}, 返回数据: {}", sseConnectId, body);
      return Result.success(bean);
    } catch (Exception e) {
      log.error("调用code生成接口失败，sessionId: {}", sseConnectId, e);
      return Result.error("请求失败", null);
    }
  }

}
