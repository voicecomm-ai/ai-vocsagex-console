package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行异常事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeRunExceptionEvent extends BaseNodeEvent {

  private String error;


}