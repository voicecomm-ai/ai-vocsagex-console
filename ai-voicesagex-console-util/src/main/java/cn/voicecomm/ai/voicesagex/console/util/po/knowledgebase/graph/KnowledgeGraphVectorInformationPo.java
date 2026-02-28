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
 * 图谱可视化管理
 *
 * @author ryc
 * @date 2025-09-16 14:52:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_graph_vector_information")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class KnowledgeGraphVectorInformationPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -3034812472816819222L;
  /**
   * 主键id
   */
  @TableId(value = "\"vector_job_id\"", type = IdType.NONE)
  private String vectorJobId;
  /**
   * 知识库id
   */
  @TableField(value = "\"space_id\"")
  private Integer spaceId;

}
