package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型分类Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelCategoryPageDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 7820273559058454677L;

  /**
   * 主键id
   */
  private Integer id;
  /**
   * 模型名称
   */
  private String name;
  /**
   * 是否预设 0：否；1：是
   */
  private Boolean isBuilt;
  /**
   * 标签数据集合
   */
  private List<ModelTagDto> modelTagList;

}
