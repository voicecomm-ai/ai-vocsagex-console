package cn.voicecomm.ai.voicesagex.console.api.dto.application;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用发布和上架历史响应
 *
 * @author wangf
 * @date 2025/10/27 上午 10:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppPublishAndOnShelfTimeResp implements Serializable {


  /**
   * 上一次发布时间描述
   */
  private String lastPublishedTimeDesc;


  /**
   * 上一次上架时间描述
   */
  private String lastOnShelfTimeDesc;

}
