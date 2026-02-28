package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
   * 视觉配置类
   */
  @Data
  @Accessors(chain = true)
  public class Vision implements Serializable {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 配置项
     */
    private Configs configs;
  }