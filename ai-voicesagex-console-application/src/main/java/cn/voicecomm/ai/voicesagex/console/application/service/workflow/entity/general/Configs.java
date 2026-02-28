package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
   * 视觉配置项类
   */
  @Data
  @Accessors(chain = true)
  public class Configs implements Serializable {

    /**
     * 细节级别
     */
    private String detail;

    /**
     * 变量选择器列表
     */
    private List<String> variable_selector;
  }