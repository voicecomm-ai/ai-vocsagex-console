package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * mcp批量请求
 *
 * @author wangf
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class McpTagBatchReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 226672693485674785L;

  /**
   * id集合
   */
  @NotEmpty(message = "mcp id列表不能为空")
  private List<Integer> ids;

  /**
   * 标签id集合
   */
  @NotEmpty(message = "标签id列表不能为空")
  private List<Integer> tagIds;
}
