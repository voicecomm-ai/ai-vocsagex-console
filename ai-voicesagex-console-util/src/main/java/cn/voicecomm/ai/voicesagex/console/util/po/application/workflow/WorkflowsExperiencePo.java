package cn.voicecomm.ai.voicesagex.console.util.po.application.workflow;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 工作流发布表，存储租户及应用的工作流配置
 *
 * @author gaox
 * @date 2025/10/19
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "\"workflows_experience\"")
public class WorkflowsExperiencePo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键ID，自增整数
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 应用ID
   */
  @TableField(value = "app_id")
  private Integer app_id;

  /**
   * 工作流类型 (workflow/chat)
   */
  @TableField(value = "\"type\"")
  private String type;

  /**
   * 工作流图配置 (JSON)
   */
  @TableField(value = "graph")
  private String graph;

  /**
   * 功能特性配置 (JSON)
   */
  @TableField(value = "features")
  private String features;

  /**
   * 环境变量 (JSON)
   */
  @TableField(value = "environment_variables")
  private String environment_variables;

  /**
   * 对话变量 (JSON)
   */
  @TableField(value = "conversation_variables")
  private String conversation_variables;


  /**
   * 创建者ID
   */
  @TableField(value = "create_by", fill = FieldFill.INSERT)
  private Integer create_by;

  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime create_time;
}