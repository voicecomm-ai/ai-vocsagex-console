package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 图运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GraphRunStartedEvent extends BaseGraphEvent {

}