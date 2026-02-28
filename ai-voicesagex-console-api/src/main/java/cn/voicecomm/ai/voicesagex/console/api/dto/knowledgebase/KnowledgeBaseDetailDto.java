package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.ChunkingStrategy;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class KnowledgeBaseDetailDto implements Serializable {

  /**
   * 知识库ID
   */
  private Integer id;

  /**
   * 知识库名称
   */
  private String name;

  /**
   * 知识库描述
   */
  private String description;

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
   * QA模型ID
   */
  private Integer qaModelId;

  /**
   * 是否启用多模态
   */
  private Boolean enableMultimodal;

  /**
   * Embedding模型ID
   */
  private Integer embeddingModelId;

  /**
   * 检索策略
   */
  private SearchStrategy searchStrategy;

  /**
   * 是否启用Rerank模型
   */
  private Boolean enableRerank;

  /**
   * Rerank模型ID
   */
  private Integer rerankModelId;

  /**
   * TOP K
   */
  private Integer topK;

  /**
   * score阈值
   */
  private Float score;

  /**
   * 是否启用Score阈值
   */
  private Boolean enableScore;

  /**
   * 混合检索语义匹配权重
   */
  private Float semanticMatchingWeight;

  /**
   * 混合检索关键词匹配权重
   */
  private Float keywordMatchingWeight;

  /**
   * 分段标识符
   */
  private String chunkIdentifier;
  /**
   * 分段最大长度
   */
  private Integer chunkMaxLength;
  /**
   * 分段重叠长度
   */
  private Integer chunkOverlapLength;
  /**
   * 父块用作上下文：PARAGRAPH;FULLTEXT
   */
  private String parentChunkContext;
  /**
   * 段落分段标识符
   */
  private String paragraphChunkIdentifier;
  /**
   * 段落分段最大长度
   */
  private Integer paragraphChunkMaxLength;
  /**
   * 替换掉连续的空格、换行符和制表符
   */
  private Boolean enableTextProcessFirstRule;
  /**
   * 删除所有 URL 和电子邮件地址
   */
  private Boolean enableTextProcessSecondRule;

  /**
   * 创建时间
   */
  private LocalDateTime createTime;

  /**
   * 更新时间
   */
  private LocalDateTime updateTime;
}
