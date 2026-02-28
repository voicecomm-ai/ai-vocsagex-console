package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import lombok.Data;

import java.io.Serializable;

/**
 * Q&A分段设置
 */
@Data
public class QaSetting implements Serializable {

  /**
   * 是否启用QA分段
   */
  private Boolean enable;

  /**
   * 语言
   */
  private String language;

  /**
   * 模型ID
   */
  private Integer modelId;

}
