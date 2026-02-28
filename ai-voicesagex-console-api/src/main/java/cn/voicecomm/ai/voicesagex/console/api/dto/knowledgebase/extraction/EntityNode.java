package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityNode {

  /**
   * 实体的本体
   */
  private String tag;

  /**
   * 实体名称
   */
  private String name;
}
