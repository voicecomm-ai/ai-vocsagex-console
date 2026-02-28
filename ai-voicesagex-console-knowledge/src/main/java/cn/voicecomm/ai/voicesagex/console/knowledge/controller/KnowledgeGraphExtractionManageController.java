package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeExtractionManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ChunkCountReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ConfigInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.DropStatusVerification;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDoExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDropExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeSaveExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeUpdateExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationTypeSelectDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListDetailVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DropDocumentVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DropVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ExtractPreviewVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.InsertVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.KnowledgeEntryMapVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.StatusVerificationClear;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.UpdateVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationEdgeTypePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationKnowledgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationTotalVO;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName KnowledgeGraphExtractionManageController
 * @Author wangyang
 * @Date 2025/9/15 13:54
 */

@RestController
@Tag(name = "知识抽取控制管理")
@RequestMapping("/extractionManage")
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphExtractionManageController {

  private final KnowledgeExtractionManageService knowledgeExtractionManageService;


  @PostMapping("/insertExtractionJob")
  @Operation(summary = "新增抽取任务", description = "新增抽取任务")
  public Result<Boolean> insertExtractionJob(
      @RequestBody @Validated KnowledgeSaveExtractionDto knowledgeSaveExtractionDto) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.insertExtractionJob(
        knowledgeSaveExtractionDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/updateExtractionJob")
  @Operation(summary = "编辑抽取任务", description = "编辑抽取任务")
  public Result<Boolean> updateExtractionJob(
      @RequestBody @Validated KnowledgeUpdateExtractionDto knowledgeExtractionDto) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.updateExtractionJob(
        knowledgeExtractionDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/deleteExtractionJob")
  @Operation(summary = "删除抽取任务", description = "删除抽取任务")
  public Result<Boolean> deleteExtractionJob(
      @RequestBody @Validated KnowledgeDropExtractionDto knowledgeDropExtractionDto) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.deleteExtractionJob(
        knowledgeDropExtractionDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/extractionJobList")
  @Operation(summary = "任务抽取列表", description = "任务抽取列表")
  public Result<PagingRespDto<KnowledgeExtractionListDto>> extractionJobList(
      @RequestBody @Validated KnowledgeExtractionReq knowledgeExtractionReq) {
    CommonRespDto<PagingRespDto<KnowledgeExtractionListDto>> respDto = knowledgeExtractionManageService.extractionJobList(
        knowledgeExtractionReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }

    return Result.success(respDto.getData());
  }


  @PostMapping(value = "/upload")
  @Operation(summary = "抽取文档上传", description = "抽取文档上传")
  @ResponseBody
  public Result<String> upload(@RequestParam("file") MultipartFile file,
      @RequestParam(value = "jobId", required = false) Integer jobId) {
    CommonRespDto<String> respDto = knowledgeExtractionManageService.upload(file, jobId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), "上传文件成功！");
  }


  @PostMapping(value = "/uploadMulti")
  @Operation(summary = "多文件上传", description = "支持doc、docx、xlsx、xls、pdf格式文件，单文件限制100MB，最多10个文件")
  public Result<?> uploadMulti(
      @RequestParam("files") MultipartFile[] files,
      @RequestParam(value = "jobId", required = false) Integer jobId) {
    log.info("请求到达");
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.uploadFiles(files, jobId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), "上传文件成功！");
  }

  @PostMapping("/extractDocument")
  @Operation(summary = "文档抽取", description = "文档抽取")
  public Result<Boolean> extractDocument(
      @RequestBody @Validated KnowledgeDoExtractionDto knowledgeDoExtractionDto) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.extractDocument(
        knowledgeDoExtractionDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/documentList")
  @Operation(summary = "文档列表", description = "文档列表")
  public Result<PagingRespDto<DocumentListDetailVO>> documentList(
      @RequestBody @Validated DocumentListVO documentListVO) {

    CommonRespDto<PagingRespDto<DocumentListDetailVO>> respDto = knowledgeExtractionManageService.documentList(
        documentListVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/documentChunkCount")
  @Operation(summary = "获取文档类型有效片段数", description = "获取文档类型有效片段数")
  public Result<Integer> documentChunkCount(@RequestBody @Validated ChunkCountReq chunkCountReq) {

    CommonRespDto<Integer> respDto = CommonRespDto.success(
        knowledgeExtractionManageService.documentChunkCount(
            chunkCountReq));

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/verificationTotal")
  @Operation(summary = "校验统计值", description = "校验统计值")
  public Result<VerificationTotalVO> verificationTotal(
      @RequestBody @Validated DropStatusVerification dropStatusVerification) {
    CommonRespDto<VerificationTotalVO> respDto = knowledgeExtractionManageService.verificationTotal(
        dropStatusVerification);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getTagInfo")
  @Operation(summary = "知识校验主客体类型选择", description = "知识校验主客体类型选择")
  public Result<List<String>> getTagInfo(
      @RequestBody @Validated VerificationTypeSelectDto verificationTypeSelectDto) {
    CommonRespDto<List<String>> respDto = knowledgeExtractionManageService.getTagInfo(
        verificationTypeSelectDto);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getExtractConfig")
  @Operation(summary = "获取文档抽取配置", description = "获取文档抽取配置")
  public Result<DocumentConfigInfoVO> getConfig(
      @RequestBody @Validated ConfigInfoDto configInfoDto) {
    CommonRespDto<DocumentConfigInfoVO> respDto = knowledgeExtractionManageService.getConfig(
        configInfoDto);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/knowledgeVerificationList")
  @Operation(summary = "知识校验列表", description = "知识校验列表")
  public Result<PagingRespDto<VerificationListVO>> knowledgeVerificationList(
      @RequestBody @Validated VerificationInfoDto verificationInfoDto) {
    CommonRespDto<PagingRespDto<VerificationListVO>> respDto = knowledgeExtractionManageService.knowledgeVerificationList(
        verificationInfoDto);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getEdgeTypeProperty")
  @Operation(summary = "关系属性选择", description = "关系属性选择")
  public Result<EdgePropertyResVO> getEdgeTypeProperty(
      @RequestBody @Validated VerificationEdgeTypePropertyVO verificationEdgeTypePropertyVO) {
    CommonRespDto<EdgePropertyResVO> respDto = knowledgeExtractionManageService.getEdgeTypeProperty(
        verificationEdgeTypePropertyVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/originalInformation")
  @Operation(summary = "获取原文信息", description = "获取原文信息")
  public Result<OriginalInformationVO> originalInformation(
      @RequestBody @Validated OriginalInformationInfoVO originalInformationInfoVO) {
    CommonRespDto<OriginalInformationVO> respDto = knowledgeExtractionManageService.originalInformation(
        originalInformationInfoVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/deleteDocument")
  @Operation(summary = "文档列表删除", description = "文档列表删除")
  public Result<Boolean> deleteDocument(
      @RequestBody @Validated DropDocumentVO dropDocumentVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.deleteDocument(
        dropDocumentVO.getDocumentId());

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/extractPreview")
  @Operation(summary = "预览抽取", description = "预览抽取")
  public Result<List<ExtractPreviewVO>> testPreview(
      @RequestBody @Validated DocumentConfigVO documentConfigVO) {
    CommonRespDto<List<ExtractPreviewVO>> respDto = knowledgeExtractionManageService.testPreview(
        documentConfigVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/extractConfig")
  @Operation(summary = "文档抽取配置", description = "文档抽取配置")
  public Result<Boolean> extractConfig(
      @RequestBody @Validated DocumentConfigVO documentConfigVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.documentConfig(
        documentConfigVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/knowledgeEntryMap")
  @Operation(summary = "知识入图", description = "知识入图")
  public Result<Boolean> knowledgeEntryMap(
      @RequestBody @Validated KnowledgeEntryMapVO knowledgeEntryMapVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.knowledgeEntryMap(
        knowledgeEntryMapVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/updateVerification")
  @Operation(summary = "知识校验编辑", description = "知识校验编辑")
  public Result<Boolean> updateVerification(
      @RequestBody @Validated UpdateVerificationVO updateVerificationVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.updateVerification(
        updateVerificationVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/deleteVerification")
  @Operation(summary = "批量删除", description = "批量删除")
  public Result<Boolean> deleteVerification(
      @RequestBody @Validated DropVerificationVO updateVerificationVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.deleteVerification(
        updateVerificationVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/insertVerification")
  @Operation(summary = "新增知识校验", description = "新增知识校验")
  public Result<Boolean> insertVerification(
      @RequestBody @Validated InsertVerificationVO insertVerificationVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.insertVerification(
        insertVerificationVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/verificationKnowledge")
  @Operation(summary = "知识校验", description = "知识校验")
  public Result<Boolean> verificationKnowledge(
      @RequestBody @Validated VerificationKnowledgeVO verificationKnowledgeVO) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.verificationKnowledge(
        verificationKnowledgeVO);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/dropStatus")
  @Operation(summary = "清空状态", description = "清空状态")
  public Result<Boolean> dropStatus(
      @RequestBody @Validated StatusVerificationClear statusVerificationClear) {
    CommonRespDto<Boolean> respDto = knowledgeExtractionManageService.dropStatus(
        statusVerificationClear);

    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


}
