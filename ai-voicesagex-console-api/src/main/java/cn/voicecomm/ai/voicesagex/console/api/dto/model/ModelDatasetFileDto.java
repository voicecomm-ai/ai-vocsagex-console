package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据集文件Dto
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelDatasetFileDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 5964933180550534818L;

  /**
   * 主键id
   */
  private Integer id;
  /**
   * 数据集id
   */
  private Integer datasetId;
  /**
   * 文件名称
   */
  private String name;
  /**
   * 文件大小
   */
  private String size;
  /**
   * 文件路径
   */
  private String path;

}
