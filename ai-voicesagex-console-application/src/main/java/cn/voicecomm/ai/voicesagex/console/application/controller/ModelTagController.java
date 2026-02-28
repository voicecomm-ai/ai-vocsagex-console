package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelTagDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型标签
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@RestController
@RequestMapping("/modelTag")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelTagController {

  private final ModelTagService modelTagService;

  /**
   * 新增模型标签
   *
   * @param modelTagDto 标签对象
   * @return 新增的id
   */
  @PostMapping("/save")
  public Result<Integer> save(
      @Validated(value = {AddGroup.class}) @RequestBody ModelTagDto modelTagDto) {
    CommonRespDto<Integer> respDto = modelTagService.save(modelTagDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新模型标签
   *
   * @param modelTagDto 标签对象
   * @return 是否成功
   */
  @PostMapping("/update")
  public Result<Boolean> update(
      @Validated(value = {UpdateGroup.class}) @RequestBody ModelTagDto modelTagDto) {
    CommonRespDto<Boolean> respDto = modelTagService.update(modelTagDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除模型标签
   *
   * @param id 主键id
   * @return 是否成功
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelTagService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
