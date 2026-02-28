package cn.voicecomm.ai.voicesagex.console.util.vo;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiwh
 * @date 2024/5/30 14:09
 */
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAuditVo extends BaseVo {

  @Serial
  private static final long serialVersionUID = 2417160116728635607L;

  /**
   * 创建人id
   */
  private Integer createBy;

  /**
   * 更新人id
   */
  private Integer updateBy;
}
