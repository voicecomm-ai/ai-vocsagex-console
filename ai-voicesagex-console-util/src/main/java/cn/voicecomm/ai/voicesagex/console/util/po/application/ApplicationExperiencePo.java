package cn.voicecomm.ai.voicesagex.console.util.po.application;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 应用-体验
 */
@Data
@Accessors(chain = true)
@TableName(value = "application_experience")
public class ApplicationExperiencePo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
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
   * 创建人
   */
  @TableField(value = "create_by")
  private Integer createBy;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /**
   * 开启工作流追踪
   */
  @TableField(value = "enable_workflow_trace")
  private Boolean enableWorkflowTrace;

  /**
   * 应用id
   */
  @TableField(value = "app_id")
  private Integer appId;


  /**
   * agent类型  single单个，multiple多个
   */
  @TableField(value = "agent_type")
  private String agentType;
}