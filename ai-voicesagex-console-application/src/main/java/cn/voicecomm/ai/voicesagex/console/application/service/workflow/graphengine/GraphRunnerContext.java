package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;


import cn.hutool.core.collection.ConcurrentHashSet;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.WorkflowNodeExecutionsPo;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;

/**
 * 图运行时上下文
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@Data
public class GraphRunnerContext {

  // 用于跟踪节点完成状态的映射
  private final Map<String, CompletableFuture<NodeRunResult>> nodeCompletionFutures = new ConcurrentHashMap<>();
  private final Map<String, WorkflowNodeExecutionsPo> nodeExecutionRecords = new ConcurrentHashMap<>();
  private final Set<String> hasExecutedNodeIds = new ConcurrentHashSet<>();
  // 最终输出
  private final Map<String, TimeValue<Object>> outputMapWithTime = new ConcurrentHashMap<>();

  private final AtomicInteger executeSteps = new AtomicInteger(0);
  private final AtomicLong total_tokens = new AtomicLong(0);
  private final AtomicInteger loop_index = new AtomicInteger(0);

  // 添加中断标志，用于控制工作流整体停止执行
  private final AtomicBoolean interrupted = new AtomicBoolean(false);
  private String error;

  public record TimeValue<T>(T value, long insertTime) {

    // 首次创建
    static <T> TimeValue<T> first(T value) {
      return new TimeValue<>(value, System.nanoTime());
    }

    // 仅更新 value，保留原 insertTime
    TimeValue<T> withUpdatedValue(T newValue) {
      return new TimeValue<>(newValue, this.insertTime);
    }
  }

  /**
   * 设置中断标志
   */
  public void interrupt() {
    this.interrupted.set(true);
  }

  /**
   * 检查是否已被中断
   *
   * @return 是否中断
   */
  public boolean isInterrupted() {
    return this.interrupted.get();
  }
}

