package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系Dto
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgeDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -7800854348959948201L;
  /**
   * 主键id
   */
  private Integer tagEdgeId;
  /**
   * 知识库id
   */
  private Integer spaceId;
  /**
   * 类型  0：Tag；1：Edge
   */
  private Integer type;
  /**
   * 名称
   */
  @NotBlank(message = "名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(max = 50, message = "名称不能超过50个字", groups = {AddGroup.class, UpdateGroup.class})
  private String tagName;
  /**
   * 描述
   */
  private String description;
  /**
   * ttlCol 字段
   */
  private String ttlCol;
  /**
   * 过期时间（小时为单位）
   */
  private Integer ttlDuration;
  /**
   * 是否删除 false 否 true 删除
   */
  private Boolean deleted;

}
