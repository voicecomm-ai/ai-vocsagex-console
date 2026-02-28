package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName DocumentInformationPo
 * @Author wangyang
 * @Date 2025/9/15 15:57
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_document_information")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeDocumentInformationPo extends BaseAuditPo {

  /**
   * '文档ID'
   */
  @TableId(value = "\"document_id\"", type = IdType.AUTO)
  private Integer documentId;
  /**
   * '关联抽取任务id'
   */
  @TableField(value = "\"extraction_id\"")
  private Integer extractionId;


  /**
   * '文档名称'
   */
  @TableField(value = "\"document_name\"")
  private String documentName;

  /**
   * '文档名称'
   */
  @TableField(value = "\"file_format\"")
  private String fileFormat;

  /**
   * '文档名称'
   */
  @TableField(value = "\"total_pages\"")
  private Integer totalPages;

  @TableField(value = "\"analysis\"")
  private Integer analysis;

  /**
   * ''抽取状态''
   */
  @TableField(value = "\"document_status\"")
  private Integer documentStatus;

  /**
   * '''chunk总数量'''
   */
  @TableField(value = "\"chunk_size\"")
  private Integer chunkSize;
  /**
   * '文件路径'
   */
  @TableField(value = "\"file_path\"")
  private String filePath;

  @TableField(value = "\"job_id\"")
  private String jobId;

  /**
   * 是否删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

  @TableField(exist = false)
  private List<String> sheetNames;


}
