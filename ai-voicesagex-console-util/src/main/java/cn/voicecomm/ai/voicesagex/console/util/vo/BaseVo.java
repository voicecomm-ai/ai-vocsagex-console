package cn.voicecomm.ai.voicesagex.console.util.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class BaseVo implements Serializable {

  @Serial
  private static final long serialVersionUID = 6695691472991314219L;

  /**
   * 创建时间 pattern = "yyyy-MM-dd HH:mm:ss"
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 更新时间 pattern = "yyyy-MM-dd HH:mm:ss"
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
}
