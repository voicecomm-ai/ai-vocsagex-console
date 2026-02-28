package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelDatasetService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetPageReq;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
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
 * 模型数据集
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
@RestController
@RequestMapping("/modelDataset")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelDatasetController {

  private final ModelDatasetService modelDatasetService;

  /**
   * 数据集列表
   *
   * @return 数据集数据集
   */
  @PostMapping("/page")
  public Result<PagingRespDto<ModelDatasetDto>> getPageList(@Validated @RequestBody ModelDatasetPageReq pageReq) {
    CommonRespDto<PagingRespDto<ModelDatasetDto>> respDto = modelDatasetService.getPageList(pageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 数据集详情
   *
   * @param id 主键id
   * @return 数据集详情数据
   */
  @GetMapping("/info")
  public Result<ModelDatasetDto> getInfo(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<ModelDatasetDto> respDto = modelDatasetService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增数据集
   *
   * @param modelDatasetDto 数据集对象
   * @return 数据集id
   */
  @PostMapping("/save")
  public Result<Integer> save(
      @Validated(value = {AddGroup.class}) @RequestBody ModelDatasetDto modelDatasetDto) {
    CommonRespDto<Integer> respDto = modelDatasetService.save(modelDatasetDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新数据集
   *
   * @param modelDatasetDto 数据集对象
   * @return 是否更新成功
   */
  @PostMapping("/update")
  public Result<Boolean> update(
      @Validated(value = {UpdateGroup.class}) @RequestBody ModelDatasetDto modelDatasetDto) {
    CommonRespDto<Boolean> respDto = modelDatasetService.update(modelDatasetDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除数据集
   *
   * @param id 主键id
   * @return 是否删除成功
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelDatasetService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
