package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration;

import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.NodeTypeUtil.NODE_TYPE_CLASSES_MAPPING;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionMetadataKey;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionTriggeredFrom;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowNodeExecutionsMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayAnySegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArraySegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.NoneSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunner;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunnerContext;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.NodeExecutionContext;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.NodeHandlerMapping;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopSingleRunResult;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.NodeTypeUtil;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.SseEmitterManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import kotlin.NotImplementedError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author: gaox
 * @date: 2025/11/13 11:39
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class IterationNodeHandler extends BaseNodeHandler {

  private final GraphRunner graphRunner;
  private final SseEmitterManager sseEmitterManager;
  private final WorkflowNodeExecutionsMapper workflowNodeExecutionsMapper;
  private final AtomicBoolean globalInterrupted = new AtomicBoolean();

  /**
   * 运行
   *
   * @param variablePool
   * @param nodeCanvas
   * @param graph
   * @param workflowRunId
   * @param appId
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);
    String nodeId = nodeCanvas.getStr("id");
    IterationNode nodeData = JSONUtil.toBean(jsonObject, IterationNode.class);
    Map<String, Object> outputs = new HashMap<>();
    outputs.put("usage", new UsageInfo());
    Segment variable = variablePool.get(nodeData.getIterator_selector());
    if (variable == null) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error("批处理节点未找到变量")
          .build();
    }
    if (!(variable instanceof ArraySegment) && !(variable instanceof NoneSegment)) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error("批处理节点的变量必须为数组")
          .build();
    }
    if (variable instanceof NoneSegment || CollUtil.isEmpty((Collection<?>) variable.getValue())) {
      // Try our best to preserve the type informat.
      Segment output;
      if (variable instanceof ArraySegment arraySegment) {
        output = arraySegment.setValue(new ArrayList<>());
      } else {
        output = new ArrayAnySegment(new ArrayList<>());
      }

      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.SUCCEEDED)
          .outputs(Collections.singletonMap("output", output))
          .build();
    }

    List<?> iteratorListValue = (List<?>) variable.toObject();

    if (iteratorListValue == null) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error("批处理节点的变量必须为数组")
          .build();
    }

    Map<String, Object> inputs = Collections.singletonMap("iterator_selector", iteratorListValue);

    if (nodeData.getStart_node_id() == null) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .error("未找到开始节点")
          .build();
    }

    List<Object> outputList = new ArrayList<>();
    Map<String, Double> loopDurationMap = new HashMap<>();

    // 发送迭代开始事件
    sendIterationStartedEvent(workflowRunId, nodeCanvas);
    AtomicLong costTokens = new AtomicLong();
    globalInterrupted.set(false);
    JSONArray nodes = graph.getJSONArray("nodes");
    List<Object> list = nodes.stream().filter(node -> {
      JSONObject nodeObj = (JSONObject) node;
      String iterationId = nodeObj.getJSONObject("data").getStr("iteration_id");
      return iterationId != null && iterationId.equals(nodeId);
    }).toList();
    // 创建新的graph对象，只包含过滤后的节点
    try {
      JSONObject filteredGraph = graph.clone();
      filteredGraph.set("nodes", JSONUtil.parse(list));

      String currentNodeId = nodeData.getStart_node_id();
      if (nodeData.is_parallel()) {
        for (int i = 0; i < iteratorListValue.size(); i++) {
          outputList.add(null);
        }
        int maxWorkers = Math.min(
            nodeData.getParallel_nums(),
            iteratorListValue.size()
        );
        Semaphore semaphore = new Semaphore(maxWorkers);

        try (ExecutorService executor =
            Executors.newVirtualThreadPerTaskExecutor()) {

          List<Future<Long>> futures = new ArrayList<>();

          for (int i = 0; i < iteratorListValue.size(); i++) {

            int finalI = i;
            Future<Long> future = executor.submit(() -> {
              semaphore.acquire();
              try {
                GraphRun graphRun = GraphRun.init(filteredGraph, nodeData.getStart_node_id());
                IterationRunContext context = new IterationRunContext();
                context.setIsParallel(true);
                VariablePool clone = BeanUtil.copyProperties(variablePool, VariablePool.class);
                context.setVariablePool(clone);
                context.setNodeCanvas(nodeCanvas);
                context.setWorkflowRunId(workflowRunId);
                context.setAppId(appId);

                context.setNodeId(nodeId);
                context.setCurrentNodeId(currentNodeId);

                context.setIteratorListValue(iteratorListValue);
                context.setGraphRun(graphRun);
                context.setFilteredGraph(filteredGraph);
                context.setNodeData(nodeData);

                context.setOutputList(outputList);
                context.setLoopDurationMap(loopDurationMap);

                context.setInputs(inputs);
                context.setOutputs(outputs);
                context.setIndex(finalI);
                return singleRun(context);
              } finally {
                semaphore.release();
              }
            });

            futures.add(future);
          }

          // 收集所有任务的结果并累加到costTokens
          for (Future<Long> future : futures) {
            try {
              Long tokens = future.get();
              costTokens.addAndGet(tokens);
            } catch (Exception e) {
              log.warn("获取并行任务结果时发生错误", e);
              throw new RuntimeException(e);
            }
          }

        }
      } else {
        for (int i = 0; i < iteratorListValue.size(); i++) {
          GraphRun graphRun = GraphRun.init(filteredGraph, nodeData.getStart_node_id());
          IterationRunContext context = new IterationRunContext();
          context.setIsParallel(false);

          VariablePool clone = BeanUtil.copyProperties(variablePool, VariablePool.class);
          context.setVariablePool(clone);
          context.setNodeCanvas(nodeCanvas);
          context.setWorkflowRunId(workflowRunId);
          context.setAppId(appId);

          context.setNodeId(nodeId);
          context.setCurrentNodeId(currentNodeId);

          context.setIteratorListValue(iteratorListValue);
          context.setGraphRun(graphRun);
          context.setFilteredGraph(filteredGraph);
          context.setNodeData(nodeData);

          context.setOutputList(outputList);
          context.setLoopDurationMap(loopDurationMap);

          context.setInputs(inputs);
          context.setOutputs(outputs);
          context.setIndex(i);
          costTokens.addAndGet(singleRun(context));
          if (globalInterrupted.get()) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("节点运行异常")
                .error_type("IterationNodeException")
                .build();
          }
        }
      }
      outputs.put("output", outputList);
    } catch (Exception e) {
      // 异常处理
      log.warn("Unexpected error during iteration", e);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("节点运行异常" + e.getMessage())
          .error_type(e.getClass().getSimpleName())
          .build();
    }
    // 7. 发送批处理成功完成事件
    Map<WorkflowNodeExecutionMetadataKey, Object> successMetadata = new HashMap<>();
    successMetadata.put(WorkflowNodeExecutionMetadataKey.TOTAL_TOKENS, costTokens);
    successMetadata.put(WorkflowNodeExecutionMetadataKey.LOOP_DURATION_MAP,
        loopDurationMap);
    sendLoopCompletedEvent(workflowRunId, nodeCanvas);
    if (globalInterrupted.get()) {
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error("节点运行异常")
          .error_type("IterationNodeException")
          .metadata(successMetadata)
          .build();
    }
    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(inputs)
        .outputs(outputs)
        .metadata(successMetadata)
        .build();
  }

  private Long singleRun(IterationRunContext iterationRunContext) {
    log.info("开始执行index为{}的批处理", iterationRunContext.getIndex());
    LocalDateTime loopStartTime = LocalDateTime.now();
    WorkflowNodeExecutionsPo workflowNodeExecutionsPo;
    workflowNodeExecutionsPo = addNodeRunResult(iterationRunContext.getAppId(),
        iterationRunContext.getNodeCanvas(), iterationRunContext.getWorkflowRunId(),
        iterationRunContext.getIndex(), iterationRunContext.getNodeId(),
        iterationRunContext.getIndex());
    iterationRunContext.getVariablePool()
        .add(Arrays.asList(iterationRunContext.getNodeId(), "index"),
            iterationRunContext.getIndex());
    String iteratorInputType = iterationRunContext.getNodeData().getIterator_input_type();
    switch (iteratorInputType) {
      case "array[object]":
        iterationRunContext.getVariablePool()
            .add(Arrays.asList(iterationRunContext.getNodeId(), "item"),
                JSONUtil.parseObj(
                    iterationRunContext.getIteratorListValue()
                        .get(iterationRunContext.getIndex())));
        break;
      case "array[number]":
        iterationRunContext.getVariablePool()
            .add(Arrays.asList(iterationRunContext.getNodeId(), "item"),
                NumberUtil.parseNumber(
                    iterationRunContext.getIteratorListValue().get(iterationRunContext.getIndex())
                        .toString()));
        break;
      default:
        iterationRunContext.getVariablePool()
            .add(Arrays.asList(iterationRunContext.getNodeId(), "item"),
                iterationRunContext.getIteratorListValue().get(iterationRunContext.getIndex()));
    }
    LoopSingleRunResult loopSingleRunResult = singleRunIteration(iterationRunContext);

    // 记录批处理持续时间
    double duration =
        Duration.between(loopStartTime, LocalDateTime.now()).toMillis() / 1000.0;
    iterationRunContext.getLoopDurationMap()
        .put(String.valueOf(iterationRunContext.getIndex()), duration);
    updateNodeRunResult(workflowNodeExecutionsPo, iterationRunContext.getInputs(), duration,
        loopSingleRunResult.getSuccess() ? WorkflowNodeExecutionStatus.SUCCEEDED.getValue()
            : WorkflowNodeExecutionStatus.FAILED.getValue());
    // 更新总token数
    UsageInfo usage = (UsageInfo) iterationRunContext.getOutputs().get("usage");

    if (usage != null) {
      usage.setTotal_tokens(
          usage.getTotal_tokens() + Math.toIntExact(loopSingleRunResult.getTokens()));
    }

    // 发送批处理继续事件
    sendLoopNextEvent(iterationRunContext.getWorkflowRunId(), iterationRunContext.getNodeCanvas());
    return loopSingleRunResult.getTokens();
  }

  /**
   * 发送批处理完成执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendLoopCompletedEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "iteration_completed");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送批处理完成事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送批处理完成事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  /**
   * 发送批处理继续执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendLoopNextEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "iteration_next");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送批处理继续事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送批处理继续事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
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

  private LoopSingleRunResult singleRunIteration(IterationRunContext iterationRunContext) {
    LoopSingleRunResult loopSingleRunResult = new LoopSingleRunResult();
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    GraphRunnerContext graphRunnerContext = new GraphRunnerContext();
    graphRunnerContext.getLoop_index().set(iterationRunContext.getIndex());
    // 获取下一个节点
    List<String> nextNodeIds = graphRunner.getNextNodeIds(iterationRunContext.getGraphRun(),
        iterationRunContext.getCurrentNodeId(),
        null);
    // 递归执行后续节点
    for (String nextNodeId : nextNodeIds) {
      // 再次检查是否已被中断
      if (globalInterrupted.get()) {
        log.info("检测到工作流已被中断，停止调度新节点");
        loopSingleRunResult.setSuccess(false);
        loopSingleRunResult.setTokens(graphRunnerContext.getTotal_tokens().get());
        return loopSingleRunResult;
      }
      NodeExecutionContext nextNodeContext = new NodeExecutionContext(nextNodeId,
          iterationRunContext.getGraphRun(), iterationRunContext.getVariablePool(),
          iterationRunContext.getWorkflowRunId(),
          iterationRunContext.getFilteredGraph(), iterationRunContext.getAppId());
      CompletableFuture<Void> future =
          graphRunner.executeNodesRecursively(nextNodeContext, graphRunnerContext)
              .whenComplete((r, ex) -> {
                if (graphRunnerContext.isInterrupted()) {
                  globalInterrupted.set(true);
                }
              });
      futures.add(future);
    }

    // 等待所有分支完成
    if (!futures.isEmpty()) {
      CompletableFuture<Void> allDone = CompletableFuture.allOf(
          futures.toArray(new CompletableFuture[0]));
      try {
        allDone.join();
      } catch (Exception e) {
        log.error("等待所有分支完成时发生错误", e);
      }
    }
    if (globalInterrupted.get()) {
      log.info("检测到工作流已被中断，停止调度新节点");
      loopSingleRunResult.setSuccess(false);
      loopSingleRunResult.setTokens(graphRunnerContext.getTotal_tokens().get());
      return loopSingleRunResult;
    }
    Segment segment = iterationRunContext.getVariablePool()
        .get(iterationRunContext.getNodeData().getOutput_selector());
    if (iterationRunContext.getIsParallel()) {
      // 是并行，把每个结果放到对应位置
      if (segment != null) {
        iterationRunContext.getOutputList().set(iterationRunContext.getIndex(), segment.toObject());
      }
    } else {
      if (segment != null) {
        iterationRunContext.getOutputList().add(segment.toObject());
      } else {
        iterationRunContext.getOutputList().add(null);
      }
    }
    loopSingleRunResult.setSuccess(true);
    loopSingleRunResult.setTokens(graphRunnerContext.getTotal_tokens().get());
    return loopSingleRunResult;
  }

  public WorkflowNodeExecutionsPo addNodeRunResult(Integer appId, JSONObject node,
      String workflowRunId, int index, String loopNodeId, Integer loopIndex) {
    Integer toolAppId =
        node.getJSONObject("data").getStr("type").equals("agent") || node.getJSONObject("data")
            .getStr("type").equals("workflow") ? node.getJSONObject("data").getInt("appId")
            : null;
    if (node.getJSONObject("data").getStr("type").equals("mcp")) {
      toolAppId = node.getJSONObject("data").getInt("mcp_id");
    }
    WorkflowNodeExecutionsPo build = WorkflowNodeExecutionsPo.builder().id(null).app_id(appId)
        .workflow_id(appId).workflow_run_id(workflowRunId)
        .triggered_from(WorkflowNodeExecutionTriggeredFrom.WORKFLOW_RUN.getValue()).index(index)
        .node_execution_id(node.getStr("id")).node_id(node.getStr("id"))
        .node_type(node.getJSONObject("data").getStr("type"))
        .title(node.getJSONObject("data").getStr("title")).process_data("{}").outputs("{}")
        .status(WorkflowNodeExecutionStatus.RUNNING.getValue()).execution_metadata("{}")
        .predecessor_node_id(loopNodeId).loop_index(loopIndex)
        .toolAppId(toolAppId)
        .createdBy(UserAuthUtil.getUserId()).build();
    workflowNodeExecutionsMapper.insert(build);
    return build;
  }

  /**
   * 发送批处理开始执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendIterationStartedEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "iteration_started");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送批处理开始事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送批处理开始事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);

    IterationNode nodeData = JSONUtil.toBean(jsonObject, IterationNode.class);
    Map<String, List<String>> variableMapping = new HashMap<>();
    variableMapping.put(nodeId + ".input_selector", nodeData.getIterator_selector());
    // Get node configs from graph_config
    Map<String, JSONObject> nodeConfigs = new HashMap<>();

    Map<String, List<String>> finalMapping = new HashMap<>();
    // 提取节点配置
    JSONArray nodes = JSONUtil.getByPath(graph, "nodes", JSONUtil.createArray());
    if (nodes != null) {
      for (Object o : nodes) {
        JSONObject nodeObj = (JSONObject) o;
        if (nodeObj.containsKey("id")) {
          nodeConfigs.put((String) nodeObj.get("id"), nodeObj);
        }
      }

      for (Map.Entry<String, JSONObject> entry : nodeConfigs.entrySet()) {
        String subNodeId = entry.getKey();
        JSONObject subNodeConfig = entry.getValue();

        JSONObject data = JSONUtil.parseObj(subNodeConfig.get("data"));
        String iterationId = data.getStr("iteration_id");
        if (!nodeId.equals(iterationId)) {
          continue;
        }

        // variable selector to variable mapping
        try {
          // Get node class
          String nodeType = Objects.requireNonNull(NodeType.getByValue(data.getStr("type")))
              .getValue();
          if (!NODE_TYPE_CLASSES_MAPPING.containsKey(nodeType)) {
            continue;
          }
          BaseNodeHandler nodeHandler = NodeHandlerMapping.getNodeHandler(nodeType);
          if (nodeType.equals(NodeType.ITERATION.getValue())) {
            continue;
          }
          Map<String, List<String>> subNodeVariableMapping = nodeHandler._extractVariableSelectorToVariableMapping(
              subNodeConfig, graph, nodeId);
          // remove iteration variables
          Map<String, List<String>> filteredMapping = new HashMap<>();
          for (Map.Entry<String, List<String>> mappingEntry : subNodeVariableMapping.entrySet()) {
            String key = mappingEntry.getKey();
            List<String> value = mappingEntry.getValue();
            if (!value.isEmpty() && !nodeId.equals(value.getFirst())) {
              filteredMapping.put(subNodeId + "." + key, value);
            }
          }

          variableMapping.putAll(filteredMapping);
        } catch (NotImplementedError ignored) {
        }
      }
      Set<String> ids = nodes.stream().filter(o -> {
        JSONObject nodeObj = (JSONObject) o;
        JSONObject data = JSONUtil.getByPath(nodeObj, "data", null);
        String iterationId = data.getStr("iteration_id");
        return nodeId.equals(iterationId);
      }).map(o -> {
        JSONObject nodeObj = (JSONObject) o;
        return nodeObj.getStr("id");
      }).collect(Collectors.toSet());
      // remove variable out from iteration
      for (Map.Entry<String, List<String>> entry : variableMapping.entrySet()) {
        String key = entry.getKey();
        List<String> value = entry.getValue();
        if (!ids.contains(value.getFirst())) {
          finalMapping.put(key, value);
        }
      }
    }
    return finalMapping;
  }
}
