package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.api.application.UploadFilesService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.UploadFilesResp;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工作流
 */
@RestController
@RequestMapping("/uploadFile")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UploadFileController {

  private final UploadFilesService uploadFilesService;


  /**
   * 文件上传
   *
   * @param file 文件
   * @return 文件信息
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<UploadFilesResp> upload(@Valid @NotNull(message = "文件不能为空") MultipartFile file) {
    log.info("文件上传, file={}", file.getOriginalFilename());
    CommonRespDto<UploadFilesResp> respDto = uploadFilesService.upload(file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 远程文件上传
   *
   * @param url 文件url
   * @return 文件信息
   */
  @PostMapping(value = "/remoteFileUpload")
  public Result<UploadFilesResp> remoteFileUpload(@RequestBody JSONObject object) {
    log.info("远程文件上传, url={}", object.getStr("url"));
    CommonRespDto<UploadFilesResp> respDto = uploadFilesService.remoteFileUpload(object.getStr("url"));
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}