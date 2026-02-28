package cn.voicecomm.ai.voicesagex.console.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAuditDto extends BaseDto {

  @Serial
  private static final long serialVersionUID = -6665813914362655159L;

  /**
   * 创建人id
   */
  private Integer createBy;

  /**
   * 更新人id
   */
  private Integer updateBy;

}
