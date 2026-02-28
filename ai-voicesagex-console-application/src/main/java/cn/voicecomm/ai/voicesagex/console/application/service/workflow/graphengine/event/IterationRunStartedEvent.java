package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 迭代运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class IterationRunStartedEvent extends BaseIterationEvent {

  private LocalDateTime startAt;
  private Map<String, Object> inputs;
  private Map<String, Object> metadata;
  private String predecessorNodeId;

}