package cn.voicecomm.ai.voicesagex.console.util.po.application;

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

/**
 * 工作流运行表，存储工作流的整体运行信息
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "workflow_runs")
public class WorkflowRunsPo implements Serializable {

  /**
   * 运行ID，主键
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 运行ID
   */
  @TableField(value = "workflow_run_id")
  private String workflow_run_id;

  /**
   * 工作空间ID，用于多租户隔离
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
   * 工作流类型
   */
  @TableField(value = "\"type\"")
  private String type;

  /**
   * 触发来源: debugging表示画布调试, app-run表示(已发布)应用执行
   */
  @TableField(value = "triggered_from")
  private String triggered_from;

  /**
   * 版本
   */
  @TableField(value = "version")
  private String version;

  /**
   * 工作流画布配置(JSON格式)
   */
  @TableField(value = "graph")
  private String graph;

  /**
   * 输入参数
   */
  @TableField(value = "inputs")
  private String inputs;

  /**
   * 执行状态: running/succeeded/failed/stopped
   */
  @TableField(value = "\"status\"")
  private String status;

  /**
   * 输出内容(可选)
   */
  @TableField(value = "outputs")
  private String outputs;

  /**
   * 错误原因(可选)
   */
  @TableField(value = "error")
  private String error;

  /**
   * 时间消耗(秒)(可选)
   */
  @TableField(value = "elapsed_time")
  private Double elapsed_time;

  /**
   * 使用的总token数(可选)
   */
  @TableField(value = "total_tokens")
  private Long total_tokens;

  /**
   * 总步骤数(冗余字段)，默认为0
   */
  @TableField(value = "total_steps")
  private Integer total_steps;

  /**
   * 异常数量
   */
  @TableField(value = "exceptions_count")
  private Integer exceptions_count;

  /**
   * 创建人
   */
  @TableField(value = "created_by")
  private Integer created_by;

  /**
   * 创建时间
   */
  @TableField(value = "created_at")
  private LocalDateTime created_at;

  /**
   * 运行结束时间
   */
  @TableField(value = "finished_at")
  private LocalDateTime finished_at;

  /**
   * 运行类型 draft测试版 published发布版 experience体验版
   */
  @TableField(value = "run_type")
  private String run_type;

}