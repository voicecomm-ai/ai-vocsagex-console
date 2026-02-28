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


@TableName("document_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DocumentConfigPo extends BaseAuditPo {

  @TableId(type = IdType.AUTO, value = "\"id\"")
  protected Integer id;

  @TableField(value = "\"extract_entity_model\"")
  private String extractEntityModel;

  @TableField(value = "\"entity_prompt_require\"")
  private String entityPromptRequire;

  @TableField(value = "\"entity_prompt_other_require\"")
  private String entityPromptOtherRequire;

  @TableField(value = "\"entity_prompt_output\"")
  private String entityPromptOutput;

  @TableField(value = "\"extract_relation_model\"")
  private String extractRelationModel;

  @TableField(value = "\"relation_prompt_require\"")
  private String relationPromptRequire;

  @TableField(value = "\"relation_prompt_other_require\"")
  private String relationPromptOtherRequire;

  @TableField(value = "\"relation_prompt_output\"")
  private String relationPromptOutput;

  @TableField(value = "\"document_id\"")
  private Integer documentId;


}
