package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelApiKeyService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyPageReq;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
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
 * 模型密钥
 *
 * @author ryc
 * @date 2025-07-09 09:57:34
 */
@RestController
@RequestMapping("/modelApiKey")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelApiKeyController {

  private final ModelApiKeyService modelApiKeyService;

  /**
   * 模型密钥列表
   *
   * @return 模型密钥数据集
   */
  @PostMapping("/list")
  public Result<List<ModelApiKeyDto>> getList(@Validated @RequestBody ModelApiKeyPageReq modelApiKeyPageReq) {
    CommonRespDto<List<ModelApiKeyDto>> respDto = modelApiKeyService.getList(modelApiKeyPageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型密钥详情
   *
   * @param id 主键id
   * @return 模型密钥详情数据
   */
  @GetMapping("/info")
  public Result<ModelApiKeyDto> getInfo(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<ModelApiKeyDto> respDto = modelApiKeyService.getInfo(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增模型密钥
   *
   * @param modelApiKeyDto 模型密钥对象
   * @return 模型密钥id
   */
  @PostMapping("/save")
  public Result<String> save(
      @Validated(value = {AddGroup.class}) @RequestBody ModelApiKeyDto modelApiKeyDto) {
    CommonRespDto<String> respDto = modelApiKeyService.save(modelApiKeyDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 删除模型密钥
   *
   * @param id 主键id
   * @return 是否删除成功
   */
  @DeleteMapping("/delete")
  public Result<Boolean> delete(
      @RequestParam(value = "id", required = false) @NotNull(message = "主键id不能为空") Integer id) {
    CommonRespDto<Boolean> respDto = modelApiKeyService.delete(id);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }
}
