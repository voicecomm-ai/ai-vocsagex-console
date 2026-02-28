package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleMenuRelationDto;

import java.util.List;

public interface BackendRoleMenuRelationService {

  /**
   * 根据角色id 查询菜单关联关系
   */
  CommonRespDto<List<BackendRoleMenuRelationDto>> getRelationsByRoleId(Integer roleId);


  /**
   * 根据角色id,批量更新菜单关联关系
   */
  CommonRespDto<Void> batchSave(Integer roleId, List<Integer> menuIds);

  /**
   * 根据角色id删除
   */
  CommonRespDto<Void> deleteByRoleId(Integer roleId);

  /**
   * 查询角色ids 是否拥有菜单权限
   */
  CommonRespDto<Boolean> hasMenu(List<Integer> roleIds, Integer menuId);

}
