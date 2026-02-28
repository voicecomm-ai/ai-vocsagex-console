package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
   * 重试配置类
   */
  @Data
  @Accessors(chain = true)
  public class RetryConfig implements Serializable {

    /**
     * 是否启用重试
     */
    private boolean retry_enabled;

    /**
     * 最大重试次数
     */
    private int max_retries;

    /**
     * 重试间隔（毫秒）
     */
    private int retry_interval;
  }