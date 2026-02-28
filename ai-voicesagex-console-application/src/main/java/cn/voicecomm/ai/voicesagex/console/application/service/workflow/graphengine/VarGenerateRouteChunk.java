package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import java.util.List;
import lombok.Data;

/**
 * 变量生成路由块类
 * 表示变量类型的路由块
 */
@Data
public class VarGenerateRouteChunk extends GenerateRouteChunk {
    /**
     * 生成路由块类型，固定为VAR
     */
    private final ChunkType type = ChunkType.VAR;
    
    /**
     * 值选择器
     */
    private List<String> valueSelector;
}
