package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系属性Dto
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgePropertyDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 1167648278550023265L;
  /**
   * 主键id
   */
  private Integer propertyId;
  /**
   * Tag/Edge id
   */
  private Integer tagEdgeId;
  /**
   * 类型  0：Tag；1：Edge
   */
  private Integer type;
  /**
   * 名称
   */
  @NotBlank(message = "属性名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(max = 50, message = "属性名称不能超过50个字", groups = {AddGroup.class, UpdateGroup.class})
  private String propertyName;
  /**
   * 属性类型
   */
  private String propertyType;
  /**
   * 额外信息
   */
  private String extra;
  /**
   * 是否必填：0：必填；1：不必填
   */
  private Integer tagRequired;
  /**
   * 默认值
   */
  @JsonProperty("defaultValueAsString")
  private String defaultValue;
  /**
   * 是否删除 false 否 true 删除
   */
  private Boolean deleted;
  /**
   * 本体名称
   */
  private String tagName;
  /**
   * 知识库id
   */
  private Integer spaceId;
}
