package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@TableName("knowledge_graph_pattern_edge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GraphPatternEdgePo extends BaseAuditPo {

  @TableField(value = "\"space_id\"")
  private Integer spaceId;


  @TableField(value = "\"source_value\"")
  private String source;
  @TableField("\"target_value\"")
  private String target;

  @TableField("\"value\"")
  private String value;

  /**
   * 是否删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

}
