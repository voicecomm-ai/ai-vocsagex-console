package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.Graph.RunCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图边实体类 表示图中两个节点之间的连接关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdge {

  /**
   * 源节点ID
   */
  private String source_node_id;

  /**
   * 目标节点ID
   */
  private String target_node_id;

  /**
   * 运行条件
   */
  private RunCondition run_condition;


  /**
   * 边状态
   */
  @Builder.Default
  private volatile GraphEdgeState state = GraphEdgeState.UNKNOWN;

}
