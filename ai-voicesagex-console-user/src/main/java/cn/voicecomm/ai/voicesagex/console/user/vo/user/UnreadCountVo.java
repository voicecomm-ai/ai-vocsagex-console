package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UnreadCountVo implements Serializable {

  @Serial
  private static final long serialVersionUID = 5546331086954542248L;

  /**
   * 未读消息总数
   */
  private Long total;
}
