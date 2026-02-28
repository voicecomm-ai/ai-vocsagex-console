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
public class BackendUserDto extends BaseAuditDto {

  @Serial
  private static final long serialVersionUID = -2419396186402647414L;

  private Integer id;

  /**
   * 账号
   */
  private String account;

  /**
   * 组织id
   */
  private Integer deptId;

  /**
   * 用户名称
   */
  private String username;

  /**
   * 密码
   */
  private String password;

  /**
   * 状态（0 正常 1 禁用 2 删除 3 锁定 4 禁用中）
   */
  private Byte status;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 账号是否过期
   */
  private boolean isAccountExpired;

  /**
   * 账号是否锁定
   */
  private boolean isAccountLocked;

  /**
   * 密码是否过期
   */
  private boolean isCredentialsExpired;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 角色
   */
  private String roleName;

  private String updateByName;

  /**
   * 组织名称
   */
  private String deptName;


  /**
   * 数据权限 1本部门（含下级）2本部门 3仅本人
   */
  private Integer dataPermission;

  /**
   * 部门及其上级id 用于回显
   */
  private List<Integer> deptIdList;
}
