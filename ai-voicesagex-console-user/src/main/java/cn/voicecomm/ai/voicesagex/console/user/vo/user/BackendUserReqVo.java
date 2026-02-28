package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BackendUserReqVo {

  /**
   * id
   */
  @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
  private Integer id;

  /**
   * 角色id
   */
  @NotNull(message = "角色不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private Integer roleId;

  /**
   * 组织id
   */
  private Integer deptId;

  /**
   * 账号
   */
  @Size(max = 50, message = "账号不超过50个字", groups = {AddGroup.class})
  private String account;

  /**
   * 用户名称
   */
  @Size(max = 50, message = "用户名称不超过50个字", groups = {AddGroup.class, UpdateGroup.class})
  private String username;

  /**
   * 密码
   */
  private String password;

  /**
   * 状态（0 正常 1 禁用 2 删除）
   */
  @NotNull(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private Byte status;

  /**
   * 组织id
   */
  private String application;

  /**
   * 手机号
   */
  private String phone;


}
