package cn.voicecomm.ai.voicesagex.console.user.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendDepartmentService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendDepartmentDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织部门
 *
 */
@RestController
@RequestMapping("/department")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BackendDepartmentController {

  private final BackendDepartmentService backendDepartmentService;

  /**
   * 添加新部门
   */
  @PostMapping("/add")
  public Result<Void> add(@Validated @RequestBody BackendDepartmentDto dto) {
    CommonRespDto<Void> resp = backendDepartmentService.add(dto);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success();
    }
    return Result.error(resp.getMsg());
  }


  /**
   * 删除部门
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(Integer id) {
    CommonRespDto<Void> resp = backendDepartmentService.delete(id);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success();
    }
    return Result.error(resp.getMsg());
  }


  /**
   * 获取部门树
   */
  @PostMapping("/getDepartmentTree")
  public Result<List<BackendDepartmentDto>> getDepartmentTree(
    @RequestBody BackendDepartmentDto dto) {
    CommonRespDto<List<BackendDepartmentDto>> resp = backendDepartmentService.getDepartmentTree(
      dto);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }
    return Result.error(resp.getMsg());
  }


  /**
   * 根据id获取部门树
   */
  @GetMapping("/getDepartmentTreeById")
  public Result<List<BackendDepartmentDto>> getDepartmentTreeById(Integer id) {
    CommonRespDto<List<BackendDepartmentDto>> resp = backendDepartmentService.getDepartmentTreeById(
      id);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }
    return Result.error(resp.getMsg());
  }


  /**
   * 获取指定部门的信息
   */
  @GetMapping("/getInfo")
  public Result<BackendDepartmentDto> getInfo(Integer id) {
    CommonRespDto<BackendDepartmentDto> resp = backendDepartmentService.getInfo(id);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }
    return Result.error(resp.getMsg());
  }

  /**
   * 更新部门信息
   */
  @PostMapping("/update")
  public Result<Void> update(@Validated @RequestBody BackendDepartmentDto dto) {
    CommonRespDto<Void> resp = backendDepartmentService.update(dto);
    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success();
    }
    return Result.error(resp.getMsg());
  }


  /**
   * 根据部门ID获取所有父部门ID
   */
  @GetMapping("/getParentDeptIdsById")
  public Result<List<Integer>> getParentDeptIdsById(@RequestParam Integer deptId) {
    List<Integer> parentDeptIdsById = backendDepartmentService.getParentDeptIdsById(deptId, null);
    return Result.success(parentDeptIdsById);
  }
}
