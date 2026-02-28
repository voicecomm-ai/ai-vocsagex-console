package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 图运行部分成功事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GraphRunPartialSucceededEvent extends BaseGraphEvent {

  private Integer exceptionsCount;
  private Map<String, Object> outputs;


}