package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Mcp分页列表请求
 *
 * @author wangf
 * @date 2025/7/8 下午 2:11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class McpPageReq extends PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -3077886410445561958L;

  /**
   * mcp名称
   */
  @Size(max = 50, message = "mcp名称长度不能超过50")
  private String displayName;

  /**
   * 标签id集合
   */
  private List<Integer> tagIdList;

  /**
   * 是否上架
   */
  private Boolean isShelf;

}
