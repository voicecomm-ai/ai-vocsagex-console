package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型标签Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelTagDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -9120157507661359947L;

  /**
   * 主键id
   */
  @NotNull(message = "主键id不能为空", groups = {UpdateGroup.class})
  private Integer id;
  /**
   * 分类id
   */
  @NotNull(message = "分类id不能为空", groups = {AddGroup.class})
  private Integer categoryId;
  /**
   * 标签名称
   */
  @NotBlank(message = "标签名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  @Size(message = "不能超过50个字", max = 50, groups = {AddGroup.class, UpdateGroup.class})
  private String name;

  /**
   * 关联模型数量
   */
  private Long modelRelationNum;
}
