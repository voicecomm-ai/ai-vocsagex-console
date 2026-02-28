package cn.voicecomm.ai.voicesagex.console.api.dto.message;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelAttachmentDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 3482694877053960320L;

  /**
   * 主键id
   */
  private Integer id;
  /**
   * 模型类型名称
   */
  private String typeName;
  /**
   * 原因
   */
  private String reason;
  /**
   * 生成状态
   */
  private Integer generateStatus;
}
