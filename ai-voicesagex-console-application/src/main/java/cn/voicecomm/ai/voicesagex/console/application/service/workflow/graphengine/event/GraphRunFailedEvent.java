package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 图运行失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GraphRunFailedEvent extends BaseGraphEvent {

  private String error;
  private Integer exceptionsCount = 0;

}