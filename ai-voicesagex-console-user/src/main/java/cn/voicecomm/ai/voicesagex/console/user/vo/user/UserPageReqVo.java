package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import cn.voicecomm.ai.voicesagex.console.util.vo.PagingReqVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageReqVo extends PagingReqVo {

  @Serial
  private static final long serialVersionUID = 5494979334445954018L;

  /**
   * 账号/用户名
   */
  private String accountOrName;

  /**
   * 角色
   */
  private Integer roleId;

  /**
   * 状态
   */
  private Integer status;

  /**
   * 部门id
   */
  private Integer deptId;
}
