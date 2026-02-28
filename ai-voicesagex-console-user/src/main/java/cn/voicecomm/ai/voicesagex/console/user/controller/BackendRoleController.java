package cn.voicecomm.ai.voicesagex.console.user.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.RolePageReqDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendRoleConverter;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendRoleReqVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 */
@RestController
@RequestMapping("/role")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BackendRoleController {

  @DubboReference
  private BackendRoleService backendRoleService;

  private final BackendRoleConverter backendRoleConverter;

  /**
   * 获取角色
   */
  @GetMapping("/getInfo/{id}")
  public Result<BackendRoleDto> getInfo(@PathVariable Integer id) {

    CommonRespDto<BackendRoleDto> resp = backendRoleService.getRoleDetail(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 获取角色分页
   */
  @PostMapping("/getPage")
  public Result<PagingRespDto<BackendRoleDto>> getPage(@RequestBody RolePageReqDto reqVo) {
    CommonRespDto<PagingRespDto<BackendRoleDto>> resp = backendRoleService.getRolePage(reqVo);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 修改角色
   */
  @PostMapping("updateRole")
  public Result<Boolean> updateRole(
    @Validated(value = {UpdateGroup.class}) @RequestBody BackendRoleReqVo vo) {

    CommonRespDto<Boolean> resp = backendRoleService.updateRole(
      backendRoleConverter.reqVoToDto(vo));

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData(), "修改角色成功");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 删除角色
   */
  @DeleteMapping("/delete/{id}")
  public Result<Void> delete(@PathVariable Integer id) {

    CommonRespDto<Void> resp = backendRoleService.deleteRole(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("删除成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 新增角色
   */
  @PostMapping("/add")
  public Result<Void> add(@Validated(value = {AddGroup.class}) @RequestBody BackendRoleReqVo vo) {

    CommonRespDto<Void> resp = backendRoleService.addRole(backendRoleConverter.reqVoToDto(vo));

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("新增成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 根据部门id获取角色列表
   */
  @GetMapping("/getRolesByDeptId")
  public Result<List<BackendRoleDto>> getRolesByDeptId(@RequestParam Integer deptId) {

    CommonRespDto<List<BackendRoleDto>> resp = backendRoleService.getRolesByDeptId(deptId);

    return Result.success(resp.getData());
  }

  /**
   * 根据部门id获取角色列表(包含自己)
   */
  @GetMapping("/getRolesByDeptIdWithSelf")
  public Result<List<BackendRoleDto>> getRolesByDeptIdWithSelf(@RequestParam Integer deptId) {

    CommonRespDto<List<BackendRoleDto>> resp = backendRoleService.getRolesByDeptIdWithSelf(deptId);

    return Result.success(resp.getData());
  }

  /**
   * 获取全部角色
   */
  @GetMapping("/getAllRoles")
  public Result<List<BackendRoleDto>> getAllRoles() {

    CommonRespDto<List<BackendRoleDto>> resp = backendRoleService.getAllRoles();

    return Result.success(resp.getData());
  }

}
