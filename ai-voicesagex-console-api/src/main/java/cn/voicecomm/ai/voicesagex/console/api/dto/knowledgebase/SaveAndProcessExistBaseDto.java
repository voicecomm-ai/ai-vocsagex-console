package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SaveAndProcessExistBaseDto implements Serializable {

  /**
   * 分段策略
   */
  private ChunkingStrategy chunkingStrategy;

  /**
   * 知识库ID
   */
  private Integer knowledgeBaseId;
  /**
   * 知识库文档ID
   */
  private List<Integer> documentIds;
  /**
   * 分段预览参数
   */
  private PreviewChunksDto previewParams;
}
