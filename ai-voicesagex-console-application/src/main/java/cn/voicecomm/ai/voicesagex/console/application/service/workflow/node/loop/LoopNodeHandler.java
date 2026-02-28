package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.VariableFactory.SEGMENT_TO_VARIABLE_MAP;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.VariableFactory.build_segment_with_type;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType.FLOAT;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType.INTEGER;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType.SEGMENT_TYPE_TO_CLASS_MAP;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType.fromValue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.InputType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionMetadataKey;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowNodeExecutionsMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunner;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunnerContext;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.NodeExecutionContext;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.NodeHandlerMapping;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.Condition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopNode.LoopVariableData;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ConditionProcessor;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.NodeTypeUtil;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ProcessConditionsResult;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.SseEmitterManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 循环节点处理
 *
 * @author: gaox
 * @date: 2025/11/13 9:44
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class LoopNodeHandler extends BaseNodeHandler {

  private final GraphRunner graphRunner;
  private final SseEmitterManager sseEmitterManager;
  private final WorkflowNodeExecutionsMapper workflowNodeExecutionsMapper;

  /**
   * 运行
   *
   * @param variablePool  变量池
   * @param nodeCanvas    节点画布
   * @param graph         画布
   * @param workflowRunId 工作流运行ID
   * @param appId         应用
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);
    String nodeId = nodeCanvas.getStr("id");
    LoopNode nodeData = JSONUtil.toBean(jsonObject, LoopNode.class);
    Integer loopCount = nodeData.getLoop_count();
    List<Condition> breakConditions = nodeData.getBreak_conditions();
    String logicalOperator = nodeData.getLogical_operator();
    Map<String, Object> inputs = new HashMap<>();
    Map<String, Object> runInputs = new HashMap<>();
    inputs.put("loop_count", loopCount);
    Map<String, Object> outputs = new HashMap<>();
    outputs.put("usage", new UsageInfo());
    // 验证循环起始节点
    if (StrUtil.isBlank(nodeData.getStart_node_id())) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("循环节点 " + nodeId + " 缺少 start_node_id 字段")
          .build();
    }
    // 3. 初始化循环变量
    Map<String, List<String>> loop_variable_selectors = new HashMap<>();
    if (nodeData.getLoop_variables() != null && !nodeData.getLoop_variables().isEmpty()) {
      Map<String, Function<LoopVariableData, Segment>> value_processor = new HashMap<>();
      value_processor.put("constant",
          var -> get_segment_for_constant(fromValue(var.getVar_type()), var.getValue()));
      value_processor.put("variable",
          var -> variablePool.get((List<String>) var.getValue()));

      for (LoopVariableData loop_variable : nodeData.getLoop_variables()) {
        if (!value_processor.containsKey(loop_variable.getValue_type())) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("循环变量 " + loop_variable.getLabel() + " 的值类型 '"
                  + loop_variable.getValue_type()
                  + "' 无效")
              .build();
        }

        Segment processed_segment = value_processor.get(loop_variable.getValue_type())
            .apply(loop_variable);
        if (processed_segment == null) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("循环变量 " + loop_variable.getLabel() + " 的值无效")
              .build();
        }

        List<String> variable_selector = Arrays.asList(nodeId, loop_variable.getLabel());
        Object variable = segment_to_variable(processed_segment, variable_selector, null, null,
            null);
        variablePool.add(variable_selector, variable);
        loop_variable_selectors.put(loop_variable.getLabel(), variable_selector);
        inputs.put(loop_variable.getLabel(), processed_segment.getValue());
        runInputs.put(loop_variable.getLabel(), processed_segment.getValue());
      }
    }
    ConditionProcessor conditionProcessor = new ConditionProcessor();

    Map<String, Double> loopDurationMap = new HashMap<>();
    Map<String, Map<String, Object>> singleLoopVariableMap = new HashMap<>();

    // 4. 发送循环开始事件
    sendLoopStartedEvent(workflowRunId, nodeCanvas);
    int costTokens = 0;
    try {
      // 5. 检查初始中断条件
      boolean reachBreakCondition = false;
      if (breakConditions != null && !breakConditions.isEmpty()) {
        breakConditions.forEach(condition -> {
          if (condition.getNumberVarType().equals(InputType.VARIABLE.getValue())) {
            List<String> selector = JSONUtil.toList(JSONUtil.parseArray(condition.getValue()),
                String.class);
            condition.setValue("{{#" + String.join(".", selector) + "#}}");
          }
        });
        ProcessConditionsResult result = conditionProcessor.processConditions(
            variablePool,
            breakConditions,
            logicalOperator
        );
        reachBreakCondition = result.finalResult();
      }

      if (reachBreakCondition) {
        loopCount = 0;
      }

      JSONArray nodes = graph.getJSONArray("nodes");
      List<Object> list = nodes.stream().filter(node -> {
        JSONObject nodeObj = (JSONObject) node;
        String loopId = nodeObj.getJSONObject("data").getStr("loop_id");
        return loopId != null && loopId.equals(nodeId);
      }).toList();
      // 创建新的graph对象，只包含过滤后的节点
      JSONObject filteredGraph = graph.clone();
      filteredGraph.set("nodes", JSONUtil.parse(list));

      String currentNodeId = nodeData.getStart_node_id();
      // 6. 执行循环迭代
      for (int i = 0; i < loopCount; i++) {
        GraphRun graphRun = GraphRun.init(filteredGraph, nodeData.getStart_node_id());
        LocalDateTime loopStartTime = LocalDateTime.now();
        WorkflowNodeExecutionsPo workflowNodeExecutionsPo;
        // 如果是第二次以后的循环，添加循环节点运行结果用于展示
        workflowNodeExecutionsPo = graphRunner.addNodeRunResult(appId, nodeCanvas, workflowRunId, i,
            i, nodeId);

        LoopSingleRunResult loopSingleRunResult = singleRun(variablePool, graphRun, currentNodeId,
            workflowRunId, appId, filteredGraph, i);

        if (CollUtil.isNotEmpty(nodeData.getLoop_variables())) {
          for (LoopVariableData entry : nodeData.getLoop_variables()) {
            String key = entry.getLabel();
            List<String> selector = List.of(nodeId, entry.getLabel());
            Segment segment = variablePool.get(selector);
            outputs.put(key, segment != null ? segment.getValue() : null);
            runInputs.put(key, segment != null ? segment.getValue() : null);
          }
        }
        outputs.put("loop_round", i + 1);

        // 记录循环持续时间
        double duration =
            Duration.between(loopStartTime, LocalDateTime.now()).toMillis() / 1000.0;
        loopDurationMap.put(String.valueOf(i), duration);
        updateNodeRunResult(workflowNodeExecutionsPo, runInputs, duration,
            loopSingleRunResult.getSuccess() ? WorkflowNodeExecutionStatus.SUCCEEDED.getValue()
                : WorkflowNodeExecutionStatus.FAILED.getValue());
        // 更新总token数
        costTokens += loopSingleRunResult.getTokens();
        UsageInfo usage = (UsageInfo) outputs.get("usage");

        if (usage != null) {
          usage.setTotal_tokens(costTokens);
        }
        // 收集循环变量值
        Map<String, Object> singleLoopVariable = new HashMap<>();
        for (Entry<String, List<String>> entry : loop_variable_selectors.entrySet()) {
          String key = entry.getKey();
          List<String> selector = entry.getValue();
          Segment segment = variablePool.get(selector);
          singleLoopVariable.put(key, segment != null ? segment.getValue() : null);
        }
        singleLoopVariableMap.put(String.valueOf(i), singleLoopVariable);

        if (!loopSingleRunResult.getSuccess()) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .outputs(outputs)
              .error(loopSingleRunResult.getError())
              .error_type(loopSingleRunResult.getErrorType())
              .build();
        }

        // 检查循环中断条件
        if (breakConditions != null && !breakConditions.isEmpty()) {
          ProcessConditionsResult processConditionsResult = conditionProcessor.processConditions(
              variablePool,
              breakConditions,
              logicalOperator
          );
          reachBreakCondition = processConditionsResult.finalResult();
        }

        if (reachBreakCondition) {
          break;
        }

        // 发送循环继续事件
        sendLoopNextEvent(workflowRunId, nodeCanvas);
      }

      // 7. 发送循环成功完成事件
      Map<WorkflowNodeExecutionMetadataKey, Object> successMetadata = new HashMap<>();
      successMetadata.put(WorkflowNodeExecutionMetadataKey.TOTAL_TOKENS, costTokens);
      successMetadata.put(WorkflowNodeExecutionMetadataKey.COMPLETED_REASON,
          reachBreakCondition ? "loop_break" : "loop_completed");
      successMetadata.put(WorkflowNodeExecutionMetadataKey.LOOP_DURATION_MAP,
          loopDurationMap);
      successMetadata.put(WorkflowNodeExecutionMetadataKey.LOOP_VARIABLE_MAP,
          singleLoopVariableMap);
      sendLoopCompletedEvent(workflowRunId, nodeCanvas);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.SUCCEEDED)
          .inputs(inputs)
          .outputs(outputs)
          .metadata(successMetadata)
          .build();
    } catch (Exception e) {
      // 8. 异常处理
      log.warn("Unexpected error during loop", e);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("节点运行异常" + e.getMessage())
          .error_type(e.getClass().getSimpleName())
          .build();
    }
  }

  public void updateNodeRunResult(WorkflowNodeExecutionsPo build, Map<String, Object> inputs,
      double duration, String status) {
    build.setInputs(JacksonUtil.toJsonStr(inputs))
        .setStatus(status)
        .setElapsed_time(duration)
        .setFinished_at(LocalDateTime.now());
    workflowNodeExecutionsMapper.updateById(build);
  }

  /**
   * 发送循环开始执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendLoopStartedEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "loop_started");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送循环开始事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送循环开始事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  /**
   * 发送循环继续执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendLoopNextEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "loop_next");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送循环继续事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送循环继续事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  /**
   * 发送循环完成执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendLoopCompletedEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "loop_completed");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送循环完成事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送循环完成事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  private LoopSingleRunResult singleRun(VariablePool variablePool, GraphRun graphRun,
      String currentNodeId, String workflowRunId, Integer appId, JSONObject filteredGraph,
      Integer loopIndex) {
    LoopSingleRunResult loopSingleRunResult = new LoopSingleRunResult();
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    GraphRunnerContext graphRunnerContext = new GraphRunnerContext();
    graphRunnerContext.getLoop_index().set(loopIndex);
    // 获取下一个节点
    List<String> nextNodeIds = graphRunner.getNextNodeIds(graphRun, currentNodeId,
        null);
    // 递归执行后续节点
    for (String nextNodeId : nextNodeIds) {
      // 再次检查是否已被中断
      if (graphRunnerContext.isInterrupted()) {
        log.info("检测到工作流已被中断，停止调度新节点");
        loopSingleRunResult.setSuccess(false);
        loopSingleRunResult.setTokens(graphRunnerContext.getTotal_tokens().get());
        return loopSingleRunResult;
      }
      NodeExecutionContext nextNodeContext = new NodeExecutionContext(nextNodeId,
          graphRun, variablePool,
          workflowRunId,
          filteredGraph, appId);
      CompletableFuture<Void> future = graphRunner.executeNodesRecursively(nextNodeContext,
          graphRunnerContext);
      futures.add(future);
    }

    // 等待所有分支完成
    if (!futures.isEmpty()) {
      CompletableFuture<Void> allDone = CompletableFuture.allOf(
          futures.toArray(new CompletableFuture[0]));
      try {
        allDone.join();
      } catch (Exception e) {
        log.error("执行节点发生异常", e);
        graphRunnerContext.interrupt();
        throw new RuntimeException(e);
      }
    }
    // 根据时间整理成LinkedHashMap
    LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();
    // 按 insertTime 排序
    graphRunnerContext.getOutputMapWithTime().entrySet().stream()
        .sorted(Comparator.comparing(e -> e.getValue().insertTime()))
        .forEach(e -> outputMap.put(e.getKey(), e.getValue().value()));
    loopSingleRunResult.setSuccess(!graphRunnerContext.isInterrupted());
    if (graphRunnerContext.isInterrupted()) {
      loopSingleRunResult.setError("循环内部节点执行错误");
      loopSingleRunResult.setErrorType("loopNodeError");
    }
    loopSingleRunResult.setOutputs(outputMap);
    loopSingleRunResult.setTokens(graphRunnerContext.getTotal_tokens().get());
    return loopSingleRunResult;
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    LoopNode nodeData = JSONUtil.toBean(jsonObject, LoopNode.class);
    Map<String, List<String>> variableMapping = new HashMap<>();

    // 提取节点配置
    JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
    for (Object o : nodes) {
      JSONObject nodeObj = (JSONObject) o;
      String subNodeId = nodeObj.getStr("id");
      JSONObject data = JSONUtil.getByPath(nodeObj, "data", null);
      String loopId = data.getStr("loop_id");
      if (!nodeId.equals(loopId)) {
        continue;
      }
      try {
        String nodeType = data.getStr("type");
        if (!NodeHandlerMapping.nodeHandlerMapping.containsKey(nodeType)) {
          continue;
        }
        BaseNodeHandler nodeHandler = NodeHandlerMapping.getNodeHandler(nodeType);
        Map<String, List<String>> subNodeVariableMapping = nodeHandler._extractVariableSelectorToVariableMapping(
            nodeObj, graph, nodeId);
        // 移除循环变量
        Map<String, List<String>> filteredMapping = new HashMap<>();
        for (Entry<String, List<String>> mappingEntry : subNodeVariableMapping.entrySet()) {
          String key = mappingEntry.getKey();
          List<String> value = mappingEntry.getValue();
          if (!nodeId.equals(value.getFirst())) {
            filteredMapping.put(subNodeId + "." + key, value);
          }
        }
        variableMapping.putAll(filteredMapping);
      } catch (Exception e) {
        log.error("提取子节点变量映射失败: {}", subNodeId, e);
      }
    }

    // 处理循环变量
    if (nodeData.getLoop_variables() != null) {
      for (LoopVariableData loopVariable : nodeData.getLoop_variables()) {
        if ("variable".equals(loopVariable.getValue_type())) {
          if (loopVariable.getValue() != null) {
            List<String> selector = (List<String>) loopVariable.getValue();
            variableMapping.put(nodeId + "." + loopVariable.getLabel(), selector);
          }
        }
      }
    }
    Set<String> loopNodeIds = nodes.stream().filter(o -> {
      JSONObject nodeObj = (JSONObject) o;
      JSONObject data = JSONUtil.getByPath(nodeObj, "data", null);
      String loopId = data.getStr("loop_id");
      return nodeId.equals(loopId);
    }).map(o -> {
      JSONObject nodeObj = (JSONObject) o;
      return nodeObj.getStr("id");
    }).collect(Collectors.toSet());
    Map<String, List<String>> finalMapping = new HashMap<>();
    for (Entry<String, List<String>> entry : variableMapping.entrySet()) {
      String key = entry.getKey();
      List<String> value = entry.getValue();
      if (!loopNodeIds.contains(value.getFirst())) {
        finalMapping.put(key, value);
      }
    }

    return finalMapping;
  }

  /**
   * 获取常量值对应的段
   *
   * @param var_type       变量类型
   * @param original_value 原始值
   * @return 段对象
   * @author gaox
   */
  public Segment get_segment_for_constant(SegmentType var_type, Object original_value) {
    Object value;

    // 1. 确保类型处理逻辑与_VALID_VAR_TYPE(entities.py)保持同步
    // 2. 考虑将此方法移到LoopVariableData类中以获得更好的封装

    if (!var_type.is_array_type() || var_type == SegmentType.ARRAY_BOOLEAN) {
      value = original_value;
    } else if (var_type == SegmentType.ARRAY_NUMBER ||
        var_type == SegmentType.ARRAY_OBJECT ||
        var_type == SegmentType.ARRAY_STRING) {
      if (original_value instanceof String) {
        try {
          // 解析JSON字符串
          value = JSONUtil.parse(original_value);
        } catch (Exception e) {
          log.error("解析循环节点JSON值失败, value_type={}, value={}", original_value, var_type);
          value = original_value;
        }
      } else {
        log.error("循环节点出现意外值, value_type={}, value={}", original_value, var_type);
        value = original_value;
      }
    } else {
      throw new AssertionError("此语句不应该被执行到.");
    }

    try {
      return build_segment_with_type(var_type, value);
    } catch (RuntimeException type_exc) {
      // 如果适用，尝试将值解析为JSON编码的字符串
      if (!(original_value instanceof String)) {
        throw type_exc;
      }
      try {
        value = JSONUtil.parse((String) original_value);
        return build_segment_with_type(var_type, value);
      } catch (RuntimeException parse_exc) {
        throw type_exc;
      }
    }
  }

  /**
   * 将段对象转换为变量对象
   */
  public static Variable segment_to_variable(
      Segment segment,
      List<String> selector,
      String id,
      String name,
      String description) {

    // 如果段对象已经是变量对象，直接返回
    if (segment instanceof Variable) {
      return (Variable) segment;
    }

    // 获取变量名称，如果未提供则使用选择器的最后一个元素
    String variableName = name != null ? name : selector.getLast();

    // 生成变量ID，如果未提供则使用UUID生成
    String variableId = id != null ? id : UUID.randomUUID().toString();

    // 获取段对象的类型
    Class<? extends Segment> segmentType = SEGMENT_TYPE_TO_CLASS_MAP.get(segment.getValue_type());
    // 在映射关系中查找对应的变量类
    Class<? extends Variable> variableClass = SEGMENT_TO_VARIABLE_MAP.get(segmentType);
    if (variableClass == null) {
      // 如果类型是NUMBER，根据值的实际类型来决定具体的段类型
      if (segment.getValue_type() == SegmentType.NUMBER) {
        Object value = segment.getValue();
        if (value instanceof Integer) {
          // 如果值是整数或可以表示为整数的数值，使用IntegerSegment
          segment.setValue_type(INTEGER);
        } else if (value instanceof Double || value instanceof Float) {
          // 如果值是浮点数，使用FloatSegment
          segment.setValue_type(FLOAT);
        }
      } else {
        throw new RuntimeException("不支持的段类型 " + segmentType);
      }
    }

    try {
      // 获取变量实际值类型
      Variable result = new Variable(segment.getValue_type(), segment.getValue(),
          variableName);

      // 设置公共字段
      result.setId(variableId)
          .setDescription(description)
          .setSelector(new ArrayList<>(selector));

      return result;
    } catch (Exception e) {
      throw new RuntimeException("创建变量对象失败: " + e.getMessage());
    }
  }

}
