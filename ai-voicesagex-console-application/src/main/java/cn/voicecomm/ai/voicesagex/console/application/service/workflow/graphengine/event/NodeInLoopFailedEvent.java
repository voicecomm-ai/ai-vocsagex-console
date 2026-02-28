package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 循环中节点失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeInLoopFailedEvent extends BaseNodeEvent {

  private String error;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}