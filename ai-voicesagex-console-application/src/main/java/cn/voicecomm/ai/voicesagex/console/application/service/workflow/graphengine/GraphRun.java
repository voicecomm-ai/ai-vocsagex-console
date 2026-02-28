package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.answer.AnswerStreamGenerateRoute;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndStreamGeneratorRouter;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndStreamParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流图结构实体类
 * <p>
 * 该类用于表示整个工作流的图结构，包括节点、边、并行结构等核心组件。 它是工作流引擎的核心数据结构，负责存储和管理整个工作流的拓扑结构信息。
 * <p>
 * 主要功能包括：
 * <ol>
 *   <li>存储工作流图的完整结构信息（节点、边、并行结构等）</li>
 *   <li>提供图的初始化和构建功能</li>
 *   <li>管理图中节点间的连接关系</li>
 *   <li>管理并行执行结构</li>
 *   <li>管理答案流生成路由和结束流参数</li>
 * </ol>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphRun {

  /**
   * 图的根节点ID
   * <p>
   * 表示整个工作流图的起始节点，是工作流执行的入口点。 所有工作流的执行都从该节点开始，通过图中的边逐步执行后续节点。
   * <p>
   * 该字段是工作流图执行流程控制的核心，决定了工作流的起始位置。
   */
  private String root_node_id;

  /**
   * 图中所有节点ID列表
   * <p>
   * 包含当前工作流图中的所有节点ID，用于快速判断节点是否存在于图中。 该列表在图初始化时构建，包含了从根节点可达的所有节点。
   * <p>
   * 使用ArrayList作为实现类，支持按索引访问和顺序遍历。 默认初始化为空列表，避免空指针异常。
   */
  private List<String> node_ids = new ArrayList<>();

  /**
   * 节点配置映射 (节点ID -> 节点配置)
   * <p>
   * 存储每个节点的详细配置信息，key为节点ID，value为对应的NodeCanvas对象。 NodeCanvas包含了节点的类型、数据和其他配置信息。
   * <p>
   * 使用ConcurrentHashMap实现线程安全，支持在多线程环境下并发访问。 该映射用于快速查找特定节点的配置信息。
   */
  private Map<String, JSONObject> node_id_config_mapping = new ConcurrentHashMap<>();

  /**
   * (出边)图边映射 (源节点ID -> 边列表)
   * <p>
   * 表示图中节点之间的正向连接关系，key为源节点ID，value为从该节点出发的所有边(GraphEdge)列表。 用于正向遍历图结构，确定从某个节点可以到达哪些后续节点。
   * <p>
   * 使用ConcurrentHashMap实现线程安全，支持在多线程环境下并发访问。 每个节点可能有多条出边，表示不同的执行路径。
   */
  private Map<String, List<GraphEdge>> outgoing_edge_mapping = new ConcurrentHashMap<>();

  /**
   * (入边)图边映射 (目标节点ID -> 边列表)
   * <p>
   * 表示图中节点之间的入边连接关系，key为目标节点ID，value为指向该节点的所有边(GraphEdge)列表。 用于反向遍历图结构，确定哪些节点可以到达当前节点。
   * <p>
   * 使用ConcurrentHashMap实现线程安全，支持在多线程环境下并发访问。 主要用于依赖分析、前驱节点查找等场景。
   */
  private Map<String, List<GraphEdge>> incoming_edge_mapping = new ConcurrentHashMap<>();

  /**
   * 图并行结构映射 (并行ID -> 并行结构)
   * <p>
   * 存储图中的所有并行结构信息，key为并行结构的唯一ID，value为对应的GraphParallel对象。 用于管理和执行图中的并行任务，支持复杂的并行执行逻辑。
   * <p>
   * 使用ConcurrentHashMap实现线程安全，支持在多线程环境下并发访问。 每个并行结构包含起始节点、结束节点和父子关系等信息。
   */
  private Map<String, GraphParallel> parallel_mapping = new ConcurrentHashMap<>();

  /**
   * 节点并行映射 (节点ID -> 并行ID)
   * <p>
   * 表示每个节点所属的并行结构，key为节点ID，value为该节点所属的并行结构ID。 用于确定节点的并行执行环境，支持并行结构的管理和控制。
   * <p>
   * 使用ConcurrentHashMap实现线程安全，支持在多线程环境下并发访问。 通过该映射可以快速查找节点所属的并行结构。
   */
  private Map<String, String> node_parallel_mapping = new ConcurrentHashMap<>();

  /**
   * 答案流生成路由
   * <p>
   * 用于管理答案节点的生成顺序和依赖关系，确保答案节点按照正确的顺序生成。 包含答案依赖关系和生成路由信息，支持复杂的工作流答案处理逻辑。
   * <p>
   * 该字段在图初始化时构建，基于节点配置和边关系分析得出。
   */
  private AnswerStreamGenerateRoute answer_stream_generate_routes;

  /**
   * 结束流参数
   * <p>
   * 包含工作流结束时的相关参数配置，用于控制工作流的结束行为。 可能包括结束条件、回调处理等配置信息。
   * <p>
   * 该字段在图初始化时构建，基于节点配置分析得出。
   */
  private EndStreamParam end_stream_param;

  /**
   * 初始化图结构
   * <p>
   * 根据给定的图配置和根节点ID构建完整的图结构，是GraphRun类的核心方法。 该方法负责解析图配置，构建节点、边、并行结构等核心组件。
   * <p>
   * 主要处理步骤：
   * <ol>
   *   <li>验证图配置的有效性</li>
   *   <li>解析边配置，构建正向和反向边映射</li>
   *   <li>确定根节点</li>
   *   <li>检查图结构的有效性（如循环引用检测）</li>
   *   <li>构建节点列表和配置映射</li>
   *   <li>识别并构建并行结构</li>
   *   <li>初始化答案流生成路由和结束流参数</li>
   * </ol>
   *
   * @param graph      图配置对象，包含节点和边的配置信息
   * @param rootNodeId 根节点ID，指定工作流的起始节点；如果为null或空，则自动识别
   * @return 初始化完成的GraphRun实例
   * @throws IllegalArgumentException 当图配置无效或根节点不存在时抛出异常
   */
  public static GraphRun init(JSONObject graph, String rootNodeId) {
    // 获取图中的边配置列表
    List<JSONObject> edges = JSONUtil.getByPath(graph, "edges", new ArrayList<>());
    // 获取图中的节点配置列表
    List<JSONObject> nodeConfigs = JSONUtil.getByPath(graph, "nodes", new ArrayList<>());

    // 验证节点配置是否为空
    if (nodeConfigs == null || nodeConfigs.isEmpty()) {
      throw new IllegalArgumentException("Graph must have at least one node");
    }

    // 重新组织边映射，构建正向和反向边映射结构
    Map<String, List<GraphEdge>> edgeMapping = new ConcurrentHashMap<>();
    Map<String, List<GraphEdge>> reverseEdgeMapping = new ConcurrentHashMap<>();
    // 用于记录所有作为边目标的节点ID集合
    Set<String> targetEdgeIds = new HashSet<>();

    // 获取错误策略为"fail-branch"的节点ID列表，用于处理分支条件
    List<String> failBranchSourceNodeIds = nodeConfigs.stream().filter(
            node -> "fail-branch".equals(JSONUtil.getByPath(node, "data.error_strategy")))
        .map(node -> node.getStr("id")).toList();

    // 遍历所有边配置，构建图的边结构
    for (JSONObject edgeConfig : edges) {
      // 获取边的源节点ID
      String sourceNodeId = edgeConfig.getStr("source");
      // 跳过源节点ID为空的边
      if (sourceNodeId == null || sourceNodeId.isEmpty()) {
        continue;
      }

      // 确保源节点在边映射中存在对应的列表
      edgeMapping.putIfAbsent(sourceNodeId, new ArrayList<>());

      // 获取边的目标节点ID
      String targetNodeId = edgeConfig.getStr("target");
      // 跳过目标节点ID为空的边
      if (targetNodeId == null || targetNodeId.isEmpty()) {
        continue;
      }

      // 确保目标节点在反向边映射中存在对应的列表
      reverseEdgeMapping.putIfAbsent(targetNodeId, new ArrayList<>());
      // 将目标节点ID添加到目标节点集合中
      targetEdgeIds.add(targetNodeId);

      // 解析运行条件，处理分支逻辑
      RunCondition runCondition = null;
      // 获取边的源句柄，用于判断分支条件
      String sourceHandle = edgeConfig.getStr("sourceHandle");
      if (sourceHandle != null) {
        // 处理失败分支的情况
        if (failBranchSourceNodeIds.contains(sourceNodeId) && !"fail-branch".equals(sourceHandle)) {
          runCondition = RunCondition.builder().type("branch_identify")
              .branch_identify("success-branch").build();
        } else if (!"source".equals(sourceHandle)) {
          // 处理其他分支情况
          runCondition = RunCondition.builder().type("branch_identify")
              .branch_identify(sourceHandle).build();
        }
      }

      // 创建图边对象并添加到映射中
      GraphEdge graphEdge = GraphEdge.builder().source_node_id(sourceNodeId)
          .target_node_id(targetNodeId).run_condition(runCondition).build();

      // 将边添加到正向边映射中
      edgeMapping.get(sourceNodeId).add(graphEdge);
      // 将边添加到反向边映射中
      reverseEdgeMapping.get(targetNodeId).add(graphEdge);
    }

    // 获取没有前驱节点的节点（即根节点候选）
    List<JSONObject> rootNodeConfigs = new ArrayList<>();
    // 存储所有节点ID到节点配置的映射
    Map<String, JSONObject> allNodeIdConfigMapping = new ConcurrentHashMap<>();

    // 遍历所有节点配置，构建节点映射和根节点列表
    for (JSONObject nodeConfig : nodeConfigs) {
      // 获取节点ID
      String nodeId = nodeConfig.getStr("id");
      // 跳过ID为空的节点
      if (nodeId == null || nodeId.isEmpty()) {
        continue;
      }

      // 如果节点不是任何边的目标节点，则认为是根节点候选
      if (!targetEdgeIds.contains(nodeId)) {
        rootNodeConfigs.add(nodeConfig);
      }

      // 将节点ID和配置添加到映射中
      allNodeIdConfigMapping.put(nodeId, nodeConfig);
    }

    // 提取根节点候选的ID列表
    List<String> rootNodeIds = rootNodeConfigs.stream().map(node -> node.getStr("id")).toList();

    // 获取根节点
    if (rootNodeId == null || rootNodeId.isEmpty()) {
      // 如果没有指定根节点ID，则使用START类型的节点作为根节点
      rootNodeId = rootNodeConfigs.stream()
          .filter(nodeConfig -> NodeType.START.getValue()
              .equals(JSONUtil.getByPath(nodeConfig, "data.type")))
          .map(node -> node.getStr("id")).findFirst().orElse(null);
    }

    // 验证根节点是否存在
    if (rootNodeId == null || rootNodeId.isEmpty() || !rootNodeIds.contains(rootNodeId)) {
      throw new IllegalArgumentException("Root node id " + rootNodeId + " not found in the graph");
    }

    // 检查是否存在循环引用（节点连接到前一个节点）
    checkConnectedToPreviousNode(Collections.singletonList(rootNodeId), edgeMapping);

    // 从根节点开始递归获取所有可达的节点ID
    List<String> nodeIds = new ArrayList<>();
    nodeIds.add(rootNodeId);
    // 递归添加所有从根节点可达的节点ID
    recursivelyAddNodeIds(nodeIds, edgeMapping, rootNodeId);

    // 构建当前图中使用的节点配置映射
    Map<String, JSONObject> nodeIdConfigMapping = new ConcurrentHashMap<>();
    for (String nodeId : nodeIds) {
      // 从所有节点配置映射中获取当前节点的配置
      nodeIdConfigMapping.put(nodeId, allNodeIdConfigMapping.get(nodeId));
    }

    // 过滤reverseEdgeMapping，只保留从根节点可达的节点相关的反向边
    Map<String, List<GraphEdge>> filteredReverseEdgeMapping = new ConcurrentHashMap<>();
    for (String nodeId : nodeIds) {
      // 检查原reverseEdgeMapping中是否有该节点的入边
      if (reverseEdgeMapping.containsKey(nodeId)) {
        // 过滤出边的源节点也在可达节点列表中的边
        List<GraphEdge> filteredEdges = reverseEdgeMapping.get(nodeId).stream()
            .filter(edge -> nodeIds.contains(edge.getSource_node_id()))
            .toList();
        // 只保留源节点也在可达节点列表中的入边
        if (!filteredEdges.isEmpty()) {
          filteredReverseEdgeMapping.put(nodeId, new ArrayList<>(filteredEdges));
        }
      }
    }

    // 初始化并行映射结构
    Map<String, GraphParallel> parallelMapping = new ConcurrentHashMap<>();
    Map<String, String> nodeParallelMapping = new ConcurrentHashMap<>();
    // 递归添加并行结构
    recursivelyAddParallels(edgeMapping, filteredReverseEdgeMapping, rootNodeId, parallelMapping,
        nodeParallelMapping, null);

    // 检查是否超过N层并行嵌套限制
    for (GraphParallel parallel : parallelMapping.values()) {
      // 如果并行结构有父并行结构，则检查嵌套层级
      if (parallel.getParent_parallel_id() != null) {
        checkExceedParallelLimit(parallelMapping, 5, // WORKFLOW_PARALLEL_DEPTH_LIMIT 默认值
            parallel.getParent_parallel_id());
      }
    }

    // todo 初始化答案流生成路由
    AnswerStreamGenerateRoute answerStreamGenerateRoutes = AnswerStreamGenerateRoute.init(
        nodeIdConfigMapping, filteredReverseEdgeMapping, nodeParallelMapping);

    // 初始化结束流参数
    EndStreamParam endStreamParam = EndStreamGeneratorRouter.init(nodeIdConfigMapping,
        filteredReverseEdgeMapping, nodeParallelMapping);

    // 构建并返回初始化完成的图实例
    return GraphRun.builder().root_node_id(rootNodeId).node_ids(nodeIds)
        .node_id_config_mapping(nodeIdConfigMapping).outgoing_edge_mapping(edgeMapping)
        .incoming_edge_mapping(filteredReverseEdgeMapping).parallel_mapping(parallelMapping)
        .node_parallel_mapping(nodeParallelMapping)
        .answer_stream_generate_routes(answerStreamGenerateRoutes).end_stream_param(
            endStreamParam)
        .build();
  }

  /**
   * 添加额外的边到图中
   * <p>
   * 在图初始化后动态添加边，用于运行时修改图结构。 该方法主要用于在工作流执行过程中动态添加新的执行路径。
   * <p>
   * 主要处理逻辑：
   * <ol>
   *   <li>验证源节点和目标节点是否存在于图中</li>
   *   <li>检查是否已存在相同的边</li>
   *   <li>创建新的边并添加到边映射中</li>
   * </ol>
   *
   * @param sourceNodeId 源节点ID，边的起始节点
   * @param targetNodeId 目标节点ID，边的结束节点
   * @param runCondition 运行条件，控制边是否执行的条件
   */
  public void addExtraEdge(String sourceNodeId, String targetNodeId, RunCondition runCondition) {
    // 检查源节点和目标节点是否在图中存在
    if (!node_ids.contains(sourceNodeId) || !node_ids.contains(targetNodeId)) {
      // 如果节点不存在，则直接返回，不添加边
      return;
    }

    // 确保源节点在边映射中存在对应的列表
    outgoing_edge_mapping.putIfAbsent(sourceNodeId, new ArrayList<>());

    // 检查是否已存在相同的边（相同源节点和目标节点）
    boolean targetExists = outgoing_edge_mapping.get(sourceNodeId).stream()
        .anyMatch(edge -> targetNodeId.equals(edge.getTarget_node_id()));

    // 如果已存在相同的边，则直接返回，避免重复添加
    if (targetExists) {
      return;
    }

    // 创建图边对象
    GraphEdge graphEdge = GraphEdge.builder().source_node_id(sourceNodeId)
        .target_node_id(targetNodeId).run_condition(runCondition).build();

    // 将边添加到源节点的边列表中
    outgoing_edge_mapping.get(sourceNodeId).add(graphEdge);
  }

  /**
   * 获取图的叶节点ID列表
   * <p>
   * 叶节点是没有出边或只连接到根节点的节点。 这些节点通常是工作流的结束点或特殊处理节点。
   * <p>
   * 判断叶节点的条件：
   * <ol>
   *   <li>节点没有出边（edges == null || edges.isEmpty()）</li>
   *   <li>节点只有一条出边，且该边指向根节点</li>
   * </ol>
   *
   * @return 叶节点ID列表，包含所有叶节点的ID
   */
  public List<String> getLeafNodeIds() {
    List<String> leafNodeIds = new ArrayList<>();
    // 遍历图中的所有节点
    for (String nodeId : node_ids) {
      // 获取节点的出边列表
      List<GraphEdge> edges = outgoing_edge_mapping.get(nodeId);
      // 判断是否为叶节点
      if (edges == null || edges.isEmpty() || (edges.size() == 1 && edges.getFirst()
          .getTarget_node_id().equals(root_node_id))) {
        // 如果是叶节点，则添加到结果列表中
        leafNodeIds.add(nodeId);
      }
    }
    return leafNodeIds;
  }

  /**
   * 递归添加节点ID
   * <p>
   * 从指定节点开始，递归遍历所有可达的节点并添加到节点ID列表中。 该方法使用深度优先搜索算法遍历图结构。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取当前节点的所有出边</li>
   *   <li>遍历每条出边</li>
   *   <li>如果目标节点尚未添加到列表中，则添加并递归处理</li>
   *   <li>避免重复添加节点</li>
   * </ol>
   *
   * @param nodeIds     节点ID列表，用于存储遍历过程中发现的节点ID
   * @param edgeMapping 边映射，包含节点间的连接关系
   * @param nodeId      当前处理的节点ID
   */
  private static void recursivelyAddNodeIds(List<String> nodeIds,
      Map<String, List<GraphEdge>> edgeMapping, String nodeId) {
    // 获取当前节点的所有出边
    List<GraphEdge> edges = edgeMapping.get(nodeId);
    if (edges != null) {
      // 遍历当前节点的所有出边
      for (GraphEdge edge : edges) {
        // 如果目标节点尚未添加到列表中
        if (!nodeIds.contains(edge.getTarget_node_id())) {
          // 将目标节点添加到列表中
          nodeIds.add(edge.getTarget_node_id());
          // 递归处理目标节点
          recursivelyAddNodeIds(nodeIds, edgeMapping, edge.getTarget_node_id());
        }
      }
    }
  }

  /**
   * 检查是否连接到前一个节点（检测循环引用）
   * <p>
   * 确保图中不存在循环引用的情况，防止工作流执行时出现无限循环。 该方法使用深度优先搜索算法检测图中的循环结构。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取当前路径中的最后一个节点</li>
   *   <li>获取该节点的所有出边</li>
   *   <li>遍历每条出边</li>
   *   <li>检查目标节点是否已在当前路径中（循环引用检测）</li>
   *   <li>如果发现循环引用，则抛出异常</li>
   *   <li>否则继续递归检查</li>
   * </ol>
   *
   * @param route       当前路径，包含从根节点到当前节点的所有节点ID
   * @param edgeMapping 边映射，包含节点间的连接关系
   * @throws IllegalArgumentException 当检测到循环引用时抛出异常
   */
  private static void checkConnectedToPreviousNode(List<String> route,
      Map<String, List<GraphEdge>> edgeMapping) {
    // 获取当前路径中的最后一个节点ID
    String lastNodeId = route.getLast();
    // 获取该节点的所有出边
    List<GraphEdge> edges = edgeMapping.get(lastNodeId);

    if (edges != null) {
      // 遍历当前节点的所有出边
      for (GraphEdge edge : edges) {
        // 跳过目标节点ID为空的边
        if (edge.getTarget_node_id() == null || edge.getTarget_node_id().isEmpty()) {
          continue;
        }

        // 检查目标节点是否已在当前路径中（循环引用检测）
        if (route.contains(edge.getTarget_node_id())) {
          // 如果发现循环引用，则抛出异常
          throw new IllegalArgumentException("Node " + edge.getSource_node_id()
              + " is connected to the previous node, please check the graph.");
        }

        // 构建新的路径，包含当前边的目标节点
        List<String> newRoute = new ArrayList<>(route);
        newRoute.add(edge.getTarget_node_id());
        // 递归检查新的路径
        checkConnectedToPreviousNode(newRoute, edgeMapping);
      }
    }
  }

  /**
   * 递归添加并行结构
   * <p>
   * 从指定起始节点开始，递归识别和构建图中的并行执行结构。 该方法是并行结构识别的核心算法，负责分析图中的并行执行路径。
   * <p>
   * 主要处理逻辑：
   * <ol>
   *   <li>获取当前节点的所有出边</li>
   *   <li>如果出边数量大于1，则可能存在并行结构</li>
   *   <li>根据运行条件对边进行分类</li>
   *   <li>为每个条件分支创建并行结构</li>
   *   <li>递归处理后续节点</li>
   * </ol>
   *
   * @param edgeMapping         正向边映射，包含节点间的正向连接关系
   * @param reverseEdgeMapping  反向边映射，包含节点间的反向连接关系
   * @param startNodeId         起始节点ID，当前处理的节点
   * @param parallelMapping     并行结构映射，存储已识别的并行结构
   * @param nodeParallelMapping 节点并行映射，记录节点所属的并行结构
   * @param parentParallel      父并行结构，当前并行结构的父结构
   */
  private static void recursivelyAddParallels(Map<String, List<GraphEdge>> edgeMapping,
      Map<String, List<GraphEdge>> reverseEdgeMapping, String startNodeId,
      Map<String, GraphParallel> parallelMapping, Map<String, String> nodeParallelMapping,
      GraphParallel parentParallel) {

    // 获取当前节点的所有出边，如果没有则使用空列表
    List<GraphEdge> targetNodeEdges = edgeMapping.getOrDefault(startNodeId, new ArrayList<>());
    // 当前并行结构引用
    GraphParallel parallel = null;

    // 如果当前节点有多个出边，则可能存在并行结构
    if (targetNodeEdges.size() > 1) {
      // 获取当前并行中的所有节点ID，按条件分类存储
      Map<String, List<String>> parallelBranchNodeIds = new HashMap<>();
      // 按条件对边进行分类的映射
      Map<String, List<GraphEdge>> conditionEdgeMappings = new HashMap<>();

      // 根据运行条件对边进行分类
      for (GraphEdge edge : targetNodeEdges) {
        // 如果边没有运行条件，则归为"default"类别
        if (edge.getRun_condition() == null) {
          parallelBranchNodeIds.computeIfAbsent("default", k -> new ArrayList<>())
              .add(edge.getTarget_node_id());
        } else {
          // 如果边有运行条件，则根据条件哈希值分类
          String conditionHash = edge.getRun_condition().getHash();
          conditionEdgeMappings.computeIfAbsent(conditionHash, k -> new ArrayList<>()).add(edge);
        }
      }

      // 处理有条件边的分类，将相同条件的多条边归为一类
      for (Map.Entry<String, List<GraphEdge>> entry : conditionEdgeMappings.entrySet()) {
        // 如果某个条件有多条边，则将这些边的目标节点归为一类
        if (entry.getValue().size() > 1) {
          for (GraphEdge edge : entry.getValue()) {
            parallelBranchNodeIds.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                .add(edge.getTarget_node_id());
          }
        }
      }

      // 为每个条件分支创建并行结构
      Map<String, GraphParallel> conditionParallels = new HashMap<>();

      // 遍历所有条件分支
      for (Map.Entry<String, List<String>> entry : parallelBranchNodeIds.entrySet()) {
        // 获取条件哈希值
        String conditionHash = entry.getKey();
        // 获取该条件下的并行分支节点ID列表
        List<String> conditionParallelBranchNodeIds = entry.getValue();

        // 当前并行结构引用
        GraphParallel currentParallel = null;
        // 如果存在并行分支节点，则创建并行结构
        if (!conditionParallelBranchNodeIds.isEmpty()) {
          // 获取父并行结构ID
          String parentParallelId = parentParallel != null ? parentParallel.getId() : null;
          // 获取父并行结构的起始节点ID
          String parentParallelStartNodeId =
              parentParallel != null ? parentParallel.getStart_from_node_id() : null;

          // 构建新的并行结构
          currentParallel = GraphParallel.builder().start_from_node_id(startNodeId)
              .parent_parallel_id(parentParallelId)
              .parent_parallel_start_node_id(parentParallelStartNodeId).build();

          // 将新创建的并行结构添加到并行映射中
          parallelMapping.put(currentParallel.getId(), currentParallel);
          // 将条件哈希值与并行结构关联
          conditionParallels.put(conditionHash, currentParallel);

          // 获取并行结构中的所有节点ID
          Map<String, List<String>> inBranchNodeIds = fetchAllNodeIdsInParallels(edgeMapping,
              reverseEdgeMapping, conditionParallelBranchNodeIds);

          // 收集所有分支节点ID并建立节点与并行结构的映射关系
          List<String> parallelNodeIds = new ArrayList<>();
          // 遍历所有分支中的节点
          for (List<String> nodeIds : inBranchNodeIds.values()) {
            for (String nodeId : nodeIds) {
              // 检查节点是否在父并行结构中
              boolean inParentParallel = true;
              if (parentParallelId != null) {
                inParentParallel = false;
                // 获取节点所属的并行结构ID
                String parallelId = nodeParallelMapping.get(nodeId);
                // 如果节点所属的并行结构与父并行结构相同，则认为在父并行结构中
                if (parallelId != null && parallelId.equals(parentParallelId)) {
                  inParentParallel = true;
                }
              }

              // 如果节点在当前并行结构中，则建立映射关系
              if (inParentParallel) {
                parallelNodeIds.add(nodeId);
                // 建立节点与并行结构的映射关系
                nodeParallelMapping.put(nodeId, currentParallel.getId());
              }
            }
          }

          // 查找并行结构外部的目标节点
          Set<String> outsideParallelTargetNodeIds = new HashSet<>();
          // 遍历并行结构中的所有节点
          for (String nodeId : parallelNodeIds) {
            // 跳过起始节点
            if (nodeId.equals(currentParallel.getStart_from_node_id())) {
              continue;
            }

            // 获取节点的出边列表
            List<GraphEdge> nodeEdges = edgeMapping.get(nodeId);
            // 跳过没有出边的节点
            if (nodeEdges == null || nodeEdges.isEmpty()) {
              continue;
            }

            // 跳过有多个出边的节点
            if (nodeEdges.size() > 1) {
              continue;
            }

            // 获取节点的唯一出边的目标节点ID
            String targetNodeId = nodeEdges.getFirst().getTarget_node_id();
            // 如果目标节点在并行结构内部，则跳过
            if (parallelNodeIds.contains(targetNodeId)) {
              continue;
            }

            // 获取父并行结构引用
            GraphParallel parentParallelRef = null;
            if (parentParallelId != null) {
              parentParallelRef = parallelMapping.get(parentParallelId);
              // 如果父并行结构不存在，则跳过
              if (parentParallelRef == null) {
                continue;
              }
            }

            // 检查目标节点是否满足并行结构结束条件
            String targetNodeParallelId = nodeParallelMapping.get(targetNodeId);
            // 条件1：目标节点属于父并行结构
            boolean condition1 = (targetNodeParallelId != null && targetNodeParallelId.equals(
                parentParallelId));
            // 条件2：父并行结构有结束节点且目标节点就是结束节点
            boolean condition2 = (parentParallelRef != null
                && parentParallelRef.getEnd_to_node_id() != null && targetNodeId.equals(
                parentParallelRef.getEnd_to_node_id()));
            // 条件3：目标节点不属于任何并行结构且没有父并行结构
            boolean condition3 = (targetNodeParallelId == null && parentParallelRef == null);

            // 如果满足任一条件，则认为是并行结构外部的目标节点
            if (condition1 || condition2 || condition3) {
              outsideParallelTargetNodeIds.add(targetNodeId);
            }
          }

          // 如果只有一个外部目标节点，则设置为并行结构的结束节点
          if (outsideParallelTargetNodeIds.size() == 1) {
            // 检查特殊情况：父并行结构和当前并行结构的结束节点相同
            if (parentParallel != null && parentParallel.getEnd_to_node_id() != null
                && currentParallel.getEnd_to_node_id() != null
                && currentParallel.getEnd_to_node_id().equals(parentParallel.getEnd_to_node_id())) {
              // 在这种情况下，当前并行结构不设置结束节点
              currentParallel.setEnd_to_node_id(null);
            } else {
              // 设置并行结构的结束节点为唯一的外部目标节点
              currentParallel.setEnd_to_node_id(outsideParallelTargetNodeIds.iterator().next());
            }
          }
        }

        // 递归处理条件边
        if (!conditionEdgeMappings.isEmpty()) {
          // 遍历所有条件边映射
          for (Map.Entry<String, List<GraphEdge>> conditionEntry : conditionEdgeMappings.entrySet()) {
            // 获取条件哈希值
            String hash = conditionEntry.getKey();
            // 获取该条件下的边列表
            List<GraphEdge> edges = conditionEntry.getValue();

            // 遍历该条件下的所有边
            for (GraphEdge edge : edges) {
              // 获取当前并行结构引用
              GraphParallel currentParallelRef = getCurrentParallel(parallelMapping, edge,
                  conditionParallels.get(hash), parentParallel);

              // 递归处理边的目标节点
              recursivelyAddParallels(edgeMapping, reverseEdgeMapping, edge.getTarget_node_id(),
                  parallelMapping, nodeParallelMapping, currentParallelRef);
            }
          }
        } else {
          // 递归处理普通边（没有条件分类的情况）
          for (GraphEdge edge : targetNodeEdges) {
            // 获取当前并行结构引用
            GraphParallel currentParallelRef = getCurrentParallel(parallelMapping, edge, parallel,
                parentParallel);

            // 递归处理边的目标节点
            recursivelyAddParallels(edgeMapping, reverseEdgeMapping, edge.getTarget_node_id(),
                parallelMapping, nodeParallelMapping, currentParallelRef);
          }
        }
      }
    } else {
      // 如果当前节点只有一个出边，则继续递归处理
      for (GraphEdge edge : targetNodeEdges) {
        // 获取当前并行结构引用
        GraphParallel currentParallel = getCurrentParallel(parallelMapping, edge, parallel,
            parentParallel);

        // 递归处理边的目标节点
        recursivelyAddParallels(edgeMapping, reverseEdgeMapping, edge.getTarget_node_id(),
            parallelMapping, nodeParallelMapping, currentParallel);
      }
    }
  }

  /**
   * 获取当前并行结构
   * <p>
   * 根据边和父并行结构确定当前应该使用的并行结构。 该方法用于在递归处理图结构时确定节点所属的并行结构。
   * <p>
   * 判断逻辑：
   * <ol>
   *   <li>优先使用当前并行结构（如果存在）</li>
   *   <li>否则检查是否应该使用父并行结构</li>
   *   <li>如果边的目标节点不是父并行结构的结束节点，则使用父并行结构</li>
   *   <li>否则尝试获取父并行结构的父并行结构</li>
   * </ol>
   *
   * @param parallelMapping 并行映射，存储所有已识别的并行结构
   * @param edge            当前处理的图边
   * @param parallel        当前并行结构
   * @param parentParallel  父并行结构
   * @return 当前应该使用的并行结构
   */
  private static GraphParallel getCurrentParallel(Map<String, GraphParallel> parallelMapping,
      GraphEdge edge, GraphParallel parallel, GraphParallel parentParallel) {

    // 当前并行结构引用
    GraphParallel currentParallel = null;
    // 优先使用当前并行结构
    if (parallel != null) {
      currentParallel = parallel;
    } else if (parentParallel != null) {
      // 获取父并行结构的结束节点ID
      String endToNodeId = parentParallel.getEnd_to_node_id();
      // 如果父并行结构没有结束节点，或者边的目标节点不是结束节点
      if (!edge.getTarget_node_id().equals(endToNodeId)) {
        // 使用父并行结构
        currentParallel = parentParallel;
      } else {
        // 获取父并行的父并行结构ID
        String parentParallelParentParallelId = parentParallel.getParent_parallel_id();
        // 如果存在父并行的父并行结构
        if (parentParallelParentParallelId != null) {
          // 获取父并行的父并行结构
          GraphParallel parentParallelParentParallel = parallelMapping.get(
              parentParallelParentParallelId);
          // 如果父并行的父并行结构存在
          if (parentParallelParentParallel != null) {
            // 获取父并行的父并行结构的结束节点ID
            String endNodeId = parentParallelParentParallel.getEnd_to_node_id();
            // 如果父并行的父并行结构没有结束节点，或者边的目标节点不是结束节点
            if (!edge.getTarget_node_id().equals(endNodeId)) {
              // 使用父并行的父并行结构
              currentParallel = parentParallelParentParallel;
            }
          }
        }
      }
    }

    return currentParallel;
  }

  /**
   * 检查是否超过N层并行嵌套限制
   * <p>
   * 防止并行结构嵌套过深导致复杂性问题，通过递归方式检查嵌套层级。 该方法确保并行结构的嵌套层级不超过预设限制。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取父并行结构</li>
   *   <li>递增当前层级计数</li>
   *   <li>如果当前层级超过限制，则抛出异常</li>
   *   <li>如果父并行结构还有父并行结构，则递归检查</li>
   * </ol>
   *
   * @param parallelMapping  并行映射，存储所有已识别的并行结构
   * @param levelLimit       层级限制，最大允许的嵌套层级
   * @param parentParallelId 父并行ID，当前检查的并行结构的父结构ID
   * @param currentLevel     当前层级，递归调用时的层级计数
   * @throws IllegalArgumentException 当超过嵌套限制时抛出异常
   */
  private static void checkExceedParallelLimit(Map<String, GraphParallel> parallelMapping,
      int levelLimit, String parentParallelId, int currentLevel) {

    // 获取父并行结构
    GraphParallel parentParallel = parallelMapping.get(parentParallelId);
    // 如果父并行结构不存在，则直接返回
    if (parentParallel == null) {
      return;
    }

    // 递增当前层级计数
    currentLevel++;
    // 如果当前层级超过限制，则抛出异常
    if (currentLevel > levelLimit) {
      throw new IllegalArgumentException("Exceeds " + levelLimit + " layers of parallel");
    }

    // 如果父并行结构还有父并行结构，则递归检查
    if (parentParallel.getParent_parallel_id() != null) {
      checkExceedParallelLimit(parallelMapping, levelLimit, parentParallel.getParent_parallel_id(),
          currentLevel);
    }
  }

  /**
   * 检查是否超过N层并行嵌套限制（重载版本）
   * <p>
   * 提供默认的当前层级参数，简化方法调用。 该方法是上述方法的重载版本，用于初始调用。
   *
   * @param parallelMapping  并行映射，存储所有已识别的并行结构
   * @param levelLimit       层级限制，最大允许的嵌套层级
   * @param parentParallelId 父并行ID，当前检查的并行结构的父结构ID
   */
  private static void checkExceedParallelLimit(Map<String, GraphParallel> parallelMapping,
      int levelLimit, String parentParallelId) {
    // 调用带当前层级参数的版本，默认从第1层开始
    checkExceedParallelLimit(parallelMapping, levelLimit, parentParallelId, 1);
  }

  /**
   * 递归添加并行节点ID
   * <p>
   * 从指定起始节点开始，递归收集到合并节点之间的所有节点ID。 该方法用于构建并行结构中分支路径的节点列表。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取起始节点的所有出边</li>
   *   <li>遍历每条出边</li>
   *   <li>如果目标节点不是合并节点且尚未添加到列表中，则添加并递归处理</li>
   * </ol>
   *
   * @param branchNodeIds 分支节点ID列表，用于存储分支路径中的节点ID
   * @param edgeMapping   边映射，包含节点间的连接关系
   * @param mergeNodeId   合并节点ID，并行分支的汇合点
   * @param startNodeId   起始节点ID，当前处理的节点
   */
  private static void recursivelyAddParallelNodeIds(List<String> branchNodeIds,
      Map<String, List<GraphEdge>> edgeMapping, String mergeNodeId, String startNodeId) {

    // 获取起始节点的所有出边
    List<GraphEdge> edges = edgeMapping.get(startNodeId);
    if (edges != null) {
      // 遍历所有出边
      for (GraphEdge edge : edges) {
        // 获取边的目标节点ID
        String targetNodeId = edge.getTarget_node_id();
        // 如果目标节点不是合并节点且尚未添加到列表中
        if (!targetNodeId.equals(mergeNodeId) && !branchNodeIds.contains(targetNodeId)) {
          // 将目标节点添加到列表中
          branchNodeIds.add(targetNodeId);
          // 递归处理目标节点
          recursivelyAddParallelNodeIds(branchNodeIds, edgeMapping, mergeNodeId, targetNodeId);
        }
      }
    }
  }

  /**
   * 获取并行中的所有节点ID
   * <p>
   * 分析并行结构中的所有分支路径，收集每个分支中的节点ID。 该方法是并行结构分析的核心，用于识别并行分支中的所有节点。
   * <p>
   * 主要处理步骤：
   * <ol>
   *   <li>获取每个分支的所有节点ID</li>
   *   <li>分析每个分支中的节点，识别叶节点和合并节点</li>
   *   <li>处理重复的结束节点ID</li>
   *   <li>建立分支与合并节点的映射关系</li>
   *   <li>构建每个分支内部的节点ID列表</li>
   * </ol>
   *
   * @param edgeMapping           正向边映射，包含节点间的正向连接关系
   * @param reverseEdgeMapping    反向边映射，包含节点间的反向连接关系
   * @param parallelBranchNodeIds 并行分支节点ID列表，包含所有分支的起始节点ID
   * @return 分支节点ID映射，key为分支起始节点ID，value为该分支中的所有节点ID列表
   */
  private static Map<String, List<String>> fetchAllNodeIdsInParallels(
      Map<String, List<GraphEdge>> edgeMapping, Map<String, List<GraphEdge>> reverseEdgeMapping,
      List<String> parallelBranchNodeIds) {

    // 获取每个分支的所有节点ID
    Map<String, List<String>> routesNodeIds = new HashMap<>();
    // 遍历所有并行分支节点
    for (String parallelBranchNodeId : parallelBranchNodeIds) {
      // 为每个分支节点初始化节点ID列表
      routesNodeIds.put(parallelBranchNodeId, new ArrayList<>());
      // 将分支起始节点添加到列表中
      routesNodeIds.get(parallelBranchNodeId).add(parallelBranchNodeId);

      // 获取从分支起始节点可达的所有节点ID（递归获取路由）
      recursivelyFetchRoutes(edgeMapping, parallelBranchNodeId,
          routesNodeIds.get(parallelBranchNodeId));
    }

    // 从路由节点ID中获取叶节点ID
    Map<String, List<String>> leafNodeIds = new HashMap<>();
    // 存储合并节点与分支节点的映射关系
    Map<String, List<String>> mergeBranchNodeIds = new HashMap<>();

    // 分析每个分支中的节点，识别叶节点和合并节点
    for (Map.Entry<String, List<String>> entry : routesNodeIds.entrySet()) {
      // 获取分支起始节点ID
      String branchNodeId = entry.getKey();
      // 获取该分支中的所有节点ID
      List<String> nodeIds = entry.getValue();

      // 遍历分支中的所有节点
      for (String nodeId : nodeIds) {
        // 获取节点的出边列表
        List<GraphEdge> edges = edgeMapping.get(nodeId);
        // 如果节点没有出边，则认为是叶节点
        if (edges == null || edges.isEmpty()) {
          leafNodeIds.computeIfAbsent(branchNodeId, k -> new ArrayList<>()).add(nodeId);
        }

        // 检查节点是否为合并节点
        for (Map.Entry<String, List<String>> innerEntry : routesNodeIds.entrySet()) {
          // 获取另一个分支的起始节点ID
          String branchNodeId2 = innerEntry.getKey();
          // 获取另一个分支中的所有节点ID
          List<String> innerRoute2 = innerEntry.getValue();

          // 获取指向当前节点的所有边
          List<GraphEdge> reverseEdges = reverseEdgeMapping.getOrDefault(nodeId, new ArrayList<>());
          // 如果节点被多个分支共享且有多个入边，则可能是合并节点
          if (!branchNodeId.equals(branchNodeId2) && innerRoute2.contains(nodeId)
              && reverseEdges.size() > 1 && isNodeInRoutes(reverseEdgeMapping, nodeId,
              routesNodeIds)) {

            // 将合并节点与分支节点关联
            mergeBranchNodeIds.computeIfAbsent(nodeId, k -> new ArrayList<>()).add(branchNodeId2);
          }
        }
      }
    }

    // 按分支节点ID数量降序排序mergeBranchNodeIds
    List<Map.Entry<String, List<String>>> sortedEntries = new ArrayList<>(
        mergeBranchNodeIds.entrySet());
    // 按照分支节点数量进行降序排序
    sortedEntries.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));
    // 重新构建排序后的映射
    mergeBranchNodeIds = new LinkedHashMap<>();
    for (Map.Entry<String, List<String>> entry : sortedEntries) {
      mergeBranchNodeIds.put(entry.getKey(), entry.getValue());
    }

    // 处理重复的结束节点ID
    Map<String[], List<String>> duplicateEndNodeIds = new HashMap<>();
    // 遍历所有合并节点
    for (Map.Entry<String, List<String>> entry1 : mergeBranchNodeIds.entrySet()) {
      // 获取合并节点ID
      String nodeId = entry1.getKey();
      // 获取关联的分支节点列表
      List<String> branchNodeIds = entry1.getValue();

      // 与其它合并节点进行比较
      for (Map.Entry<String, List<String>> entry2 : mergeBranchNodeIds.entrySet()) {
        // 获取另一个合并节点ID
        String nodeId2 = entry2.getKey();
        // 获取另一个合并节点关联的分支节点列表
        List<String> branchNodeIds2 = entry2.getValue();

        // 检查两个节点是否具有相同的分支节点集合
        if (!nodeId.equals(nodeId2) && new HashSet<>(branchNodeIds).containsAll(branchNodeIds2)
            && new HashSet<>(branchNodeIds2).containsAll(branchNodeIds)) {

          // 构建键值对用于比较
          String[] key1 = {nodeId, nodeId2};
          String[] key2 = {nodeId2, nodeId};

          // 避免重复处理相同的节点对
          if (!containsKey(duplicateEndNodeIds, key1) && !containsKey(duplicateEndNodeIds, key2)) {
            duplicateEndNodeIds.put(key1, branchNodeIds);
          }
        }
      }
    }

    // 处理重复的结束节点，保留正确的顺序
    for (Map.Entry<String[], List<String>> entry : duplicateEndNodeIds.entrySet()) {
      // 获取节点对
      String[] nodeIds = entry.getKey();
      String nodeId = nodeIds[0];
      String nodeId2 = nodeIds[1];
      // 获取关联的分支节点列表
      List<String> branchNodeIds = entry.getValue();

      // 检查哪个节点在后面
      if (isNode2AfterNode1(nodeId, nodeId2, edgeMapping)) {
        // 如果nodeId2在nodeId之后，且两个节点都存在，则移除nodeId2
        if (mergeBranchNodeIds.containsKey(nodeId)) {
          mergeBranchNodeIds.remove(nodeId2);
        }
      } else if (isNode2AfterNode1(nodeId2, nodeId, edgeMapping)) {
        // 如果nodeId在nodeId2之后，且两个节点都存在，则移除nodeId
        if (mergeBranchNodeIds.containsKey(nodeId) && mergeBranchNodeIds.containsKey(nodeId2)) {
          mergeBranchNodeIds.remove(nodeId);
        }
      }
    }

    // 建立分支与合并节点的映射关系
    Map<String, String> branchesMergeNodeIds = new HashMap<>();
    // 遍历所有合并节点
    for (Map.Entry<String, List<String>> entry : mergeBranchNodeIds.entrySet()) {
      // 获取合并节点ID
      String nodeId = entry.getKey();
      // 获取关联的分支节点列表
      List<String> branchNodeIds = entry.getValue();

      // 如果关联的分支节点数量小于等于1，则跳过
      if (branchNodeIds.size() <= 1) {
        continue;
      }

      // 为每个分支节点建立与合并节点的映射关系
      for (String branchNodeId : branchNodeIds) {
        // 如果该分支节点已经建立了映射关系，则跳过
        if (branchesMergeNodeIds.containsKey(branchNodeId)) {
          continue;
        }
        // 建立分支节点与合并节点的映射关系
        branchesMergeNodeIds.put(branchNodeId, nodeId);
      }
    }

    // 构建每个分支内部的节点ID列表
    Map<String, List<String>> inBranchNodeIds = new HashMap<>();
    // 遍历所有分支路由
    for (Map.Entry<String, List<String>> entry : routesNodeIds.entrySet()) {
      // 获取分支起始节点ID
      String branchNodeId = entry.getKey();
      // 获取该分支中的所有节点ID
      List<String> nodeIds = entry.getValue();

      // 为分支起始节点初始化节点ID列表
      inBranchNodeIds.put(branchNodeId, new ArrayList<>());
      // 如果该分支起始节点没有对应的合并节点
      if (!branchesMergeNodeIds.containsKey(branchNodeId)) {
        // 当前分支中的所有节点ID都在这个线程中
        inBranchNodeIds.get(branchNodeId).add(branchNodeId);
        // 将该分支中的所有节点ID添加到列表中
        inBranchNodeIds.get(branchNodeId).addAll(nodeIds);
      } else {
        // 获取该分支对应的合并节点ID
        String mergeNodeId = branchesMergeNodeIds.get(branchNodeId);
        // 如果合并节点与分支起始节点不同
        if (!mergeNodeId.equals(branchNodeId)) {
          // 将分支起始节点添加到列表中
          inBranchNodeIds.get(branchNodeId).add(branchNodeId);
        }

        // 获取从branchNodeId到mergeNodeId的所有节点ID
        recursivelyAddParallelNodeIds(inBranchNodeIds.get(branchNodeId), edgeMapping, mergeNodeId,
            branchNodeId);
      }
    }

    return inBranchNodeIds;
  }

  /**
   * 检查键是否存在于映射中
   * <p>
   * 用于比较字符串数组键是否已存在于映射中，解决数组作为键的比较问题。 由于数组的equals方法比较的是引用而非内容，需要特殊处理。
   *
   * @param map 映射，存储键值对数据
   * @param key 键，需要检查是否存在的字符串数组
   * @return 是否存在，true表示存在，false表示不存在
   */
  private static boolean containsKey(Map<String[], List<String>> map, String[] key) {
    // 遍历映射中的所有键
    for (String[] k : map.keySet()) {
      // 使用Arrays.equals方法比较数组内容
      if (Arrays.equals(k, key)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 递归获取路由
   * <p>
   * 从指定起始节点开始，递归收集所有可达的节点ID。 该方法使用深度优先搜索算法遍历图结构。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取起始节点的所有出边</li>
   *   <li>遍历每条出边</li>
   *   <li>如果目标节点尚未添加到列表中，则添加并递归处理</li>
   * </ol>
   *
   * @param edgeMapping   边映射，包含节点间的连接关系
   * @param startNodeId   起始节点ID，当前处理的节点
   * @param routesNodeIds 路由节点ID列表，用于存储遍历过程中发现的节点ID
   */
  private static void recursivelyFetchRoutes(Map<String, List<GraphEdge>> edgeMapping,
      String startNodeId, List<String> routesNodeIds) {

    // 获取起始节点的所有出边
    List<GraphEdge> edges = edgeMapping.get(startNodeId);
    if (edges != null) {
      // 遍历所有出边
      for (GraphEdge edge : edges) {
        // 获取边的目标节点ID
        String targetNodeId = edge.getTarget_node_id();
        // 如果目标节点尚未添加到列表中
        if (!routesNodeIds.contains(targetNodeId)) {
          // 将目标节点添加到列表中
          routesNodeIds.add(targetNodeId);
          // 递归处理目标节点
          recursivelyFetchRoutes(edgeMapping, targetNodeId, routesNodeIds);
        }
      }
    }
  }

  /**
   * 递归检查节点是否在路由中
   * <p>
   * 判断指定节点是否在给定的路由节点集合中，用于识别合并节点。 该方法通过分析节点的前驱关系来判断是否为合并节点。
   * <p>
   * 判断逻辑：
   * <ol>
   *   <li>获取节点的所有入边</li>
   *   <li>收集所有路由节点ID</li>
   *   <li>分析前驱节点的分支关系</li>
   *   <li>判断是否存在共享的起始节点</li>
   * </ol>
   *
   * @param reverseEdgeMapping 反向边映射，包含节点间的反向连接关系
   * @param startNodeId        起始节点ID，当前检查的节点
   * @param routesNodeIds      路由节点ID映射，包含所有路由中的节点
   * @return 是否在路由中，true表示是合并节点，false表示不是
   */
  private static boolean isNodeInRoutes(Map<String, List<GraphEdge>> reverseEdgeMapping,
      String startNodeId, Map<String, List<String>> routesNodeIds) {

    // 获取节点的所有入边
    List<GraphEdge> edges = reverseEdgeMapping.get(startNodeId);
    // 如果没有入边，则不是合并节点
    if (edges == null || edges.isEmpty()) {
      return false;
    }

    // 收集所有路由节点ID
    Set<String> allRoutesNodeIds = new HashSet<>();
    // 存储并行起始节点映射
    Map<String, List<String>> parallelStartNodeIds = new HashMap<>();

    // 遍历所有路由节点
    for (Map.Entry<String, List<String>> entry : routesNodeIds.entrySet()) {
      // 获取分支起始节点ID
      String branchNodeId = entry.getKey();
      // 获取该分支中的所有节点ID
      List<String> nodeIds = entry.getValue();
      // 将所有节点ID添加到集合中
      allRoutesNodeIds.addAll(nodeIds);

      // 获取分支起始节点的所有入边
      List<GraphEdge> reverseEdges = reverseEdgeMapping.get(branchNodeId);
      if (reverseEdges != null) {
        // 遍历所有入边
        for (GraphEdge edge : reverseEdges) {
          // 获取边的源节点ID（前驱节点）
          String sourceNodeId = edge.getSource_node_id();
          // 将前驱节点与分支起始节点关联
          parallelStartNodeIds.computeIfAbsent(sourceNodeId, k -> new ArrayList<>())
              .add(branchNodeId);
        }
      }
    }

    // 检查是否存在共享的起始节点
    for (List<String> branchNodeIds : parallelStartNodeIds.values()) {
      // 构建分支节点ID集合
      Set<String> branchNodeIdSet = new HashSet<>(branchNodeIds);
      // 构建路由节点ID集合
      Set<String> routesNodeIdSet = routesNodeIds.keySet();

      // 如果两个集合相互包含，则认为存在共享的起始节点
      if (branchNodeIdSet.containsAll(routesNodeIdSet) && routesNodeIdSet.containsAll(
          branchNodeIdSet)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 检查node2是否在node1之后
   * <p>
   * 判断在图结构中node2是否位于node1的后续位置。 该方法通过遍历图结构来判断节点间的先后关系。
   * <p>
   * 算法逻辑：
   * <ol>
   *   <li>获取node1的所有出边</li>
   *   <li>遍历每条出边</li>
   *   <li>如果直接连接到node2，则node2在node1之后</li>
   *   <li>否则递归检查后续节点</li>
   * </ol>
   *
   * @param node1Id     节点1 ID，起始节点
   * @param node2Id     节点2 ID，目标节点
   * @param edgeMapping 边映射，包含节点间的连接关系
   * @return node2是否在node1之后，true表示在之后，false表示不在之后
   */
  private static boolean isNode2AfterNode1(String node1Id, String node2Id,
      Map<String, List<GraphEdge>> edgeMapping) {
    // 获取node1的所有出边
    List<GraphEdge> edges = edgeMapping.get(node1Id);
    // 如果没有出边，则node2不在node1之后
    if (edges == null || edges.isEmpty()) {
      return false;
    }

    // 遍历node1的所有出边
    for (GraphEdge edge : edges) {
      // 如果直接连接到node2，则node2在node1之后
      if (node2Id.equals(edge.getTarget_node_id())) {
        return true;
      }

      // 递归检查后续节点
      if (isNode2AfterNode1(edge.getTarget_node_id(), node2Id, edgeMapping)) {
        return true;
      }
    }

    return false;
  }
}

/**
 * 图并行结构实体类
 * <p>
 * 用于表示图中的并行执行结构，包含并行结构的起始、结束和层级信息。 该类描述了工作流中并行执行的结构信息，支持嵌套的并行结构。
 * <p>
 * 主要功能： 1. 描述并行结构的基本信息（ID、起始节点、结束节点等） 2. 支持并行结构的嵌套关系（父子关系） 3. 提供并行结构的唯一标识
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GraphParallel {

  /**
   * 并行结构ID
   * <p>
   * 使用UUID生成唯一标识符，确保每个并行结构都有唯一的ID。 该ID用于在并行映射中唯一标识并行结构。
   * <p>
   */
  @Default
  private String id = UUID.randomUUID().toString();

  /**
   * 起始节点ID
   * <p>
   * 标识并行结构的起始节点，所有并行分支都从该节点开始。 该节点通常是一个分支节点，有多条出边指向不同的并行分支。
   * <p>
   */
  private String start_from_node_id;

  /**
   * 父并行结构ID
   * <p>
   * 指向包含当前并行结构的父并行结构，用于支持嵌套的并行结构。 如果为null，则表示当前并行结构是最外层的并行结构。
   * <p>
   */
  private String parent_parallel_id;

  /**
   * 父并行起始节点ID
   * <p>
   * 父并行结构的起始节点ID，用于维护父子并行结构间的关系。 该字段有助于在复杂的嵌套结构中追踪并行结构的层次关系。
   * <p>
   */
  private String parent_parallel_start_node_id;

  /**
   * 结束节点ID
   * <p>
   * 标识并行结构的结束节点，所有并行分支在此节点汇合。 该节点是并行执行的汇合点，等待所有并行分支执行完成后继续执行。
   * <p>
   */
  private String end_to_node_id;
}
