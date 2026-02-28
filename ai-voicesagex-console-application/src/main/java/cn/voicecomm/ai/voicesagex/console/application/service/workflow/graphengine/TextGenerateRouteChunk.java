package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import lombok.Data;

/**
 * 文本生成路由块类
 * 表示文本类型的路由块
 */
@Data
public class TextGenerateRouteChunk extends GenerateRouteChunk {
    /**
     * 生成路由块类型，固定为TEXT
     */
    private final ChunkType type = ChunkType.TEXT;
    
    /**
     * 文本内容
     */
    private String text;
}
