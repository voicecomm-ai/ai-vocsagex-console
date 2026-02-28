package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * mcp标签dto
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
public class McpTagDto extends BaseDto implements Serializable {

  /**
   * 主键id
   */
  private Integer id;

  /**
   * 标签名称
   */
  @Size(max = 50, message = "标签名称长度不能超过50")
  private String name;


  /**
   * 使用该标签的mcp数量
   */
  private Long tagUsedNumber = 0L;



}