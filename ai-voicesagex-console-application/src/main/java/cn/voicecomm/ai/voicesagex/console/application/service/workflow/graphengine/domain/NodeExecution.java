package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.domain;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Entity representing the execution state of a single node.
 * <p>
 * This is a mutable entity that tracks the runtime state of a node
 * during graph execution.
 *
 * @author gaox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NodeExecution {

  /**
   * 节点ID
   */
  private String nodeId;

  /**
   * 节点状态
   */
  private NodeState state = NodeState.UNKNOWN;

  /**
   * 重试次数
   */
  private int retryCount = 0;

  /**
   * 执行ID
   */
  private String executionId;

  /**
   * 错误信息
   */
  private String error;

  /**
   * 标记节点已启动并设置执行ID
   *
   * @param executionId 执行ID
   */
  public void markStarted(String executionId) {
    this.state = NodeState.TAKEN;
    this.executionId = executionId;
  }

  /**
   * 标记节点已被占用
   */
  public void markTaken() {
    this.state = NodeState.TAKEN;
    this.error = null;
  }

  /**
   * 标记节点执行失败并记录错误信息
   *
   * @param error 错误信息
   */
  public void markFailed(String error) {
    this.error = error;
  }

  /**
   * 标记节点被跳过
   */
  public void markSkipped() {
    this.state = NodeState.SKIPPED;
  }

  /**
   * 增加节点重试次数
   */
  public void incrementRetry() {
    this.retryCount++;
  }
}

