package cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: gaox
 * @date: 2025/7/31 14:44
 */
@Getter
@AllArgsConstructor
public enum WorkflowNodeExecutionMetadataKey {

  /**
   * 总 token 数量（用于 LLM 调用统计）
   */
  TOTAL_TOKENS("total_tokens"),

  /**
   * 总费用金额
   */
  TOTAL_PRICE("total_price"),

  /**
   * 费用货币类型（如 USD、CNY）
   */
  CURRENCY("currency"),

  /**
   * 工具调用信息（如调用的工具名称、参数等）
   */
  TOOL_INFO("tool_info"),

  /**
   * Agent 执行日志或思考过程
   */
  AGENT_LOG("agent_log"),

  /**
   * 迭代（Iteration）节点的运行 ID
   */
  ITERATION_ID("iteration_id"),

  /**
   * 当前迭代的索引（从 0 开始）
   */
  ITERATION_INDEX("iteration_index"),

  /**
   * 循环（Loop）节点的运行 ID
   */
  LOOP_ID("loop_id"),

  /**
   * 当前循环的索引（从 0 开始）
   */
  LOOP_INDEX("loop_index"),

  /**
   * 并行（Parallel）执行的运行 ID
   */
  PARALLEL_ID("parallel_id"),

  /**
   * 并行分支的起始节点 ID
   */
  PARALLEL_START_NODE_ID("parallel_start_node_id"),

  /**
   * 父级并行执行的 ID（用于嵌套并行）
   */
  PARENT_PARALLEL_ID("parent_parallel_id"),

  /**
   * 父级并行分支的起始节点 ID
   */
  PARENT_PARALLEL_START_NODE_ID("parent_parallel_start_node_id"),

  /**
   * 并行模式下的运行 ID（特定上下文）
   */
  PARALLEL_MODE_RUN_ID("parallel_mode_run_id"),

  /**
   * 单次迭代的耗时映射（key: iteration_id, value: duration_ms）
   */
  ITERATION_DURATION_MAP("iteration_duration_map"),

  /**
   * 单次循环的耗时映射（key: loop_id, value: duration_ms）
   */
  LOOP_DURATION_MAP("loop_duration_map"),

  /**
   * 错误处理策略（如 "continue_on_error"）
   */
  ERROR_STRATEGY("error_strategy"),

  /**
   * 运行完成原因
   */
  COMPLETED_REASON("completed_reason"),

  /**
   * 单次循环的变量输出映射（key: variable_name, value: output_value）
   */
  LOOP_VARIABLE_MAP("loop_variable_map");

  private final String key;
}
