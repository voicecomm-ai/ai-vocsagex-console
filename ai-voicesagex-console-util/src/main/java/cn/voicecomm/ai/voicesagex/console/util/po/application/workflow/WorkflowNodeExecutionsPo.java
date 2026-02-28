package cn.voicecomm.ai.voicesagex.console.util.po.application.workflow;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 工作流节点运行
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "workflow_node_executions")
public class WorkflowNodeExecutionsPo implements Serializable {

  /**
   * 主键ID
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
   * 工作流ID
   */
  @TableField(value = "workflow_id")
  private Integer workflow_id;

  /**
   * 触发来源
   */
  @TableField(value = "triggered_from")
  private String triggered_from;

  /**
   * 工作流运行ID（单节点运行时为NULL）
   */
  @TableField(value = "workflow_run_id")
  private String workflow_run_id;

  /**
   * 执行序号
   */
  @TableField(value = "\"index\"")
  private Integer index;

  /**
   * 前置节点ID
   */
  @TableField(value = "predecessor_node_id")
  private String predecessor_node_id;

  /**
   * 节点执行ID
   */
  @TableField(value = "node_execution_id")
  private String node_execution_id;

  /**
   * 节点ID
   */
  @TableField(value = "node_id")
  private String node_id;

  /**
   * 节点类型（如：start, llm, tool等）
   */
  @TableField(value = "node_type")
  private String node_type;

  /**
   * 节点标题
   */
  @TableField(value = "title")
  private String title;

  /**
   * 输入参数（JSON格式）
   */
  @TableField(value = "inputs")
  private String inputs;

  /**
   * 处理数据（JSON格式）
   */
  @TableField(value = "process_data")
  private String process_data;

  /**
   * 输出结果（JSON格式）
   */
  @TableField(value = "outputs")
  private String outputs;

  /**
   * 执行状态（running/succeeded/failed/exception/retry）
   */
  @TableField(value = "\"status\"")
  private String status;

  /**
   * 错误信息
   */
  @TableField(value = "error")
  private String error;

  /**
   * 执行耗时（秒）
   */
  @TableField(value = "elapsed_time")
  private Double elapsed_time;

  /**
   * 执行元数据（JSON格式）
   */
  @TableField(value = "execution_metadata")
  private String execution_metadata;

  /**
   * 循环节点ID
   */
  @TableField(value = "loop_index")
  private Integer loop_index;

  /**
   * 嵌套的agent或者workflow的appId
   */
  @TableField(value = "tool_app_id")
  private Integer toolAppId;

  /**
   * 完成时间
   */
  @TableField(value = "finished_at")
  private LocalDateTime finished_at;

  /**
   * 创建者角色（account/end_user）
   */
  @TableField(value = "created_by_role")
  private String created_by_role;

  /**
   * 创建者ID
   */
  @TableField(value = "created_by", fill = FieldFill.INSERT)
  private Integer createdBy;


  /**
   * 创建时间
   */
  @TableField(value = "create_time", fill = FieldFill.INSERT)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;
}