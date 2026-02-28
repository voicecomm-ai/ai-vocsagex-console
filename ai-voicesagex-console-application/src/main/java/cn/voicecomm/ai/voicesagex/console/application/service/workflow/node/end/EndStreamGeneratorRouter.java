package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphEdge;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndNode.Output;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 结束节点流生成路由处理器
 * <p>
 * 该类负责处理工作流中结束节点的流式输出配置，包括： 1. 提取流式变量选择器 2. 构建结束节点依赖关系 3. 生成流式输出参数
 */
public class EndStreamGeneratorRouter {

  /**
   * 初始化流生成路由参数
   *
   * @param node_id_config_mapping 节点ID到配置的映射关系
   * @param reverse_edge_mapping   反向边映射关系（目标节点ID -> 边列表）
   * @param node_parallel_mapping  节点并行映射关系
   * @return 流式输出参数对象
   */
  public static EndStreamParam init(
      Map<String, JSONObject> node_id_config_mapping,
      Map<String, List<GraphEdge>> reverse_edge_mapping,
      Map<String, String> node_parallel_mapping) {

    // 解析结束节点的流输出变量选择器
    Map<String, List<List<String>>> end_stream_variable_selectors_mapping = new HashMap<>();

    for (Map.Entry<String, JSONObject> entry : node_id_config_mapping.entrySet()) {
      String end_node_id = entry.getKey();
      JSONObject node_config = entry.getValue();

      // 检查是否为结束节点
      if (!isEndNode(node_config)) {
        continue;
      }

      // 跳过并行中的结束节点
      if (node_parallel_mapping.containsKey(end_node_id)) {
        continue;
      }

      // 获取流输出的变量选择器
      List<List<String>> stream_variable_selectors = extractStreamVariableSelector(
          node_id_config_mapping, node_config);
      end_stream_variable_selectors_mapping.put(end_node_id, stream_variable_selectors);
    }

    // 获取结束节点依赖关系
    Set<String> end_node_ids = end_stream_variable_selectors_mapping.keySet();
    Map<String, List<String>> end_dependencies = fetchEndsDependencies(
        end_node_ids,
        reverse_edge_mapping,
        node_id_config_mapping
    );

    // 构建并返回流式输出参数对象
    return EndStreamParam.builder()
        .end_stream_variable_selector_mapping(end_stream_variable_selectors_mapping)
        .end_dependencies(end_dependencies)
        .build();
  }

  /**
   * 从节点数据中提取流变量选择器
   *
   * @param node_id_config_mapping 节点ID到配置的映射关系
   * @param nodeData               节点数据对象
   * @return 流变量选择器列表
   */
  public static List<List<String>> extractStreamVariableSelectorFromNodeData(
      Map<String, JSONObject> node_id_config_mapping,
      EndNode nodeData) {

    // 获取节点输出变量选择器
    List<Output> outputs = nodeData.getOutputs();

    List<List<String>> value_selectors = new ArrayList<>();
    if (CollUtil.isEmpty(outputs)) {
      return value_selectors;
    }

    for (Output variable_selector : outputs) {
      if (variable_selector.getValue_selector() == null || variable_selector.getValue_selector()
          .isEmpty()) {
        continue;
      }

      String node_id = variable_selector.getValue_selector().getFirst();

      // 检查节点是否存在于配置映射中且不是系统节点
      if (!"sys".equals(node_id) && node_id_config_mapping.containsKey(node_id)) {
        JSONObject node = node_id_config_mapping.get(node_id);
        String node_type = getNodeDataType(node);

        // 如果是LLM节点且选择器指向"text"输出，则添加到流变量选择器中
        if (!value_selectors.contains(variable_selector.getValue_selector())
            && NodeType.LLM.getValue().equals(node_type)
            && "text".equals(variable_selector.getValue_selector().get(1))) {
          value_selectors.add(new ArrayList<>(variable_selector.getValue_selector()));
        }
      }
    }

    return value_selectors;
  }

