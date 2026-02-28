package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * 结束节点流参数实体类
 * <p>
 * 用于存储工作流中结束节点的流式输出配置信息，包括： 1. 结束节点依赖关系 2. 流变量选择器映射关系
 */
@Data
@Builder
public class EndStreamParam {

  /**
   * 结束节点依赖关系映射
   * <p>
   * Key: 结束节点ID Value: 该结束节点依赖的节点ID列表
   */
  private Map<String, List<String>> end_dependencies;

  /**
   * 结束节点流变量选择器映射
   * <p>
   * Key: 结束节点ID Value: 该结束节点关联的流变量选择器列表
   * <p>
   * 流变量选择器是一个字符串列表，表示变量的路径选择器
   */
  private Map<String, List<List<String>>> end_stream_variable_selector_mapping;
}