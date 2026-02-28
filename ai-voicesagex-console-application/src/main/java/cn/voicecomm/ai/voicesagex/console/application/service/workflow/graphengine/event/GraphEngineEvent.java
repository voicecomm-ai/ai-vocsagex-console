package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 图引擎事件基类
 */
@Data
@Accessors(chain = true)
@SuperBuilder
public class GraphEngineEvent {
    // 基类可以包含通用属性和方法
}