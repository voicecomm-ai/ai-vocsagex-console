package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
public class NodeRunFailedEvent extends BaseNodeEvent {

  private String error;




}