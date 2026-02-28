package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class KnowledgeBaseDocumentDto implements Serializable {

  /**
   * 文档ID
   */
  private Integer id;

  /**
   * 文档名称
   */
  private String name;

  /**
   * 知识库ID
   */
  private Integer knowledgeBaseId;

  /**
   * 分段策略
   */
  private String chunkingStrategy;

  /**
   * 唯一文件名
   */
  private String uniqueName;

  /**
   * 文档解析状态：WAIT IN_PROGRESS SUCCESS FAILED
   */
  private String processStatus;

  /**
   * 字数统计
   */
  private Long wordCount;

  /**
   * 文档状态：ENABLE DISABLE
   */
  private String status;

  /**
   * 是否归档
   */
  private Boolean isArchived;

  /**
   * 文档分块内容
   */
  private String chunks;

  /**
   * 文档预览分块内容
   */
  private String previewChunks;

  /**
   * 文档处理失败原因
   */
  private String processFailedReason;

  /**
   * 被禁用的分段primary key列表
   */
  private List<Integer> disabledPrimaryKeys;

  /**
   * 被编辑的分段primary key列表
   */
  private List<Integer> editedPrimaryKeys;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createTime;

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updateTime;
}
