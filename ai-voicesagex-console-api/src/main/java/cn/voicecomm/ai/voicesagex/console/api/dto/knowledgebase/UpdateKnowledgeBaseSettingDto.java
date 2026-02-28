package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateKnowledgeBaseSettingDto implements Serializable {

  /**
   * 知识库id
   */
  Integer id;
  /**
   * Embedding模型ID
   */
  Integer embeddingModelId;
  /**
   * 检索策略
   */
  SearchStrategy searchStrategy;
  /**
   * 是否启用Rerank模型
   */
  Boolean enableRerank;
  /**
   * Rerank模型ID
   */
  Integer rerankModelId;
  /**
   * Top K
   */
  Integer topK;
  /**
   * 是否启用Score
   */
  Boolean enableScore;
  /**
   * 是否启用多模态
   */
  Boolean enableMultimodal;
  /**
   * Score阈值
   */
  Float score;
  /**
   * 混合检索权重设置——语义
   */
  Float hybridSearchSemanticMatchingWeight;
  /**
   * 混合检索权重设置——关键词
   */
  Float hybridSearchKeywordMatchingWeight;
  /**
   * 知识库名称
   */
  @NotBlank(message = "知识库名称不能为空")
  String name;
  /**
   * 知识库描述
   */
  @Size(max = 400, message = "知识库描述不能超过400个字符")
  String description;
}
