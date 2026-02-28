package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * mcp和标签关联dto
 *
 * @author wangf
 * @date 2025/7/8 下午 2:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class McpTagRelationDto extends BaseDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * mcp id
   */
  private Integer mcpId;

  /**
   * 标签id
   */
  private Integer tagId;
}