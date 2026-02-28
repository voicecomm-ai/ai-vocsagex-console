package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author adminst
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Entity extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -314370146985356839L;
  /**
   * 实体id
   */
  private String entityId;
  /**
   * 图空间id
   */
  private Long spaceId;
  /**
   * 本体id
   */
  private Long tagEdgeId;
  /**
   * 本体名称
   */
  private String tagName;
  /**
   * 实体名称
   */
  private String entityName;
  /**
   * 来源
   */
  private String origin;
}
