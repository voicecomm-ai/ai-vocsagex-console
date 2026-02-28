package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import lombok.Data;

import java.io.Serializable;

/**
 * 文本预处理规则
 */
@Data
public class CleanerSetting implements Serializable {

  /**
   * 替换掉连续的空格、换行符和制表符
   */
  private Boolean filterBlank;

  /**
   * 删除所有 URL和电子邮件地址
   */
  private Boolean removeUrl;
}
