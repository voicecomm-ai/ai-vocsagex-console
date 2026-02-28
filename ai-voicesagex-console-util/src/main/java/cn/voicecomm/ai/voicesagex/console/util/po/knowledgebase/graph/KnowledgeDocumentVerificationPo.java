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

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_document_verification")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeDocumentVerificationPo extends BaseAuditPo {

  /**
   * '文档校验 id'
   */
  @TableId(value = "\"verification_id\"", type = IdType.AUTO)
  private Integer verificationId;

  @TableField(value = "\"chunk_id\"")
  private Integer chunkId;


  /**
   * ''所属文档ID' '
   */
  @TableField(value = "\"document_id\"")
  private Integer documentId;

  /**
   * '主体类型'
   */
  @TableField(value = "\"subject\"")
  private String subject;

  /**
   * ''主体名称''
   */
  @TableField(value = "\"subject_tag_name\"")
  private String subjectTagName;


  /**
   * '主体类型'
   */
  @TableField(value = "\"object\"")
  private String object;


  /**
   * ''关系/属性''
   */
  @TableField(value = "\"edge_type\"")
  private String edgeType;

  /**
   * ''主体名称''
   */
  @TableField(value = "\"object_tag_name\"")
  private String objectTagName;


  /**
   * 校验状态，0初始状态，1表示已校验,2已入图
   */
  @TableField(value = "\"verification_status\"")
  private Integer verificationStatus;

  @TableField(value = "\"type\"")
  private Integer type;

  @TableField(value = "\"property_type\"")
  private String propertyType;

  @TableField(value = "\"deleted\"")
  private Boolean deleted;
}
