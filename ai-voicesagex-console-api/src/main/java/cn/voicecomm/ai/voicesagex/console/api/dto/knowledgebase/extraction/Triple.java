package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三元组
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Triple {

  /**
   * 起点
   */
  private EntityNode source_node;

  /**
   * 终点
   */
  private EntityNode target_node;


  /**
   * 关系名称
   */
  private String edge_type;
}
