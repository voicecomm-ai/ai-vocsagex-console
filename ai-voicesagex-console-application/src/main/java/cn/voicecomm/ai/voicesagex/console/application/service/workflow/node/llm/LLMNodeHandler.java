package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.llm;

import static cn.voicecomm.ai.voicesagex.console.application.controller.PromptGenerateController.buildPromptRequest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.constant.application.workflow.WorkflowConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.LLMRequest;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.LLMRequest.PromptMessage;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.LLMResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.LLMUsage;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.SystemVariableKey;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Configs;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Vision;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArraySegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.FileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.NoneSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.StringSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.llm.LLMNode.PromptTemplate;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * LLM节点处理
 *
 * @author wangf
 * @date 2025/8/1 下午 4:28
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class LLMNodeHandler extends BaseNodeHandler {

  @DubboReference
  public ModelService modelService;


  @Value("${algoUrlPrefix}${chat.llmInvoke}")
  private String llmInvokeUrl;

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    try {
      LLMNode llmNode = getNode(nodeCanvas, LLMNode.class);
      log.info("开始执行LLM节点处理，节点数据: {}", JSONUtil.toJsonStr(llmNode));
      log.info("解析LLM节点配置: {}", JSONUtil.toJsonStr(llmNode));
      // 从变量池获取变量和获取值
      Map<String, Object> inputs = _fetchInputs(variablePool, llmNode);
      log.info("获取输入变量: {}", JSONUtil.toJsonStr(inputs));
      // 获取文件
      List<File> files = _fetchFiles(variablePool, llmNode);
      log.info("获取文件列表，文件数量: {}", files.size());
      Map<String, Object> nodeInputs = new LinkedHashMap<>();
      if (CollUtil.isNotEmpty(files)) {
        // 将文件列表转换为字典列表
        List<Map<String, Object>> fileDictList = files.stream().map(BeanUtil::beanToMap)
            .collect(Collectors.toList());

        // 添加到节点输入中
        nodeInputs.put("#files#", fileDictList);
        log.info("文件列表已添加到节点输入: {}", JSONUtil.toJsonStr(fileDictList));
      }
      // todo 获取上下文值,抛出event
      String contextValue = _fetchContext(variablePool, llmNode);
      if (contextValue != null) {
        nodeInputs.put("#context#", contextValue);
        log.info("上下文值已添加到节点输入，长度: {}", contextValue.length());
      }
      Segment segment = variablePool.get(
          List.of(WorkflowConstants.SYSTEM_VARIABLE_NODE_ID, SystemVariableKey.QUERY.getValue()));
      String query = "";
      if (segment != null) {
        query = segment.getText();
      }
      log.info("获取查询内容: {}", query);
      Integer modelId = llmNode.getModel().getId();
      log.info("使用模型ID: {}", modelId);
      // 构建请求
      LLMRequest llmRequest = LLMRequest.builder().model_instance_provider("ollama").stream(false)
          .is_history(false).build();

      CommonRespDto<ModelDto> modelDtoComm = modelService.getInfo(modelId);
      if (modelDtoComm.getData() != null && BooleanUtil.isFalse(
          modelDtoComm.getData().getIsShelf())) {
        log.warn("模型不可用，modelId: {}", modelId);
        return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(inputs)
            .error(StrUtil.format("{}模型已下架", modelDtoComm.getData().getName()))
            .error_type("LLMNodeError").build();
      }
      CommonRespDto<ModelDto> commonRespDto = modelService.getInfo(modelId);
      ModelDto modelDto = commonRespDto.getData();
      log.info("获取模型信息成功: {}", JSONUtil.toJsonStr(modelDto));
      JSONObject promptRequestJson = buildPromptRequest(modelDto);
      llmRequest.setModel_instance_config(promptRequestJson);
      // 视觉
      if (llmNode.getVision() != null && llmNode.getVision().isEnabled()) {
        log.info("启用视觉功能");
        llmRequest.setIs_vision(true);
        ArrayFileSegment fileSegment = (ArrayFileSegment) variablePool.get(
            llmNode.getVision().getConfigs().getVariable_selector());
        Object value = fileSegment.getValue();
        List<File> fileList = new ArrayList<>();
        if (value instanceof List<?> rawList) {
          if (rawList.isEmpty() || rawList.getFirst() instanceof File) {
            fileList = (List<File>) rawList;
          }
        }
        List<String> imageList = new ArrayList<>();
        for (File file : fileList) {
          imageList.add(Base64.getEncoder().encodeToString(
              FileUtil.readString(file.getStorage_key(), StandardCharsets.UTF_8).getBytes()));
        }
        llmRequest.setVision_images(imageList);
        llmRequest.setVision_resolution(llmNode.getVision().getConfigs().getDetail());
        log.info("视觉配置已设置，图片数量: {}, 分辨率: {}", imageList.size(),
            llmNode.getVision().getConfigs().getDetail());
      }
      // 结构化输出
      if (llmNode.isStructured_output_enabled()) {
        log.info("启用结构化输出");
        llmRequest.setIs_structured_output(true);
        llmRequest.setStructured_output_schema(llmNode.getStructured_output().getSchema());
        log.info("结构化输出schema: {}", llmNode.getStructured_output().getSchema());
      }
      // 输入消息
      List<PromptMessage> promptMessages = new ArrayList<>();
      List<JSONObject> processPromptMessages = new ArrayList<>();
      for (PromptTemplate template : llmNode.getPrompt_template()) {
        String type = "system";
        switch (template.getRole()) {
          case "system" -> type = "system";
          case "user" -> type = "human";
          case "assistant" -> type = "ai";
        }
        String text = variablePool.convertTemplate(template.getText()).getText();

        PromptMessage promptMessage = PromptMessage.builder().type(type)
            .content(text.replace("{{#context#}}", StrUtil.blankToDefault(contextValue, "")))
            .build();
        promptMessages.add(promptMessage);
        JSONObject processPrompt = JSONUtil.createObj().putOnce("role", template.getRole())
            .putOnce("text", promptMessage.getContent()).putOnce("files", new ArrayList<>());
        processPromptMessages.add(processPrompt);
      }
      llmRequest.setPrompt_messages(promptMessages);
      log.info("构建提示消息完成，消息数量: {}", promptMessages.size());
      // http请求
      log.info("LLM节点请求 url：{}，参数：{}", llmInvokeUrl, JSONUtil.toJsonStr(llmRequest));
      String post = HttpUtil.post(llmInvokeUrl, JSONUtil.toJsonStr(llmRequest));
      log.info("LLM节点请求结果：{}", JSONUtil.toJsonStr(post));
      LLMResponse response = JSONUtil.toBean(post, LLMResponse.class);
      if (response.getCode() != 1000) {
        return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(nodeInputs)
            .error(StrUtil.subAfter(response.getMsg(), ":", false))
            .error_type("LLMNodeError")
            .build();
      }
      Map<String, Object> outputs = Map.of("text", response.getData().getAssistant_message(),
          "usage", response.getUsage(), "finish_reason", "stop");
      Map<String, Object> processData = Map.of("model_mode", "chat", "prompts",
          processPromptMessages, "usage", response.getUsage(), "finish_reason", "stop",
          "model_provider", "ollama", "model_name", modelDto.getInternalName());
      log.info("构建输出数据完成");

      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.SUCCEEDED)
          .inputs(nodeInputs).process_data(processData).llm_usage(
              LLMUsage.builder().prompt_tokens(response.getUsage().getPrompt_tokens())
                  .completion_tokens(response.getUsage().getCompletion_tokens())
                  .total_tokens(response.getUsage().getTotal_tokens()).build()).outputs(outputs)
          .edge_source_handle(null).build();
    } catch (Exception e) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error(e.getMessage()).error_type("LLMNodeError")
          .build();
    }
  }

  private Map<String, Object> _fetchInputs(VariablePool variablePool, LLMNode llmNode) {
    log.info("开始获取输入变量");
    Map<String, Object> inputs = new LinkedHashMap<>();
    // 从变量池获取变量和值
    List<VariableSelector> variableSelectors = new ArrayList<>();
    for (PromptTemplate prompt : llmNode.getPrompt_template()) {
      VariableTemplateParser templateParser = new VariableTemplateParser(prompt.getText());
      variableSelectors.addAll(templateParser.extractVariableSelectors());
    }
    for (VariableSelector variableSelector : variableSelectors) {
      Segment segment = variablePool.get(variableSelector.getValueSelector());
      if (segment == null) {
        inputs.put(variableSelector.getVariable(), "");
      } else if (segment instanceof NoneSegment) {
        inputs.put(variableSelector.getVariable(), "");
      } else {
        inputs.put(variableSelector.getVariable(), segment.toObject());
      }
    }
    log.info("输入变量获取完成");
    return inputs;
  }

  private List<File> _fetchFiles(VariablePool variablePool, LLMNode llmNode) {
    log.info("开始获取文件列表");
    Boolean b = Optional.ofNullable(llmNode).map(LLMNode::getVision).map(Vision::isEnabled)
        .orElse(false);
    if (!b) {
      log.info("视觉功能未启用，返回空文件列表");
      return new ArrayList<>();
    }
    List<String> visionVariableSelector = Optional.of(llmNode).map(LLMNode::getVision)
        .map(Vision::getConfigs).map(Configs::getVariable_selector).orElse(new ArrayList<>());
    Segment segment = variablePool.get(visionVariableSelector);
    List<File> fileList = switch (segment) {
      case FileSegment segmentTemp -> List.of((File) segmentTemp.getValue());
      case ArrayFileSegment segmentTemp -> {
        Object value = segmentTemp.getValue();
        if (value instanceof List<?> rawList) {
          if (rawList.isEmpty() || rawList.getFirst() instanceof File) {
            yield (List<File>) rawList;
          }
        }
        yield new ArrayList<>();
      }
      case null, default -> new ArrayList<>();
    };
    log.info("文件列表获取完成，文件数量: {}", fileList.size());
    return fileList;
  }

  private String _fetchContext(VariablePool variablePool, LLMNode llmNode) {
    log.info("开始获取上下文");
    if (!llmNode.getContext().isEnabled()) {
      log.info("上下文功能未启用");
      return null;
    }
    if (CollUtil.isEmpty(llmNode.getContext().getVariable_selector())) {
      log.info("上下文变量选择器为空");
      return null;
    }
    Segment segment = variablePool.get(llmNode.getContext().getVariable_selector());

    return switch (segment) {
      case null -> {
        log.info("上下文段为空");
        yield null;
      }
      case StringSegment segmentObj -> {
        log.info("获取字符串类型上下文，长度: {}", segmentObj.getText().length());
        yield segmentObj.getText();
      }
      case ArraySegment segmentObj -> {
        StringBuilder contextStr = new StringBuilder();
        List<?> valueList = (List<?>) segmentObj.getValue();
        for (Object o : valueList) {
          if (o instanceof String) {
            contextStr.append(o).append("\n");
          } else {
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap(o);
            if (stringObjectMap.containsKey("content")) {
              contextStr.append(stringObjectMap.get("content")).append("\n");
            }
          }
        }
        log.info("获取数组类型上下文，段落数量: {}, 总长度: {}", valueList.size(),
            contextStr.length());
        yield contextStr.toString();
      }
      default -> {
        log.info("未知类型上下文");
        yield null;
      }
    };
  }


  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    log.info("开始提取变量选择器映射关系");
    LLMNode llmNode = getNode(node, LLMNode.class);
    if (CollUtil.isEmpty(llmNode.getPrompt_template())) {
      log.info("提示模板为空，返回空映射");
      return new LinkedHashMap<>();
    }
    // 处理提示模板
    List<VariableSelector> variableSelectors = new ArrayList<>();
    for (PromptTemplate prompt : llmNode.getPrompt_template()) {
      VariableTemplateParser templateParser = new VariableTemplateParser(prompt.getText());
      variableSelectors.addAll(templateParser.extractVariableSelectors());
    }
    log.info("提取变量选择器数量: {}", variableSelectors.size());
    // 构建变量映射
    LinkedHashMap<String, List<String>> variableMapping = new LinkedHashMap<>();
    for (VariableSelector variableSelector : variableSelectors) {
      variableMapping.put(variableSelector.getVariable(), variableSelector.getValueSelector());
    }
    // todo 处理内存配置
