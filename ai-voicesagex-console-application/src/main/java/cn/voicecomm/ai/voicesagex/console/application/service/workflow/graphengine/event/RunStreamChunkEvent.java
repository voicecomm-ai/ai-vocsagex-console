package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 运行流块事件
 */
@Data
@SuperBuilder
public class RunStreamChunkEvent {

  /**
   * 块内容
   */
  private String chunkContent;

  /**
   * 来源变量选择器
   */
  private List<String> fromVariableSelector;
}