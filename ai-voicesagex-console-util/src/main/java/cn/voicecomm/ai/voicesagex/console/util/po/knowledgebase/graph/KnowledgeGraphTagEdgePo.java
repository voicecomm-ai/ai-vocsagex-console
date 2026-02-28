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
 * 图知识库本体关系
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_graph_tag_edge")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeGraphTagEdgePo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -7014615414813087772L;

  /**
   * 主键id
   */
  @TableId(value = "\"tag_edge_id\"", type = IdType.AUTO)
  private Integer tagEdgeId;
  /**
   * 知识库id
   */
  @TableField(value = "\"space_id\"")
  private Integer spaceId;
  /**
   * 类型  0：Tag；1：Edge
   */
  @TableField(value = "\"type\"")
  private Integer type;
  /**
   * 名称
   */
  @TableField(value = "\"tag_name\"")
  private String tagName;
  /**
   * 描述
   */
  @TableField(value = "\"description\"")
  private String description;
  /**
   * ttlCol 字段
   */
  @TableField(value = "\"ttl_col\"")
  private String ttlCol;
  /**
   * 过期时间（小时为单位）
   */
  @TableField(value = "\"ttl_duration\"")
  private Integer ttlDuration;
  /**
   * 是否删除 false 否 true 删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

}
