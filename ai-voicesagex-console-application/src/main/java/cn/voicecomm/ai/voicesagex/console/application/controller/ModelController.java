package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelBatchReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDownloadParamDto.ArchDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.api.valid.UpdateGroup;
import cn.voicecomm.ai.voicesagex.console.application.handler.ModelSseHandler;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模型
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@RestController
@RequestMapping("/model")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelController {

  private final ModelService modelService;

  private final ModelSseHandler modelSseHandler;

  /**
   * 模型分页列表
   *
   * @param pageReq 分页列表参数
   * @return 分页数据
   */
  @PostMapping("/page")
  public Result<PagingRespDto<ModelPageDto>> getPageList(@RequestBody ModelPageReq pageReq) {
    CommonRespDto<PagingRespDto<ModelPageDto>> respDto = modelService.getPageList(pageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型列表
   *
   * @param modelReq 列表参数
   * @return 分页数据
   */
  @PostMapping("/list")
  public Result<List<ModelPageDto>> getList(@RequestBody ModelReq modelReq) {
    CommonRespDto<List<ModelPageDto>> respDto = modelService.getList(modelReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型详情
   *
   * @param id 主键id
   * @return 模型对象
   */
  @GetMapping("/info")
  public Result<ModelDto> getInfo(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<ModelDto> respDto = modelService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型是否可用
   *
   * @param id 主键id
   * @return 模型对象
   */
  @GetMapping("/isAvailable")
  public Result<Boolean> isAvailable(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelService.isAvailable(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增模型
   *
   * @param modelDto 模型对象
   * @return 新增的id
   */
  @PostMapping("/save")
  public Result<Integer> save(@Validated(value = {AddGroup.class}) @RequestBody ModelDto modelDto) {
    CommonRespDto<Integer> respDto = modelService.save(modelDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新模型
   *
   * @param modelDto 模型对象
   * @return 是否成功
   */
  @PostMapping("/update")
  public Result<Boolean> update(
      @Validated(value = {UpdateGroup.class}) @RequestBody ModelDto modelDto) {
    CommonRespDto<Boolean> respDto = modelService.update(modelDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除模型
   *
   * @param id 主键id
   * @return 是否成功
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量删除模型
   *
   * @param ids 主键id集合
   * @return 是否成功
   */
  @DeleteMapping("/delete-batch")
  public Result<Boolean> deleteBatch(
      @RequestBody @NotEmpty(message = "id不能为空") List<Integer> ids) {
    CommonRespDto<Boolean> respDto = modelService.deleteBatch(ids);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量上下架模型
   *
   * @param modelBatchReq 批量的对象
   * @return 是否成功
   */
  @PostMapping("/shelf-batch")
  public Result<Boolean> shelfBatch(@RequestBody @Validated ModelBatchReq modelBatchReq) {
    CommonRespDto<Boolean> respDto = modelService.shelfBatch(modelBatchReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 批量更新分类
   *
   * @param modelBatchReq 批量的对象
   * @return 是否成功
   */
  @PostMapping("/updatBatch/category")
  public Result<Boolean> updateCategory(@RequestBody @Validated ModelBatchReq modelBatchReq) {
    CommonRespDto<Boolean> respDto = modelService.updateCategoryBatch(modelBatchReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 预训练模型调用
   *
   * @param authHeader
   * @param modelInvokeBaseDto
   * @return
   */
  @PostMapping(value = "/pre-trained/v1/invoke", produces = {MediaType.APPLICATION_JSON_VALUE,
      MediaType.TEXT_EVENT_STREAM_VALUE})
  public Object invoke(@RequestHeader("Authorization") String authHeader,
      @RequestBody ModelInvokeBaseDto modelInvokeBaseDto) {
    if (modelInvokeBaseDto.isStream()) {
      return modelSseHandler.invokeWithSse(authHeader.substring(7), modelInvokeBaseDto);
    } else {
      return modelService.invokeStandard(authHeader.substring(7), modelInvokeBaseDto);
    }
  }

  /**
   * 预训练模型下载
   *
   * @param
   * @return
   */
  @PostMapping(value = "/pre-trained/download")
  public Result<Boolean> preTrainedDownload(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id,
      @Validated @RequestBody ArchDto archDto) {
    CommonRespDto<Boolean> respDto = modelService.preTrainedDownload(id, archDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 模型下载回调
   *
   * @param modelParamDto
   * @return
   */
  @PostMapping(value = "/download/callback")
  public Result<Void> downloadCallback(@RequestBody ModelParamDto modelParamDto) {
    CommonRespDto<Void> respDto = modelService.downloadCallback(modelParamDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 获取模型文件目录结构
   *
   * @param id
   * @return
   */
  @GetMapping("file/build-tree")
  public Result<ZipNodeDto> buildTree(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<ZipNodeDto> respDto = modelService.buildTree(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取算法模型配置文件
   *
   * @param id
   * @return
   */
  @GetMapping("algorithm/config")
  public Result<String> getAlgorithmConfig(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id,
      @RequestParam(value = "type", required = false) @NotNull(message = "类型不能为空") Integer type,
      @RequestParam(value = "modelSource", required = false) Integer modelSource) {
    CommonRespDto<String> respDto = modelService.getAlgorithmConfig(id, type, modelSource);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

}
