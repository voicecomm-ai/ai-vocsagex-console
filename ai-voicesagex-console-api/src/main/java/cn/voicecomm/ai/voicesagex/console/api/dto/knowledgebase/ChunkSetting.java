package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 分段设置
 */
@Data
public class ChunkSetting implements Serializable {

  /**
   * 分段标识符
   */
  @Size(min = 50, max = 2000, message = "分段最大长度为2000，最小为50")
  private String chunkIdentifier;
  /**
   * 分段最大长度
   */
  private Integer chunkSize;
  /**
   * 分段重叠长度
   */
  private Integer chunkOverlap;
}
