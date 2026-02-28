package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.parameterextractor;

import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.ParameterExtractorRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.ParameterExtractorResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.LLMUsage;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code.CodeNode.OutputsType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 参数提取节点处理器
 *
 * @author wangf
 * @date 2025/9/8 下午 2:01
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class ParameterExtractorNodeHandler extends BaseNodeHandler {

  @DubboReference
  public ModelService modelService;

  @Value("${algoUrlPrefix}${chat.parameterExtractor}")
  private String parameterExtractorUrl;


  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {

    ParameterExtractorNode nodeData = getNode(nodeCanvas, ParameterExtractorNode.class);
    log.info("开始执行参数提取节点处理，节点数据: {}", JSONUtil.toJsonStr(nodeData));
    log.info("解析参数提取节点配置: {}", JSONUtil.toJsonStr(nodeData));
    Segment segment = variablePool.get(nodeData.getQuery());
    String query = segment == null ? "" : segment.getText();

    // 获取参数
    Map<String, Object> nodeInputs = new LinkedHashMap<>();

    nodeInputs.put("query", query);
    nodeInputs.put("files", new ArrayList<>());
    nodeInputs.put("parameters", nodeData.getParameters());

    // 转化参数为实际值，获取实际指令
    String realInstruction = variablePool.convertTemplate(nodeData.getInstruction()).getText();
    nodeInputs.put("instruction", realInstruction);

    ParameterExtractorRequest request = new ParameterExtractorRequest().setModel_instance_provider(
        "ollama").setInstruction(realInstruction).setQuery(query);

    // 获取模型信息
    CommonRespDto<ModelDto> modelDtoComm = modelService.getInfo(nodeData.getModel().getId());
    if (modelDtoComm.getData() != null && BooleanUtil.isFalse(
        modelDtoComm.getData().getIsShelf())) {
      log.warn("模型不可用，modelId: {}", nodeData.getModel().getId());
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(nodeInputs)
          .error(StrUtil.format("{}模型已下架", modelDtoComm.getData().getName()))
          .error_type("ParameterExtractorNodeError").build();
    }
    CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(nodeData.getModel().getId());
    ModelDto modelDto = commonRespDto.getData();
    JSONObject promptRequestJson = buildPromptRequest(modelDto);
    request.setModel_instance_config(promptRequestJson);
    request.setArgs_schema(getOutputSchema(nodeData));

    // http请求
    log.info("参数提取节点请求 url：{}，参数：{}", parameterExtractorUrl, JSONUtil.toJsonStr(request));
    String post = HttpUtil.post(parameterExtractorUrl, JSONUtil.toJsonStr(request));
    log.info("参数提取节点请求结果：{}", JSONUtil.toJsonStr(post));

    ParameterExtractorResponse response = JSONUtil.toBean(post, ParameterExtractorResponse.class);
    if (response.getCode() != 1000) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(nodeInputs)
          .error(StrUtil.subAfter(response.getMsg(), ":", false))
          .error_type("ParameterExtractorNodeError").build();
    }

    // 运行数据
    Map<String, Object> processData = Map.of("model_mode", "chat", "prompts", realInstruction,
        "usage", response.getUsage(), "finish_reason", "stop", "model_provider", "ollama",
        "model_name", modelDto.getInternalName());
    Map<String, Object> result = response.getData()
        .getResult();
    if (result != null) {
      result.put("usage", response.getUsage());
    }
    return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.SUCCEEDED).inputs(nodeInputs)
        .process_data(processData).llm_usage(
            LLMUsage.builder().prompt_tokens(response.getUsage().getPrompt_tokens())
                .completion_tokens(response.getUsage().getCompletion_tokens())
                .total_tokens(response.getUsage().getTotal_tokens()).build())
        .outputs(result)
        .edge_source_handle(null).build();
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(
      JSONObject nodeCanvas, JSONObject graph, String nodeId) {
    ParameterExtractorNode nodeData = getNode(nodeCanvas, ParameterExtractorNode.class);

    // 初始化变量映射
    Map<String, List<String>> variableMapping = new LinkedHashMap<>();
    variableMapping.put("query", nodeData.getQuery());

    // 如果存在指令，则提取选择器
    if (nodeData.getInstruction() != null && !nodeData.getInstruction().isEmpty()) {
      List<VariableSelector> selectors = VariableTemplateParser.extractSelectorsFromTemplate(
          nodeData.getInstruction());
      for (VariableSelector selector : selectors) {
        variableMapping.put(selector.getVariable(), selector.getValueSelector());
      }
    }

    // 为所有键添加节点ID前缀
    return variableMapping.entrySet().stream().collect(
        Collectors.toMap(entry -> nodeCanvas.getStr("id") + "." + entry.getKey(), Entry::getValue));
  }


  private static JSONObject getOutputSchema(ParameterExtractorNode nodeData) {
    if (CollUtil.isEmpty(nodeData.getParameters())) {
      return null;
    }
    // 输出参数构建为jsonSchema
    JSONObject outputSchema = JSONUtil.createObj();
    outputSchema.putOnce("type", "object");
    JSONObject properties = JSONUtil.createObj();
    JSONArray required = JSONUtil.createArray();
    // 遍历所有输出参数，构建properties和required字段
    nodeData.getParameters().forEach(parameter -> {
      JSONObject property = JSONUtil.createObj();
      // 处理数组类型的输出参数
      String valueType = parameter.getType().toLowerCase();
      property.putOnce("description", parameter.getDescription());
      if (valueType.startsWith("array")) {
        property.putOnce("type", "array");
        if (OutputsType.fromValue(valueType) == OutputsType.ARRAY_OBJECT) {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "object"));
        } else if (OutputsType.fromValue(valueType) == OutputsType.ARRAY_STRING) {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "string"));
        } else {
          property.putOnce("items", JSONUtil.createObj().putOnce("type", "number"));
        }
      } else {
        // 处理非数组类型的输出参数
        property.putOnce("type", valueType);
      }
      properties.putOnce(parameter.getName(), property);
      required.add(parameter.getName());
    });
    outputSchema.putOnce("properties", properties);
    outputSchema.putOnce("required", required);
    return outputSchema;
  }
}