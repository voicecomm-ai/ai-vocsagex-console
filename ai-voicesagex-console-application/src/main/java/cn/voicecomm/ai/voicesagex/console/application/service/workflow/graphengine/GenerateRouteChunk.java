package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import lombok.Data;
import lombok.Getter;

/**
 * 生成路由块基类
 * 定义生成路由的基本结构
 */
@Data
public class GenerateRouteChunk {
    /**
     * 路由块类型枚举
     */
    @Getter
    public enum ChunkType {
        VAR("var"),
        TEXT("text");

        private final String value;

        ChunkType(String value) {
            this.value = value;
        }

    }

    /**
     * 生成路由块类型
     */
    private ChunkType type;
}
