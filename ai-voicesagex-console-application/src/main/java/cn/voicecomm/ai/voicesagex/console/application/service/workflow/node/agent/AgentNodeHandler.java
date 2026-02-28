package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.AgentTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.controller.ChatController;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentGroup;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode.McpParam;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariableTemplateParser.VariableSelector;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatReqVo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: gaox
 * @date: 2025/9/10 14:12
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class AgentNodeHandler extends BaseNodeHandler {

  private final ApplicationService applicationService;
  @Autowired
  private ChatController chatController;


  /**
   * 运行
   *
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);
    AgentNode nodeData = JSONUtil.toBean(jsonObject, AgentNode.class);
    log.info("开始执行Agent节点处理，节点数据: {}", JSONUtil.toJsonStr(nodeData));
    Integer dataAppId = nodeData.getAppId();
    CommonRespDto<Boolean> respDto = applicationService.checkOnShelf(dataAppId);
    if (respDto.getData() == null) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error(respDto.getMsg()).error_type("AgentNodeError").build();
    }
    if (StrUtil.isBlank(nodeData.getQueryValue())) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error("用户问题为空").error_type("AgentNodeError").build();
    }

    String text = variablePool.convertTemplate(nodeData.getQueryValue()).getText();

    List<McpParam> param = nodeData.getParam();
    HashMap<String, Object> args = new LinkedHashMap<>();
    args.put("user_question", text);
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
            return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 为空").error_type("AgentNodeError").build();
          }
          args.put(name, null);
          continue;
        }

        Object finalValue;

        // 先把模板变量替换成字符串（仅对 String 或需要模板处理的值）
        if ("Variable".equalsIgnoreCase(p.getValue_type())) {
          String instr = String.valueOf(rawValue);
          SegmentGroup segmentGroup = variablePool.convertTemplate(instr);
          Set<Segment> collect = segmentGroup.getValue().stream().filter(Objects::nonNull)
              .collect(Collectors.toSet());
          if (p.getType().equalsIgnoreCase("File") || p.getType().equalsIgnoreCase("array[file]")) {
            return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数类型错误").error_type("AgentNodeError").build();
          } else {
            if (CollUtil.isEmpty(collect) && p.getRequired()) {
              return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
                  .error("参数 " + name + " 不能为空").error_type("AgentNodeError").build();
            }
            finalValue = segmentGroup.getText();
          }
        } else {
          finalValue = rawValue; // 常量直接取
        }
        if (finalValue == null || "".equals(finalValue)) {
          if (p.getRequired()) {
            return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
                .error("参数 " + name + " 不能为空").error_type("AgentNodeError").build();
          }
          continue;
        }

        // 再根据 type 强转为对应的数据类型
        switch (p.getType().toLowerCase()) {
          case "number":
            try {
              if (finalValue instanceof Number) {
                args.put(name, finalValue);
              } else {
                args.put(name, new BigDecimal(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 number 失败，值: {}", name, finalValue, e);
              args.put(name, finalValue);
            }
            break;
          case "array[string]":
          case "array[number]":
          case "array[object]":
            try {
              if (finalValue instanceof Collection) {
                args.put(name, finalValue);
              } else {
                // 尝试把 JSON 数组字符串转为 List
                args.put(name, JSONUtil.parseArray(finalValue.toString()));
              }
            } catch (Exception e) {
              log.warn("参数 {} 转换为 array 失败，值: {}", name, finalValue, e);
              args.put(name, finalValue);
            }
            break;
          case "boolean":
            args.put(name, Boolean.parseBoolean(finalValue.toString()));
            break;
          case "object":
            args.put(name, JSONUtil.parseObj(finalValue));
            break;
          case "string":
          default:
            args.put(name, finalValue);
            break;
        }
      }
    }

    CommonRespDto<ApplicationDto> dtoCommonRespDto = applicationService.getById(dataAppId);
    if (!dtoCommonRespDto.isOk()) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error(dtoCommonRespDto.getMsg()).error_type("AgentNodeError").build();
    }
    ApplicationDto applicationDto = dtoCommonRespDto.getData();
    TestChatReqVo testChatReqVo = new TestChatReqVo();
    testChatReqVo.setApplicationId(dataAppId);
    testChatReqVo.setRunType(ApplicationStatusEnum.EXPERIENCE.getKey());
    testChatReqVo.setQuery(text);
    testChatReqVo.setChatHistory(List.of());
    if (AgentTypeEnum.SINGLE.getKey().equals(applicationDto.getAgentType())) {
      testChatReqVo.setInputs(JSONUtil.parseObj(args));
    } else {
      // 复制args
      HashMap<String, Object> inputs = new LinkedHashMap<>(args);
      inputs.remove("user_question");
      // 根据key的前缀进行分组
      Map<String, Map<String, Object>> groupedMap = inputs.entrySet().stream()
          .collect(Collectors.groupingBy(
              entry -> {
                String key = entry.getKey();
                int dotIndex = key.indexOf('&');
                if (dotIndex > 0) {
                  return key.substring(0, dotIndex);
                }
                return key;
              },
              Collectors.toMap(
                  entry -> {
                    String key = entry.getKey();
                    int dotIndex = key.indexOf('&');
                    if (dotIndex > 0 && dotIndex < key.length() - 1) {
                      return key.substring(dotIndex + 1);
                    }
                    return key;
                  },
                  Map.Entry::getValue
              )
          ));
      testChatReqVo.setInputs(JSONUtil.parseObj(groupedMap));
    }
    testChatReqVo.setConversationId(UUID.randomUUID().toString());
    testChatReqVo.setIsSync(true);

    SseEmitter emitter = new SseEmitter(12 * 60 * 60 * 1000L);
    try {
      JSONObject entries;
      if (AgentTypeEnum.SINGLE.getKey().equals(applicationDto.getAgentType())) {
        log.info("agent节点（单智能体）对话生成--开始，参数：{}", JSONUtil.toJsonStr(testChatReqVo));
        entries = chatController.executeSingleAgentChat(testChatReqVo, emitter,
            UserAuthUtil.getUserId());
      } else {
        log.info("agent节点（多智能体）对话生成--开始，参数：{}", JSONUtil.toJsonStr(testChatReqVo));
        entries = chatController.executeMultipleAgentChat(testChatReqVo, emitter,
            UserAuthUtil.getUserId());
      }
      log.info("agent节点对话生成--完成：{}", entries);
      if (entries == null) {
        log.error("agent节点对话结果为空");
        return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(args)
            .error("agent节点对话结果为空").error_type("AgentNodeError").retry_index(0).build();
      }
      LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();
      Object chatHistory = entries.getJSONArray("chat_history").getLast();
      String byPath = JSONUtil.getByPath(JSONUtil.parse(chatHistory), "content", "");
      outputMap.put("result", byPath);
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.SUCCEEDED).inputs(args)
          .process_data(null).outputs(outputMap).build();
    } catch (Exception e) {
      log.error("执行 ChatController.executeChat 异常", e);
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED).inputs(args)
          .error(e.getMessage()).error_type("AgentNodeError").retry_index(0).build();
    }

  }

//
//  public static void main(String[] args) {
//    HashMap<String, Object> args1 = new LinkedHashMap<>();
//    args1.put("user_question", "你好");
//    args1.put("1&agent_id", "1");
//    args1.put("1&agent_name", 2);
//    args1.put("2&user", "张三");
//    args1.put("2&age", 22);
//    args1.put("2&male", true);
//    args1.remove("user_question");
//    Map<String, Map<String, Object>> groupedMap = args1.entrySet().stream()
//        .collect(Collectors.groupingBy(
//            entry -> {
//              String key = entry.getKey();
//              int dotIndex = key.indexOf('&');
//              if (dotIndex > 0) {
//                return key.substring(0, dotIndex);
//              }
//              return key;
//            },
//            Collectors.toMap(
//                entry -> {
//                  String key = entry.getKey();
//                  int dotIndex = key.indexOf('&');
//                  if (dotIndex > 0 && dotIndex < key.length() - 1) {
//                    return key.substring(dotIndex + 1);
//                  }
//                  return key;
//                },
//                Map.Entry::getValue
//            )
//        ));
//    System.out.println(JSONUtil.toJsonPrettyStr(JSONUtil.parseObj(groupedMap)));
//
//  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    AgentNode nodeData = JSONUtil.toBean(jsonObject, AgentNode.class);

    List<VariableSelector> variableSelectors = new ArrayList<>();
    if (CollUtil.isNotEmpty(nodeData.getParam())) {
      for (McpParam param : nodeData.getParam()) {
        if ("Variable".equals(param.getValue_type()) && ObjUtil.isNotNull(param.getValue())) {
          VariableTemplateParser parser = new VariableTemplateParser(param.getValue().toString());
          variableSelectors.addAll(parser.extractVariableSelectors());
        }
      }
    }

    // 添加 queryValue
    if ("Variable".equals(nodeData.getQuery_value_type()) && StrUtil.isNotBlank(
        nodeData.getQueryValue())) {
      VariableTemplateParser parser = new VariableTemplateParser(nodeData.getQueryValue());
      variableSelectors.addAll(parser.extractVariableSelectors());
    }

    for (VariableSelector selector : variableSelectors) {
      map.put(selector.getVariable(), new ArrayList<>(selector.getValueSelector()));
    }

    return map.entrySet().stream().collect(
        Collectors.toMap(entry -> node.getStr("id") + "." + entry.getKey(), Map.Entry::getValue));
  }
}
