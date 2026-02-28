package cn.voicecomm.ai.voicesagex.console.util.po.user;

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

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "department")
public class DepartmentPo extends BaseAuditPo {

  /**
   * id
   */
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 部门名称
   */
  @TableField(value = "department_name")
  private String departmentName;

  /**
   * 部门级别
   */
  @TableField(value = "\"level\"")
  private Integer level;

  /**
   * 备注
   */
  @TableField(value = "remark")
  private String remark;

  /**
   * 父级id
   */
  @TableField(value = "parent_id")
  private Integer parentId;
}
