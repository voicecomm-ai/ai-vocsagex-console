package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class BackendRoleReqVo {

  /**
   * id
   */
  @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
  private Integer id;

  /**
   * 角色名称
   */
  @Size(max = 20, message = "角色名称不超过20个字", groups = {AddGroup.class, UpdateGroup.class})
  private String roleName;

  /**
   * 描述
   */
  private String description;

  /**
   * 菜单id列表
   */
  @NotEmpty(message = "菜单不能为空", groups = {AddGroup.class, UpdateGroup.class})
  private List<Integer> menuIds;

  /**
   * 部门id
   */
  private Integer deptId;

  /**
   * 数据权限 1本部门（含下级）2本部门 3仅本人
   */
  private Integer dataPermission;


  /**
   * 通用模板库可见角色对象List
   */
  private List<Integer> commonTemplateRoleIdList;
}
