package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.KnowledgeBaseType;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.SearchStrategy;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseDto implements Serializable {

  private Integer id;

  private String name;

  private String description;

  private SearchStrategy searchStrategy;

  private List<KnowledgeBaseTagDto> tags;

  /**
   * 知识库类型（TRAD-传统，GRAPH-图谱）
   */
  private KnowledgeBaseType type;

  /**
   * Embedding模型
   */
  private Integer embeddingModelId;

  /**
   * 是否启用Rerank模型
   */
  private Boolean enableRerankModel;

  /**
   * Rerank模型ID
   */
  private Integer rerankModelId;

  /**
   * TOP K
   */
  private Integer topK;

  /**
   * Score
   */
  private Float score;

  /**
   * 是否启用Score
   */
  private Boolean enableScore;

  /**
   * 混合检索语义匹配权重
   */
  private Float hybridSearchSemanticMatchingWeight;

  /**
   * 混合检索关键词匹配权重
   */
  private Float hybridSearchKeywordMatchingWeight;

  /**
   * 知识库是否为空（没有文档）
   */
  private Boolean isEmpty;

  /**
   * 文档数量
   */
  private Integer documentCount;

  /**
   * 应用数量
   */
  private Integer applicationCount;

  /**
   * 文档字符数
   */
  private Long worldCount;


  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;
}
