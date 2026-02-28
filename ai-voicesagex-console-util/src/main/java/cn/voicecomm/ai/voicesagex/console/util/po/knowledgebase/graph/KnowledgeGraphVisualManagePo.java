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
 * 图谱可视化管理
 *
 * @author ryc
 * @date 2025-09-16 14:52:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_graph_visual_manage")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeGraphVisualManagePo extends BaseAuditPo {

  /**
   * 主键id
   */
  @TableId(value = "\"visual_id\"", type = IdType.AUTO)
  private Integer visualId;
  /**
   * 知识库id
   */
  @TableField(value = "\"space_id\"")
  private Integer spaceId;
  /**
   * 中心节点名称
   */
  @TableField(value = "\"entity_name\"")
  private String entityName;
  /**
   * 中心点名称
   */
  @TableField(value = "\"centre_vertex\"")
  private String centreVertex;
  /**
   * vertex ID
   */
  @TableField(value = "\"entity_id\"")
  private String entityId;
  /**
   * 是否删除 false 否 true 删除
   */
  @TableField(value = "\"deleted\"")
  private Boolean deleted;

}
