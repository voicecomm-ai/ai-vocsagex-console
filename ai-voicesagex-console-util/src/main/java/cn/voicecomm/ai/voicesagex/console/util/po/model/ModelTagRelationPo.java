package cn.voicecomm.ai.voicesagex.console.util.po.model;

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
 * 模型-标签关系
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_tag_relation")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelTagRelationPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -2676028774315456541L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  /**
   * 分类id
   */
  @TableField(value = "\"category_id\"")
  private Integer categoryId;
  /**
   * 标签id
   */
  @TableField(value = "\"tag_id\"")
  private Integer tagId;
  /**
   * 模型id
   */
  @TableField(value = "\"model_id\"")
  private Integer modelId;

}
