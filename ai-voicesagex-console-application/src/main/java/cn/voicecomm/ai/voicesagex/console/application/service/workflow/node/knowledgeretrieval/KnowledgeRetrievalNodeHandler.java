package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseMetadataService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArraySegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode.Condition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode.DataSet;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode.MetadataFilteringCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode.MultipleRetrievalConfig;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/7/31 14:55
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class KnowledgeRetrievalNodeHandler extends BaseNodeHandler {


  @Value("${algoUrlPrefix}${chat.retrieveKnowledge}")
  private String retrieveKnowledgeUrl;
  @DubboReference
  private KnowledgeBaseService knowledgeBaseService;
  @DubboReference
  private KnowledgeBaseMetadataService knowledgeBaseMetadataService;
  @DubboReference
  public ModelService modelService;

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);
    KnowledgeRetrievalNode nodeData = JSONUtil.toBean(jsonObject,
        KnowledgeRetrievalNode.class);

    // 提取 query
    Segment segment = variablePool.get(nodeData.getQuery_variable_selector());
    String query = segment == null ? "" : segment.getText();
    // 检查变量是否存在
    if (StrUtil.isBlank(query)) {
      Map<String, Object> inputs = new HashMap<>();
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("Query variable is null or empty.")
          .build();
    }
    Map<String, Object> inputs = Collections.singletonMap("query", query);

    try {

      JSONObject requestBody = JSONUtil.createObj();
      List<JSONObject> knowledgeBaseList = new ArrayList<>();
      List<Integer> dataSetIds = nodeData.getDataSet_list().stream().map(DataSet::getId).toList();
      CommonRespDto<List<KnowledgeBaseDto>> respDto = knowledgeBaseService.getKnowledgeBasesByIds(
          dataSetIds);
      if (!respDto.isOk()) {
        log.error("知识库获取异常");
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .inputs(inputs)
            .error("知识库获取失败！")
            .error_type("KnowledgeRetrievalNodeError")
            .build();
      }
      List<KnowledgeBaseDto> knowledgeBaseDtos = respDto.getData();

      if (CollUtil.isNotEmpty(knowledgeBaseDtos)) {
        // 构建知识库请求参数 knowledge_base_list
        for (KnowledgeBaseDto knowledgeBase : knowledgeBaseDtos) {
          JSONObject retrieveConfig = JSONUtil.createObj();

          JSONObject json = JSONUtil.createObj();
          json.putOnce("knowledge_base_id", knowledgeBase.getId());
          json.putOnce("knowledge_base_retrieve_type", knowledgeBase.getSearchStrategy().name());
          json.putOnce("knowledge_base_retrieve_config", retrieveConfig);
          json.putOnce("knowledge_base_description", knowledgeBase.getDescription());
          retrieveConfig.putOnce("top_k", knowledgeBase.getTopK());

          if (Boolean.TRUE.equals(knowledgeBase.getEnableScore())) {
            retrieveConfig.putOnce("score_threshold", knowledgeBase.getScore());
          } else {
            retrieveConfig.putOnce("score_threshold", null);
          }

          retrieveConfig.putOnce("is_rerank", knowledgeBase.getEnableRerankModel());
          if (Boolean.TRUE.equals(knowledgeBase.getEnableRerankModel())) {
            Integer rerankModelId = knowledgeBase.getRerankModelId();
            CommonRespDto<Boolean> available = modelService.isAvailable(rerankModelId);
            if (!available.isOk()) {
              log.error("知识库{}中Rerank模型获取异常", knowledgeBase.getName());
              return NodeRunResult.builder()
                  .status(WorkflowNodeExecutionStatus.FAILED)
                  .inputs(inputs)
                  .error("知识库:" + knowledgeBase.getName() + " Rerank" + available.getMsg())
                  .error_type("KnowledgeRetrievalNodeError")
                  .build();
            }
            CommonRespDto<ModelDto> rerankModelResp = modelService.getInfo(rerankModelId);
            ModelDto rerankModel = rerankModelResp.getData();
            retrieveConfig.putOnce("rerank_model_instance_provider",
                rerankModel.getLoadingMode());
            JSONObject rerankModelConfig = JSONUtil.createObj();
            rerankModelConfig.putOnce("model_name", rerankModel.getInternalName());
            rerankModelConfig.putOnce("base_url", rerankModel.getUrl());
            rerankModelConfig.putOnce("apikey", rerankModel.getApiKey());
            rerankModelConfig.putOnce("is_support_vision", rerankModel.getIsSupportFunction());
            rerankModelConfig.putOnce("context_length", rerankModel.getContextLength());
            rerankModelConfig.putOnce("max_token_length", rerankModel.getTokenMax());
            rerankModelConfig.putOnce("is_support_function", rerankModel.getIsSupportVisual());
            retrieveConfig.putOnce("rerank_model_instance_config", rerankModelConfig);
          }
          Integer embeddingModelId = knowledgeBase.getEmbeddingModelId();
          CommonRespDto<Boolean> modelServiceAvailable = modelService.isAvailable(
              embeddingModelId);
          if (!modelServiceAvailable.isOk()) {
            log.error("知识库{}中Embedding模型获取异常", knowledgeBase.getId());
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("知识库:" + knowledgeBase.getName() + " Embedding"
                    + modelServiceAvailable.getMsg())
                .error_type("KnowledgeRetrievalNodeError")
                .build();
          }
          CommonRespDto<ModelDto> embeddingModelResp = modelService.getInfo(embeddingModelId);
          ModelDto embeddingModel = embeddingModelResp.getData();

          retrieveConfig.putOnce("embedding_model_instance_provider",
              embeddingModel.getLoadingMode());
          JSONObject embeddingModelConfig = JSONUtil.createObj();
          embeddingModelConfig.putOnce("model_name", embeddingModel.getInternalName());
          embeddingModelConfig.putOnce("base_url", embeddingModel.getUrl());
          embeddingModelConfig.putOnce("apikey", embeddingModel.getApiKey());
          embeddingModelConfig.putOnce("is_support_vision",
              embeddingModel.getIsSupportFunction());
          embeddingModelConfig.putOnce("context_length", embeddingModel.getContextLength());
          embeddingModelConfig.putOnce("max_token_length", embeddingModel.getTokenMax());
          embeddingModelConfig.putOnce("is_support_function",
              embeddingModel.getIsSupportVisual());
          retrieveConfig.putOnce("embedding_model_instance_config", embeddingModelConfig);

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
          knowledgeBaseList.add(json);
        }
        requestBody.putOnce("knowledge_base_list", knowledgeBaseList);
        requestBody.putOnce("query", query);
        requestBody.putOnce("is_recall", true);
        // 构建召回请求参数
        JSONObject knowledgeRecallConfig = JSONUtil.createObj();
        requestBody.putOnce("knowledge_recall_config", knowledgeRecallConfig);
        MultipleRetrievalConfig multipleRetrievalConfig = nodeData.getMultiple_retrieval_config();
        if (multipleRetrievalConfig.getReranking_mode().equals("embedding_model")) {
          knowledgeRecallConfig.putOnce("rerank_type", "WEIGHT");
        } else {
          knowledgeRecallConfig.putOnce("rerank_type", "MODEL");
        }
        knowledgeRecallConfig.putOnce("top_k", multipleRetrievalConfig.getTopK());
        if (Boolean.TRUE.equals(multipleRetrievalConfig.getEnableScore())) {
          knowledgeRecallConfig.putOnce("score_threshold",
              multipleRetrievalConfig.getScore_threshold());
        }
        if (multipleRetrievalConfig.getReranking_mode().equals("embedding_model")) {
          // 构建召回embedding模式参数
          knowledgeRecallConfig.putOnce("semantic_weight",
              multipleRetrievalConfig.getWeights().getVector_setting().getVector_weight());
          Integer weightModelId = multipleRetrievalConfig.getWeights().getModel().getId();
          if (weightModelId == null) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("召回设置中未配置Embedding模型")
                .error_type("KnowledgeRetrievalNodeError")
                .build();
          }
          CommonRespDto<Boolean> available = modelService.isAvailable(weightModelId);
          if (!available.isOk()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("召回设置中Embedding" + available.getMsg())
                .error_type("KnowledgeRetrievalNodeError")
                .build();
          }
          CommonRespDto<ModelDto> info = modelService.getInfo(weightModelId);
          ModelDto weightModel = info.getData();
          knowledgeRecallConfig.putOnce("embedding_model_instance_provider",
              weightModel.getLoadingMode());
          JSONObject embeddingModelConfig = JSONUtil.createObj();
          embeddingModelConfig.putOnce("model_name", weightModel.getInternalName());
          embeddingModelConfig.putOnce("base_url", weightModel.getUrl());
          embeddingModelConfig.putOnce("apikey", weightModel.getApiKey());
          embeddingModelConfig.putOnce("is_support_vision", weightModel.getIsSupportFunction());
          embeddingModelConfig.putOnce("context_length", weightModel.getContextLength());
          embeddingModelConfig.putOnce("max_token_length", weightModel.getTokenMax());
          embeddingModelConfig.putOnce("is_support_function", weightModel.getIsSupportVisual());
          knowledgeRecallConfig.putOnce("embedding_model_instance_config", embeddingModelConfig);
        } else {
          // 构建召回rerank模式参数
          Integer rerankModelId = multipleRetrievalConfig.getReranking_model().getId();
          if (rerankModelId == null) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("召回设置中未配置Rerank模型")
                .error_type("KnowledgeRetrievalNodeError")
                .build();
          }
          CommonRespDto<Boolean> rerankAvailable = modelService.isAvailable(rerankModelId);
          if (!rerankAvailable.isOk()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("召回设置中Rerank" + rerankAvailable.getMsg())
                .error_type("KnowledgeRetrievalNodeError")
                .build();
          }
          CommonRespDto<ModelDto> info1 = modelService.getInfo(rerankModelId);
          ModelDto rerankModel = info1.getData();
          knowledgeRecallConfig.putOnce("rerank_model_instance_provider",
              rerankModel.getLoadingMode());
          JSONObject rerankModelConfig = JSONUtil.createObj();
          rerankModelConfig.putOnce("model_name", rerankModel.getInternalName());
          rerankModelConfig.putOnce("base_url", rerankModel.getUrl());
          rerankModelConfig.putOnce("apikey", rerankModel.getApiKey());
          rerankModelConfig.putOnce("is_support_vision", rerankModel.getIsSupportFunction());
          rerankModelConfig.putOnce("context_length", rerankModel.getContextLength());
          rerankModelConfig.putOnce("max_token_length", rerankModel.getTokenMax());
          rerankModelConfig.putOnce("is_support_function", rerankModel.getIsSupportVisual());
          knowledgeRecallConfig.putOnce("rerank_model_instance_config", rerankModelConfig);
        }

        // 设置元数据过滤请求参数
        String metadataFilteringMode = nodeData.getMetadata_filtering_mode();
        if (metadataFilteringMode.equals("disabled")) {
          requestBody.putOnce("is_metadata_filter", false);
        } else {
          requestBody.putOnce("is_metadata_filter", true);
          JSONObject metadataInfo = JSONUtil.createObj();
          requestBody.putOnce("metadata_info", metadataInfo);

          if (metadataFilteringMode.equals("automatic")) {
            requestBody.putOnce("metadata_mode", "AUTOMATIC");
            // 自动模式
            Integer metadataModelId = nodeData.getMetadata_model().getId();
            CommonRespDto<Boolean> modelServiceAvailable = modelService.isAvailable(
                metadataModelId);
            if (!modelServiceAvailable.isOk()) {
              return NodeRunResult.builder()
                  .status(WorkflowNodeExecutionStatus.FAILED)
                  .inputs(inputs)
                  .error("文档属性过滤" + modelServiceAvailable.getMsg())
                  .error_type("KnowledgeRetrievalNodeError")
                  .build();
            }
            CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(metadataModelId);
            ModelDto metadataModel = commonRespDto.getData();
            JSONObject chatModel = JSONUtil.createObj();

            JSONObject modelInstanceConfig = JSONUtil.createObj();
            chatModel.putOnce("model_instance_provider", metadataModel.getLoadingMode());
            modelInstanceConfig.putOnce("model_name", metadataModel.getInternalName());
            modelInstanceConfig.putOnce("base_url", metadataModel.getUrl());
            modelInstanceConfig.putOnce("apikey", metadataModel.getApiKey());
            modelInstanceConfig.putOnce("is_support_vision", metadataModel.getIsSupportFunction());
            modelInstanceConfig.putOnce("context_length", metadataModel.getContextLength());
            modelInstanceConfig.putOnce("max_token_length", metadataModel.getTokenMax());
            modelInstanceConfig.putOnce("is_support_function", metadataModel.getIsSupportVisual());
            chatModel.putOnce("model_instance_config", modelInstanceConfig);
            metadataInfo.putOnce("chat_model", chatModel);
            List<Integer> knowledgeIds = knowledgeBaseDtos.stream().map(KnowledgeBaseDto::getId)
                .toList();
            CommonRespDto<List<KnowledgeBaseMetadataBaseDto>> metadatas =
                knowledgeBaseMetadataService.getSameMetadataByKnowledgeBaseIds(knowledgeIds);
            List<JSONObject> metadatasDto = metadatas.getData().stream().map(metadata -> {
              JSONObject metadataDto = JSONUtil.createObj();
              metadataDto.putOnce("metadata_name", metadata.getName().toLowerCase());
              metadataDto.putOnce("metadata_type", metadata.getType().name().toLowerCase());
              return metadataDto;
            }).toList();
            metadataInfo.putOnce("metadatas", metadatasDto);
          } else {
            MetadataFilteringCondition metadataFilteringCondition = nodeData.getMetadata_filtering_condition();
            metadataInfo.putOnce("logical_operator",
                metadataFilteringCondition.getLogical_operator().toUpperCase());
            // 手动模式
            requestBody.putOnce("metadata_mode", "MANUAL");
            List<JSONObject> metadatas = new ArrayList<>();

            List<Condition> conditions = metadataFilteringCondition.getConditions();
            for (Condition condition : conditions) {
              SegmentGroup segmentGroup = variablePool.convertTemplate(
                  String.valueOf(condition.getValue()));
              String operatorValue = segmentGroup.getText();
              if (StrUtil.isBlank(operatorValue)) {
                continue;
              }
              JSONObject metadata = JSONUtil.createObj();
              metadata.putOnce("metadata_name", condition.getName());
              metadata.putOnce("metadata_type", condition.getType());
              metadata.putOnce("operator_name", condition.getComparison_operator());
              metadata.putOnce("operator_value", operatorValue);

              metadatas.add(metadata);
            }
            metadataInfo.putOnce("metadatas", metadatas);
          }
        }
        String jsonStr = JSONUtil.toJsonStr(requestBody);
        log.info("开始请求知识检索接口:{}", jsonStr);
        String post = HttpUtil.post(retrieveKnowledgeUrl, jsonStr);
        log.info("知识检索接口返回结果：{}", post);
        JSONObject respObj = JSONUtil.parseObj(post);
        if (respObj.getInt("code") != 1000) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error(StrUtil.subAfter(respObj.getStr("msg"), ":", false))
              .error_type("KnowledgeRetrievalNodeError")
              .retry_index(0)
              .build();
        }

        String usage = respObj.getStr("usage");
        UsageInfo usageInfo = JSONUtil.toBean(JSONUtil.toJsonStr(usage), UsageInfo.class);
        List<KnowledgeRetrievalResult> results = respObj.getJSONObject("data")
            .getBeanList("result", KnowledgeRetrievalResult.class);

        ArraySegment arraySegment = new ArraySegment(SegmentType.ARRAY_ANY, results);

        Map<String, Object> outputs = Map.of("result", arraySegment, "usage", usageInfo);

        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.SUCCEEDED)
            .inputs(inputs)
            .process_data(null)
            .outputs(outputs)
            .build();
      }else {
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .inputs(inputs)
            .error("知识库获取失败！")
            .error_type("KnowledgeRetrievalNodeError")
            .build();
      }
    } catch (Exception e) {
      log.warn("Unexpected error during knowledge retrieval", e);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("节点运行异常" + e.getMessage())
          .error_type(e.getClass().getSimpleName())
          .build();
    }
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    KnowledgeRetrievalNode nodeData = JSONUtil.toBean(jsonObject,
        KnowledgeRetrievalNode.class);

    map.put(node.getStr("id") + ".query", nodeData.getQuery_variable_selector());
    return map;
  }
}
