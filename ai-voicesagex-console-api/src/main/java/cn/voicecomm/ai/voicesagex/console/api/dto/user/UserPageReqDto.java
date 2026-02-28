package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageReqDto extends PagingReqDto {

  @Serial
  private static final long serialVersionUID = -7500305675336455801L;

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
