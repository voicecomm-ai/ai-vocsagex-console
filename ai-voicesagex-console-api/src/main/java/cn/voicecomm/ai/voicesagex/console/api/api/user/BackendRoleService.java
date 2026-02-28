package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.RolePageReqDto;

import java.util.List;
import java.util.Map;

public interface BackendRoleService {

  /**
   * 新增角色
   */
  CommonRespDto<Void> addRole(BackendRoleDto roleDto);

  /**
   * 编辑角色
   */
  CommonRespDto<Boolean> updateRole(BackendRoleDto roleDto);

  /**
   * 删除角色
   */
  CommonRespDto<Void> deleteRole(Integer roleId);

  /**
   * 获取角色详情
   */
  CommonRespDto<BackendRoleDto> getRoleDetail(Integer roleId);


  /**
   * 查询角色分页列表
   */
  CommonRespDto<PagingRespDto<BackendRoleDto>> getRolePage(RolePageReqDto dto);

  /**
   * 根据角色id查询
   */
  CommonRespDto<List<Integer>> getUserIdsByRoleId(Integer roleId);


  /**
   * 用户id 角色名（多角色 / 拼接）
   */
  CommonRespDto<Map<Integer, String>> getRoleNamesMap(List<Integer> userIds);


  /**
   * 根据用户ID获取角色信息
   *
   * @param userId 用户ID，用于标识特定的用户
   * @return BackendRoleDto 返回后端角色数据传输对象，包含用户的角色信息
   */
  BackendRoleDto getRoleInfoByUserId(Integer userId);

  /**
   * 根据部门id获取角色列表
   */
  CommonRespDto<List<BackendRoleDto>> getRolesByDeptId(Integer deptId);

  /**
   * 根据部门id获取角色列表（包含自己）
   */
  CommonRespDto<List<BackendRoleDto>> getRolesByDeptIdWithSelf(Integer deptId);

  CommonRespDto<List<BackendRoleDto>> getAllRoles();
}
