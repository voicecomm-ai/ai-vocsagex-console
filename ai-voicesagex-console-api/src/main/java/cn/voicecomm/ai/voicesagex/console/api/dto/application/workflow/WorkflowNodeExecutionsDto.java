package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class WorkflowNodeExecutionsDto implements Serializable {

  /**
   * 主键ID
   */
  private Integer id;

  /**
   * 租户ID
   */
  private String tenant_id;

  /**
   * 应用ID
   */
  private Integer app_id;

  /**
   * 工作流ID
   */
  private Integer workflow_id;

  /**
   * 触发来源
   */
  private String triggered_from;

  /**
   * 工作流运行ID（单节点运行时为NULL）
   */
  private String workflow_run_id;

  /**
   * 执行序号
   */
  private Integer index;

  /**
   * 前置节点ID
   */
  private String predecessor_node_id;

  /**
   * 节点执行ID
   */
  private String node_execution_id;

  /**
   * 节点ID
   */
  private String node_id;

  /**
   * 节点类型（如：start, llm, tool等）
   */
  private String node_type;

  /**
   * 节点标题
   */
  private String title;

  /**
   * 输入参数（JSON格式）
   */
  private JsonNode inputs;

  /**
   * 处理数据（JSON格式）
   */
  private JsonNode process_data;

  /**
   * 输出结果（JSON格式）
   */
  private JsonNode outputs;

  /**
   * 执行状态（running/succeeded/failed/exception/retry）
   *
   * @see cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus
   */
  private String status;

  /**
   * 错误信息
   */
  private String error;

  /**
   * 执行耗时（秒）
   */
  private Double elapsed_time;

  /**
   * 执行元数据（JSON格式）
   */
  private JsonNode execution_metadata;

  /**
   * 完成时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime finished_at;

  /**
   * 创建者角色（account/end_user）
   */
  private String created_by_role;

  /**
   * 创建者ID
   */
  private Integer createdBy;

  /**
   * 执行人名称
   */
  private String executor_name;


  /**
   * 创建时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  /**
   * 循环节点列表
   */
  private List<WorkflowNodeExecutionsDto> loopList = new ArrayList<>();

  /**
   * 循环节点ID
   */
  private Integer loop_index;


  /**
   * 嵌套的agent或者workflow的appId
   */
  private Integer toolAppId;



  /**
   * 嵌套的agent或者workflow的图标url
   */
  private String iconUrl;
}