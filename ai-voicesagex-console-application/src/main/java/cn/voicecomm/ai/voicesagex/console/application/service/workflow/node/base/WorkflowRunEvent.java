package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base;

import lombok.Builder;
import lombok.Data;

/**
 * @author: gaox
 * @date: 2025/8/21 16:33
 */
@Data
@Builder
public class WorkflowRunEvent {
  private String event;
  private String workflow_run_id;
  private String task_id;
  private Object data;

}
