package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.event;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.LLMUsage;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 模型调用完成事件
 */
@Data
@SuperBuilder
public class ModelInvokeCompletedEvent{
    /**
     * 文本
     */
    private String text;
    
    /**
     * 使用情况
     */
     private LLMUsage usage;
    
    /**
     * 完成原因
     */
    private String finishReason;
}