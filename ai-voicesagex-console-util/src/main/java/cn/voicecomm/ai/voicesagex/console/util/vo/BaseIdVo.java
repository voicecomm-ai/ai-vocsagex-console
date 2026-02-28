package cn.voicecomm.ai.voicesagex.console.util.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiwh
 * @date 2024/5/30 14:09
 */
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseIdVo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1563637081737566121L;

  /**
   * 查询的ID
   */
  private Integer id;
}
