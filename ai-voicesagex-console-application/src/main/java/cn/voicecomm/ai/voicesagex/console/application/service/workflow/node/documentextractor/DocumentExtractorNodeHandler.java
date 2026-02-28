package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.documentextractor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayStringSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.net.URI;
import java.nio.file.Paths;
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
public class DocumentExtractorNodeHandler extends BaseNodeHandler {

  @DubboReference
  public ModelService modelService;


  @Value("${algoUrlPrefix}${chat.documentExtractor}")
  private String documentExtractorUrl;

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject data = JSONUtil.getByPath(nodeCanvas, "data", null);

    DocumentExtractorNode nodeData = JSONUtil.toBean(data, DocumentExtractorNode.class);
    // 提取 query
    Segment segment = variablePool.get(nodeData.getVariable_selector());
    Map<String, Object> inputs = Collections.singletonMap("variable_selector", segment);
    if (segment == null){
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("输入参数为空")
          .error_type("DocumentExtractorNodeError")
          .retry_index(0)
          .build();
    }
    List<File> list = new ArrayList<>();
    switch (segment.getValue()) {
      case null -> {
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .inputs(inputs)
            .error("输入参数为空")
            .error_type("DocumentExtractorNodeError")
            .retry_index(0)
            .build();
      }
      case List<?> valueList -> {
        // 如果segment.getValue()已经是List类型，直接转换
        for (Object obj : valueList) {
          if (obj instanceof File) {
            list.add((File) obj);
          }
        }
      }
      case File file -> list.add(file);
      default -> {
      }
    }

    List<String> fileNameList = new ArrayList<>();
    for (File file : list) {
      String fileName = extractFileName(file.getUrl());
      fileNameList.add(fileName);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("file", fileNameList);
    String jsonStr = JSONUtil.toJsonStr(params);
    log.info("开始请求文档提取接口:{}", jsonStr);
    String post = HttpUtil.post(documentExtractorUrl, jsonStr);
    log.info("文档提取接口返回结果：{}", post);
    JSONObject jsonObject = JSONUtil.parseObj(post);
    if (jsonObject.getInt("code") != 1000) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error(StrUtil.subAfter(jsonObject.getStr("msg"), ":", false))
          .error_type("DocumentExtractorNodeError")
          .retry_index(0)
          .build();
    }
    List<String> stringList = jsonObject.getJSONObject("data").getBeanList("text", String.class);
    ArrayStringSegment arraySegment = new ArrayStringSegment(stringList);
    Map<String, Object> outputs;
    outputs = Map.of("text", arraySegment.getValue());

    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(inputs)
        .process_data(null)
        .outputs(outputs)
        .build();
  }

  public static String extractFileName(String url) {
    try {
      URI uri = URI.create(url);
      String path = uri.getPath();
      return Paths.get(path).getFileName().toString();
    } catch (Exception e) {
      // 处理异常
      return null;
    }
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    DocumentExtractorNode nodeData = JSONUtil.toBean(jsonObject,
        DocumentExtractorNode.class);

    map.put(node.getStr("id") + ".files", nodeData.getVariable_selector());
    return map;
  }
}