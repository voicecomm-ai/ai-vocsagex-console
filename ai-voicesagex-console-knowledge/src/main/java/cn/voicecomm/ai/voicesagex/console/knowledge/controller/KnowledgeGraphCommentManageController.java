package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphCommentManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用模块
 *
 * @author ryc
 * @date 2025/5/21
 */
@RestController
@RequestMapping("/commentManage")
@Slf4j
@RequiredArgsConstructor
@Validated
public class KnowledgeGraphCommentManageController {

  private final KnowledgeGraphCommentManageService graphCommentManageService;

  /**
   * 上传文件
   *
   * @param fileDir 文件夹目录
   * @param file    文件
   * @return
   */
  @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<String> upload(@RequestParam(value = "fileDir", required = false) String fileDir,
      @RequestParam(value = "file", required = false) @NotNull(message = "文件不能为空") MultipartFile file) {
    CommonRespDto<String> respDto = graphCommentManageService.upload(fileDir, file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }


}
