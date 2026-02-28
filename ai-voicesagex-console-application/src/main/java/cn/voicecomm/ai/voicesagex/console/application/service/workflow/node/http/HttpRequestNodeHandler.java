package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.HttpEnum.BodyType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.impl.UploadFilesServiceImpl;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http.HttpRequestNode.BodyData;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.FileRebuildUtils;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.UploadFilesPo;
import java.io.IOException;
import java.net.URLConnection;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/9/8 14:12
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class HttpRequestNodeHandler extends BaseNodeHandler {

  private final FileRebuildUtils fileRebuildUtils;
  private final UploadFilesServiceImpl uploadFilesServiceImpl;

  /**
   * 运行
   *
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    Map<String, Object> processData = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);
    HttpRequestNode nodeData = JSONUtil.toBean(jsonObject, HttpRequestNode.class);
    Map<String, Object> inputs = new HashMap<>();
    try {
      // 创建HTTP执行器
      Executor httpExecutor = new Executor(
          nodeData,
          variablePool
          // maxRetries
      );
      // 记录请求日志
      processData.put("request", httpExecutor.toLog());
      // 执行HTTP请求
      Response response = httpExecutor.invoke();

      // 提取文件
      ArrayFileSegment files = extractFiles(httpExecutor.getUrl(), response);
      Map<String, Object> outputs = new HashMap<>();
      outputs.put("status_code", response.getResponse().statusCode());
      outputs.put("body", !response.isFile() ? new String(response.getResponse().body()) : "");
      outputs.put("headers", response.getHeaders());
      outputs.put("files", files);

      inputs.put("url", httpExecutor.getUrl());
      inputs.put("method", httpExecutor.getMethod());
      inputs.put("params", httpExecutor.getParams());
      inputs.put("headers", httpExecutor.getHeaders());
      if (nodeData.getBody().getType().equals(BodyType.RAW_TEXT.getValue()) || nodeData.getBody()
          .getType().equals(BodyType.BINARY.getValue())) {
        inputs.put("body", httpExecutor.getContent());
      } else {
        Map<String, String> input = new HashMap<>();
        Map<String, String> data = httpExecutor.getData();
        if (data != null) {
          input.putAll(data);
        }
        if (nodeData.getBody().getType().equals(BodyType.FORM_DATA.getValue())) {
          List<Entry<String, String>> params = httpExecutor.getParams();
          params.stream().filter(e -> StrUtil.isNotBlank(e.getKey()))
              .forEach(param -> input.put(param.getKey(), param.getValue()));
          if (CollUtil.isNotEmpty(httpExecutor.getFiles())) {
            httpExecutor.getFiles().stream().filter(e -> StrUtil.isNotBlank(e.getKey()))
                .forEach(file -> input.put(file.getKey(), file.getValue().getFilename()));
          }
          inputs.put("params", null);
        }
        inputs.put("body", input);
      }

      // 检查响应是否失败且需要继续处理错误
      if (!isSuccess(response.getResponse())) {
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .inputs(inputs)
            .outputs(outputs)
            .error("Request failed with status code " + response.getResponse().statusCode())
            .error_type("HTTPResponseCodeError")
            .process_data(processData)
            .build();
      }
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.SUCCEEDED)
          .inputs(inputs)
          .outputs(outputs)
          .process_data(processData)
          .build();

    } catch (Exception e) {
      log.info("http request node {} failed to run: ", nodeCanvas.getStr("id"), e);

      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error(e.getMessage())
          .error_type(e.getClass().getSimpleName())
          .process_data(processData)
          .build();
    }
  }

  private boolean isSuccess(HttpResponse<?> response) {
    int code = response.statusCode();
    return code >= 200 && code < 300;
  }

  public ArrayFileSegment extractFiles(String url, Response response) throws IOException {
    List<File> files = new ArrayList<>();

    if (!response.isFile()) {
      return new ArrayFileSegment(Collections.emptyList());
    }

    String contentType = response.getContentType();
    byte[] content = response.getResponse().body();
    String contentDispositionType = null;

    // 如果有 Content-Disposition，尝试用 filename 猜 MIME
    String contentDispositionFilename = response.getContentDispositionFilename();
    if (contentDispositionFilename != null && !contentDispositionFilename.isEmpty()) {
      contentDispositionType = URLConnection.guessContentTypeFromName(contentDispositionFilename);
    }

    // 根据 URL 猜文件名
    String filename = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
    filename = filename.substring(filename.lastIndexOf("/") + 1);

    String mimeType = contentDispositionType != null ? contentDispositionType :
        contentType != null ? contentType :
            URLConnection.guessContentTypeFromName(filename);

    if (mimeType == null) {
      mimeType = "application/octet-stream";
    }

    // --- 创建工具文件 ---
    UploadFilesPo uploadFilesPo = uploadFilesServiceImpl.uploadFile(filename, content, mimeType,
        null, null);

    // --- 构建 File 对象 ---
    Map<String, Object> mapping = new HashMap<>();
    mapping.put("tool_file_id", uploadFilesPo.getId());
    mapping.put("transfer_method", "tool_file");

    File file = fileRebuildUtils.buildFromMapping(mapping, null, null, false);
    files.add(file);

    return new ArrayFileSegment(files);
  }


  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    HttpRequestNode nodeData = JSONUtil.toBean(jsonObject, HttpRequestNode.class);
    List<VariableSelector> selectors = new ArrayList<>();

    // Extract selectors from URL, headers, and params
    selectors.addAll(VariableTemplateParser.extractSelectorsFromTemplate(nodeData.getUrl()));
    selectors.addAll(
        VariableTemplateParser.extractSelectorsFromTemplate(nodeData.getHeaders()));
    selectors.addAll(
        VariableTemplateParser.extractSelectorsFromTemplate(nodeData.getParams()));

    // Extract selectors from body if present
    if (nodeData.getBody() != null) {
      String bodyType = nodeData.getBody().getType();
      List<BodyData> data = nodeData.getBody().getData();

      switch (bodyType) {
        case "binary":
          if (data.size() != 1) {
            throw new RuntimeException("invalid body data, should have only one item");
          }
          List<String> selector = data.getFirst().getFile();
          selectors.add(new VariableSelector(
              "#" + String.join(".", selector) + "#",
              selector
          ));
          break;

        case "json":
        case "raw-text":
          if (data.size() != 1) {
            throw new RuntimeException("invalid body data, should have only one item");
          }
          selectors.addAll(
              VariableTemplateParser.extractSelectorsFromTemplate(data.getFirst().getKey()));
          selectors.addAll(
              VariableTemplateParser.extractSelectorsFromTemplate(data.getFirst().getValue()));
          break;

        case "x-www-form-urlencoded":
          for (BodyData item : data) {
            selectors.addAll(VariableTemplateParser.extractSelectorsFromTemplate(item.getKey()));
            selectors.addAll(VariableTemplateParser.extractSelectorsFromTemplate(item.getValue()));
          }
          break;

        case "form-data":
          for (BodyData item : data) {
            selectors.addAll(VariableTemplateParser.extractSelectorsFromTemplate(item.getKey()));
            if ("text".equals(item.getType())) {
              selectors.addAll(
                  VariableTemplateParser.extractSelectorsFromTemplate(item.getValue()));
            } else if ("file".equals(item.getType())) {
              selectors.add(new VariableSelector(
                  "#" + String.join(".", item.getFile()) + "#",
                  item.getFile()
              ));
            }
          }
          break;
      }
    }

    // Create mapping
    Map<String, List<String>> mapping = new HashMap<>();
    for (VariableSelector selectorIter : selectors) {
      mapping.put(node.getStr("id") + "." + selectorIter.getVariable(),
          selectorIter.getValueSelector());
    }

    return mapping;
  }

}
