package cn.voicecomm.ai.voicesagex.console.user.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMenuService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenusAndNumDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendMenuConverter;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendMenuVo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单
 *
 */
@RestController
@RequestMapping("/menu")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BackendMenuController {

  private final BackendMenuService backendMenuService;

  private final BackendMenuConverter backendMenuConverter;


  /**
   * 获取菜单列表
   */
  @GetMapping("/getList")
  public Result<List<BackendMenuVo>> getList() {
    CommonRespDto<List<BackendMenuDto>> resp = backendMenuService.getMenuListByUserId(
      UserAuthUtil.getUserId());
    if (resp.isOk()) {
      return Result.success(backendMenuConverter.dtoListToVoList(resp.getData()));
    }
    return Result.error(resp.getMsg());
  }

  /**
   * 获取全部菜单列表
   */
  @GetMapping("/getAllList")
  public Result<List<BackendMenuVo>> getAllList() {
    CommonRespDto<List<BackendMenuDto>> resp = backendMenuService.getAllMenuList();
    if (resp.isOk()) {
      return Result.success(backendMenuConverter.dtoListToVoList(resp.getData()));
    }
    return Result.error(resp.getMsg());
  }

  /**
   * 获取菜单列表(含uri)
   */
  @GetMapping("/getMenuList")
  public Result<BackendMenuListDto> getMenuList() {
    CommonRespDto<BackendMenuListDto> resp = backendMenuService.getMenuList();
    if (resp.isOk()) {
      return Result.success(resp.getData());
    }
    return Result.error(resp.getMsg());
  }

  /**
   * 根据父级id获取菜单列表及各自数量
   * @param parentId 父级id
   * @return 菜单列表及数量
   */
  @GetMapping("/getMenusAndNumByParentId")
  public Result<List<BackendMenusAndNumDto>> getMenuList(Integer parentId) {
    CommonRespDto<List<BackendMenusAndNumDto>> resp = backendMenuService.getMenusAndNumByParentId(parentId);
    if (resp.isOk()) {
      return Result.success(resp.getData());
    }
    return Result.error(resp.getMsg());
  }
}
