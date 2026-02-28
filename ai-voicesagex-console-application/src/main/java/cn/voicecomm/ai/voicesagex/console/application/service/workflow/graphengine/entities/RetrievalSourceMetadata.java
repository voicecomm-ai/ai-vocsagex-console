package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.entities;

import java.util.Map;
import lombok.Data;

/**
 * 检索源元数据
 */
@Data
public class RetrievalSourceMetadata {
  /**
   * 位置
   */
  private Integer position;

  /**
   * 数据集ID
   */
  private String datasetId;

  /**
   * 数据集名称
   */
  private String datasetName;

  /**
   * 文档ID
   */
  private String documentId;

  /**
   * 文档名称
   */
  private String documentName;

  /**
   * 数据源类型
   */
  private String dataSourceType;

  /**
   * 段落ID
   */
  private String segmentId;

  /**
   * 检索器来源
   */
  private String retrieverFrom;

  /**
   * 分数
   */
  private Float score;

  /**
   * 命中次数
   */
  private Integer hitCount;

  /**
   * 字数
   */
  private Integer wordCount;

  /**
   * 段落位置
   */
  private Integer segmentPosition;

  /**
   * 索引节点哈希
   */
  private String indexNodeHash;

  /**
   * 内容
   */
  private String content;

  /**
   * 页码
   */
  private Integer page;

  /**
   * 文档元数据
   */
  private Map<String, Object> docMetadata;

  /**
   * 标题
   */
  private String title;
}
