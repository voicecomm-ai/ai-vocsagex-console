package cn.voicecomm.ai.voicesagex.console.util.po.mcp;

import cn.voicecomm.ai.voicesagex.console.util.handler.JsonStringHandler;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * mcp
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "mcp")
public class McpPo extends BasePo implements Serializable {

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * mcp名称
   */
  @TableField(value = "display_name")
  private String displayName;

  /**
   * mcp内部名称
   */
  @TableField(value = "internal_name")
  private String internalName;

  /**
   * 描述
   */
  @TableField(value = "description")
  private String description;

  /**
   * 参数
   */
  @TableField(value = "params", typeHandler = JsonStringHandler.class)
  private String params;

  /**
   * mcp图标路径
   */
  @TableField(value = "mcp_icon_url")
  private String mcpIconUrl;

  /**
   * 是否上架
   */
  @TableField(value = "is_shelf")
  private Boolean isShelf;

  /**
   * 调用方式  streamable_http，stdio
   */
  @TableField(value = "transport")
  private String transport;

  /**
   * mcp url
   */
  @TableField(value = "url")
  private String url;
}