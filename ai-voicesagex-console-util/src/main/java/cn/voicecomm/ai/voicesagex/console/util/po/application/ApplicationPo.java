package cn.voicecomm.ai.voicesagex.console.util.po.application;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "\"application\"")
public class ApplicationPo extends BaseAuditPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;


  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用类型---智能体agent,工作流应用workflow,智能体编排应用agent_arrangement
   */
  @TableField(value = "\"type\"")
  private String type;

  /**
   * 应用名称
   */
  @TableField(value = "\"name\"")
  private String name;

  /**
   * 描述
   */
  @TableField(value = "description")
  private String description;

  /**
   * 图标地址
   */
  @TableField(value = "icon_url")
  private String iconUrl;

  /**
   * 状态  -1删除，0草稿，1已发布
   */
  @TableField(value = "\"status\"")
  private Integer status;


  /**
   * 是否允许api访问
   */
  @TableField(value = "api_accessable")
  private Boolean apiAccessable;


  /**
   * URL是否可访问
   */
  @TableField(value = "url_accessable")
  private Boolean urlAccessable;


  /**
   * URL地址的key
   */
  @TableField(value = "url_key")
  private String urlKey;

  /**
   * 是否内置
   */
  @TableField(value = "is_integrated")
  private Boolean isIntegrated;


  /**
   * agent类型  single单个，multiple多个
   */
  @TableField(value = "agent_type")
  private String agentType;


}