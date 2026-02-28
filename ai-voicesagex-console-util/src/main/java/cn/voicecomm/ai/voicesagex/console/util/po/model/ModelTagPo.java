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
 * 模型标签
 *
 * @author ryc
 * @date 2025-06-03 17:22:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_tag")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelTagPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = 3435764053343519193L;

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
   * 标签名称
   */
  @TableField(value = "\"name\"")
  private String name;

}
