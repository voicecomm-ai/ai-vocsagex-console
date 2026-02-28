package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentChatHistoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.ChatHistorySaveDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.InitChatReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.chat.ChatResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.chat.ChatResponse.AdditionalKwargs;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.chat.ChatResponse.ChatData;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.AgentTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.application.service.AgentConfigurationService;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatReqVo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 对话
 */
@RestController
@RequestMapping("/chat")
@Slf4j
@RequiredArgsConstructor
public class ChatController {

  private final static ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
  private final AgentChatHistoryService agentChatHistoryService;
  private final ApplicationService applicationService;

  private final AgentConfigurationService agentConfigurationService;

  @DubboReference
  public ModelService modelService;

  @DubboReference
  public AgentInfoService agentInfoService;


  /**
   * 对话接口
   */
  @Value("${algoUrlPrefix}${chat.agentChat}")
  private String agentChatUrl;


  /**
   * 多智能体对话接口
   */
  @Value("${algoUrlPrefix}${chat.multipleAgentChat}")
  private String multipleAgentChatUrl;


  /**
   * 测试对话
   *
   * @param body 请求体
   * @return sse
   */
  @PostMapping(value = "/test", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
  public SseEmitter chatTest(@RequestBody TestChatReqVo body) {

    Integer userId = UserAuthUtil.getUserId();

    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    executeSingleAgentChat(body, emitter, userId);

    return emitter;
  }

  /**
   * 测试对话--多智能体
   *
   * @param body 请求体
   * @return sse
   */
  @PostMapping(value = "/multipleAgentTest", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
  public SseEmitter multipleAgentTest(@RequestBody TestChatReqVo body) {

    Integer userId = UserAuthUtil.getUserId();

    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    executeMultipleAgentChat(body, emitter, userId);

    return emitter;
  }

  /**
   * 执行单智能体对话
   *
   * @param chatReqVo 请求体
   * @param emitter   sse
   * @param userId    用户id
   * @return json对象
   */
  public JSONObject executeSingleAgentChat(TestChatReqVo chatReqVo, SseEmitter emitter,
      Integer userId) {
    log.info("执行单智能体对话，conversationId：{}", chatReqVo.getConversationId());

    // 使用Future来跟踪特定任务
    CompletableFuture<JSONObject> futureResult = new CompletableFuture<>();
    // 监听emitter连接状态，当连接断开时取消后台请求
    AtomicReference<CompletableFuture<Void>> streamFutureRef = new AtomicReference<>();

    try {
      emitter.send(SseEmitter.event().comment("connected"));
    } catch (IOException e) {
      log.info("发送首包异常 {}", e.getMessage());
      emitter.completeWithError(e);
    }

    // 启动虚拟线程心跳
    Thread heartbeatThread = Thread.startVirtualThread(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          emitter.send(SseEmitter.event().comment("ping"));
          Thread.sleep(1_000L); // 心跳
        }
      } catch (IOException e) {
        log.info("测试对话心跳检测--连接已关闭");
        // 客户端断开
        emitter.completeWithError(e);
      } catch (InterruptedException e) {
        // 正常中断，退出线程
        Thread.currentThread().interrupt();
      }
    });

    emitter.onTimeout(() -> {
      log.info("测试对话生成--超时");
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });
    emitter.onCompletion(() -> {
      log.info("测试对话生成--Sse连接已关闭");
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });
    emitter.onError((throwable) -> {
      log.info("测试对话生成--错误", throwable);
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });

    JSONObject chatRequestBody = JSONUtil.createObj();

