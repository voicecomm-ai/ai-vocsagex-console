package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * mcp app 关系删除参数
 *
 * @author wangf
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class McpAppRelationRemoveReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 226672693485674785L;

  /**
   * mcp id
   */
  @NotNull(message = "mcp id不能为空")
  private Integer mcpId;

  /**
   * 应用id
   */
  @NotNull(message = "应用id不能为空")
  private Integer applicationId;
}
