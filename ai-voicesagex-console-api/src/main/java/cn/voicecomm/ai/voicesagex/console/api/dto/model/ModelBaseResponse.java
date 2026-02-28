package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author adminst
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ModelBaseResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 1083917167572712371L;

  /**
   * 状态码（1000=成功, 2000=失败）
   */
  private int code;

  /**
   * 状态信息（成功时为唯一标识符，失败时为错误描述）
   */
  private String msg;

  /**
   * 优化后的提示词数据（失败时为null）
   */
  private Object data;

}
