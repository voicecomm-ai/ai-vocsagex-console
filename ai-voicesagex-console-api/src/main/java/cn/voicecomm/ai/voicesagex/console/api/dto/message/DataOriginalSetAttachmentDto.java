package cn.voicecomm.ai.voicesagex.console.api.dto.message;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 训练模型Dto
 *
 * @author ryc
 * @date 2025-07-31 16:22:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DataOriginalSetAttachmentDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = 8469457821162195157L;
  /**
   * 主键id
   */
  private Integer id;
  /**
   * 数据量
   */
  private Integer dataNum;
  /**
   * 原因
   */
  private String reason;
}
