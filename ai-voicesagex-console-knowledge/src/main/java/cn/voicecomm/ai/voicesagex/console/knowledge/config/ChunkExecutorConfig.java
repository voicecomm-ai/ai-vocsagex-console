package cn.voicecomm.ai.voicesagex.console.knowledge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ChunkExecutorConfig {


    @Value("${task.chunk.pool.core-size}")
    private int corePoolSize;

    @Value("${task.chunk.pool.max-size}")
    private int maxPoolSize;

    @Value("${task.chunk.pool.queue-capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor chunkExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("process_chunk-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
