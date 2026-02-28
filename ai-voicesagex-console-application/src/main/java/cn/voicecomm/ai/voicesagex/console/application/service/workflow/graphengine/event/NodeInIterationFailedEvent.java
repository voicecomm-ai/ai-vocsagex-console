package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 迭代中节点失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeInIterationFailedEvent extends BaseNodeEvent {

  private String error;

}