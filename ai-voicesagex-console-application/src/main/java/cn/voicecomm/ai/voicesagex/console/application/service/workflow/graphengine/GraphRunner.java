package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowNodeExecutionsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.WorkflowRunsDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionTriggeredFrom;
import cn.voicecomm.ai.voicesagex.console.application.converter.WorkflowConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowNodeExecutionsMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.WorkflowRunsMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.NodeCanvas;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRunnerContext.TimeValue;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.NodeHandlerMapping;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.FileRebuildUtils;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.NodeTypeUtil;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.SseEmitterManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import cn.voicecomm.ai.voicesagex.console.util.po.application.WorkflowRunsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 图执行器，负责顺序执行图中的节点并推送SSE事件
 *
 * @author wangf
 * @date 2025/8/25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GraphRunner {

  private final SseEmitterManager sseEmitterManager;
  private final WorkflowNodeExecutionsMapper workflowNodeExecutionsMapper;
  private final WorkflowRunsMapper workflowRunsMapper;
  private final WorkflowConverter workflowConverter;
  private final FileRebuildUtils fileRebuildUtils;

  // 虚拟线程执行器
  private final ExecutorService virtualThreadExecutor;


  /**
   * 执行图中的节点
   *
   * @param workflowRunId         工作流运行ID
   * @param userInputs            用户输入
   * @param applicationStatusEnum 应用状态枚举
   * @param appId                 应用ID
   */
  public CommonRespDto<GraphRunnerContext> runWorkflow(String workflowRunId, WorkflowPo workflowPo,
      JSONObject userInputs,
      ApplicationStatusEnum applicationStatusEnum, Integer appId) {
    log.info("开始执行工作流，workflowRunId: {}", workflowRunId);
    GraphRunnerContext graphRunnerContext = new GraphRunnerContext();
    JSONObject graphJson = JSONUtil.parseObj(workflowPo.getGraph());
    JSONArray nodes = graphJson.getJSONArray("nodes");
    Optional<JSONObject> start = nodes.stream()
        .filter(e -> JSONUtil.getByPath(JSONUtil.parseObj(e), "data.type").equals("start"))
        .map(JSONUtil::parseObj).findAny();
    if (start.isEmpty()) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().id(workflowRunId).name("error").data("未找到开始节点！"));
      return CommonRespDto.error("未找到开始节点！");
    }

    // 重复请求过滤
    if (existSameRequest(workflowRunId)) {
      sseEmitterManager.sendEvent(workflowRunId, SseEmitter.event().id(workflowRunId).name("error")
          .data(StrUtil.format("workflow_run_id:{} 重复请求", workflowRunId)));
      return CommonRespDto.error(StrUtil.format("workflow_run_id:{} 重复请求", workflowRunId));
    }

    String startNodeId = start.get().getStr("id");

    try {
      // 初始化变量池
      VariablePool variablePool = new VariablePool().setUser_inputs(userInputs)
          .setEnvironment_variables(
              JSONUtil.toList(workflowPo.getEnvironment_variables(), Variable.class));

      NodeCanvas startNodeCanvas = NodeTypeUtil.getNodeCanvas(start.get());
      // 重建用户输入中的文件
      assert startNodeCanvas != null;
      Map<String, Object> userInputsMap = fileRebuildUtils.rebuildFileForUserInputsInStartNode("",
          (StartNode) startNodeCanvas.getData(), BeanUtil.beanToMap(userInputs));
      variablePool._setupVariablePool("", new ArrayList<>(), UserAuthUtil.getUserId(),
          userInputsMap, workflowPo, new ArrayList<>(), "", "");

      // 发送工作流开始事件
      sendWorkflowStartedEvent(workflowRunId, workflowPo, JSONUtil.parseObj(userInputsMap),
          applicationStatusEnum);

      // 创建图运行对象
      GraphRun graphRun = GraphRun.init(graphJson, startNodeId);
      // 执行根节点
      NodeExecutionContext nodeExecutionContext = new NodeExecutionContext(
          graphRun.getRoot_node_id(), graphRun, variablePool, workflowRunId, graphJson, appId);
      // 使用并行执行方式运行工作流
      executeWorkflowInParallel(nodeExecutionContext, graphRunnerContext);

      log.info("工作流执行成功完成，workflowRunId: {}, executeSteps: {}, totalTokens: {}",
          workflowRunId, graphRunnerContext.getExecuteSteps().get(),
          graphRunnerContext.getTotal_tokens().get());
      // 发送工作流成功完成事件
      sendWorkflowSucceededEvent(workflowRunId, graphRunnerContext);
      return CommonRespDto.success(graphRunnerContext);
    } catch (Exception e) {
      log.error("Error during graph execution, workflowRunId: {}, errorMessage: {}", workflowRunId,
          e.getMessage(), e);
      if (!graphRunnerContext.isInterrupted()) {
        sendWorkflowFailedEvent(workflowRunId, e.getMessage(), graphRunnerContext);
      }
      return CommonRespDto.error(e.getMessage());
    }
  }

  /**
   * 并行执行工作流
   *
   * @param nodeExecutionContext 节点执行上下文
   * @param graphRunnerContext   工作流运行上下文
   */
  private void executeWorkflowInParallel(NodeExecutionContext nodeExecutionContext,
      GraphRunnerContext graphRunnerContext) {

    String currentNodeId = nodeExecutionContext.getGraphRun().getRoot_node_id();

    long rootNodeStartTime = System.currentTimeMillis();
    NodeRunResult rootNodeResult = executeGraphNode(nodeExecutionContext, graphRunnerContext);
    handleNodeResult(nodeExecutionContext, rootNodeResult, rootNodeStartTime, graphRunnerContext);

    // 检查是否已被中断
    if (graphRunnerContext.isInterrupted()) {
      log.info("检测到工作流已被中断，停止执行后续节点");
      return;
    }

    // 获取下一个节点
    List<String> nextNodeIds = getNextNodeIds(nodeExecutionContext.getGraphRun(), currentNodeId,
        rootNodeResult.getEdge_source_handle());

    List<CompletableFuture<Void>> futures = new ArrayList<>();
    // 递归执行后续节点
    for (String nextNodeId : nextNodeIds) {
      // 再次检查是否已被中断
      if (graphRunnerContext.isInterrupted()) {
        log.info("检测到工作流已被中断，停止调度新节点");
        break;
      }

      NodeExecutionContext nextNodeContext = new NodeExecutionContext(nextNodeId,
          nodeExecutionContext.getGraphRun(), nodeExecutionContext.getVariablePool(),
          nodeExecutionContext.getWorkflowRunId(), nodeExecutionContext.getGraphJson(),
          nodeExecutionContext.getAppId());
      CompletableFuture<Void> future = executeNodesRecursively(nextNodeContext, graphRunnerContext);
      futures.add(future);
    }
    CompletableFuture<Void> all = CompletableFuture.allOf(
        futures.toArray(new CompletableFuture[0]));
    try {
      all.join(); // 阻塞等待，若 any 异常则抛出 CompletionException
    } catch (CompletionException e) {
      log.error("执行节点发生异常", e);
      graphRunnerContext.interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * 递归执行节点（支持并行）
   *
   * @param nodeExecutionContext 节点执行上下文
   * @param graphRunnerContext   工作流运行上下文
   * @return 节点执行结果
   */
  public CompletableFuture<Void> executeNodesRecursively(NodeExecutionContext nodeExecutionContext,
      GraphRunnerContext graphRunnerContext) {

    // 检查是否已被中断，如果中断则直接返回已完成的future
    if (graphRunnerContext.isInterrupted()) {
      log.info("检测到工作流已被中断，停止执行节点: {}", nodeExecutionContext.getNodeId());
      return CompletableFuture.completedFuture(null);
    }

    if (!graphRunnerContext.getHasExecutedNodeIds().add(nodeExecutionContext.getNodeId())) {
      log.info("节点已在等待执行：{}", nodeExecutionContext.getNodeId());
      return CompletableFuture.completedFuture(null);
    }

    // 检查前置边
    List<GraphEdge> incomingEdges = nodeExecutionContext.getGraphRun().getIncoming_edge_mapping()
        .get(nodeExecutionContext.getNodeId());

    JSONObject currentNode = nodeExecutionContext.getGraphRun().getNode_id_config_mapping()
        .get(nodeExecutionContext.getNodeId());
    if (currentNode == null) {
      log.error("Node not found: {}", nodeExecutionContext.getNodeId());
      // 设置中断标志
      graphRunnerContext.interrupt();
      return CompletableFuture.completedFuture(null);
    }

    CompletableFuture<Void> nodeFuture;

    // 检查是否有多个前驱节点（汇聚点）
    if (incomingEdges.size() > 1) {
      // 检查所有前驱节点是否都已经准备好
      // 所有入边都不处于UNKNOWN状态，并且至少有一条边处于TAKEN状态
      // 这种情况下表示前置节点已经执行完成，但当前节点尚未被触发执行
      // 如果不是所有的前驱节点都已准备好，创建一个监听机制
      // 创建一个总的CompletableFuture来等待所有前驱节点
      CompletableFuture<Void> waitForAllPredecessors = new CompletableFuture<>();

      CompletableFuture.runAsync(() -> {
        try {
          // 创建一个任务来监控前驱节点的注册情况
          CompletableFuture<?> monitoringFuture = CompletableFuture.runAsync(() -> {
            while (!waitForAllPredecessors.isDone()) {
              // 检查是否已被中断
              if (graphRunnerContext.isInterrupted()) {
                waitForAllPredecessors.completeExceptionally(
                    new RuntimeException("工作流已被中断"));
                break;
              }

              try {
                // UNKNOWN就是未处理，要么是无需处理的skip，要么就是需要处理的并且是taken
                if (CollUtil.allMatch(incomingEdges, e -> e.getState() != GraphEdgeState.UNKNOWN)
                    && CollUtil.anyMatch(incomingEdges,
                    e -> e.getState() == GraphEdgeState.TAKEN)) {
                  // 所有前驱节点都已注册，等待它们全部完成
                  waitForAllPredecessors.complete(null);
                }
                // 短暂休眠以避免过度占用CPU
                Thread.sleep(100);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                waitForAllPredecessors.completeExceptionally(e);
                break;
              } catch (Exception e) {
                waitForAllPredecessors.completeExceptionally(e);
                break;
              }
            }
          }, virtualThreadExecutor);

          // 给监控任务设置超时时间，避免无限等待（10分钟）
          CompletableFuture<Void> timeoutFuture = CompletableFuture.runAsync(() -> {
            try {
              Thread.sleep(600 * 1000); // 10分钟超时
              if (!waitForAllPredecessors.isDone()) {
                log.error("等待前置节点完成超时，节点ID: {}", nodeExecutionContext.getNodeId());
                waitForAllPredecessors.completeExceptionally(new RuntimeException(
                    "等待前置节点完成超时，节点ID: " + nodeExecutionContext.getNodeId()));
              }
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }, virtualThreadExecutor);

          // 监听任务完成时，取消超时任务
          monitoringFuture.whenComplete((v, throwable) -> {
            if (!timeoutFuture.isDone()) {
              timeoutFuture.cancel(true);
            }
          });

          // 超时任务触发时，取消监控任务
          timeoutFuture.whenComplete((v, throwable) -> {
            if (!monitoringFuture.isDone()) {
              monitoringFuture.cancel(true);
            }
            if (throwable instanceof InterruptedException) {
              Thread.currentThread().interrupt();
            } else if (throwable != null) {
              waitForAllPredecessors.completeExceptionally(throwable);
            }
          });

        } catch (Exception e) {
          waitForAllPredecessors.completeExceptionally(e);
        }
      }, virtualThreadExecutor);

      log.info("等待所有前驱节点完成，节点ID: {}", nodeExecutionContext.getNodeId());
      // 当所有前驱节点完成后执行当前节点
      AtomicReference<Long> startTime = new AtomicReference<>();
      nodeFuture = waitForAllPredecessors.thenCompose(v -> {
        // 再次检查是否已被中断
        if (graphRunnerContext.isInterrupted()) {
          return null;
        }

        return CompletableFuture.supplyAsync(() -> {
          log.info("所有前驱节点完成，当前节点ID：{}", nodeExecutionContext.getNodeId());
          startTime.set(System.currentTimeMillis());

          return executeGraphNode(nodeExecutionContext, graphRunnerContext);
        }, virtualThreadExecutor).thenAccept(
            result -> handleNodeResult(nodeExecutionContext, result, startTime.get(),
                graphRunnerContext));
      });
    } else {
      // 没有汇聚点，直接执行
      AtomicReference<Long> startTime = new AtomicReference<>();
      nodeFuture = CompletableFuture.supplyAsync(() -> {
        // 检查是否已被中断
        if (graphRunnerContext.isInterrupted()) {
          return null;
        }

        log.info("没有多个前置入边，直接执行...");
        startTime.set(System.currentTimeMillis());
        return executeGraphNode(nodeExecutionContext, graphRunnerContext);
      }, virtualThreadExecutor).thenAccept(
          result -> handleNodeResult(nodeExecutionContext, result, startTime.get(),
              graphRunnerContext));
    }

    // 获取下一个节点
    return nodeFuture.thenCompose(v -> {
      // 检查是否已被中断
      if (graphRunnerContext.isInterrupted()) {
        return CompletableFuture.completedFuture(null);
      }

      // 获取已完成节点的结果
      CompletableFuture<NodeRunResult> resultFuture = graphRunnerContext.getNodeCompletionFutures()
          .get(nodeExecutionContext.getNodeId());
      if (resultFuture != null) {
        return resultFuture.thenCompose(result -> {
          List<String> nextNodeIds = getNextNodeIds(nodeExecutionContext.getGraphRun(),
              nodeExecutionContext.getNodeId(), result.getEdge_source_handle());

          if (nextNodeIds.isEmpty()) {
            // 最终输出
            finalResultPut(result.getOutputs(), graphRunnerContext);
            return CompletableFuture.completedFuture(null);
          }

          List<CompletableFuture<Void>> nextFutures = new ArrayList<>();
          for (String nextNodeId : nextNodeIds) {
            NodeExecutionContext nextNodeContext = new NodeExecutionContext(nextNodeId,
                nodeExecutionContext.getGraphRun(), nodeExecutionContext.getVariablePool(),
                nodeExecutionContext.getWorkflowRunId(), nodeExecutionContext.getGraphJson(),
                nodeExecutionContext.getAppId());
            CompletableFuture<Void> future = executeNodesRecursively(nextNodeContext,
                graphRunnerContext);
            nextFutures.add(future);
          }

          return CompletableFuture.allOf(nextFutures.toArray(new CompletableFuture[0]));
        });
      } else {
        return CompletableFuture.completedFuture(null);
      }
    }).exceptionally(throwable -> {
      log.error("执行节点时发生异常，nodeId: {}", nodeExecutionContext.getNodeId(), throwable);
      // 设置中断标志，停止其他节点执行
      graphRunnerContext.interrupt();
      throw new RuntimeException("执行节点时发生异常", throwable);
    });
  }

  /**
   * 处理节点执行结果
   *
   * @param nodeExecutionContext 节点执行上下文
   * @param result               节点执行结果
   * @param startTime            节点开始执行时间
   * @param graphRunnerContext   图运行上下文
   */
  private void handleNodeResult(NodeExecutionContext nodeExecutionContext, NodeRunResult result,
      Long startTime, GraphRunnerContext graphRunnerContext) {
    try {
      String nodeId = nodeExecutionContext.getNodeId();
      GraphRun graphRun = nodeExecutionContext.getGraphRun();
      VariablePool variablePool = nodeExecutionContext.getVariablePool();
      String workflowRunId = nodeExecutionContext.getWorkflowRunId();

      JSONObject currentNode = graphRun.getNode_id_config_mapping().get(nodeId);

      // 添加节点使用tokens
      if (result.getOutputs() != null && result.getOutputs().containsKey("usage")) {
        // 增加状态graphRunnerContext.getTotal_tokens()
        UsageInfo usage = JSONUtil.toBean(JacksonUtil.toJsonStr(result.getOutputs().get("usage")),
            UsageInfo.class);
        graphRunnerContext.getTotal_tokens().addAndGet(usage.getTotal_tokens());
      }
      // 节点执行步骤+1
      graphRunnerContext.getExecuteSteps().incrementAndGet();

      // 记录节点执行记录
      WorkflowNodeExecutionsPo nodeExecutionsPo = graphRunnerContext.getNodeExecutionRecords()
          .get(nodeId);
      // 检查执行结果
      if (result.getStatus() == WorkflowNodeExecutionStatus.FAILED) {
        log.error("节点执行失败，nodeId: {}, error: {}", nodeId, result.getError());

        // 设置中断标志
        graphRunnerContext.interrupt();
        graphRunnerContext.setError(result.getError());
        if (nodeExecutionsPo != null) {
          updateNodeRunResult(nodeExecutionsPo, result, startTime);
          sendNodeFailedEvent(workflowRunId, nodeExecutionsPo);
        }
        sendWorkflowFailedEvent(workflowRunId, result.getError(), graphRunnerContext);
        return;
      }

      log.info("节点执行完成，nodeType:{},nodeName:{},nodeId: {}, status: {}",
          JSONUtil.getByPath(currentNode, "data.type"),
          JSONUtil.getByPath(currentNode, "data.title"), nodeId, result.getStatus().getValue());

      if (nodeExecutionsPo != null) {
        updateNodeRunResult(nodeExecutionsPo, result, startTime);
        graphRunnerContext.getNodeExecutionRecords().put(nodeId, nodeExecutionsPo);
        sendNodeCompletedEvent(workflowRunId, nodeExecutionsPo);
      }

      // 更新变量池
      updateVariablePoolWithNodeResult(variablePool, nodeId, result);
    } catch (Exception e) {
      log.error("处理节点结果时发生错误，nodeId: {}", nodeExecutionContext.getNodeId(), e);
      // 设置中断标志
      graphRunnerContext.interrupt();
      sendWorkflowFailedEvent(nodeExecutionContext.getWorkflowRunId(), e.getMessage(),
          graphRunnerContext);
    }
  }

  /**
   * 执行节点（带上下文）
   *
   * @param nodeExecutionContext 节点执行上下文
   * @param graphRunnerContext   图形运行上下文
   * @return 节点执行结果
   */
  private NodeRunResult executeGraphNode(NodeExecutionContext nodeExecutionContext,
      GraphRunnerContext graphRunnerContext) {
    String nodeId = nodeExecutionContext.getNodeId();
    GraphRun graphRun = nodeExecutionContext.getGraphRun();
    VariablePool variablePool = nodeExecutionContext.getVariablePool();
    String workflowRunId = nodeExecutionContext.getWorkflowRunId();
    JSONObject graphJson = nodeExecutionContext.getGraphJson();
    Integer appId = nodeExecutionContext.getAppId();

    JSONObject currentNode = graphRun.getNode_id_config_mapping().get(nodeId);
    // 发送节点开始事件
    sendNodeStartedEvent(nodeExecutionContext.getWorkflowRunId(), currentNode);
    if (currentNode == null) {
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error("Node not found: " + nodeId).build();
    }
    //

    String loopId = graphRun.getNode_id_config_mapping().get(nodeId).getStr("parentId");

    // 记录节点执行
    WorkflowNodeExecutionsPo nodeExecutionsPo = addNodeRunResult(nodeExecutionContext.getAppId(),
        currentNode, workflowRunId, 0, graphRunnerContext.getLoop_index().get(), loopId); // 索引暂时设为0
    graphRunnerContext.getNodeExecutionRecords().put(nodeId, nodeExecutionsPo);

    // 执行节点
    NodeRunResult nodeRunResult = executeNode(currentNode, variablePool, graphJson, workflowRunId,
        appId);

    // 将结果包装成CompletableFuture并保存
    CompletableFuture<NodeRunResult> future = CompletableFuture.completedFuture(nodeRunResult);
    graphRunnerContext.getNodeCompletionFutures().put(nodeId, future);

    return nodeRunResult;
  }

  /**
   * 使用节点执行结果更新变量池
   *
   * @param variablePool  变量池
   * @param nodeId        节点ID
   * @param nodeRunResult 节点执行结果
   */
  private void updateVariablePoolWithNodeResult(VariablePool variablePool, String nodeId,
      NodeRunResult nodeRunResult) {
    if (nodeRunResult.getOutputs() != null) {
      for (Entry<String, Object> objectEntry : nodeRunResult.getOutputs().entrySet()) {
        variablePool.appendVariablesRecursively(nodeId, List.of(objectEntry.getKey()),
            objectEntry.getValue());
      }
    }
  }

  /**
   * 更新节点执行结果
   *
   * @param build         节点执行记录
   * @param nodeRunResult 节点执行结果
   * @param startTime     节点开始执行时间
   */
  public void updateNodeRunResult(WorkflowNodeExecutionsPo build, NodeRunResult nodeRunResult,
      Long startTime) {
    build.setInputs(JacksonUtil.toJsonStr(nodeRunResult.getInputs()))
        .setProcess_data(JacksonUtil.toJsonStr(nodeRunResult.getProcess_data()))
        .setOutputs(JacksonUtil.toJsonStr(nodeRunResult.getOutputs()))
        .setStatus(nodeRunResult.getStatus().getValue()).setError(nodeRunResult.getError())
        .setElapsed_time((System.currentTimeMillis() - startTime) / 1000.00)
        .setExecution_metadata(JacksonUtil.toJsonStr(nodeRunResult.getMetadata()))
        .setFinished_at(LocalDateTime.now());
    workflowNodeExecutionsMapper.updateById(build);
  }


  /**
   * 检查是否存在相同的请求
   *
   * @param workflowRunId 工作流运行ID
   * @return 是否存在相同请求
   */
  private boolean existSameRequest(String workflowRunId) {
    Long l = workflowRunsMapper.selectCount(Wrappers.<WorkflowRunsPo>lambdaQuery()
        .eq(WorkflowRunsPo::getWorkflow_run_id, workflowRunId));
    return l > 0;
  }

  /**
   * 添加节点执行记录
   *
   * @param appId         应用ID
   * @param node          节点
   * @param workflowRunId 工作流运行ID
   * @param index         节点索引
   * @param loopIndex     循环索引
   * @return 节点执行记录
   */
  public WorkflowNodeExecutionsPo addNodeRunResult(Integer appId, JSONObject node,
      String workflowRunId, int index, int loopIndex, String predecessorNodeId) {
    Integer toolAppId =
        node.getJSONObject("data").getStr("type").equals("agent") || node.getJSONObject("data")
            .getStr("type").equals("workflow") ? node.getJSONObject("data").getInt("appId")
            : null;
    if (node.getJSONObject("data").getStr("type").equals("mcp")) {
      toolAppId = node.getJSONObject("data").getInt("mcp_id");
    }
    WorkflowNodeExecutionsPo build = WorkflowNodeExecutionsPo.builder().app_id(appId).id(null)
        .workflow_id(appId).workflow_run_id(workflowRunId)
        .triggered_from(WorkflowNodeExecutionTriggeredFrom.WORKFLOW_RUN.getValue()).index(index)
        .node_execution_id(node.getStr("id")).node_id(node.getStr("id"))
        .node_type(node.getJSONObject("data").getStr("type"))
        .title(node.getJSONObject("data").getStr("title")).process_data("{}").outputs("{}")
        .status(WorkflowNodeExecutionStatus.RUNNING.getValue()).execution_metadata("{}")
        .loop_index(loopIndex).predecessor_node_id(predecessorNodeId)
        .toolAppId(toolAppId)
        .createdBy(UserAuthUtil.getUserId()).build();
    workflowNodeExecutionsMapper.insert(build);
    return build;
  }

  /**
   * 执行单个节点
   *
   * @param nodeCanvas    节点配置
   * @param variablePool  变量池
   * @param workflowRunId 工作流运行ID
   * @param appId         应用ID
   * @return 节点执行结果
   */
  public NodeRunResult executeNode(JSONObject nodeCanvas, VariablePool variablePool,
      JSONObject graph, String workflowRunId, Integer appId) {
    try {
      BaseNodeHandler nodeHandler = NodeHandlerMapping.getNodeHandler(nodeCanvas);
      if (nodeHandler == null) {
        log.error("未找到节点处理器，nodeType: {}", JSONUtil.getByPath(nodeCanvas, "data.type"));
        return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
            .error("No handler found for node type: " + JSONUtil.getByPath(nodeCanvas, "data.type"))
            .build();
      }

      return nodeHandler.run(variablePool, nodeCanvas, graph, workflowRunId, appId);
    } catch (Exception e) {
      log.error("执行节点时发生错误，nodeId: {}, errorMessage: {}", nodeCanvas.getStr("id"),
          e.getMessage(), e);
      return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.FAILED)
          .error(e.getMessage()).build();
    }
  }

  /**
   * 获取所有下一个节点ID（支持多条出边）
   *
   * @param graphRun           当前图运行对象
   * @param nodeId             当前节点ID
   * @param edge_source_handle 边的源节点处理方式
   * @return 下一个节点ID列表
   */
  public List<String> getNextNodeIds(GraphRun graphRun, String nodeId, String edge_source_handle) {
    Map<String, List<GraphEdge>> outgoingEdgeMapping = graphRun.getOutgoing_edge_mapping();
    List<GraphEdge> outgoingEdges = outgoingEdgeMapping.get(nodeId);
    Map<String, List<GraphEdge>> incomingEdgeMapping = graphRun.getIncoming_edge_mapping();
    List<String> nextNodeIds = new ArrayList<>();

    if (CollUtil.isEmpty(outgoingEdges)) {
      log.info("没有找到出边，nodeId: {}", nodeId);
      return nextNodeIds;
    }

    if (StrUtil.isNotBlank(edge_source_handle)) {
      // 根据条件筛选出边
      Map<Boolean, List<GraphEdge>> edgeFilters = outgoingEdges.stream().collect(
          Collectors.partitioningBy(
              edge -> edge.getSource_node_id().equals(nodeId) && edge.getRun_condition() != null
                  && edge_source_handle.equals(edge.getRun_condition().getBranch_identify())));
      // 获取满足条件的
      edgeFilters.get(true).forEach(edge -> nextNodeIds.add(edge.getTarget_node_id()));
      // 入边的map中，所有来源是当前节点，并且目标节点id属于nextNodeIds的，state标记为taken
      markState(incomingEdgeMapping, nextNodeIds, nodeId);

      // 未满足条件的
      List<String> skippedNodeIds = edgeFilters.get(false).stream()
          .map(GraphEdge::getTarget_node_id).toList();
      // 所有来源是当前的节点，并且目标节点id属于skippedNodeIds的，（递归）state标记未skipped，
      markSkippedRecursively(incomingEdgeMapping, outgoingEdgeMapping, skippedNodeIds, nodeId);
    } else {
      // 默认获取所有出边的目标节点
      outgoingEdges.forEach(edge -> nextNodeIds.add(edge.getTarget_node_id()));
      // 入边的map中，所有来源是当前节点，并且目标节点id属于nextNodeIds的，state标记为taken
      markState(incomingEdgeMapping, nextNodeIds, nodeId);
    }

    return nextNodeIds;
  }

  /**
   * 标记入边状态
   *
   * @param incomingEdgeMapping 入边映射
   * @param nextNodeIds         下一个节点ID列表
   * @param nodeId              当前节点ID
   */
  private static void markState(Map<String, List<GraphEdge>> incomingEdgeMapping,
      List<String> nextNodeIds, String nodeId) {
    if (CollUtil.isEmpty(incomingEdgeMapping) || CollUtil.isEmpty(nextNodeIds)) {
      return;
    }

    incomingEdgeMapping.entrySet().stream().filter(entry -> nextNodeIds.contains(entry.getKey()))
        .forEach(entry -> entry.getValue().forEach(edge -> {
          if (edge.getSource_node_id().equals(nodeId)) {
            edge.setState(GraphEdgeState.TAKEN);
          }
        }));
  }

  /**
   * 递归标记边状态为skipped
   *
   * @param incomingEdgeMapping 入边映射
   * @param outgoingEdgeMapping 出边映射
   * @param skippedNodeIds      跳过的节点ID列表
   * @param nodeId              当前节点ID
   */
  private static void markSkippedRecursively(Map<String, List<GraphEdge>> incomingEdgeMapping,
      Map<String, List<GraphEdge>> outgoingEdgeMapping, List<String> skippedNodeIds,
      String nodeId) {
    if (CollUtil.isEmpty(incomingEdgeMapping) || CollUtil.isEmpty(skippedNodeIds)) {
      return;
    }
    // 标记当前节点的入边为SKIPPED状态
    // 筛选所有跳过节点的入边
    incomingEdgeMapping.entrySet().stream().filter(entry -> skippedNodeIds.contains(entry.getKey()))
        // 所有跳过节点的入边中  来源节点是当前节点的线，标记为SKIPPED
        .forEach(entry -> entry.getValue().forEach(edge -> {
          if (edge.getSource_node_id().equals(nodeId)) {
            edge.setState(GraphEdgeState.SKIPPED);
          }
        }));

    // 递归标记下游节点的入边
    for (String skippedNodeId : skippedNodeIds) {

      // 如果skippedNodeId的所有入边都是skipped，则继续递归
      List<GraphEdge> graphEdges = incomingEdgeMapping.get(skippedNodeId);
      if (graphEdges.stream().anyMatch(edge -> edge.getState() != GraphEdgeState.SKIPPED)) {
        continue;
      }
      // 获取下游节点的所有出边
      List<GraphEdge> outgoingEdges = outgoingEdgeMapping.get(skippedNodeId);
      if (CollUtil.isNotEmpty(outgoingEdges)) {
        List<String> nextNodeIds = outgoingEdges.stream().map(GraphEdge::getTarget_node_id)
            .distinct().toList();

        // 合并操作：设置边状态并筛选可继续递归的节点
        List<String> nextSkippedNodeIds = new ArrayList<>();
        for (String nextNodeId : nextNodeIds) {
          List<GraphEdge> nextNodeIncomingEdges = incomingEdgeMapping.get(nextNodeId);
          if (CollUtil.isEmpty(nextNodeIncomingEdges)) {
            // 如果没有入边，理论上不应该出现这种情况，但为了安全起见不继续递归
            continue;
          }

          // 设置从skippedNodeId出发的边为SKIPPED状态，并检查是否所有边都是SKIPPED
          boolean allSkipped = true;
          for (GraphEdge edge : nextNodeIncomingEdges) {
            if (edge.getSource_node_id().equals(skippedNodeId)) {
              edge.setState(GraphEdgeState.SKIPPED);
            }
            // 检查所有入边是否都是SKIPPED状态
            if (edge.getState() != GraphEdgeState.SKIPPED) {
              allSkipped = false;
            }
          }

          // 只有当下游节点的所有入边都是SKIPPED状态时，才继续递归标记
          if (allSkipped) {
            nextSkippedNodeIds.add(nextNodeId);
          }
        }

        // 继续递归标记确认可以被跳过的节点
        if (!nextSkippedNodeIds.isEmpty()) {
          markSkippedRecursively(incomingEdgeMapping, outgoingEdgeMapping, nextSkippedNodeIds,
              skippedNodeId);
        }
      }
    }
  }


  /**
   * 发送工作流开始事件
   *
   * @param workflowRunId         工作流运行
   * @param workflowPo            工作流信息
   * @param userInputs            用户输入
   * @param applicationStatusEnum 应用状态枚举
   */
  public void sendWorkflowStartedEvent(String workflowRunId, WorkflowPo workflowPo,
      JSONObject userInputs, ApplicationStatusEnum applicationStatusEnum) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "workflow_started");
      eventData.put("workflow_run_id", workflowRunId);

      WorkflowRunsPo workflowRunsPo = WorkflowRunsPo.builder().workflow_run_id(workflowRunId)
          .graph(workflowPo.getGraph()).triggered_from("debugging")
          .tenant_id(StrUtil.isBlank(workflowPo.getTenant_id()) ? "" : workflowPo.getTenant_id())
          .type(workflowPo.getType())
          .version(StrUtil.isBlank(workflowPo.getTenant_id()) ? "" : workflowPo.getVersion())
          .app_id(workflowPo.getApp_id()).workflow_id(workflowPo.getId())
          .created_at(LocalDateTime.now()).status(WorkflowExecutionStatus.RUNNING.getValue())
          .created_by(UserAuthUtil.getUserId()).created_at(LocalDateTime.now())
          .inputs(JacksonUtil.toJsonStr(userInputs)).run_type(applicationStatusEnum.getKey())
          .build();
      // 插入
      workflowRunsMapper.insert(workflowRunsPo);
      WorkflowRunsDto workflowRunsDto = workflowConverter.workflowRunPoToDto(workflowRunsPo);
      eventData.put("data", workflowRunsDto);
      log.info("发送工作流开始事件，workflowRunId: {}, workflowData: {}", workflowRunId,
          JacksonUtil.toJsonStr(workflowRunsDto));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送工作流开始事件失败，workflowRunId: {}, errorMessage: {}", workflowRunId,
          e.getMessage(), e);
    }
  }

  /**
   * 发送节点开始执行事件
   *
   * @param workflowRunId 工作流运行ID
   * @param nodeCanvas    节点配置
   */
  public void sendNodeStartedEvent(String workflowRunId, JSONObject nodeCanvas) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "node_started");
      eventData.put("workflow_run_id", workflowRunId);
      NodeCanvas canvas = NodeTypeUtil.getNodeCanvas(nodeCanvas);

      assert canvas != null;
      eventData.put("data",
          JSONUtil.parseObj(canvas.getData()).set("node_id", nodeCanvas.getStr("id")));

      log.info("发送节点开始事件，workflowRunId: {}, nodeId: {}, nodeType: {}", workflowRunId,
          nodeCanvas.getStr("id"), JSONUtil.getByPath(nodeCanvas, "data.type"));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送节点开始事件失败，workflowRunId: {}, nodeId: {}, errorMessage: {}",
          workflowRunId, nodeCanvas.getStr("id"), e.getMessage(), e);
    }
  }

  /**
   * 发送节点执行完成事件
   *
   * @param workflowRunId 工作流运行ID
   */
  public void sendNodeCompletedEvent(String workflowRunId,
      WorkflowNodeExecutionsPo nodeExecutionsPo) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "node_completed");
      eventData.put("workflow_run_id", workflowRunId);

      WorkflowNodeExecutionsDto workflowNodeExecutionsDto = workflowConverter.nodeExecutionPoToDto(
          nodeExecutionsPo);
      eventData.put("data", workflowNodeExecutionsDto);

      log.info("发送节点完成事件，workflowRunId: {}, nodeId: {}, status: {}", workflowRunId,
          nodeExecutionsPo.getNode_id(), nodeExecutionsPo.getStatus());
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送节点完成事件失败，workflowRunId: {}, nodeExecutionId: {}, errorMessage: {}",
          workflowRunId, nodeExecutionsPo.getId(), e.getMessage(), e);
    }
  }

  /**
   * 发送节点执行失败 事件
   *
   * @param workflowRunId 工作流运行ID
   */
  public void sendNodeFailedEvent(String workflowRunId, WorkflowNodeExecutionsPo nodeExecutionsPo) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "node_failed");
      eventData.put("workflow_run_id", workflowRunId);
      WorkflowNodeExecutionsDto workflowNodeExecutionsDto = workflowConverter.nodeExecutionPoToDto(
          nodeExecutionsPo);
      eventData.put("data", workflowNodeExecutionsDto);

      log.info("发送节点失败事件，workflowRunId: {}, nodeId: {}, error: {}", workflowRunId,
          workflowNodeExecutionsDto.getNode_id(), workflowNodeExecutionsDto.getError());
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送节点失败事件失败，workflowRunId: {}, nodeExecutionId: {}, errorMessage: {}",
          workflowRunId, nodeExecutionsPo.getId(), e.getMessage(), e);
    }
  }


  /**
   * 发送工作流成功完成事件
   *
   * @param workflowRunId      工作流运行ID
   * @param graphRunnerContext 运行上下文
   */
  public void sendWorkflowSucceededEvent(String workflowRunId,
      GraphRunnerContext graphRunnerContext) {
    if (graphRunnerContext.isInterrupted()) {
      return;
    }
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "workflow_succeeded");
      eventData.put("workflow_run_id", workflowRunId);

      // 根据时间整理成LinkedHashMap
      LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();
      // 按 insertTime 排序
      graphRunnerContext.getOutputMapWithTime().entrySet().stream()
          .sorted(Comparator.comparing(e -> e.getValue().insertTime()))
          .forEach(e -> outputMap.put(e.getKey(), e.getValue().value()));

      WorkflowRunsPo runsPo = workflowRunsMapper.selectOne(
          new LambdaQueryWrapper<WorkflowRunsPo>().eq(WorkflowRunsPo::getWorkflow_run_id,
              workflowRunId));
      // 设置字段值
      runsPo.setFinished_at(LocalDateTime.now());
      long millis = ChronoUnit.MILLIS.between(runsPo.getCreated_at(), runsPo.getFinished_at());
      double seconds = millis / 1000.00;
      runsPo.setElapsed_time(seconds);
      runsPo.setStatus(WorkflowExecutionStatus.SUCCEEDED.getValue());
      runsPo.setOutputs(JacksonUtil.toJsonStr(outputMap));
      runsPo.setTotal_tokens(graphRunnerContext.getTotal_tokens().get());
      runsPo.setTotal_steps(graphRunnerContext.getExecuteSteps().get());

      workflowRunsMapper.updateById(runsPo);
      WorkflowRunsDto workflowRunsDto = workflowConverter.workflowRunPoToDto(runsPo);
      JsonNode obj = JacksonUtil.valueToTree(workflowRunsDto);
      eventData.put("data", obj);

      log.info(
          "发送工作流成功事件，workflowRunId: {}, outputs: {}, graphRunnerContext.getExecuteSteps(): {}, totalTokens: {}",
          workflowRunId, JacksonUtil.toJsonStr(graphRunnerContext.getOutputMapWithTime()),
          graphRunnerContext.getExecuteSteps(), graphRunnerContext.getTotal_tokens());
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("close").data("Workflow execution completed"));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送工作流成功事件失败，workflowRunId: {}, errorMessage: {}", workflowRunId,
          e.getMessage(), e);
    }
  }

  /**
   * 发送工作流失败事件
   *
   * @param workflowRunId      工作流运行ID
   * @param errorMessage       错误信息
   * @param graphRunnerContext 运行上下文
   */
  public void sendWorkflowFailedEvent(String workflowRunId, String errorMessage,
      GraphRunnerContext graphRunnerContext) {
    try {
      Map<String, Object> eventData = new LinkedHashMap<>();
      eventData.put("event", "workflow_failed");
      eventData.put("workflow_run_id", workflowRunId);
      WorkflowRunsPo runsPo = workflowRunsMapper.selectOne(Wrappers.<WorkflowRunsPo>lambdaQuery()
          .eq(WorkflowRunsPo::getWorkflow_run_id, workflowRunId));
      // 设置字段值
      runsPo.setFinished_at(LocalDateTime.now());
      long millis = ChronoUnit.MILLIS.between(runsPo.getCreated_at(), runsPo.getFinished_at());
      double seconds = millis / 1000.00;
      runsPo.setElapsed_time(seconds);
      runsPo.setStatus(WorkflowExecutionStatus.FAILED.getValue());
      runsPo.setOutputs(null);
      runsPo.setError(errorMessage);
      runsPo.setTotal_tokens(graphRunnerContext.getTotal_tokens().get());
      runsPo.setTotal_steps(graphRunnerContext.getExecuteSteps().get());

      workflowRunsMapper.updateById(runsPo);
      WorkflowRunsDto workflowRunsDto = workflowConverter.workflowRunPoToDto(runsPo);

      log.info(
          "发送工作流失败事件，workflowRunId: {}, error: {}, graphRunnerContext.getExecuteSteps(): {}, totalTokens: {}",
          workflowRunId, errorMessage, graphRunnerContext.getExecuteSteps(),
          graphRunnerContext.getTotal_tokens());
      eventData.put("data", workflowRunsDto);
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("message").data(eventData));
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("close").data("Workflow execution failed"));
    } catch (Exception e) {
      sseEmitterManager.sendEvent(workflowRunId,
          SseEmitter.event().name("error").data("工作流运行失败"));
      log.error("发送工作流失败事件失败，workflowRunId: {}, errorMessage: {}", workflowRunId,
          e.getMessage(), e);
    }
  }


  /**
   * 批量添加最终结果
   *
   * @param map                键值对
   * @param graphRunnerContext 图执行器上下文，包含输出映射和时间信息
   */
  public void finalResultPut(Map<String, Object> map, GraphRunnerContext graphRunnerContext) {
    if (CollUtil.isEmpty(map)) {
      return;
    }
    map.forEach(
        (key, value) -> graphRunnerContext.getOutputMapWithTime().compute(key, (k, existing) -> {
          if (existing == null) {
            // 首次插入：记录当前时间
            return TimeValue.first(value);
          } else {
            // 已存在：只更新 value，保留原 insertTime
            return existing.withUpdatedValue(value);
          }
        }));
  }


}