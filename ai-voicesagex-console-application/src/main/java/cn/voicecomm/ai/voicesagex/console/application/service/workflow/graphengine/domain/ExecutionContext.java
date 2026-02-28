package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Immutable value object containing the context for a graph execution.
 * <p>
 * This encapsulates all the contextual information needed to execute a workflow,
 * keeping it separate from the mutable execution state.
 *
 * @author gaox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ExecutionContext {

  private String tenantId;

  private String appId;

  private String workflowId;

  private String userId;

  private int callDepth;

  private int maxExecutionSteps;

  private int maxExecutionTime;

  /**
   * Validate execution context parameters.
   */
  public void validate() {
    if (callDepth < 0) {
      throw new IllegalArgumentException("Call depth must be non-negative");
    }
    if (maxExecutionSteps <= 0) {
      throw new IllegalArgumentException("Max execution steps must be positive");
    }
    if (maxExecutionTime <= 0) {
      throw new IllegalArgumentException("Max execution time must be positive");
    }
  }
}