    executor.execute(() -> {
      try (HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build()) {
        // 获取智能体信息
        AgentInfoResponseDto agentInfo = getAgentInfoResponseDto(chatReqVo, emitter, futureResult);
        if (agentInfo == null) {
          return;
        }

        // 测试对话，如果是url对话，初始化对话并且总结标题，生成一条对话记录
        int urlChatId = initChat(chatReqVo, emitter, agentInfo, futureResult);

        // 检查并获取模型
        ModelDto chatModel = checkThenGetModel(emitter, agentInfo, chatReqVo.getConversationId(),
            futureResult);
        if (chatModel == null) {
          return;
        }
        // 模型配置
        setModelConfig(chatRequestBody, chatModel);
        // 用户输入配置
        setUserInputConfig(chatReqVo, chatRequestBody, agentInfo);
        // 长期记忆配置
        agentConfigurationService.setLongtermConfig(chatReqVo, userId, chatRequestBody, agentInfo);
        // mcp 配置
        if (!agentConfigurationService.validateAndConfigureMcpServices(chatReqVo, emitter,
            agentInfo, futureResult, chatRequestBody)) {
          return;
        }
        // 知识库配置
        if (!agentConfigurationService.validateAndConfigureKnowledgeBase(chatReqVo, emitter,
            futureResult, agentInfo, chatRequestBody)) {
          return;
        }
        HttpRequest sseRequest = HttpRequest.newBuilder().uri(URI.create(agentChatUrl))
            .header("Accept", "text/event-stream")
            .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(chatRequestBody))).build();
        TimeInterval timeInterval = DateUtil.timer();
        // 发起请求
        log.info("测试对话接口调用开始，请求参数：{}", JSONUtil.toJsonStr(chatRequestBody));
        CompletableFuture<Void> streamFuture = client.sendAsync(sseRequest, BodyHandlers.ofLines())
            .thenAccept(resp -> resp.body().filter(StrUtil::isNotBlank).forEach(line -> {
              try {
                if (line.startsWith("data:")) {
                  ChatResponse chatResponse = JSONUtil.toBean(line.substring(5),
                      ChatResponse.class);
                  ChatData data = chatResponse.getData();
                  if (chatResponse.getCode() != 1000) {
                    log.info("测试对话接口失败,是否为工作流 ：{}，耗时：{} ，数据：{}",
                        Boolean.TRUE.equals(chatReqVo.getIsSync()), timeInterval.intervalPretty(),
                        line);
                    emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                        .data(chatResponse.getMsg()));
                  }
                  log.info("测试对话接口返回，是否为工作流 ：{}，耗时：{} ，数据：{}",
                      Boolean.TRUE.equals(chatReqVo.getIsSync()), timeInterval.intervalPretty(),
                      line);
                  JSONObject eventDataJson = JSONUtil.parseObj(data);

                  if (urlChatId != 0) {
                    eventDataJson.putOnce("urlChatId", urlChatId);
                  }

                  emitter.send(SseEmitter.event().name("msg").id(chatReqVo.getConversationId())
                      .data(Objects.isNull(eventDataJson) ? chatResponse.getMsg()
                          : JSONUtil.toJsonStr(eventDataJson)));

                  if (Boolean.TRUE.equals(chatResponse.getDone())) {
                    // 测试对话完成，如果是url对话，保存到chat_history
                    if (StrUtil.isNotBlank(chatReqVo.getToken()) && ObjUtil.isNotNull(
                        eventDataJson.getInt("urlChatId"))) {

                      JSONObject humanQuestion = getHumanQuestion(data);

                      // url对话直接生成记录
                      assert humanQuestion != null;
                      agentChatHistoryService.updateChatHistory(
                          ChatHistorySaveDto.builder().id(eventDataJson.getInt("urlChatId"))
                              .chatHistory(List.of(humanQuestion, data.getChat_history().getLast()))
                              .build());
                    }
                    emitter.send(SseEmitter.event().name("close").id(chatReqVo.getConversationId())
                        .data("连接关闭"));
                    log.info("测试对话接口调用完成");
                    futureResult.complete(eventDataJson);
                    emitter.complete();
                  }
                } else {
                  // 数据异常
                  log.error("测试对话接口数据异常: {}", line);
                  emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                      .data("测试对话接口数据异常：" + line));
                  completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
                }
              } catch (Exception e) {
                if (StrUtil.contains(e.getMessage(), "Broken pipe")) {
                  log.warn("客户端或服务端关闭连接", e);
                }
                try {
                  if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
                    emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                        .data("测试对话接口数据异常：" + line));
                  }
                } catch (Exception ex) {
                  log.error("发送测试对话接口数据异常出错", ex);
                } finally {
                  completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
                }
              }
            }));
        streamFutureRef.set(streamFuture);
      } catch (Exception e) {
        log.error("异步处理测试对话失败", e);
        try {
          if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
            emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                .data("异步处理测试对话失败"));
          }
        } catch (Exception ex) {
          log.error("发送异步处理测试对话失败消息失败", e);
        } finally {
          completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
        }
      }
    });
    return getChatResult(chatReqVo, futureResult);

  }


  /**
   * 多智能体对话
   *
   * @param chatReqVo 请求参数
   * @param emitter   sse
   * @param userId    用户id
   * @return JSONObject
   */
  public JSONObject executeMultipleAgentChat(TestChatReqVo chatReqVo, SseEmitter emitter,
      Integer userId) {

    log.info("执行多智能体对话，conversationId：{}", chatReqVo.getConversationId());
    // 使用Future来跟踪特定任务
    CompletableFuture<JSONObject> futureResult = new CompletableFuture<>();
    // 监听emitter连接状态，当连接断开时取消后台请求
    AtomicReference<CompletableFuture<Void>> streamFutureRef = new AtomicReference<>();
    try {
      emitter.send(SseEmitter.event().comment("connected"));
    } catch (IOException e) {
      log.error("发送多智能体首包异常 {}", e.getMessage());
      emitter.completeWithError(e);
    }
    // 启动虚拟线程心跳
    Thread heartbeatThread = Thread.startVirtualThread(() -> {
      try {
        while (!Thread.currentThread().isInterrupted()) {
          emitter.send(SseEmitter.event().comment("ping"));
          Thread.sleep(1_000L); // 心跳
        }
      } catch (IOException e) {
        log.info("多智能体对话心跳检测--连接已关闭");
        // 客户端断开
        emitter.completeWithError(e);
      } catch (InterruptedException e) {
        // 正常中断，退出线程
        Thread.currentThread().interrupt();
      }
    });

    emitter.onTimeout(() -> {
      log.info("多智能体对话生成--超时");
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });
    emitter.onCompletion(() -> {
      log.info("多智能体对话生成--Sse连接已关闭");
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });
    emitter.onError((callBack) -> {
      log.info("多智能体对话生成--错误", callBack);
      if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
        streamFutureRef.get().cancel(true);
      }
      heartbeatThread.interrupt();
    });
    JSONObject chatRequestBody = JSONUtil.createObj();
    executor.execute(() -> {
      try (HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build()) {
        // 获取智能体信息
        AgentInfoResponseDto agentInfo = getAgentInfoResponseDto(chatReqVo, emitter, futureResult);
        if (agentInfo == null) {
          return;
        }
        // 多智能体对话，如果是url对话，初始化对话并且总结标题，生成一条对话记录
        int urlChatId = initChat(chatReqVo, emitter, agentInfo, futureResult);

        // 检查并获取模型
        ModelDto chatModel = checkThenGetModel(emitter, agentInfo, chatReqVo.getConversationId(),
            futureResult);
        if (chatModel == null) {
          return;
        }
        // 模型配置
        setMultipleModelConfig(chatRequestBody, chatModel);
        // 用户输入配置
        if (!validateAndConfigureMultiAgentInput(chatReqVo, chatRequestBody, agentInfo, emitter,
            futureResult)) {
          return;
        }

        // 长期记忆配置
        agentConfigurationService.setLongtermConfig(chatReqVo, userId, chatRequestBody, agentInfo);

        HttpRequest sseRequest = HttpRequest.newBuilder().uri(URI.create(multipleAgentChatUrl))
            .header("Accept", "text/event-stream")
            .POST(BodyPublishers.ofString(JSONUtil.toJsonStr(chatRequestBody))).build();
        TimeInterval timeInterval = DateUtil.timer();
        // 发起请求
        log.info("多智能体对话接口调用开始，请求参数：{}", JSONUtil.toJsonStr(chatRequestBody));
        // key 任务名   value（key 智能体名称--value内容）
        Map<String, LinkedHashMap<String, StringBuilder>> subAgentProcess = new LinkedHashMap<>();
        JSONArray subAgentThinkingList = JSONUtil.createArray();
        CompletableFuture<Void> streamFuture = client.sendAsync(sseRequest, BodyHandlers.ofLines())
            .thenAccept(resp -> resp.body().filter(StrUtil::isNotBlank).forEach(line -> {
              try {
                log.info("多智能体对话接口返回,是否为工作流 ：{}，耗时：{} ，数据：{}",
                    Boolean.TRUE.equals(chatReqVo.getIsSync()), timeInterval.intervalPretty(),
                    line);
                if (line.startsWith("data:")) {
                  ChatResponse chatResponse = JSONUtil.toBean(line.substring(5),
                      ChatResponse.class);
                  ChatData data = chatResponse.getData();
                  if (chatResponse.getCode() != 1000) {
                    emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                        .data("远程接口错误：" + chatResponse.getMsg()));
                  }
                  JSONObject eventDataJson = JSONUtil.parseObj(data);

                  if (urlChatId != 0) {
                    eventDataJson.putOnce("urlChatId", urlChatId);
                  }
                  // 子智能体消息
                  Optional<String> agentNameOpt = Optional.ofNullable(data)
                      .map(ChatData::getAdditional_kwargs).map(AdditionalKwargs::getAgent_name);
                  if (agentNameOpt.isPresent() && StrUtil.isNotBlank(agentNameOpt.get())) {
                    if ("FAILED".equals(data.getAdditional_kwargs().getAgent_status())) {
                      emitter.send(
                          SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                              .data("子智能体执行失败"));
                    }
                    JSONObject additionalKwargs = eventDataJson.getJSONObject("additional_kwargs");
                    String agentName = additionalKwargs.getStr("agent_name");
                    agentName = agentName.substring(agentName.indexOf("_") + 1);
                    additionalKwargs.set("agent_name", agentName);
                    eventDataJson.set("additional_kwargs", additionalKwargs);

                    // 拼接子智能体消息  任务-智能体-智能体内容
                    LinkedHashMap<String, StringBuilder> taskMap = subAgentProcess.computeIfAbsent(
                        data.getAdditional_kwargs().getTask_name(), k -> new LinkedHashMap<>());
                    taskMap.computeIfAbsent(agentName, k -> new StringBuilder())
                        .append(data.getAdditional_kwargs().getAgent_content());
                  }

                  emitter.send(SseEmitter.event().name("msg").id(chatReqVo.getConversationId())
                      .data(Objects.isNull(eventDataJson) ? chatResponse.getMsg()
                          : JSONUtil.toJsonStr(eventDataJson)));

                  if (Boolean.TRUE.equals(chatResponse.getDone())) {
                    // getAgent_name 为空时，代表子智能体执行结束，开始执行主智能体消息
                    // 拼接所有子智能体消息，作为子智能体执行过程的消息
                    subAgentProcess.forEach((key, value) -> {
                      JSONObject jsonObject = JSONUtil.createObj();
                      jsonObject.putOnce("task_name", key);
                      JSONArray agentArray = JSONUtil.createArray();
                      value.forEach((k, v) -> {
                        JSONObject agentJson = JSONUtil.createObj();
                        agentJson.putOnce("agent_name", k);
                        agentJson.putOnce("agent_content", v);
                        agentArray.add(agentJson);
                      });
                      jsonObject.putOnce("agent_array", agentArray);
                      subAgentThinkingList.add(jsonObject);
                    });
                    log.info("多智能体思考过程：{}", JSONUtil.toJsonStr(subAgentThinkingList));
                    // 多智能体对话完成，如果是url对话，保存到chat_history
                    if (StrUtil.isNotBlank(chatReqVo.getToken()) && ObjUtil.isNotNull(
                        eventDataJson.getInt("urlChatId"))) {
                      // url对话直接生成记录
                      assert data != null;
                      JSONObject last = data.getChat_history().getLast();
                      last.putOnce("subAgentThinkingProcess", subAgentThinkingList);
                      JSONObject humanQuestion = getHumanQuestion(data);
                      assert humanQuestion != null;
                      agentChatHistoryService.updateChatHistory(
                          ChatHistorySaveDto.builder().id(eventDataJson.getInt("urlChatId"))
                              .chatHistory(List.of(humanQuestion, last)).build());
                    }
                    emitter.send(SseEmitter.event().name("close").id(chatReqVo.getConversationId())
                        .data("连接关闭"));
                    log.info("多智能体对话接口调用完成");
                    futureResult.complete(eventDataJson);
                    emitter.complete();
                  }
                } else {
                  log.error("多智能体对话接口数据异常: {}", line);
                  emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                      .data("多智能体对话接口数据异常：" + line));
                  completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
                }
              } catch (Exception e) {
                if (StrUtil.contains(e.getMessage(), "Broken pipe")) {
                  log.warn("多智能体客户端或服务端关闭连接", e);
                }
                try {
                  if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
                    emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                        .data("多智能体对话接口数据异常：" + line));
                  }
                } catch (Exception ex) {
                  log.error("发送多智能体对话接口数据异常出错", ex);
                } finally {
                  completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
                }
              }
            }));
        streamFutureRef.set(streamFuture);
      } catch (Exception e) {
        log.error("异步处理多智能体对话失败", e);
        try {
          if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
            emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
                .data("异步处理多智能体对话失败"));
          }
        } catch (Exception ex) {
          log.error("发送异步处理多智能体对话失败消息失败", e);
        } finally {
          completeSseWithNullResultByStreamFuture(emitter, futureResult, streamFutureRef);
        }
      }
    });
    return getChatResult(chatReqVo, futureResult);
  }

  @Nullable
  private JSONObject getChatResult(TestChatReqVo chatReqVo,
      CompletableFuture<JSONObject> futureResult) {
    if (BooleanUtil.isTrue(chatReqVo.getIsSync())) {
      try {
        // 等待特定任务完成，而不是等待整个executor终止
        return futureResult.get(120, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      } catch (TimeoutException e) {
        log.error("同步等待超时", e);
        throw new RuntimeException(e);
      }
    }
    return null;
  }


  /**
   * 获取Agent信息
   *
   * @param chatReqVo    请求参数
   * @param emitter      发送器
   * @param futureResult 异步结果
   * @return AgentInfoResponseDto
   */
  @Nullable
  private AgentInfoResponseDto getAgentInfoResponseDto(TestChatReqVo chatReqVo, SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult) {
    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(
        chatReqVo.getApplicationId());
    if (!dtoCommonRespDto.isOk()) {
      log.error("获取应用信息异常");
      try {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data(dtoCommonRespDto.getMsg()));
      } catch (Exception e) {
        log.error("发送获取应用信息异常出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return null;
    }
    AgentInfoResponseDto agentInfo;
    if (ApplicationStatusEnum.EXPERIENCE.getKey().equals(chatReqVo.getRunType())) {
      CommonRespDto<AgentInfoResponseDto> agentInfoResp = agentInfoService.getExperienceInfo(
          chatReqVo.getApplicationId());
      agentInfo = agentInfoResp.getData();
      if (!agentInfoResp.isOk() || Objects.isNull(agentInfo)) {
        log.error("获取发现页Agent信息异常");
        try {
          emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
              .data("获取发现页Agent信息异常"));
        } catch (Exception e) {
          log.error("发送获取发现页Agent信息异常出错", e);
        } finally {
          completeSseWithNullResult(emitter, futureResult);
        }
        return null;
      }
    } else if (ApplicationStatusEnum.PUBLISHED.getKey().equals(chatReqVo.getRunType())) {
      CommonRespDto<AgentInfoResponseDto> agentInfoResp = agentInfoService.getPublishedInfo(
          chatReqVo.getApplicationId());
      agentInfo = agentInfoResp.getData();
      if (!agentInfoResp.isOk() || Objects.isNull(agentInfo)) {
        log.error("获取已发布Agent信息异常");
        try {
          emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
              .data("获取已发布Agent信息异常"));
        } catch (Exception e) {
          log.error("发送获取已发布Agent信息异常出错", e);
        } finally {
          completeSseWithNullResult(emitter, futureResult);
        }
        return null;
      }
    } else {
      CommonRespDto<AgentInfoResponseDto> agentInfoResp = agentInfoService.getInfo(
          chatReqVo.getApplicationId());
      agentInfo = agentInfoResp.getData();
      if (!agentInfoResp.isOk() || Objects.isNull(agentInfo)) {
        log.error("获取Agent信息异常：{}", agentInfo);
        try {
          emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
              .data("获取Agent信息异常"));
        } catch (Exception e) {
          log.error("发送获取Agent信息异常出错", e);
        } finally {
          completeSseWithNullResult(emitter, futureResult);
        }
        return null;
      }

    }
    if (AgentTypeEnum.MULTIPLE.getKey().equals(agentInfo.getAgentType())) {
      if (agentInfo.getSubAgentAppIds() == null || agentInfo.getSubAgentAppIds().length == 0) {
        log.error("多智能体应用没有配置子智能体");
        try {
          emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
              .data("请添加子智能体"));
        } catch (Exception e) {
          log.error("发送多智能体应用没有配置子智能体信息出错", e);
        } finally {
          completeSseWithNullResult(emitter, futureResult);
        }
        return null;
      }
    }
    return agentInfo;
  }

  @Nullable
  private ModelDto checkThenGetModel(SseEmitter emitter, AgentInfoResponseDto agentInfo,
      String conversationId, CompletableFuture<JSONObject> futureResult) {
    CommonRespDto<ModelDto> chatModelResp = modelService.getInfo(agentInfo.getModelId());
    ModelDto chatModel = chatModelResp.getData();
    if (!chatModelResp.isOk() || Objects.isNull(chatModel) || Boolean.FALSE.equals(
        chatModel.getIsShelf())) {
      log.error("对话模型获取异常：{}", chatModel);
      try {
        emitter.send(SseEmitter.event().name("error").id(conversationId)
            .data(Objects.isNull(chatModel) ? "请选择模型" : "对话模型已经下架"));
      } catch (Exception e) {
        log.error("发送对话模型已经下架信息出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return null;
    }
    return chatModel;
  }

  /**
   * 用户输入参数配置
   *
   * @param chatReqVo       请求参数
   * @param chatRequestBody 请求参数
   * @param agentInfo       应用信息
   */
  private static void setUserInputConfig(TestChatReqVo chatReqVo, JSONObject chatRequestBody,
      AgentInfoResponseDto agentInfo) {
    chatRequestBody.putOnce("chat_model_parameters", null); // todo
    chatRequestBody.putOnce("system_prompt", agentInfo.getPromptWords());
    chatRequestBody.putOnce("user_query", chatReqVo.getQuery());
    chatRequestBody.putOnce("chat_history", chatReqVo.getChatHistory());
    chatRequestBody.putOnce("chat_history_depth", agentInfo.getShortTermMemoryRounds());
    chatRequestBody.putOnce("inputs", chatReqVo.getInputs());
    // 推理模式
    chatRequestBody.putOnce("agent_mode", agentInfo.getAgentMode());
  }

  /**
   * 用户输入参数配置
   *
   * @param chatReqVo       请求参数
   * @param chatRequestBody 请求参数
   * @param agentInfo       应用信息
   */
  private boolean validateAndConfigureMultiAgentInput(TestChatReqVo chatReqVo,
      JSONObject chatRequestBody, AgentInfoResponseDto agentInfo, SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult) throws IOException, InterruptedException {
    chatRequestBody.putOnce("user_query", chatReqVo.getQuery());
    chatRequestBody.putOnce("chat_history", chatReqVo.getChatHistory());
    chatRequestBody.putOnce("chat_history_depth", agentInfo.getShortTermMemoryRounds());
    // 合作模式
    chatRequestBody.putOnce("execute_mode", agentInfo.getCooperateMode());

    // 子智能体的schema
    JSONArray subAgentSchemas = JSONUtil.createArray();
    JSONArray subAgentArgs = JSONUtil.createArray();
    List<String> deleteModelSubAgentNameList = new ArrayList<>();
    List<String> offShelfModelSubAgentNameList = new ArrayList<>();
    List<String> emptySchemaAgentList = new ArrayList<>();
    List<AgentInfoResponseDto> subAgentInfoList = new ArrayList<>();
    List<ApplicationDto> subAgentList = new ArrayList<>();
    for (int subAgentAppId : agentInfo.getSubAgentAppIds()) {
      CommonRespDto<ApplicationDto> subAgentApp = applicationService.getById(subAgentAppId);
      if (!subAgentApp.isOk() || Objects.isNull(subAgentApp.getData())) {
        log.warn("子智能体应用获取异常：{}", subAgentAppId);
        continue;
      }
      subAgentList.add(subAgentApp.getData());
      AgentInfoResponseDto subAgentInfo = agentInfoService.getPublishedInfo(subAgentAppId)
          .getData();
      if (!subAgentApp.isOk() || Objects.isNull(subAgentApp.getData())) {
        log.warn("子智能体信息获取异常：{}", subAgentAppId);
        continue;
      }
      subAgentInfoList.add(subAgentInfo);
      CommonRespDto<ModelDto> chatModelResp = modelService.getInfo(subAgentInfo.getModelId());
      ModelDto chatModel = chatModelResp.getData();
      if (!chatModelResp.isOk() || Objects.isNull(chatModel)) {
        // 模型不存在
        deleteModelSubAgentNameList.add(subAgentInfo.getApplicationName());
      } else if (Boolean.FALSE.equals(chatModel.getIsShelf())) {
        // 模型已经下架
        offShelfModelSubAgentNameList.add(subAgentInfo.getApplicationName());
      }
      String agentSchema = subAgentInfo.getAgentSchema();
      if (StrUtil.isBlank(agentSchema)) {
        log.warn("子智能体Schema信息为空 appId：{}", subAgentAppId);
        emptySchemaAgentList.add(subAgentInfo.getApplicationName());
      } else {
        subAgentSchemas.add(agentSchema);
        Object subAgentArgsObj = JSONUtil.getByPath(chatReqVo.getInputs(),
            String.valueOf(subAgentAppId));
        subAgentArgs.add(Objects.requireNonNullElseGet(subAgentArgsObj, JSONUtil::createObj));
      }
    }
    if (CollUtil.isEmpty(subAgentInfoList) || CollUtil.isEmpty(subAgentList)) {
      try {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data("子智能体不存在"));
      } catch (Exception e) {
        log.error("发送子智能体不存在出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return false;
    }
    if (CollUtil.isNotEmpty(emptySchemaAgentList)) {
      try {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data(String.join("、", emptySchemaAgentList) + "的智能体参数不存在"));
      } catch (Exception e) {
        log.error("发送智能体参数不存在出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return false;
    }
    if (CollUtil.isNotEmpty(deleteModelSubAgentNameList)) {
      try {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data(String.join("、", deleteModelSubAgentNameList) + "的模型不存在"));
      } catch (Exception e) {
        log.error("发送模型不存在出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return false;
    }
    if (CollUtil.isNotEmpty(offShelfModelSubAgentNameList)) {
      try {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data(String.join("、", offShelfModelSubAgentNameList) + "的模型已经下架"));
      } catch (Exception e) {
        log.error("发送模型已经下架出错", e);
      } finally {
        completeSseWithNullResult(emitter, futureResult);
      }
      return false;
    }
    // 知识库检测
    for (AgentInfoResponseDto subAgent : subAgentInfoList) {
      if (!agentConfigurationService.validateSubAgentKnowledgeBase(chatReqVo, emitter, futureResult,
          subAgent)) {
        return false;
      }
    }
    // mcp检测
    for (AgentInfoResponseDto subAgent : subAgentInfoList) {
      if (!agentConfigurationService.validateSubAgentMcpServices(chatReqVo, emitter, futureResult,
          subAgent)) {
        return false;
      }
    }

    // 子智能体变量  子智能体的系统提示词内的变量，列表内的顺序与子智能体一一对应
    chatRequestBody.putOnce("sub_agent_schemas", subAgentSchemas);
    chatRequestBody.putOnce("sub_agent_args", subAgentArgs);
    return true;
  }

  /**
   * 初始化对话
   *
   * @param chatReqVo    请求参数
   * @param emitter      sseEmitter
   * @param agentInfo    应用信息
   * @param futureResult 异步结果
   * @return urlChatId
   */
  private int initChat(TestChatReqVo chatReqVo, SseEmitter emitter, AgentInfoResponseDto agentInfo,
      CompletableFuture<JSONObject> futureResult) {
    // 如果没有token，则不需要初始化对话
    if (StrUtil.isBlank(chatReqVo.getToken())) {
      return 0;
    }
    // 如果有urlChatId，则不需要初始化对话
    if (chatReqVo.getUrlChatId() != null && chatReqVo.getUrlChatId() != 0) {
      return chatReqVo.getUrlChatId();
    }
    // url对话直接生成记录
    CommonRespDto<Integer> commonRespDto = agentChatHistoryService.initChat(
        new InitChatReqDto(agentInfo.getId(), agentInfo.getApplicationId(), chatReqVo.getToken(),
            chatReqVo.getUrlKey(), chatReqVo.getQuery(), List.of()));
    try {
      if (!commonRespDto.isOk()) {
        emitter.send(SseEmitter.event().name("error").id(chatReqVo.getConversationId())
            .data(commonRespDto.getMsg()));
        completeSseWithNullResult(emitter, futureResult);
        return 0;
      } else {
        Integer urlChatId = commonRespDto.getData();
        emitter.send(SseEmitter.event().name("flushUrlChat").id(chatReqVo.getConversationId())
            .data(urlChatId));
        return urlChatId;
      }
    } catch (Exception e) {
      log.error("发送对话信息出错", e);
      completeSseWithNullResult(emitter, futureResult);
      return 0;
    }
  }


  /**
   * 模型配置
   *
   * @param requestBody 请求体
   * @param chatModel   模型
   */
  private static void setModelConfig(JSONObject requestBody, ModelDto chatModel) {
    requestBody.putOnce("chat_model_instance_provider", chatModel.getLoadingMode());

    JSONObject chatModelConfig = JSONUtil.createObj();
    chatModelConfig.putOnce("llm_type", "chat");
    chatModelConfig.putOnce("model_name", chatModel.getInternalName());
    chatModelConfig.putOnce("base_url", chatModel.getUrl());
    chatModelConfig.putOnce("apikey", chatModel.getApiKey());
    chatModelConfig.putOnce("is_support_vision", chatModel.getIsSupportFunction());
    chatModelConfig.putOnce("context_length", chatModel.getContextLength());
    chatModelConfig.putOnce("max_token_length", chatModel.getTokenMax());
    chatModelConfig.putOnce("is_support_function", chatModel.getIsSupportVisual());

    requestBody.putOnce("chat_model_instance_config", chatModelConfig);
  }

  /**
   * 多智能体模型配置
   *
   * @param requestBody 请求体
   * @param model       模型
   */
  private static void setMultipleModelConfig(JSONObject requestBody, ModelDto model) {

    JSONObject chatModel = JSONUtil.createObj();
    chatModel.putOnce("model_instance_provider", "ollama");

    JSONObject modelInstanceConfig = JSONUtil.createObj();
    modelInstanceConfig.putOnce("llm_type", "chat");
    modelInstanceConfig.putOnce("model_name", model.getInternalName());
    modelInstanceConfig.putOnce("base_url", model.getUrl());
    modelInstanceConfig.putOnce("apikey", model.getApiKey());
    modelInstanceConfig.putOnce("is_support_vision", model.getIsSupportFunction());
    modelInstanceConfig.putOnce("context_length", model.getContextLength());
    modelInstanceConfig.putOnce("max_token_length", model.getTokenMax());
    modelInstanceConfig.putOnce("is_support_function", model.getIsSupportVisual());

    chatModel.putOnce("model_instance_config", modelInstanceConfig);

    requestBody.putOnce("chat_model", chatModel);
  }


  @Nullable
  private static JSONObject getHumanQuestion(ChatData data) {
    // 倒序筛选第一个“type”=“human”的作为用户问题
    JSONObject humanQuestion = null;
    for (int i = data.getChat_history().size() - 1; i >= 0; i--) {
      if ("human".equals(data.getChat_history().get(i).getStr("type"))) {
        humanQuestion = data.getChat_history().get(i);
        break;
      }
    }
    return humanQuestion;
  }

  /**
   * 聊天结果完成
   *
   * @param emitter      发送者
   * @param futureResult 异步结果
   */
  public void completeSseWithNullResult(SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult) {
    futureResult.complete(null);
    emitter.complete();
  }

  public void completeSseWithNullResultByStreamFuture(SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult,
      AtomicReference<CompletableFuture<Void>> streamFutureRef) {
    if (futureResult != null && !futureResult.isDone()) {
      futureResult.complete(null);
    }
    if (streamFutureRef.get() != null && !streamFutureRef.get().isDone()) {
      emitter.complete();
    }
  }
}
