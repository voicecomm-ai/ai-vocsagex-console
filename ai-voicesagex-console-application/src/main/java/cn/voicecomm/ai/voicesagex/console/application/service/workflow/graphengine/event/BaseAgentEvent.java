package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基代理事件类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BaseAgentEvent extends GraphEngineEvent {
  // 代理事件的通用属性
}