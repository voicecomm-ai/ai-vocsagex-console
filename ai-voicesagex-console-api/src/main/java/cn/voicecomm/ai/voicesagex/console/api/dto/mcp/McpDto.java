package cn.voicecomm.ai.voicesagex.console.api.dto.mcp;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * McpDto
 *
 * @author wangf
 * @date 2025/7/8 下午 2:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class McpDto extends BaseDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * mcp名称
   */
  @Size(max = 50, message = "mcp名称长度不能超过50", groups = {AddGroup.class, UpdateGroup.class})
  @NotBlank(message = "mcp名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String displayName;

  /**
   * mcp内部名称
   */
  @Size(max = 50, message = "mcp内部名称长度不能超过50", groups = {AddGroup.class,
      UpdateGroup.class})
  @NotBlank(message = "mcp名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String internalName;

  /**
   * 描述
   */
  @Size(max = 200, message = "描述长度不能超过200", groups = {AddGroup.class, UpdateGroup.class})
  @NotBlank(message = "描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String description;

  /**
   * 参数
   */
//  @NotBlank(message = "参数不能为空", groups = {AddGroup.class, UpdateGroup.class})
//  @Size(max = 10000, message = "参数长度不能超过10000", groups = {AddGroup.class,
//      UpdateGroup.class})
  private String params;

  /**
   * mcp图标路径
   */
  private String mcpIconUrl;

  /**
   * 是否上架
   */
  private Boolean isShelf;

  /**
   * 调用方式  streamable_http，stdio
   */
  private String transport;


  /**
   * 标签List(新增时只需传id)
   */
  private List<McpTagDto> tagList;


  /**
   * mcp url
   */
  @NotBlank(message = "mcp url不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private String url;
}
