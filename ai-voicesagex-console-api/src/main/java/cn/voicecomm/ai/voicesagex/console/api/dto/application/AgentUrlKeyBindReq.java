package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 应用绑定api key请求
 *
 * @author wangf
 * @date 2025/5/19 下午 1:42
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentUrlKeyBindReq implements Serializable {


  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 应用id
   */
  @NotNull(message = "应用id不能为空")
  private Integer appId;

  /**
   * api key id
   */
  private Integer keyId;
}