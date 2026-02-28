package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgeReq implements Serializable {

  @Serial
  private static final long serialVersionUID = -6221335191716038894L;

  /**
   * Tag/Edge id
   */
  private Integer tagEdgeId;
  /**
   * 类型  0：Tag；1：Edge
   */
  private Integer type;
}
