package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class SaveAndProcessBaseDto implements Serializable {

  /**
   * 知识库ID
   */
  private Integer knowledgeBaseId;
  /**
   * 知识库类型
   */
  private String type;
  /**
   * 分段策略
   */
  private ChunkingStrategy chunkingStrategy;
  /**
   * 是否启用QA分段
   */
  private Boolean enableQaChunk;
  /**
   * 是否启用多模态
   */
  private Boolean enableMultimodal;
  /**
   * embedding模型ID
   */
  private Integer embeddingModelId;
  /**
   * 检索策略
   */
  private String searchStrategy;
  /**
   * 是否启用rerank
   */
  private Boolean enableRerank;
  /**
   * rerank模型ID
   */
  private Integer rerankModelId;
  /**
   * top k
   */
  private Integer topK;
  /**
   * 是否启用score阈值
   */
  private Boolean enableScore;
  /**
   * score
   */
  private Float score;
  /**
   * 语义权重
   */
  private Float hybridSearchSemanticMatchingWeight;
  /**
   * 关键词权重
   */
  private Float hybridSearchKeywordMatchingWeight;
  private String name;
  private List<Integer> documentIds;
  private PreviewChunksDto previewParams;
}
