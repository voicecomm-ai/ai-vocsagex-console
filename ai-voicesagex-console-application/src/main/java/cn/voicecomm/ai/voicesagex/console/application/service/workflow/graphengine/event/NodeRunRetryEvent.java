package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行重试事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeRunRetryEvent extends NodeRunStartedEvent {

  private String error;
  private Integer retryIndex;
  private LocalDateTime startAt;


}