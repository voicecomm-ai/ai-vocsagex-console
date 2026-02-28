package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 迭代运行成功事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class IterationRunSucceededEvent extends BaseIterationEvent {

  private LocalDateTime startAt;
  private Map<String, Object> inputs;
  private Map<String, Object> outputs;
  private Map<String, Object> metadata;
  private Integer steps = 0;
  private Map<String, Double> iterationDurationMap;

}