  /**
   * 从节点配置中提取流变量选择器
   *
   * @param node_id_config_mapping 节点ID到配置的映射关系
   * @param config                 节点配置
   * @return 流变量选择器列表
   */
  private static List<List<String>> extractStreamVariableSelector(
      Map<String, JSONObject> node_id_config_mapping,
      JSONObject config) {

    EndNode nodeData = JSONUtil.toBean(JSONUtil.getByPath(config, "data", JSONUtil.createObj()),
        EndNode.class);

    return extractStreamVariableSelectorFromNodeData(node_id_config_mapping, nodeData);
  }

  /**
   * 获取结束节点依赖关系
   *
   * @param end_node_ids           结束节点ID列表
   * @param reverse_edge_mapping   反向边映射关系
   * @param node_id_config_mapping 节点ID到配置的映射关系
   * @return 结束节点依赖关系映射
   */
  private static Map<String, List<String>> fetchEndsDependencies(
      Set<String> end_node_ids,
      Map<String, List<GraphEdge>> reverse_edge_mapping,
      Map<String, JSONObject> node_id_config_mapping) {

    Map<String, List<String>> end_dependencies = new HashMap<>();

    for (String end_node_id : end_node_ids) {
      end_dependencies.putIfAbsent(end_node_id, new ArrayList<>());

      recursiveFetchEndDependencies(
          end_node_id,  // current_node_id
          end_node_id,  // end_node_id
          node_id_config_mapping,
          reverse_edge_mapping,
          end_dependencies
      );
    }

    return end_dependencies;
  }

  /**
   * 递归获取结束节点依赖关系
   *
   * @param current_node_id        当前节点ID
   * @param end_node_id            结束节点ID
   * @param node_id_config_mapping 节点ID到配置的映射关系
   * @param reverse_edge_mapping   反向边映射关系
   * @param end_dependencies       结束节点依赖关系映射
   */
  private static void recursiveFetchEndDependencies(
      String current_node_id,
      String end_node_id,
      Map<String, JSONObject> node_id_config_mapping,
      Map<String, List<GraphEdge>> reverse_edge_mapping,
      Map<String, List<String>> end_dependencies) {

    // 获取当前节点的反向边
    List<GraphEdge> reverse_edges = reverse_edge_mapping.getOrDefault(current_node_id,
        new ArrayList<>());

    for (GraphEdge edge : reverse_edges) {
      String source_node_id = edge.getSource_node_id();

      // 检查源节点是否存在于配置映射中
      if (!node_id_config_mapping.containsKey(source_node_id)) {
        continue;
      }

      // 获取源节点类型
      String source_node_type = getNodeDataType(node_id_config_mapping.get(source_node_id));

      // 如果是条件判断节点（如if-else或问题分类器），则添加到依赖列表中
      if (NodeType.IF_ELSE.getValue().equals(source_node_type) ||
          NodeType.QUESTION_CLASSIFIER.getValue().equals(source_node_type)) {
        if (!end_dependencies.get(end_node_id).contains(source_node_id)) {
          end_dependencies.get(end_node_id).add(source_node_id);
        }
      } else {
        // 递归处理其他类型的节点
        recursiveFetchEndDependencies(
            source_node_id,
            end_node_id,
            node_id_config_mapping,
            reverse_edge_mapping,
            end_dependencies
        );
      }
    }
  }

  /**
   * 检查节点是否为结束节点
   *
   * @param node_config 节点配置
   * @return 是否为结束节点
   */
  private static boolean isEndNode(JSONObject node_config) {
    Object data = node_config.get("data");
    if (data == null) {
      return false;
    }

    String node_type = JSONUtil.getByPath(node_config, "data.type").toString();
    return NodeType.END.getValue().equals(node_type);
  }

  /**
   * 从节点配置中获取节点数据类型
   *
   * @param node 节点配置
   * @return 节点数据类型
   */
  private static String getNodeDataType(JSONObject node) {
    String data = node.getStr("data");
    if (data == null) {
      return null;
    }

    return JSONUtil.getByPath(node, "data.type").toString();
  }
}