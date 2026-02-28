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
public class McpBatchReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 226672693485674785L;

  /**
   * id集合
   */
  @NotEmpty(message = "id不能为空")
  private List<Integer> ids;
  /**
   * 是否上架
   */
  private Boolean isShelf;
}
