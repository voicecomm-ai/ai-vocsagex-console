package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Component
public class SseEmitterManager {

    // 使用 ConcurrentHashMap 线程安全地管理所有 SSE 连接
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 注册一个新的 SSE 连接
     */
    public SseEmitter register(String flowId) {
        SseEmitter emitter = new SseEmitter(1800_000L); // 30分钟超时

        // 超时、完成、错误时从 map 中移除
        emitter.onTimeout(() -> emitters.remove(flowId));
        emitter.onCompletion(() -> emitters.remove(flowId));
        emitter.onError(e -> emitters.remove(flowId));

        emitters.put(flowId, emitter);
        return emitter;
    }

    /**
     * 根据 flowId 发送事件
     */
    public void sendEvent(String flowId, SseEventBuilder eventData) {
        if (StrUtil.isBlank(flowId)){
            return;
        }
        SseEmitter emitter = emitters.get(flowId);
        if (emitter != null) {
            try {
                emitter.send(eventData);
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(flowId);
            }
        }
    }

    /**
     * 关闭某个连接
     */
    public void complete(String flowId) {
        SseEmitter emitter = emitters.get(flowId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(flowId);
        }
    }

    /**
     * 广播（可选）
     */
    public void broadcast(Object data) {
        emitters.values().removeIf(emitter -> {
            try {
                emitter.send(data);
                return false;
            } catch (IOException e) {
                emitter.completeWithError(e);
                return true; // 移除
            }
        });
    }
}