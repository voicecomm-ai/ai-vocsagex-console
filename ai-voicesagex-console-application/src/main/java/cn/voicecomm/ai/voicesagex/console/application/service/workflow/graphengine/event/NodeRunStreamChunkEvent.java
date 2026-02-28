package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 节点运行流块事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class NodeRunStreamChunkEvent extends BaseNodeEvent {

  private String chunkContent;
  private List<String> fromVariableSelector;

}