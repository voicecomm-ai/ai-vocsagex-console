package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 工作流运行DTO，用于传输工作流的整体运行信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WorkflowRunsDto implements Serializable {

  /**
   * 运行ID，主键
   */
  private Integer id;

  /**
   * 运行ID
   */
  private String workflow_run_id;

  /**
   * 工作空间ID，用于多租户隔离
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
   * 工作流类型
   */
  private String type;

  /**
   * 触发来源: debugging表示画布调试, app-run表示(已发布)应用执行
   */
  private String triggered_from;

  /**
   * 版本
   */
  private String version;

  /**
   * 工作流画布配置(JSON格式)
   */
  private JsonNode graph;

  /**
   * 输入参数
   */
  private JsonNode inputs;

  /**
   * 执行状态: running/succeeded/failed/stopped
   */
  private String status;

  /**
   * 输出内容(可选)
   */
  private JsonNode outputs;

  /**
   * 错误原因(可选)
   */
  private String error;

  /**
   * 时间消耗(秒)(可选)
   */
  private Double elapsed_time;

  /**
   * 使用的总token数(可选)
   */
  private Long total_tokens;

  /**
   * 总步骤数(冗余字段)，默认为0
   */
  private Integer total_steps;

  /**
   * 异常数量
   */
  private Integer exceptions_count;

  /**
   * 创建人
   */
  private Integer created_by;

  /**
   * 创建人账号
   */
  private String createdAccount;

  /**
   * 创建时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime created_at;

  /**
   * 运行结束时间
   */
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime finished_at;
}