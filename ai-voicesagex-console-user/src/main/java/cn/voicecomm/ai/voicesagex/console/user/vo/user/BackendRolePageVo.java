package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import cn.voicecomm.ai.voicesagex.console.util.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BackendRolePageVo extends BaseVo {

  @Serial
  private static final long serialVersionUID = 2063961897657897453L;

  /**
   * id
   */
  private Integer id;

  /**
   * 角色
   */
  private List<Integer> roleIds;

  /**
   * 角色名称
   */
  private String roleName;

  /**
   * 描述
   */
  private String description;

  /**
   * 用户数量
   */
  private Integer userCount;


  /**
   * 操作人
   */
  private String updateByName;

}
