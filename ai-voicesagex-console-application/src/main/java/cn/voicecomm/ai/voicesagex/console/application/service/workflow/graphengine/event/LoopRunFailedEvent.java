package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 循环运行失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LoopRunFailedEvent extends BaseLoopEvent {

  private LocalDateTime startAt;
  private Map<String, Object> inputs;
  private Map<String, Object> outputs;
  private Map<String, Object> metadata;
  private Integer steps = 0;
  private String error;

}