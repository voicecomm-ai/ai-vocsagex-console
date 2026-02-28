package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 模型分页列表请求
 *
 * @author wangf
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class McpReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 3618720989207355349L;

  /**
   * mcp名称
   */
  @Size(max = 50, message = "mcp名称长度不能超过50")
  private String displayName;

  /**
   * 是否上架
   */
  private Boolean isShelf;

}
