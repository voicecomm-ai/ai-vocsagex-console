package cn.voicecomm.ai.voicesagex.console.user.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendUserConverter;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendUserReqVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.CheckPasswordReqVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.UpdatePasswordReqVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.UserPageReqVo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BackendUserController {

  @DubboReference
  private BackendUserService backendUserService;

  private final BackendUserConverter backendUserConverter;

  /**
   * 获取用户
   */
  @GetMapping("/getInfo/{id}")
  public Result<BackendUserDto> getInfo(@PathVariable Integer id) {

    CommonRespDto<BackendUserDto> resp = backendUserService.getInfo(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 获取用户
   */
  @GetMapping("/getCurrentUser")
  public Result<BackendUserDto> getInfo() {

    CommonRespDto<BackendUserDto> resp = backendUserService.getInfo(UserAuthUtil.getUserId());

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 获取用户分页
   */
  @PostMapping("/getPage")
  public Result<PagingRespDto<BackendUserDto>> getPage(@RequestBody UserPageReqVo reqVo) {
    CommonRespDto<PagingRespDto<BackendUserDto>> resp = backendUserService.getUserPage(
      backendUserConverter.voToDto(reqVo));

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 获取用户列表
   */
  @GetMapping("/getUserListByUserId")
  public Result<List<BackendUserDto>> getPage(
    @RequestParam(value = "type", required = false) Integer type) {

    CommonRespDto<List<BackendUserDto>> resp = backendUserService.getUserListByUserId(type);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData());
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 修改用户
   */
  @PostMapping("/updateUser")
  public Result<String> updateUser(
    @Validated(value = {UpdateGroup.class}) @RequestBody BackendUserReqVo vo) {

    CommonRespDto<Void> resp = backendUserService.update(backendUserConverter.voToDto(vo));

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("修改用户成功");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 删除账号
   */
  @DeleteMapping("/delete/{id}")
  public Result<Void> delete(@PathVariable Integer id) {

    CommonRespDto<Void> resp = backendUserService.delete(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("删除成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 新增账号
   */
  @PostMapping("/add")
  public Result<Void> add(@Validated(value = {AddGroup.class}) @RequestBody BackendUserReqVo vo) {

    CommonRespDto<Void> resp = backendUserService.add(backendUserConverter.voToDto(vo));

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("新增成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 禁用账号
   */
  @PatchMapping("/disable/{id}")
  public Result<Void> disable(@PathVariable Integer id) {

    CommonRespDto<Void> resp = backendUserService.disable(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("禁用成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 启用账号
   */
  @PatchMapping("/enable/{id}")
  public Result<Void> enable(@PathVariable Integer id) {

    CommonRespDto<Void> resp = backendUserService.enable(id);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("启用成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 重置密码
   */
  @PostMapping("/updatePassword")
  public Result<String> updatePassword(@Validated @RequestBody UpdatePasswordReqVo vo) {

    CommonRespDto<Void> resp = backendUserService.updatePassword(vo.getId(), vo.getOldPassword(),
      vo.getFirstPassword(), vo.getSecondPassword());

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("修改密码成功！");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 校验密码
   */
  @PostMapping("/checkPassword")
  public Result<String> checkPassword(@RequestBody CheckPasswordReqVo vo) {

    CommonRespDto<Void> resp = backendUserService.checkPassword(vo.getPassword());

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("密码校验通过！");
    }

    return Result.error(resp.getMsg());
  }


  /**
   * 下载导入模板
   */
  @GetMapping("/downloadBatchImportUserTemplate")
  public Result<String> downloadBatchImportUserTemplate() {

    CommonRespDto<String> resp = backendUserService.downloadBatchImportUserTemplate();

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.success(resp.getData(), "下载导入模板成功");
    }

    return Result.error(resp.getMsg());
  }

  /**
   * 批量导入用户
   */
  @PostMapping("/batchImportUser")
  public Result<String> batchImportUser(@RequestParam("roleId") Integer roleId,
    @RequestParam("file") MultipartFile file) {

    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      return Result.error("文件读取失败");
    }

    CommonRespDto<Void> resp = backendUserService.batchImportUser(roleId, bytes);

    if (Boolean.TRUE.equals(resp.isOk())) {
      return Result.successMsg("批量导入用户成功");
    }

    return Result.error(resp.getMsg());
  }

}
