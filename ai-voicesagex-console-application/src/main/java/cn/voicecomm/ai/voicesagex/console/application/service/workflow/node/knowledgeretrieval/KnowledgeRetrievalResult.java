package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文档内容实体类
 *
 * @author gaox
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KnowledgeRetrievalResult {

  /**
   * 内容
   */
  private String content;

  /**
   * 标题
   */
  private String title;

  /**
   * URL链接
   */
  private String url;

  /**
   * 图标
   */
  private String icon;

  /**
   * 元数据
   */
  private Metadata metadata;

  /**
   * 元数据实体类
   *
   * @author gaox
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class Metadata {

    /**
     * 来源
     */
    private String source;

    /**
     * 内容ID
     */
    private String contentId;

    /**
     * 内容长度
     */
    private Integer contentLen;

    /**
     * 内容哈希值
     */
    private String contentHash;

    /**
     * 分数
     */
    private Double score;

    /**
     * 文档ID
     */
    private Integer documentId;

    /**
     * 知识库ID
     */
    private Integer knowledgeBaseId;
  }
}
