package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.Data;


/**
 * url访问请求
 *
 * @author wangfan
 * @date 2026/1/6 下午 3:28
 */
@Data
public class UrlAccessReq implements Serializable {


  /**
   * 对话token
   */
  @NotEmpty(message = "对话token不能为空")
  private String token;

  /**
   * urlKey
   */
  @NotEmpty(message = "urlKey不能为空")
  private String urlKey;

}
