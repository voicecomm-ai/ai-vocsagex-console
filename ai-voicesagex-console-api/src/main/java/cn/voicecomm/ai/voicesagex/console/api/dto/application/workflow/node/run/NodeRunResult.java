package cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.LLMUsage;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionMetadataKey;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 节点执行结果类
 * <p>
 * 用于表示工作流中某个节点的执行结果，包含执行状态、输入、输出、元数据、错误信息等。 该类通常用于节点执行完成后，向工作流引擎返回执行结果。
 */
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@NoArgsConstructor  // 无参构造函数
@AllArgsConstructor  // 全参构造函数
@Builder  // 支持构建者模式创建对象
@Accessors(chain = true)
public class NodeRunResult implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 节点的执行状态，默认为 RUNNING 可能的值：RUNNING, SUCCEEDED, FAILED, SKIPPED 等
   */
  private WorkflowNodeExecutionStatus status = WorkflowNodeExecutionStatus.RUNNING;

  /**
   * 节点的输入数据 以键值对形式存储输入变量，用于记录执行时的上下文输入 可能为 null，表示无输入或未记录
   */

  private Map<String, Object> inputs;

  /**
   * 节点的中间处理数据 用于存储节点执行过程中的临时数据或调试信息 可能为 null，表示无处理数据
   */

  private Map<String, Object> process_data;

  /**
   * 节点的输出数据 以键值对形式返回节点执行结果，供后续节点使用 可能为 null，表示无输出
   */

  private Map<String, Object> outputs;

  /**
   * 节点的元数据 存储执行相关的附加信息，如耗时、模型名称、检索文档数等 键为 WorkflowNodeExecutionMetadataKey 枚举类型 可能为 null
   */

  private Map<WorkflowNodeExecutionMetadataKey, Object> metadata;

  /**
   * 大语言模型（LLM）的资源使用情况 包括 prompt tokens、completion tokens、total tokens 等 仅在调用 LLM 的节点中使用，其他节点可能为
   * null
   */

  private LLMUsage llm_usage;

  /**
   * 分支节点的源句柄标识 当节点有多个输出分支时，用于记录本次执行是从哪个分支流出的 例如：handleA、handleB 可能为 null，表示无分支或未启用
   */

  private String edge_source_handle;

  /**
   * 错误信息 当执行状态为 FAILED 时，此字段记录具体的错误描述 例如："Query is required" 或 "Rate limit exceeded" 成功执行时为 null
   */

  private String error;

  /**
   * 错误类型 表示错误的分类，便于前端或系统进行错误处理 例如：RateLimitExceeded、InvalidInput、KnowledgeRetrievalError 成功执行时为
   * null
   */

  private String error_type;

  /**
   * 重试索引 表示当前是第几次重试（从 0 开始） 用于支持单步节点的自动重试机制 默认值为 0，表示首次执行
   */
  private int retry_index = 0;
}