package cn.voicecomm.ai.voicesagex.console.util.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 用户请求的数据
 *
 * @author GeCh
 * @version v1.0.0
 * @date 2023-04-28
 */
@Data
@Builder
public class UserBase {

  /**
   * 用户id
   */
  private Integer userId;

  /**
   * 请求ip
   */
  private String userIp;

  /**
   * 用户名
   */
  private String userName;
}
