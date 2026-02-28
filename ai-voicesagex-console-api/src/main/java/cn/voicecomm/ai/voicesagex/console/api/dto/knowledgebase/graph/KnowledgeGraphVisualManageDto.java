package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图谱可视化管理Dto
 *
 * @author ryc
 * @date 2025-09-16 14:52:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphVisualManageDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -8792860102335780958L;
  /**
   * 主键id
   */
  private Integer visualId;
  /**
   * 知识库id
   */
  private Integer spaceId;
  /**
   * 中心节点名称
   */
  private String entityName;
  /**
   * 中心点名称
   */
  private String centreVertex;
  /**
   * vertex ID
   */
  private String entityId;
  /**
   * 是否删除 false 否 true 删除
   */
  private Boolean deleted;

}
