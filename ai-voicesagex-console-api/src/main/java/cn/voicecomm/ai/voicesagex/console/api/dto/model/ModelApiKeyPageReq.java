package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 模型分类列表请求
 *
 * @author wangf
 * @date 2025/5/19 下午 2:08
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelApiKeyPageReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 8711318051436168186L;
  /**
   * 模型id
   */
  @NotNull(message = "模型id不能为空")
  private Integer modelId;
}
