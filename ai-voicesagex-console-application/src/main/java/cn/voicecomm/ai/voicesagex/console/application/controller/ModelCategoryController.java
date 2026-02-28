package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelCategoryService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageReq;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型分类
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@RestController
@RequestMapping("/modelCategory")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelCategoryController {

  private final ModelCategoryService modelCategoryService;

  /**
   * 模型分类列表
   *
   * @param pageReq 查询对象
   * @return
   */
  @PostMapping("/list")
  public Result<List<ModelCategoryPageDto>> getList(@RequestBody ModelCategoryPageReq pageReq) {
    CommonRespDto<List<ModelCategoryPageDto>> respDto = modelCategoryService.getList(pageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型分类详情
   *
   * @param id 主键id
   * @return 详情对象
   */
  @GetMapping("/info")
  public Result<ModelCategoryPageDto> getInfo(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<ModelCategoryPageDto> respDto = modelCategoryService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增模型分类
   *
   * @param modelCategoryDto 模型分类对象
   * @return 新增的id
   */
  @PostMapping("/save")
  public Result<Integer> save(
      @Validated(value = {AddGroup.class}) @RequestBody ModelCategoryDto modelCategoryDto) {
    CommonRespDto<Integer> respDto = modelCategoryService.save(modelCategoryDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新模型分类
   *
   * @param modelCategoryDto 模型分类对象
   * @return 是否成功
   */
  @PostMapping("/update")
  public Result<Boolean> update(
      @Validated(value = {UpdateGroup.class}) @RequestBody ModelCategoryDto modelCategoryDto) {
    CommonRespDto<Boolean> respDto = modelCategoryService.update(modelCategoryDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除模型分类
   *
   * @param id 主键id
   * @return 是否成功
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelCategoryService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
