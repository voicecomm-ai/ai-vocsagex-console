package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.questionclassifier;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.questionclassifier.QuestionClassifierNode.ClassItem;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
public class QuestionClassifierNodeHandler extends BaseNodeHandler {

  @DubboReference
  public ModelService modelService;

  @Value("${algoUrlPrefix}${chat.questionClassifier}")
  private String questionClassifierUrl;

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);

    QuestionClassifierNode nodeData = JSONUtil.toBean(jsonObject, QuestionClassifierNode.class);
    // 提取 query
    Segment segment = variablePool.get(nodeData.getQuery_variable_selector());
    String query = segment == null ? "" : segment.getText();
    Map<String, Object> inputs = Collections.singletonMap("query", query);

    // 指令中的参数替换
    String instruction = nodeData.getInstruction();
    if (StrUtil.isNotBlank(instruction)) {
      SegmentGroup segmentGroup = variablePool.convertTemplate(instruction);
      instruction = segmentGroup.getText();
    }

    // 分类中参数替换
    List<ClassItem> classes = nodeData.getClasses();
    // 建立 映射：替换后的名字 → 原始 ClassItem
    Map<String, ClassItem> nameToOriginalMap = new HashMap<>();
    List<String> categoryNameList = CollUtil.newArrayList();
    classes.forEach(e -> {
      // 执行变量替换
      String renderedName = variablePool.convertTemplate(e.getName()).getText();

      // 建立映射：渲染后的名字 → 原始对象（含 id 和原始模板）
      nameToOriginalMap.put(renderedName, e);

      // 用于请求的列表
      categoryNameList.add(renderedName);
    });

    // 获取模型配置
    CommonRespDto<Boolean> modelServiceAvailable = modelService.isAvailable(
        nodeData.getModel_id());
    if (!modelServiceAvailable.isOk()) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("意图分类节点" + modelServiceAvailable.getMsg())
          .error_type("QuestionClassifierNodeError")
          .build();
    }
    CommonRespDto<ModelDto> info = modelService.getInfo(nodeData.getModel_id());
    ModelDto model = info.getData();
    log.info("获取到模型参数为:{}", JSONUtil.toJsonStr(model));
    Map<String, Object> params = new HashMap<>();
    params.put("model_instance_provider", model.getLoadingMode());
    JSONObject modelConfig = JSONUtil.createObj();
    modelConfig.putOnce("model_name", model.getInternalName());
    modelConfig.putOnce("base_url", model.getUrl());
    modelConfig.putOnce("apikey", model.getApiKey());
    modelConfig.putOnce("llm_type", "chat");
    modelConfig.putOnce("context_length", model.getContextLength());
    modelConfig.putOnce("max_token_length", model.getTokenMax());
    modelConfig.putOnce("is_support_vision", model.getIsSupportFunction());
    modelConfig.putOnce("is_support_function", model.getIsSupportVisual());

    params.put("model_instance_config", modelConfig);
    params.put("model_parameters", new JSONObject());
    params.put("input_querys", CollUtil.newArrayList(query));
    params.put("category_list", categoryNameList);
    params.put("instruction_list", CollUtil.newArrayList(instruction));

//    params.put("is_vision", nodeData.getVision().getEnabled());
//    params.put("vision_resolution", nodeData.getVision().getResolution());
    String jsonStr = JSONUtil.toJsonStr(params);
    log.info("开始请求意图分类接口:{}", jsonStr);
    String post = HttpUtil.post(questionClassifierUrl, jsonStr);
    log.info("意图分类接口返回结果：{}", post);
    JSONObject respObj = JSONUtil.parseObj(post);
    if (respObj.getInt("code") != 1000) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error(StrUtil.subAfter(respObj.getStr("msg"), ":", false))
          .error_type("QuestionClassifierNodeError")
          .retry_index(0)
          .build();
    }
    String usage = respObj.getStr("usage");
    UsageInfo usageInfo = JSONUtil.toBean(JSONUtil.toJsonStr(usage), UsageInfo.class);
    String respCategory = respObj.getJSONObject("data").getStr("category");

    ClassItem originalMatch = nameToOriginalMap.get(respCategory);
    String categoryName = classes.getFirst().getName();
    String categoryId = classes.getFirst().getId();
    if (originalMatch != null) {
      categoryName = originalMatch.getName();
      categoryId = originalMatch.getId();
    }
    Map<String, Object> outputs = Map.of("class_id", categoryId,
        "class_name", categoryName,
        "usage", usageInfo);

    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(inputs)
        .process_data(null)
        .outputs(outputs)
        .edge_source_handle(categoryId)
        .build();
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    QuestionClassifierNode nodeData = JSONUtil.toBean(jsonObject,
        QuestionClassifierNode.class);

    map.put("query", nodeData.getQuery_variable_selector());
    List<VariableSelector> variableSelectors = new ArrayList<>();
    if (nodeData.getInstruction() != null && StrUtil.isNotBlank(nodeData.getInstruction())) {
      VariableTemplateParser parser = new VariableTemplateParser(nodeData.getInstruction());
      variableSelectors.addAll(parser.extractVariableSelectors());
    }
    for (VariableSelector selector : variableSelectors) {
      map.put(
          selector.getVariable(),
          new ArrayList<>(selector.getValueSelector())
      );
    }

    return map.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> node.getStr("id") + "." + entry.getKey(),
            Map.Entry::getValue
        ));
  }
}
