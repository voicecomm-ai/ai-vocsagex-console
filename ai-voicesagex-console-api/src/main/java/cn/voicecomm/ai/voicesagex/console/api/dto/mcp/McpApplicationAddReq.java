package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * mcp 应用绑定请求
 *
 * @author wangf
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class McpApplicationAddReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 226672693485674785L;

  /**
   * mcp id集合
   */
  private List<Integer> mcpIds;

  /**
   * 应用id
   */
  @NotNull(message = "应用id不能为空")
  private Integer applicationId;
}
