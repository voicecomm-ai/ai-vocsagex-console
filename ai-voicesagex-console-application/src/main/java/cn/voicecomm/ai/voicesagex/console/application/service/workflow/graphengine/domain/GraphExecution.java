package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Aggregate root for graph execution.
 * <p>
 * This manages the overall execution state of a workflow graph, coordinating between multiple node
 * executions.
 *
 * @author gaox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class GraphExecution {

  /**
   * 工作流ID
   */
  private String workflowId;

  /**
   * 是否已开始执行
   */
  private boolean started = false;

  /**
   * 是否已完成执行
   */
  private boolean completed = false;

  /**
   * 是否已中止执行
   */
  private boolean aborted = false;

  /**
   * 执行错误信息
   */
  private Exception error;

  /**
   * 节点执行集合
   */
  private Map<String, NodeExecution> nodeExecutions = new HashMap<>();

  /**
   * 标记图执行已开始
   */
  public void start() {
    if (started) {
      throw new RuntimeException("Graph execution already started");
    }
    started = true;
  }

  /**
   * 标记图执行已完成
   */
  public void complete() {
    if (!started) {
      throw new RuntimeException("Cannot complete execution that hasn't started");
    }
    if (completed) {
      throw new RuntimeException("Graph execution already completed");
    }
    completed = true;
  }

  /**
   * 中止图执行
   *
   * @param reason 中止原因
   */
  public void abort(String reason) {
    aborted = true;
    error = new RuntimeException("Aborted: " + reason);
  }

  /**
   * 标记图执行失败
   *
   * @param error 错误信息
   */
  public void fail(Exception error) {
    this.error = error;
    completed = true;
  }

  /**
   * 获取或创建节点执行实体
   *
   * @param nodeId 节点ID
   * @return 节点执行实体
   */
  public NodeExecution getOrCreateNodeExecution(String nodeId) {
    return nodeExecutions.computeIfAbsent(nodeId, id -> new NodeExecution().setNodeId(nodeId));
  }

  /**
   * 检查执行是否正在运行
   *
   * @return 是否正在运行
   */
  public boolean isRunning() {
    return started && !completed && !aborted;
  }

  /**
   * 检查执行是否有错误
   *
   * @return 是否有错误
   */
  public boolean hasError() {
    return error != null;
  }

  /**
   * 获取错误信息
   *
   * @return 错误信息
   */
  public String getErrorMessage() {
    if (error == null) {
      return null;
    }
    return error.getMessage();
  }
}

