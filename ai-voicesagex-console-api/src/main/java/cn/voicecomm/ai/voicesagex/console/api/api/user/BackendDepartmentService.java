package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendDepartmentDto;
import java.util.List;
import java.util.Map;

/**
 * 后端部门服务接口 提供对部门信息进行增删查改操作的方法
 *
 * @author wangfan
 * @date 2024/10/21 下午 5:08
 */
public interface BackendDepartmentService {

  /**
   * 添加新部门
   *
   * @param dto 包含部门信息的数据传输对象
   * @return 常规响应对象，表示操作结果
   */
  CommonRespDto<Void> add(BackendDepartmentDto dto);

  /**
   * 删除指定部门
   *
   * @param id 要删除的部门的ID
   * @return 常规响应对象，表示操作结果
   */
  CommonRespDto<Void> delete(Integer id);

  /**
   * 获取指定部门的信息
   *
   * @param id 要查询的部门的ID
   * @return 包含部门信息的常规响应对象
   */
  CommonRespDto<BackendDepartmentDto> getInfo(Integer id);


  /**
   * 根据部门id获取部门树结构
   *
   * @param id 部门ID，用于获取指定部门及其子部门的树形结构
   * @return 返回一个包含部门树结构的CommonRespDto对象，其中泛型类型为BackendDepartmentDto列表
   */
  CommonRespDto<List<BackendDepartmentDto>> getDepartmentTreeById(Integer id);


  /**
   * 获取部门树结构信息
   * <p>
   * 本方法用于获取系统中的部门信息，并以树形结构进行组织返回的部门信息包括部门的层级关系， 从根部门开始，逐步展开子部门这种树形结构有助于前端进行直观展示和用户理解部门之间的关系
   *
   * @return CommonRespDto<List < BackendDepartmentDto>> 包含部门树结构信息的响应对象
   */
  CommonRespDto<List<BackendDepartmentDto>> getDepartmentTree(BackendDepartmentDto dto);

  /**
   * 更新部门信息
   *
   * @param dto 包含更新后部门信息的数据传输对象
   * @return 常规响应对象，表示操作结果
   */
  CommonRespDto<Void> update(BackendDepartmentDto dto);



  /**
   * 获取部门ID与名称的映射
   * 该方法根据BackendDepartmentDto对象中的筛选条件，返回一个映射，其中包含部门ID作为键和部门名称作为值
   * 主要用于需要快速根据部门ID获取部门名称的场景，提供了一种高效的数据查询方式
   *
   * @return 返回一个Map对象，键为部门ID（Integer），值为部门名称（String）
   */
  Map<Integer,String> getDeptIdNameMap();

  /**
   * 根据部门ID获取所有子部门ID
   *
   * @param deptId 部门ID，用于查询其所有子部门的ID
   * @return 包含所有子部门ID的列表如果该部门没有子部门，则返回空列表
   */
  List<Integer> getChildDeptIdsById(Integer deptId);


  /**
   * 根据部门ID获取所有父部门ID
   *
   * @param deptId 部门ID，用于查询其所有父部门的ID
   * @param userId
   * @return 包含所有子部门ID的列表如果该部门没有父部门，则返回空列表
   */
  List<Integer> getParentDeptIdsById(Integer deptId, Integer userId);

  /**
   * 查询第二级部门所有下级ids
   * @param deptId 部门id
   * @return
   */
  List<Integer> getParentLevelTwoChildDeptIdsById(Integer deptId);

}

