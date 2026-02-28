package cn.voicecomm.ai.voicesagex.console.knowledge.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author fulin
 */
@Configuration
@ConfigurationProperties(prefix = "nebula")
@EnableConfigurationProperties(NebulaGraphProperties.class)
@Data
public class NebulaGraphProperties {

    /**
     * NebulaGraph 服务地址列表
     */
    private List<String> hosts;
    /**
     * 登录用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 默认空间
     */
    private String space;
    /**
     * 连接池配置
     */
    private PoolConfig poolConfig;



    @Data
    public static class PoolConfig {
        private int minConnsSize;           // 最小连接数
        private int maxConnsSize;           // 最大连接数
        private int timeout;                // 连接超时时间
        private int idleTime;               // 最大空闲时间
        private int intervalIdle;           // 空闲连接检测间隔时间
        private int waitTime;               // 获取连接的最大等待时间
        private double minClusterHealthRate; // 最小集群健康率
        private boolean enableSsl;          // 是否启用 SSL
    }
}
