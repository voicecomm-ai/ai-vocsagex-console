package cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph;

import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 图知识库本体关系属性
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_graph_tag_edge_property")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgePropertyPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -2066860055800893096L;

  /**
   * 主键id
   */
  @TableId(value = "\"property_id\"", type = IdType.AUTO)
  private Integer propertyId;
  /**
   * Tag/Edge id
   */
  @TableField(value = "\"tag_edge_id\"")
  private Integer tagEdgeId;
  /**
   * 类型  0：Tag；1：Edge
   */
  @TableField(value = "\"type\"")
  private Integer type;
  /**
   * 名称
   */
  @TableField(value = "\"property_name\"")
  private String propertyName;
  /**
   * 属性类型
   */
  @TableField(value = "\"property_type\"")
  private String propertyType;
  /**
   * 额外信息
   */
  @TableField(value = "\"extra\"")
  private String extra;
  /**
   * 是否必填：0：必填；1：不必填
   */
  @TableField(value = "\"tag_required\"")
  private Integer tagRequired;
  /**
   * 默认值
   */
  @TableField(value = "\"default_value\"")
  private String defaultValue;
  /**
   * 是否删除 false 否 true 删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

}
