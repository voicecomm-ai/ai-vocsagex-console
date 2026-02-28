package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName KnowledgeChunkInformationPo
 * @Date 2025/9/16 13:30
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_chunk_information")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeChunkInformationPo extends BaseAuditPo {

  /**
   * ''chunk id''
   */
  @TableId(value = "\"chunk_id\"", type = IdType.AUTO)
  private Integer chunkId;

  /**
   * 所属文档ID
   */
  @TableField(value = "\"document_id\"")
  private Integer documentId;

  /**
   * '文本内容'
   */
  @TableField(value = "\"chunk_content\"")
  private String chunkContent;

  /**
   * '任务id  用于删除后取消任务'
   */
  @TableField(value = "\"job_id\"")
  private String jobId;

  /**
   * 'chunk 状态  0 未抽取 1 抽取完成'
   */
  @TableField(value = "\"chunk_status\"")
  private Integer chunkStatus;
  /**
   * 序号
   */
  @TableField(value = "\"chunk_index\"")
  private Integer chunkIndex;

  /**
   * '在原文中的页码'
   */
  @TableField(value = "\"page_number\"")
  private Integer pageNumber;
  /**
   * 是否删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

  /**
   * sheet名称
   */
  @TableField(value = "\"sheet_name\"")
  private String sheetName;
}
