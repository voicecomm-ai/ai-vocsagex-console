package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用标签
 *
 * @author wangf
 * @date 2025/5/19 下午 2:00
 */
@RestController
@RequestMapping("/applicationTag")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ApplicationTagController {

  private final ApplicationTagService applicationTagService;

  /**
   * 获取应用标签列表
   *
   * @return 应用标签列表
   */
  @PostMapping("/getList")
  public Result<List<ApplicationTagDto>> getList(String tagName) {
    CommonRespDto<List<ApplicationTagDto>> respDto = applicationTagService.getList(tagName);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 根据ID获取应用标签信息
   *
   * @param id 应用标签ID
   * @return 应用标签信息
   */
  @GetMapping("/getById")
  public Result<ApplicationTagDto> getById(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<ApplicationTagDto> respDto = applicationTagService.getById(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 添加应用标签
   *
   * @param dto 应用标签数据传输对象
   * @return 新增应用标签的ID
   */
  @PostMapping("/add")
  public Result<Integer> add(@RequestBody @Validated ApplicationTagDto dto) {
    CommonRespDto<Integer> respDto = applicationTagService.add(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新应用标签信息
   *
   * @param dto 应用标签数据传输对象
   * @return 更新是否成功
   */
  @PostMapping("/update")
  public Result<Void> update(@RequestBody @Validated ApplicationTagDto dto) {
    CommonRespDto<Void> respDto = applicationTagService.update(dto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除应用标签
   *
   * @param id 应用标签ID
   * @return 删除是否成功
   */
  @DeleteMapping("/delete")
  public Result<Void> delete(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Void> respDto = applicationTagService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 检查是否可以删除应用标签 （true代表未被使用，可以删除）
   *
   * @param id 应用标签ID
   * @return 是否可以删除
   */
  @GetMapping("/deleteCheck")
  public Result<Boolean> deleteCheck(@NotNull(message = "id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = applicationTagService.deleteCheck(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}
