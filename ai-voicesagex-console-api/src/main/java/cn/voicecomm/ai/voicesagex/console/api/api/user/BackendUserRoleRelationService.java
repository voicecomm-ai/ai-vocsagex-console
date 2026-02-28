package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserRoleRelationDto;

import java.util.List;
import java.util.Map;

public interface BackendUserRoleRelationService {


  CommonRespDto<Void> batchSave(Integer userId, Integer roleId);

  /**
   * 根据用户id删除
   */
  CommonRespDto<Void> deleteByUserId(Integer userId);

  /**
   * 根据角色id查询用户数量
   */
  CommonRespDto<Long> getCountByRoleId(Integer roleId);

  /**
   * 根据用户id查询 角色信息
   */
  CommonRespDto<BackendRoleDto> getRoleByUserId(Integer userId);

  /**
   * 根据用户ids查询 角色
   */
  CommonRespDto<Map<Integer, Integer>> getRoleIdsByUserIds(List<Integer> userIds);

  /**
   * 根据用户ids查询
   */
  CommonRespDto<List<BackendUserRoleRelationDto>> getRelationByUserIds(List<Integer> userIds);

  /**
   * 根据角色ids 查询用户 ids
   */
  CommonRespDto<List<Integer>> getUserIdsByRoleIds(Integer roleId);


  CommonRespDto<List<Integer>> getUserIdsByRoleIdList(List<Integer> roleIdList);


  /**
   * 传入roleId list，筛选出其中选中了通用模板库相关菜单的roleId
   */
  CommonRespDto<List<Integer>> filterTemplateRoleIds(List<Integer> roleIdList);

  /**
   * 根据角色ids 查询对应用户数量
   */
  CommonRespDto<Map<Integer, Long>> getUserCountMap(List<Integer> roleIds);
}
