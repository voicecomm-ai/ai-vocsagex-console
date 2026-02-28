package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.mcp.McpService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.converter.McpConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode.McpParam;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/9/10 14:12
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class McpNodeHandler extends BaseNodeHandler {

  private final McpMapper mcpMapper;
  @Value("${algoUrlPrefix}${mcp.invoke}")
  private String mcpInvokeUrl;
  /**
   * mcp检测接口
   */
  @Value("${algoUrlPrefix}${chat.mcpCheck}")
  private String mcpCheckUrl;
  private final McpService mcpService;
  private final McpConverter mcpConverter;

  /**
   * 运行
   *
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);

    McpNode nodeData = JSONUtil.toBean(jsonObject, McpNode.class);

    CommonRespDto<Boolean> available = mcpService.isAvailable(nodeData.getMcp_id());
    if (!available.getData()) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error(available.getMsg())
          .error_type("McpNodeError")
          .build();
    }
    McpPo mcpPo = mcpMapper.selectById(nodeData.getMcp_id());
    JSONObject mcpConfigJson = JSONUtil.createObj()
        .putOnce("transport", mcpPo.getTransport()).putOnce("url", mcpPo.getUrl());
    JSONObject mcp_config = JSONUtil.createObj();
    mcp_config.putOnce(mcpPo.getInternalName(), mcpConfigJson);

    JSONObject checkJson = JSONUtil.createObj()
        .putOnce(mcpPo.getInternalName(), mcpConfigJson);
    try (HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).build()) {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(mcpCheckUrl))
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(checkJson.toString())).build();
      HttpResponse<String> httpResponse = client.send(request, BodyHandlers.ofString());
      log.info("MCP检查---请求参数：{},请求结果：{}", JSONUtil.toJsonStr(checkJson),
          httpResponse.body());
      if (httpResponse.statusCode() != HttpStatus.HTTP_OK || !Boolean.TRUE.equals(
          JSONUtil.getByPath(JSONUtil.parseObj(httpResponse.body()), "data.available"))) {
        log.error("MCP检查失败! mcp：{}", mcpPo.getDisplayName());
        return NodeRunResult.builder()
            .status(WorkflowNodeExecutionStatus.FAILED)
            .error(mcpPo.getDisplayName() + "MCP不可用!")
            .error_type("McpNodeError")
            .build();
      }
    } catch (Exception e) {
      log.error("MCP检查异常! mcp：{}", mcpPo.getDisplayName(), e);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error(mcpPo.getDisplayName() + "MCP不可用!")
          .error_type("McpNodeError")
          .build();
    }
    List<McpParam> param = nodeData.getParam();
    JSONObject connection = new JSONObject();
    connection.set("url", mcpPo.getUrl());
    connection.set("transport", mcpPo.getTransport());
    JSONObject args = new JSONObject();
    if (param != null) {
      for (McpParam p : param) {
        if (p == null) {
          continue;
        }
        String name = p.getName();
        if (name == null) {
          continue;
        }

        Object rawValue = p.getValue();
        if (rawValue == null) {
          if (p.getRequired()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 为空")
                .error_type("McpNodeError")
                .build();
          }
          args.set(name, null);
          continue;
        }

        Object finalValue;

        // 先把模板变量替换成字符串（仅对 String 或需要模板处理的值）
        if ("Variable".equalsIgnoreCase(p.getValue_type())) {
          String instr = String.valueOf(rawValue);
          SegmentGroup segmentGroup = variablePool.convertTemplate(instr);
          Set<Segment> collect = segmentGroup.getValue().stream().filter(Objects::nonNull)
              .collect(Collectors.toSet());
          if (CollUtil.isEmpty(collect) && p.getRequired()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 不能为空")
                .error_type("McpNodeError")
                .build();
          }
          finalValue = segmentGroup.getText();
        } else {
          finalValue = rawValue; // 常量直接取
        }
        if (finalValue == null || "".equals(finalValue)) {
          if (p.getRequired()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 不能为空")
                .error_type("McpNodeError")
                .build();
          }
          finalValue = p.getDefaultValue();
          if (finalValue == null || "".equals(finalValue)) {
            continue;
          }
        }

        // 再根据 type 强转为对应的数据类型
        switch (p.getType().toLowerCase()) {
          case "number":
            try {
              if (finalValue instanceof Number) {
                args.set(name, finalValue);
              } else {
                args.set(name, new BigDecimal(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 number 失败，值: {}", name, finalValue, e);
              args.set(name, finalValue);
            }
            break;
          case "array[string]":
          case "array[number]":
          case "array[object]":
            try {
              if (finalValue instanceof Collection) {
                args.set(name, finalValue);
              } else {
                // 尝试把 JSON 数组字符串转为 List
                args.set(name, JSONUtil.parseArray(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 array 失败，值: {}", name, finalValue, e);
              args.set(name, finalValue);
            }
            break;
          case "boolean":
            args.set(name, Boolean.parseBoolean(finalValue.toString()));
            break;
          case "object":
            args.set(name, JSONUtil.parseObj(finalValue));
            break;
          case "string":
          default:
            args.set(name, finalValue);
            break;
        }
      }
    }
    JSONObject request = new JSONObject();
    request.set("mcp_name", mcpPo.getInternalName());
    request.set("tool_name", nodeData.getTool_name());
    request.set("connection", connection);
    if (MapUtil.isNotEmpty(args)) {
      request.set("args", args);
    }
    request.set("connection", connection);
    String jsonStr = toKeepEscapeJsonStr(request);
    log.info("开始mcp调用接口:{}", jsonStr);
    String post = HttpUtil.post(mcpInvokeUrl, jsonStr);
    log.info("mcp调用接口返回结果：{}", post);
    JSONObject respObj = JSONUtil.parseObj(post);
    if (respObj.getInt("code") != 1000) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(request)
          .error(StrUtil.subAfter(respObj.getStr("msg"), ":", false))
          .error_type("McpNodeError")
          .retry_index(0)
          .build();
    }
    Object result = JSONUtil.parseObj(respObj.get("data")).getObj("result");
    // 12.19 测试说不改变数据格式，不进行格式化
    Map<String, Object> outputs = Map.of("result", result);

    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(request)
        .process_data(null)
        .outputs(outputs)
        .build();
  }

  public static String toKeepEscapeJsonStr(Object obj) {
    String jsonStr = JSONUtil.toJsonStr(obj);

    // 把多余的双反斜杠替换为单个
    return jsonStr
        .replace("\\\\n", "\\n")
        .replace("\\\\t", "\\t")
        .replace("\\\\r", "\\r")
        .replace("\\\\s", "\\s");
  }

  Object parseResult(Object rawResult) {
    if (rawResult == null) {
      return null;
    }
    if (rawResult instanceof JSONObject || rawResult instanceof JSONArray) {
      return rawResult;
    }
    if (rawResult instanceof CharSequence) {
      String str = rawResult.toString().trim();
      // 特殊处理mcp网页爬虫多url工具
      if (str.startsWith("[webpage")) {
        return str;
      }
      // 判断是否可能是JSON
      if (JSONUtil.isTypeJSON(str)) {
        try {
          return JSONUtil.parse(str);
        } catch (Exception ignore) {
          return str; // 不是合法JSON，原样返回
        }
      } else {
        return str; // 普通字符串，不尝试JSON解析
      }
    }
    return rawResult; // 其他类型原样返回
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    McpNode nodeData = JSONUtil.toBean(jsonObject, McpNode.class);

    List<VariableSelector> variableSelectors = new ArrayList<>();
    if (CollUtil.isNotEmpty(nodeData.getParam())) {
      for (McpParam param : nodeData.getParam()) {
        if ("Variable".equals(param.getValue_type())) {
          VariableTemplateParser parser = new VariableTemplateParser(param.getValue().toString());
          variableSelectors.addAll(parser.extractVariableSelectors());
        }
      }
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
