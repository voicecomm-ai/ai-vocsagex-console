package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: gaox
 * @date: 2025/9/10 13:47
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpTagGroupDto implements Serializable {

  private Integer tagId;
  private String tagName;
  private List<McpDto> mcps;
}
