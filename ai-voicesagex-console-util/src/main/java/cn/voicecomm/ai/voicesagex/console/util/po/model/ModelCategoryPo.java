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
 * 模型分类
 *
 * @author ryc
 * @date 2025-06-03 17:22:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("model_category")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ModelCategoryPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = 1633164067898153008L;

  /**
   * 主键id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  /**
   * 模型名称
   */
  @TableField(value = "\"name\"")
  private String name;
  /**
   * 是否预设 0：否；1：是
   */
  @TableField(value = "\"is_built\"")
  private Boolean isBuilt;

}
