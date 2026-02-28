package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelDatasetFileService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFileDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFilePageReq;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据集文件
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
@RestController
@RequestMapping("/modelDatasetFile")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ModelDatasetFileController {

  private final ModelDatasetFileService modelDatasetFileService;

  /**
   * 数据集文件列表
   *
   * @return 数据集文件数据集
   */
  @PostMapping("/page")
  public Result<PagingRespDto<ModelDatasetFileDto>> getPageList(
      @Validated @RequestBody ModelDatasetFilePageReq pageReq) {
    CommonRespDto<PagingRespDto<ModelDatasetFileDto>> respDto = modelDatasetFileService.getPageList(
        pageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}