//    MemoryConfig memory = nodeData.getMemory();
//    if (memory != null && memory.getQueryPromptTemplate() != null) {
//      VariableTemplateParser queryParser = new VariableTemplateParser(memory.getQueryPromptTemplate());
//      List<VariableSelector> queryVariableSelectors = queryParser.extractVariableSelectors();
//      for (VariableSelector variableSelector : queryVariableSelectors) {
//        variableMapping.put(variableSelector.getVariable(), variableSelector.getValueSelector());
//      }
//    }

    // 处理上下文配置
    if (llmNode.getContext() != null && llmNode.getContext().isEnabled()) {
      variableMapping.put("#context#", llmNode.getContext().getVariable_selector());
      log.info("已添加上下文变量映射");
    }
    // 处理视觉配置
    if (llmNode.getVision() != null && llmNode.getVision().isEnabled()) {
      variableMapping.put("#files#", llmNode.getVision().getConfigs().getVariable_selector());
      log.info("已添加视觉文件变量映射");
    }

    // 处理内存查询
//    if (nodeData.getMemory() != null) {
//      variableMapping.put("#sys.query#", List.of("sys", SystemVariableKey.QUERY.getValue()));
//    }

    // todo  处理提示配置和Jinja2变量
//    if (promptConfig != null) {
//      boolean enableJinja = false;
//
//      // 检查是否启用Jinja2
//      if (promptTemplate instanceof List) {
//        @SuppressWarnings("unchecked")
//        List<Object> promptList = (List<Object>) promptTemplate;
//        enableJinja = promptList.stream()
//            .anyMatch(prompt -> prompt instanceof LLMNodeChatModelMessage &&
//                "jinja2".equals(((LLMNodeChatModelMessage) prompt).getEditionType()));
//      } else if (promptTemplate instanceof LLMNodeCompletionModelPromptTemplate) {
//        enableJinja = "jinja2".equals(((LLMNodeCompletionModelPromptTemplate) promptTemplate).getEditionType());
//      }
//
//      // 如果启用Jinja2，添加Jinja2变量
//      if (enableJinja && promptConfig.getJinja2Variables() != null) {
//        for (String jinja2Variable : promptConfig.getJinja2Variables()) {
//          variableMapping.put(jinja2Variable.getVariable(), variableSelector.getValueSelector());
//        }
//        for (List<String> jinja2Variable : promptConfig.getJinja2Variables()) {
//          variableMapping.put(jinja2Variable.getVariable(), variableSelector.getValueSelector());
//        }
//      }
//    }

    // 为所有变量键添加节点ID前缀
    Map<String, List<String>> resultMapping = new LinkedHashMap<>();
    for (Map.Entry<String, List<String>> entry : variableMapping.entrySet()) {
      String key = JSONUtil.getByPath(node, "id") + "." + entry.getKey();
      resultMapping.put(key, entry.getValue());
    }
    log.info("变量选择器映射关系提取完成，映射数量: {}", resultMapping.size());
    return resultMapping;
  }


}