package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 迭代运行下一步事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class IterationRunNextEvent extends BaseIterationEvent {

  private Integer index;
  private Object preIterationOutput;
  private Double duration;


}