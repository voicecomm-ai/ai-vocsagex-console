package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.LLMUsage;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * 图运行时状态类
 * 用于存储工作流或聊天流执行过程中的运行时状态信息
 */
@Data
@Builder
public class GraphRuntimeState {
    
    /**
     * 变量池
     * 用于存储执行过程中产生的各种变量
     */
    private VariablePool variablePool;
    
    /**
     * 开始时间
     * 记录工作流开始执行的时间戳
     */
    private Long startAt;
    
    /**
     * 总令牌数
     * 累计执行过程中消耗的总令牌数量
     */
    private Long totalTokens = 0L;
    
    /**
     * LLM使用情况
     * 记录大语言模型的使用统计信息
     */
    private LLMUsage llmUsage;
    
    /**
     * 输出结果
     * 存储工作流或聊天流执行完成后的最终输出值
     * 注意：由于该字段类型为Map<String, Object>，序列化和反序列化后值可能不一致
     */
    private Map<String, Object> outputs;
    
    /**
     * 节点运行步数
     * 记录已执行的节点步数
     */
    private Integer nodeRunSteps = 0;
    
    /**
     * 节点运行状态
     * 记录节点的运行状态信息
     */
    private RuntimeRouteState nodeRunState;
}