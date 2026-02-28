package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphExtractionAbutmentManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultCallbackData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseResultCallbackDto;
import cn.voicecomm.ai.voicesagex.console.util.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "知识抽取对接回调接口")
@RequestMapping("/extractionAbutmentManage")
@Slf4j
@Validated
@RequiredArgsConstructor
public class KnowledgeGraphExtractionAbutmentManageController {


  private final KnowledgeGraphExtractionAbutmentManageService knowledgeGraphExtractionAbutmentManageService;


  @PostMapping("/parsingFile")
  @Operation(summary = "解析文件回调接口", description = "解析文件回调接口")
  @ResponseBody
  public Result<Boolean> parsingFile(
      @RequestBody @Validated FileParseResultCallbackDto fileParseResultCallbackDto) {
    log.info("【解析文件回调接口成功:{}】",
        fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA));
    CommonRespDto<Boolean> respDto = knowledgeGraphExtractionAbutmentManageService.processDocumentExtract(
        fileParseResultCallbackDto);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/extractTriad")
  @Operation(summary = "提取三元组回调接口", description = "提取三元组回调接口")
  public Result<Boolean> extractTriad(
      @RequestBody @Validated ExtractResultCallbackData extractResultCallbackData) {

    log.info("【抽取文档回调接口成功:{}】", extractResultCallbackData);

    CommonRespDto<Boolean> respDto = knowledgeGraphExtractionAbutmentManageService.extractTriad(
        extractResultCallbackData);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());

  }


}