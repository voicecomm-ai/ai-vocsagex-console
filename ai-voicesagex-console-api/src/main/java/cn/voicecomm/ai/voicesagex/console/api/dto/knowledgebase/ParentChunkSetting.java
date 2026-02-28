package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 父子分段父段设置
 */
@Data
public class ParentChunkSetting implements Serializable {

  /**
   * 是否启用全文分段
   */
  private Boolean fulltext;

  /**
   * 分段标识符
   */
  private String chunkIdentifier;

  /**
   * 分段最大长度
   */
  @Size(min = 50, max = 2000, message = "分段最大长度为2000，最小为50")
  private Integer chunkSize;
}
