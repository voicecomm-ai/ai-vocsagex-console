package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BackendRoleDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -2561500564280649050L;

  private Integer id;

  /**
   * 部门id
   */
  private Integer deptId;

  /**
   * 部门名称
   */
  private String deptName;

  /**
   * 数据权限 1本部门（含下级）2本部门 3仅本人
   */
  private Integer dataPermission;

  /**
   * 角色名称
   */
  private String roleName;

  /**
   * 描述
   */
  private String description;

  /**
   * 是否管理员（0 否 1 是）
   */
  private Boolean isAdmin;

  /**
   * 类型（0 内置 1 自定义）
   */
  private Integer type;

  /**
   * 菜单id列表
   */
  private List<Integer> menuIds;

  private List<BackendMenuDto> menus;

  /**
   * 用户数量
   */
  private Integer userCount;

  private String updateByName;

  /**
   * 部门及其上级id 用于回显
   */
  private List<Integer> deptIdList;

}
