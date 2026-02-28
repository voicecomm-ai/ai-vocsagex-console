package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 应用上架请求参数
 *
 * @author wangf
 * @date 2025/10/27 上午 10:28
 */
@Data
@Accessors(chain = true)
public class ShelfRequest implements Serializable {

  /**
   * 应用id
   */
  @NotNull(message = "应用id不能为空")
  private Integer appId;

  /**
   * 分类id列表
   */
  private List<Integer> tagIdList;

  /**
   * 是否启用工作流跟踪
   */
  private Boolean enableWorkflowTrace;
}
