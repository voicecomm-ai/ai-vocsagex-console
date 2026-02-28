package cn.voicecomm.ai.voicesagex.console.util.po.application.workflow;

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

/**
 * 工作流表，存储租户及应用的工作流配置
 *
 * @author wangf
 * @date 2025/1/19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "\"workflows\"")
public class WorkflowPo extends BaseAuditPo implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 主键ID，自增整数
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 租户ID
   */
  @TableField(value = "tenant_id")
  private String tenant_id;

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
   * 版本 (draft/具体版本号)
   */
  @TableField(value = "version")
  private String version;

  /**
   * 标记名称
   */
  @TableField(value = "marked_name")
  private String marked_name;

  /**
   * 标记注释
   */
  @TableField(value = "marked_comment")
  private String marked_comment;

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
} 