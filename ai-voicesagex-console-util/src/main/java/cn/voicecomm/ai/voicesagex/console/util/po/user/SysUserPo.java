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
@TableName("sys_user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = -681621272888306804L;

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 账号
   */
  @TableField(value = "account")
  private String account;

  /**
   * 组织id
   */
  @TableField(value = "dept_id")
  private Integer deptId;

  /**
   * 用户名称
   */
  @TableField(value = "username")
  private String username;

  /**
   * 密码
   */
  @TableField(value = "password")
  private String password;

  /**
   * 状态（0 正常 1 禁用 2 删除）
   */
  @TableField(value = "status")
  private Byte status;

  /**
   * 手机号
   */
  @TableField(value = "phone")
  private String phone;

  /**
   * 账号是否过期
   */
  @TableField(value = "is_account_expired")
  private boolean isAccountExpired;

  /**
   * 账号是否锁定
   */
  @TableField(value = "is_account_locked")
  private boolean isAccountLocked;

  /**
   * 密码是否过期
   */
  @TableField(value = "is_credentials_expired")
  private boolean isCredentialsExpired;

}
