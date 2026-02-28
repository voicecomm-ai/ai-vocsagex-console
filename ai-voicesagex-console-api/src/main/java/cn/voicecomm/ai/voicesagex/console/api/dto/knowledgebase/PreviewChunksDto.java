package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy;
import lombok.Data;

@Data
public class PreviewChunksDto {

  /**
   * 文档ID
   */
  private Integer documentId;
  /**
   * 分段策略
   */
  private ChunkingStrategy chunkingStrategy;
  /**
   * 分段设置
   */
  private ChunkSetting chunkSetting;
  /**
   * 文本预处理规则
   */
  private CleanerSetting cleanerSetting;
  /**
   * QA设置
   */
  private QaSetting qaSetting;
  /**
   * 父子分段，父段设置
   */
  private ParentChunkSetting parentChunkSetting;
  /**
   * 父子分段，子段设置
   */
  private ChildChunkSetting childChunkSetting;
}
