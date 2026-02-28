package cn.voicecomm.ai.voicesagex.console.application.service;


import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.AgentLongTermMemoryService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatReqVo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 智能体配置服务 负责智能体的验证和配置管理
 *
 * @author wangf
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentConfigurationService {

  private final McpMapper mcpMapper;
  private final AgentLongTermMemoryService agentLongTermMemoryService;

  @Value("${algoUrlPrefix}${chat.mcpCheck}")
  private String mcpCheckUrl;

  @DubboReference
  public KnowledgeBaseService knowledgeBaseService;

  @DubboReference
  public ModelService modelService;

  /**
   * 验证并配置MCP服务 1. 根据运行类型过滤有效的MCP服务列表 2. 执行MCP服务可用性检查 3. 构建并设置MCP配置到请求体中
   *
   * @param chatReqVo       聊天请求参数
   * @param emitter         SSE推送器
   * @param agentInfo       智能体信息
   * @param futureResult    异步执行结果
   * @param chatRequestBody 聊天请求体
   * @return MCP验证是否通过
   */
  public boolean validateAndConfigureMcpServices(TestChatReqVo chatReqVo, SseEmitter emitter,
      AgentInfoResponseDto agentInfo, CompletableFuture<JSONObject> futureResult,
      JSONObject chatRequestBody) throws IOException, InterruptedException {

    // 1. 获取并过滤有效的MCP服务列表
    List<McpDto> filteredMcpList = filterValidMcpServices(chatReqVo, agentInfo);
    if (CollectionUtils.isEmpty(filteredMcpList)) {
      return true;
    }

    // 2. 执行MCP服务可用性检查
    AgentValidationResult agentValidationResult = checkMcpServicesAvailability(filteredMcpList);

    // 3. 处理检查结果
    if (agentValidationResult.hasErrors()) {
      handleUnavailableMcpServices(emitter, chatReqVo, futureResult,
          agentValidationResult.getErrors());
      return false;
    }

    // 4. 构建并设置MCP配置
    JSONObject mcpConfig = buildMcpConfiguration(filteredMcpList);
    chatRequestBody.putOnce("mcp_config", mcpConfig);
    return true;
  }

  /**
   * 根据运行类型过滤有效的MCP服务
   *
   * @param chatReqVo 聊天请求参数
   * @param agentInfo 智能体信息
   * @return 过滤后的MCP服务列表
   */
  public List<McpDto> filterValidMcpServices(TestChatReqVo chatReqVo,
      AgentInfoResponseDto agentInfo) {
    List<McpDto> mcpList = agentInfo.getMcpList();

    // 非草稿状态下需要过滤已上架的服务
    if (!ApplicationStatusEnum.DRAFT.getKey().equals(chatReqVo.getRunType())) {
      List<McpPo> shelfMcpPos = mcpMapper.selectList(
          Wrappers.<McpPo>lambdaQuery().eq(McpPo::getIsShelf, true));
      List<Integer> shelfMcpIds = shelfMcpPos.stream().map(McpPo::getId).toList();

      if (CollUtil.isNotEmpty(mcpList)) {
        return mcpList.stream()
            .filter(mcpDto -> shelfMcpIds.contains(mcpDto.getId()))
            .toList();
      }
    }

    return mcpList;
  }

  /**
   * 检查MCP服务可用性
   *
   * @param mcpList MCP服务列表
   * @return 不可用的MCP服务名称列表
   */
  public AgentValidationResult checkMcpServicesAvailability(List<McpDto> mcpList)
      throws IOException, InterruptedException {
    AgentValidationResult result = new AgentValidationResult();
    if (CollUtil.isEmpty(mcpList)) {
      return result;
    }
    for (McpDto mcpDto : mcpList) {
      JSONObject mcpConfigJson = JSONUtil.createObj()
          .putOnce("transport", mcpDto.getTransport())
          .putOnce("url", mcpDto.getUrl());

      JSONObject checkJson = JSONUtil.createObj()
          .putOnce(mcpDto.getInternalName(), mcpConfigJson);

      try (HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build()) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(mcpCheckUrl))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(checkJson.toString()))
            .build();

        HttpResponse<String> httpResponse = client.send(request, BodyHandlers.ofString());
        log.info("MCP检查---请求参数：{},请求结果：{}", JSONUtil.toJsonStr(checkJson),
            httpResponse.body());

        if (httpResponse.statusCode() != HttpStatus.HTTP_OK || !Boolean.TRUE.equals(
            JSONUtil.getByPath(JSONUtil.parseObj(httpResponse.body()), "data.available"))) {
          log.error("MCP检查失败! mcp：{}", mcpDto.getDisplayName());
          result.addError(mcpDto.getDisplayName());
        }
      }
    }
    return result;
  }

  /**
   * 处理不可用的MCP服务
   *
   * @param emitter             SSE推送器
   * @param chatReqVo           聊天请求参数
   * @param futureResult        异步执行结果
   * @param unavailableMcpNames 不可用的MCP服务名称列表
   */
  public void handleUnavailableMcpServices(SseEmitter emitter, TestChatReqVo chatReqVo,
      CompletableFuture<JSONObject> futureResult, List<String> unavailableMcpNames) {
    try {
      emitter.send(SseEmitter.event()
          .name("error")
          .id(chatReqVo.getConversationId())
          .data(StrUtil.join("、", unavailableMcpNames) + "MCP不可用"));
    } catch (Exception e) {
      log.error("发送MCP检查失败信息出错", e);
    } finally {
      completeSseWithNullResult(emitter, futureResult);
    }
  }

  /**
   * 构建MCP配置对象
   *
   * @param mcpList MCP服务列表
   * @return MCP配置JSON对象
   */
  public JSONObject buildMcpConfiguration(List<McpDto> mcpList) {
    JSONObject mcpConfig = JSONUtil.createObj();

    for (McpDto mcpDto : mcpList) {
      JSONObject mcpConfigJson = JSONUtil.createObj()
          .putOnce("transport", mcpDto.getTransport())
          .putOnce("url", mcpDto.getUrl());
      mcpConfig.putOnce(mcpDto.getInternalName(), mcpConfigJson);
    }

    return mcpConfig;
  }

  /**
   * 验证子智能体MCP服务（简化版本） 专门用于子智能体的MCP检查，不涉及配置构建
   *
   * @param chatReqVo    聊天请求参数
   * @param emitter      SSE推送器
   * @param futureResult 异步执行结果
   * @param agentInfo    智能体信息
   * @return MCP验证是否通过
   */
  public boolean validateSubAgentMcpServices(TestChatReqVo chatReqVo, SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult, AgentInfoResponseDto agentInfo)
      throws IOException, InterruptedException {

    List<McpDto> mcpList = agentInfo.getMcpList();
    if (CollUtil.isEmpty(mcpList)) {
      return true;
    }

    AgentValidationResult agentValidationResult = checkMcpServicesAvailability(mcpList);

    if (agentValidationResult.hasErrors()) {
      handleUnavailableMcpServices(emitter, chatReqVo, futureResult,
          agentValidationResult.getErrors());
      return false;
    }

    return true;
  }

  /**
   * 聊天结果完成
   *
   * @param emitter      发送者
   * @param futureResult 异步结果
   */
  private void completeSseWithNullResult(SseEmitter emitter,
      CompletableFuture<JSONObject> futureResult) {
    futureResult.complete(null);
    emitter.complete();
  }


  /**
   * 验证并配置知识库服务 1. 根据运行类型获取知识库列表 2. 验证各知识库的模型配置 3. 构建知识库配置并设置到请求体中
   *
   * @param chatReqVo       聊天请求参数
   * @param emitter         SSE推送器
   * @param futureResult    异步执行结果
   * @param agentInfo       智能体信息
   * @param chatRequestBody 聊天请求体
   * @return 知识库验证是否通过
   */
  public boolean validateAndConfigureKnowledgeBase(TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      AgentInfoResponseDto agentInfo, JSONObject chatRequestBody) {

    try {
      // 1. 获取知识库列表
      List<KnowledgeBaseDto> knowledgeBases = getKnowledgeBaseList(chatReqVo, agentInfo);
      if (CollectionUtils.isEmpty(knowledgeBases)) {
        setEmptyKnowledgeBaseConfig(chatRequestBody);
        return true;
      }

      // 2. 验证并构建知识库配置
      List<JSONObject> knowledgeBaseConfigs = buildKnowledgeBaseConfigurations(
          chatReqVo, emitter, futureResult, knowledgeBases);

      if (knowledgeBaseConfigs == null) {
        return false; // 验证失败
      }

      // 3. 设置知识库配置到请求体
      setKnowledgeBaseConfigToRequest(chatRequestBody, knowledgeBaseConfigs);
      return true;

    } catch (Exception e) {
      log.error("知识库配置处理异常", e);
      handleKnowledgeBaseError(emitter, chatReqVo, futureResult, "知识库配置处理异常");
      return false;
    }
  }

  /**
   * 根据运行类型获取知识库列表
   *
   * @param chatReqVo 聊天请求参数
   * @param agentInfo 智能体信息
   * @return 知识库列表
   */
  private List<KnowledgeBaseDto> getKnowledgeBaseList(TestChatReqVo chatReqVo,
      AgentInfoResponseDto agentInfo) {
    if (ApplicationStatusEnum.DRAFT.getKey().equals(chatReqVo.getRunType())) {
      CommonRespDto<List<KnowledgeBaseDto>> response = knowledgeBaseService.getApplicationKnowledgeBases(
          chatReqVo.getApplicationId());
      if (!response.isOk()) {
        throw new RuntimeException("知识库获取失败");
      }
      return response.getData();
    } else {
      return agentInfo.getKnowledgeBaseDtoList();
    }
  }

  /**
   * 构建知识库配置列表
   *
   * @param chatReqVo      聊天请求参数
   * @param emitter        SSE推送器
   * @param futureResult   异步执行结果
   * @param knowledgeBases 知识库列表
   * @return 知识库配置列表，验证失败时返回null
   */
  private List<JSONObject> buildKnowledgeBaseConfigurations(TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      List<KnowledgeBaseDto> knowledgeBases) {

    List<JSONObject> knowledgeBaseList = new ArrayList<>();

    for (KnowledgeBaseDto knowledgeBase : knowledgeBases) {
      try {
        JSONObject knowledgeBaseConfig = buildSingleKnowledgeBaseConfig(chatReqVo, emitter,
            futureResult, knowledgeBase);
        if (knowledgeBaseConfig == null) {
          return null; // 构建失败
        }
        knowledgeBaseList.add(knowledgeBaseConfig);
      } catch (Exception e) {
        log.error("构建知识库{}配置异常", knowledgeBase.getId(), e);
        handleKnowledgeBaseError(emitter, chatReqVo, futureResult,
            "知识库" + knowledgeBase.getName() + "配置异常");
        return null;
      }
    }

    return knowledgeBaseList;
  }

  /**
   * 构建单个知识库配置
   *
   * @param chatReqVo     聊天请求参数
   * @param emitter       SSE推送器
   * @param futureResult  异步执行结果
   * @param knowledgeBase 知识库信息
   * @return 知识库配置JSON对象，验证失败时返回null
   */
  private JSONObject buildSingleKnowledgeBaseConfig(TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      KnowledgeBaseDto knowledgeBase) {

    // 构建检索配置
    JSONObject retrieveConfig = buildRetrieveConfiguration(knowledgeBase);

    // 验证并配置Rerank模型
    if (!configureRerankModel(retrieveConfig, chatReqVo, emitter, futureResult, knowledgeBase)) {
      return null;
    }

    // 验证并配置Embedding模型
    if (!configureEmbeddingModel(retrieveConfig, chatReqVo, emitter, futureResult, knowledgeBase)) {
      return null;
    }

    // 构建完整知识库配置
    JSONObject knowledgeBaseJson = JSONUtil.createObj();
    knowledgeBaseJson.putOnce("knowledge_base_id", knowledgeBase.getId());
    knowledgeBaseJson.putOnce("knowledge_base_retrieve_type",
        knowledgeBase.getSearchStrategy().name());
    knowledgeBaseJson.putOnce("knowledge_base_retrieve_config", retrieveConfig);
    knowledgeBaseJson.putOnce("knowledge_base_description", knowledgeBase.getDescription());

    return knowledgeBaseJson;
  }

  /**
   * 构建检索配置
   *
   * @param knowledgeBase 知识库信息
   * @return 检索配置JSON对象
   */
  private JSONObject buildRetrieveConfiguration(KnowledgeBaseDto knowledgeBase) {
    JSONObject retrieveConfig = JSONUtil.createObj();
    retrieveConfig.putOnce("top_k", knowledgeBase.getTopK());

    if (Boolean.TRUE.equals(knowledgeBase.getEnableScore())) {
      retrieveConfig.putOnce("score_threshold", knowledgeBase.getScore());
    } else {
      retrieveConfig.putOnce("score_threshold", null);
    }

    retrieveConfig.putOnce("is_rerank", knowledgeBase.getEnableRerankModel());

    // 处理混合搜索配置
    if (knowledgeBase.getSearchStrategy().equals(SearchStrategy.HYBRID)) {
      retrieveConfig.putOnce("hybrid_semantic_weight",
          knowledgeBase.getHybridSearchKeywordMatchingWeight());
      retrieveConfig.putOnce("hybrid_keyword_weight",
          knowledgeBase.getHybridSearchKeywordMatchingWeight());

      if (Boolean.FALSE.equals(knowledgeBase.getEnableRerankModel())) {
        retrieveConfig.putOnce("hybrid_rerank_type", "WEIGHT");
      } else {
        retrieveConfig.putOnce("hybrid_rerank_type", "MODEL");
      }
    }

    return retrieveConfig;
  }

  /**
   * 配置Rerank模型
   *
   * @param retrieveConfig 检索配置
   * @param chatReqVo      聊天请求参数
   * @param emitter        SSE推送器
   * @param futureResult   异步执行结果
   * @param knowledgeBase  知识库信息
   * @return 配置是否成功
   */
  private boolean configureRerankModel(JSONObject retrieveConfig, TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      KnowledgeBaseDto knowledgeBase) {

    if (Boolean.FALSE.equals(knowledgeBase.getEnableRerankModel())) {
      return true;
    }

    Integer rerankModelId = knowledgeBase.getRerankModelId();
    CommonRespDto<ModelDto> rerankModelResp = modelService.getInfo(rerankModelId);
    ModelDto rerankModel = rerankModelResp.getData();

    if (!rerankModelResp.isOk() || Objects.isNull(rerankModel) ||
        Boolean.FALSE.equals(rerankModel.getIsShelf())) {
      log.error("知识库{}中Rerank模型获取异常：{}", knowledgeBase.getId(), rerankModel);
      handleKnowledgeBaseError(emitter, chatReqVo, futureResult,
          "知识库" + knowledgeBase.getName() + "中Rerank模型已经下架");
      return false;
    }

    retrieveConfig.putOnce("rerank_model_instance_provider", rerankModel.getLoadingMode());
    JSONObject rerankModelConfig = buildModelConfig(rerankModel);
    retrieveConfig.putOnce("rerank_model_instance_config", rerankModelConfig);

    return true;
  }

  /**
   * 配置Embedding模型
   *
   * @param retrieveConfig 检索配置
   * @param chatReqVo      聊天请求参数
   * @param emitter        SSE推送器
   * @param futureResult   异步执行结果
   * @param knowledgeBase  知识库信息
   * @return 配置是否成功
   */
  private boolean configureEmbeddingModel(JSONObject retrieveConfig, TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      KnowledgeBaseDto knowledgeBase) {

    Integer embeddingModelId = knowledgeBase.getEmbeddingModelId();
    CommonRespDto<ModelDto> embeddingModelResp = modelService.getInfo(embeddingModelId);
    ModelDto embeddingModel = embeddingModelResp.getData();

    if (!embeddingModelResp.isOk() || Objects.isNull(embeddingModel) ||
        Boolean.FALSE.equals(embeddingModel.getIsShelf())) {
      log.error("知识库{}中Embedding模型获取异常：{}", knowledgeBase.getId(), embeddingModel);
      handleKnowledgeBaseError(emitter, chatReqVo, futureResult,
          "知识库" + knowledgeBase.getName() + "中Embedding模型已经下架");
      return false;
    }

    retrieveConfig.putOnce("embedding_model_instance_provider", embeddingModel.getLoadingMode());
    JSONObject embeddingModelConfig = buildModelConfig(embeddingModel);
    retrieveConfig.putOnce("embedding_model_instance_config", embeddingModelConfig);

    return true;
  }

  /**
   * 构建模型配置
   *
   * @param model 模型信息
   * @return 模型配置JSON对象
   */
  private JSONObject buildModelConfig(ModelDto model) {
    JSONObject modelConfig = JSONUtil.createObj();
    modelConfig.putOnce("model_name", model.getInternalName());
    modelConfig.putOnce("base_url", model.getUrl());
    modelConfig.putOnce("apikey", model.getApiKey());
    modelConfig.putOnce("is_support_vision", model.getIsSupportFunction());
    modelConfig.putOnce("context_length", model.getContextLength());
    modelConfig.putOnce("max_token_length", model.getTokenMax());
    modelConfig.putOnce("is_support_function", model.getIsSupportVisual());
    return modelConfig;
  }

  /**
   * 设置空的知识库配置
   *
   * @param chatRequestBody 聊天请求体
   */
  private void setEmptyKnowledgeBaseConfig(JSONObject chatRequestBody) {
    chatRequestBody.putOnce("knowledge_base_list", new ArrayList<>());
    chatRequestBody.putOnce("knowledge_recall_config", null);
  }

  /**
   * 设置知识库配置到请求体
   *
   * @param chatRequestBody      聊天请求体
   * @param knowledgeBaseConfigs 知识库配置列表
   */
  private void setKnowledgeBaseConfigToRequest(JSONObject chatRequestBody,
      List<JSONObject> knowledgeBaseConfigs) {
    chatRequestBody.putOnce("knowledge_base_list", knowledgeBaseConfigs);
    chatRequestBody.putOnce("knowledge_recall_config", null);
  }

  /**
   * 处理知识库错误
   *
   * @param emitter      SSE推送器
   * @param chatReqVo    聊天请求参数
   * @param futureResult 异步执行结果
   * @param errorMessage 错误信息
   */
  private void handleKnowledgeBaseError(SseEmitter emitter, TestChatReqVo chatReqVo,
      CompletableFuture<JSONObject> futureResult, String errorMessage) {
    try {
      emitter.send(SseEmitter.event()
          .name("error")
          .id(chatReqVo.getConversationId())
          .data(errorMessage));
    } catch (Exception e) {
      log.error("发送知识库错误信息出错", e);
    } finally {
      completeSseWithNullResult(emitter, futureResult);
    }
  }


  /**
   * 验证子智能体知识库配置 专门用于验证子智能体的知识库模型配置是否有效
   *
   * @param chatReqVo    聊天请求参数
   * @param emitter      SSE推送器
   * @param futureResult 异步执行结果
   * @param subAgentInfo 子智能体信息
   * @return 知识库验证是否通过
   */
  public boolean validateSubAgentKnowledgeBase(TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      AgentInfoResponseDto subAgentInfo) {

    List<KnowledgeBaseDto> knowledgeBases = subAgentInfo.getKnowledgeBaseDtoList();

    if (CollectionUtils.isEmpty(knowledgeBases)) {
      return true;
    }

    // 复用知识库验证逻辑
    for (KnowledgeBaseDto knowledgeBase : knowledgeBases) {
      // 验证Rerank模型
      if (!validateKnowledgeBaseModel(chatReqVo, emitter, futureResult,
          knowledgeBase, knowledgeBase.getRerankModelId(), "Rerank",
          subAgentInfo.getApplicationName(), true)) {
        return false;
      }

      // 验证Embedding模型
      if (!validateKnowledgeBaseModel(chatReqVo, emitter, futureResult,
          knowledgeBase, knowledgeBase.getEmbeddingModelId(), "Embedding",
          subAgentInfo.getApplicationName(), false)) {
        return false;
      }
    }

    return true;
  }

  /**
   * 验证知识库模型配置
   *
   * @param chatReqVo       聊天请求参数
   * @param emitter         SSE推送器
   * @param futureResult    异步执行结果
   * @param knowledgeBase   知识库信息
   * @param modelId         模型ID
   * @param modelType       模型类型（Rerank/Embedding）
   * @param applicationName 应用名称（用于错误提示）
   * @param isRerankCheck   是否为Rerank模型检查
   * @return 模型验证是否通过
   */
  private boolean validateKnowledgeBaseModel(TestChatReqVo chatReqVo,
      SseEmitter emitter, CompletableFuture<JSONObject> futureResult,
      KnowledgeBaseDto knowledgeBase, Integer modelId, String modelType,
      String applicationName, boolean isRerankCheck) {

    // Rerank模型需要特殊处理：只有启用时才检查
    if (isRerankCheck && Boolean.FALSE.equals(knowledgeBase.getEnableRerankModel())) {
      return true;
    }

    if (modelId == null) {
      return true;
    }

    CommonRespDto<ModelDto> modelResp = modelService.getInfo(modelId);
    ModelDto model = modelResp.getData();

    if (!modelResp.isOk() || Objects.isNull(model) || Boolean.FALSE.equals(model.getIsShelf())) {
      log.error("知识库{}中{}模型获取异常：{}", knowledgeBase.getId(), modelType, model);

      String errorMessage = buildKnowledgeBaseModelErrorMsg(applicationName,
          knowledgeBase.getName(), modelType);

      handleKnowledgeBaseError(emitter, chatReqVo, futureResult, errorMessage);
      return false;
    }

    return true;
  }

  /**
   * 构建知识库模型错误消息
   *
   * @param applicationName   应用名称
   * @param knowledgeBaseName 知识库名称
   * @param modelType         模型类型
   * @return 错误消息
   */
  private String buildKnowledgeBaseModelErrorMsg(String applicationName,
      String knowledgeBaseName, String modelType) {

    if (StrUtil.isNotBlank(applicationName)) {
      return applicationName + "的知识库" + knowledgeBaseName + "中" + modelType + "模型已经下架";
    } else {
      return "知识库" + knowledgeBaseName + "中" + modelType + "模型已经下架";
    }
  }

  /**
   * 长期记忆配置
   *
   * @param chatReqVo       请求体
   * @param userId          用户id
   * @param chatRequestBody 请求体
   * @param agentInfo       智能体信息
   */
  public void setLongtermConfig(TestChatReqVo chatReqVo, Integer userId,
      JSONObject chatRequestBody, AgentInfoResponseDto agentInfo) {
    chatRequestBody.putOnce("is_memory", agentInfo.getLongTermMemoryEnabled());
    if (agentInfo.getLongTermMemoryEnabled()) {
      JSONObject memoryInfoJson = JSONUtil.createObj();
      memoryInfoJson.putOnce("application_id", agentInfo.getApplicationId());
      memoryInfoJson.putOnce("user_id", userId);
      memoryInfoJson.putOnce("agent_id", agentInfo.getId());
      memoryInfoJson.putOnce("data_type", chatReqVo.getRunType());
      // 长期记忆类型  always永久有效，custom自定义
      if ("custom".equals(agentInfo.getLongTermMemoryType())) {

        LocalDateTime localDateTime = LocalDateTime.now()
            .minusDays(agentInfo.getLongTermMemoryExpired());
        // 每次调用前，删除过期的记忆 (createTime < (当前时间-过期天数))
        agentLongTermMemoryService.clearExpiredData(agentInfo.getApplicationId(), userId,
            localDateTime, chatReqVo.getRunType());

        memoryInfoJson.putOnce("expired_time", LocalDateTimeUtil.formatNormal(localDateTime));
      }
      chatRequestBody.putOnce("memory_info", memoryInfoJson);
      CommonRespDto<ModelDto> commonRespDto = modelService.getMemoryModel();
      ModelDto modelDto = commonRespDto.getData();
      JSONObject promptRequestJson = buildPromptRequest(modelDto);
      chatRequestBody.putOnce("memory_model",
          JSONUtil.createObj().putOnce("model_instance_provider", "ollama")
              .putOnce("model_instance_config", promptRequestJson));
    }
  }


  /**
   * 验证知识库配置（核心逻辑复用方法） 用于验证知识库相关的模型配置是否有效
   *
   * @param knowledgeBases  知识库列表
   * @param applicationName 应用名称（用于错误提示，可为空）
   * @return 验证结果，包含错误信息列表
   */
  public AgentValidationResult validateKnowledgeBaseConfigs(
      List<KnowledgeBaseDto> knowledgeBases, String applicationName) {

    AgentValidationResult result = new AgentValidationResult();

    if (CollectionUtils.isEmpty(knowledgeBases)) {
      return result; // 空列表验证通过
    }

    for (KnowledgeBaseDto knowledgeBase : knowledgeBases) {
      // 验证Rerank模型
      String rerankError = validateKnowledgeBaseModelConfig(
          knowledgeBase, knowledgeBase.getRerankModelId(), "Rerank", applicationName, true);
      if (rerankError != null) {
        result.addError(rerankError);
      }

      // 验证Embedding模型
      String embeddingError = validateKnowledgeBaseModelConfig(
          knowledgeBase, knowledgeBase.getEmbeddingModelId(), "Embedding", applicationName, false);
      if (embeddingError != null) {
        result.addError(embeddingError);
      }
    }

    return result;
  }


  /**
   * 验证单个知识库模型配置
   *
   * @param knowledgeBase   知识库信息
   * @param modelId         模型ID
   * @param modelType       模型类型
   * @param applicationName 应用名称
   * @param isRerankCheck   是否为Rerank检查
   * @return 错误信息，验证通过时返回null
   */
  private String validateKnowledgeBaseModelConfig(KnowledgeBaseDto knowledgeBase,
      Integer modelId, String modelType, String applicationName, boolean isRerankCheck) {

    // Rerank模型特殊处理
    if (isRerankCheck && Boolean.FALSE.equals(knowledgeBase.getEnableRerankModel())) {
      return null;
    }

    if (modelId == null) {
      return null;
    }

    CommonRespDto<ModelDto> modelResp = modelService.getInfo(modelId);
    ModelDto model = modelResp.getData();

    if (!modelResp.isOk() || Objects.isNull(model) || Boolean.FALSE.equals(model.getIsShelf())) {
      log.error("知识库{}中{}模型获取异常：{}", knowledgeBase.getId(), modelType, model);
      return buildKnowledgeBaseModelErrorMsg(applicationName, knowledgeBase.getName(), modelType);
    }

    return null;
  }

  /**
   * 构建知识库配置（复用方法）
   *
   * @param knowledgeBases 知识库列表
   * @return 知识库配置列表
   */
  public List<JSONObject> buildKnowledgeBaseConfigurations(List<KnowledgeBaseDto> knowledgeBases) {
    List<JSONObject> knowledgeBaseList = new ArrayList<>();

    if (CollectionUtils.isEmpty(knowledgeBases)) {
      return knowledgeBaseList;
    }

    for (KnowledgeBaseDto knowledgeBase : knowledgeBases) {
      JSONObject knowledgeBaseConfig = buildSingleKnowledgeBaseConfiguration(knowledgeBase);
      knowledgeBaseList.add(knowledgeBaseConfig);
    }

    return knowledgeBaseList;
  }

  /**
   * 构建单个知识库配置
   */
  private JSONObject buildSingleKnowledgeBaseConfiguration(KnowledgeBaseDto knowledgeBase) {
    JSONObject retrieveConfig = buildRetrieveConfiguration(knowledgeBase);

    // 配置Rerank模型
    if (Boolean.TRUE.equals(knowledgeBase.getEnableRerankModel())) {
      Integer rerankModelId = knowledgeBase.getRerankModelId();
      CommonRespDto<ModelDto> rerankModelResp = modelService.getInfo(rerankModelId);
      ModelDto rerankModel = rerankModelResp.getData();

      if (rerankModelResp.isOk() && Objects.nonNull(rerankModel) &&
          Boolean.TRUE.equals(rerankModel.getIsShelf())) {
        retrieveConfig.putOnce("rerank_model_instance_provider", rerankModel.getLoadingMode());
        JSONObject rerankModelConfig = buildModelConfig(rerankModel);
        retrieveConfig.putOnce("rerank_model_instance_config", rerankModelConfig);
      }
    }

    // 配置Embedding模型
    Integer embeddingModelId = knowledgeBase.getEmbeddingModelId();
    CommonRespDto<ModelDto> embeddingModelResp = modelService.getInfo(embeddingModelId);
    ModelDto embeddingModel = embeddingModelResp.getData();

    if (embeddingModelResp.isOk() && Objects.nonNull(embeddingModel) &&
        Boolean.TRUE.equals(embeddingModel.getIsShelf())) {
      retrieveConfig.putOnce("embedding_model_instance_provider", embeddingModel.getLoadingMode());
      JSONObject embeddingModelConfig = buildModelConfig(embeddingModel);
      retrieveConfig.putOnce("embedding_model_instance_config", embeddingModelConfig);
    }

    // 构建完整知识库配置
    JSONObject knowledgeBaseJson = JSONUtil.createObj();
    knowledgeBaseJson.putOnce("knowledge_base_id", knowledgeBase.getId());
    knowledgeBaseJson.putOnce("knowledge_base_retrieve_type",
        knowledgeBase.getSearchStrategy().name());
    knowledgeBaseJson.putOnce("knowledge_base_retrieve_config", retrieveConfig);
    knowledgeBaseJson.putOnce("knowledge_base_description", knowledgeBase.getDescription());

    return knowledgeBaseJson;
  }


  /**
   * 检查是否存在错误并设置MCP配置
   *
   * @param agentInfo       智能体信息
   * @param chatRequestBody chat请求体
   * @return boolean
   */
  public String publishCheckAndConfigureMcpServices(JSONObject chatRequestBody,
      AgentInfoResponseDto agentInfo) {
    // 复用AgentConfigurationService中的MCP过滤逻辑
    List<McpDto> filteredMcpList = filterValidMcpServices(
        TestChatReqVo.builder().runType(ApplicationStatusEnum.DRAFT.getKey()).build(), agentInfo);

    if (CollectionUtils.isEmpty(filteredMcpList)) {
      return null; // 没有MCP服务，验证通过
    }
    //执行MCP服务可用性检查
    AgentValidationResult agentValidationResult = null;
    try {
      agentValidationResult = checkMcpServicesAvailability(
          filteredMcpList);
    } catch (IOException | InterruptedException e) {
      return "MCP检查异常";
    }

    // 处理检查结果
    if (agentValidationResult.hasErrors()) {
      return StrUtil.join("、", agentValidationResult.getErrors()) + "MCP不可用";
    }

    // 复用AgentConfigurationService中的MCP配置构建逻辑
    JSONObject mcpConfig = buildMcpConfiguration(filteredMcpList);
    chatRequestBody.putOnce("mcp_config", mcpConfig);
    return null; // 验证通过
  }


  /**
   * 检查是否存在错误并设置知识库参数
   *
   * @param chatRequestBody 测试对话参数
   * @param agentInfo       应用信息
   * @return 是否存在错误
   */
  public String publishCheckAndConfigureKnowledgeBase(JSONObject chatRequestBody,
      AgentInfoResponseDto agentInfo) {
    List<KnowledgeBaseDto> knowledgeBases = agentInfo.getKnowledgeBaseDtoList();

    // 复用AgentConfigurationService的核心验证逻辑
    AgentValidationResult validationResult =
        validateKnowledgeBaseConfigs(knowledgeBases,
            agentInfo.getApplicationName());

    // 如果有验证错误，直接返回
    if (validationResult.hasErrors()) {
      log.error("知识库配置存在错误：{}", JSONUtil.toJsonStr(validationResult.getErrors()));
      return validationResult.getErrors().getFirst();
    }

    // 复用配置构建逻辑
    List<JSONObject> knowledgeBaseConfigs =
        buildKnowledgeBaseConfigurations(knowledgeBases);

    chatRequestBody.putOnce("knowledge_base_list", knowledgeBaseConfigs);
    chatRequestBody.putOnce("knowledge_recall_config", null);
    return null;
  }

  /**
   * 验证结果
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class AgentValidationResult {

    private List<String> errors = new ArrayList<>();
    private boolean hasErrors = false;

    public void addError(String error) {
      this.errors.add(error);
      this.hasErrors = true;
    }

    public boolean hasErrors() {
      return hasErrors;
    }
  }
}