package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 循环运行下一步事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class LoopRunNextEvent extends BaseLoopEvent {

  private Integer index;
  private Object preLoopOutput;

}