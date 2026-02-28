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

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("role")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RolePo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -490393950493989785L;

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 角色名称
   */
  @TableField(value = "role_name")
  private String roleName;

  /**
   * 部门id
   */
  @TableField(value = "dept_id")
  private Integer deptId;

  /**
   * 描述
   */
  @TableField(value = "description")
  private String description;

  /**
   * 是否管理员（0 否 1 是）
   */
  @TableField(value = "is_admin")
  private Boolean isAdmin;

  /**
   * 类型（0 内置 1 自定义）
   */
  @TableField(value = "type")
  private Integer type;
  /**
   * 数据权限 1本部门（含下级）2本部门 3仅本人
   */
  @TableField(value = "data_permission")
  private Integer dataPermission;

}
