package cn.voicecomm.ai.voicesagex.console.util.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseAuditPo extends BasePo {

  @Serial
  private static final long serialVersionUID = -6218419054145547924L;

  /**
   * 创建人id
   */
  @TableField(value = "create_by", fill = FieldFill.INSERT)
  private Integer createBy;
  /**
   * 更新人id
   */
  @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
  private Integer updateBy;

}
