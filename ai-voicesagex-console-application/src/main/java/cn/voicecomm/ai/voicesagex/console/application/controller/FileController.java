package cn.voicecomm.ai.voicesagex.console.application.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.file.FileService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.FileExtractEntityDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理
 *
 * @author ryc
 * @date 2025/5/21
 */
@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
@Validated
public class FileController {

  private final FileService fileService;

  /**
   * 上传压缩包
   *
   * @param fileDir 文件夹目录
   * @param file    文件
   * @return
   */
  @PostMapping(value = "/upload-zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<String> uploadZip(
      @RequestParam("fileDir") @NotBlank(message = "文件夹目录不能为空") String fileDir,
      @RequestParam(value = "file", required = false) @NotNull(message = "文件不能为空") MultipartFile file) {
    CommonRespDto<String> respDto = fileService.uploadZip(fileDir, file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 上传文件
   *
   * @param fileDir 文件夹目录
   * @param file    文件
   * @return
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<String> upload(
      @RequestParam("fileDir") @NotBlank(message = "文件夹目录不能为空") String fileDir,
      @RequestParam(value = "file", required = false) @NotNull(message = "文件不能为空") MultipartFile file) {
    CommonRespDto<String> respDto = fileService.upload(fileDir, file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 上传文件获取结构
   *
   * @param fileDir 文件夹目录
   * @param file    文件
   * @return
   */
  @PostMapping(value = "/upload/extract-entity-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<FileExtractEntityDto> uploadExtractEntityFiles(
      @RequestParam(value = "fileDir", required = false) @NotBlank(message = "文件夹目录不能为空") String fileDir,
      @RequestParam(value = "file", required = false) @NotNull(message = "文件不能为空") MultipartFile file) {
    CommonRespDto<FileExtractEntityDto> respDto = fileService.uploadExtractEntityFiles(fileDir,
        file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 下载子文件
   *
   * @param zipPath   压缩包目录
   * @param entryPath 文件或文件目录名称
   * @return
   */
  @GetMapping(value = "/download-entry")
  public Result<Void> downloadEntry(
      @RequestParam(value = "zipPath", required = false) @NotBlank(message = "压缩包目录不能为空") String zipPath,
      @RequestParam(value = "entryPath", required = false) String entryPath,
      HttpServletResponse response) {
    CommonRespDto<Void> respDto = fileService.downloadEntry(zipPath, entryPath, response);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 检查已上传分片（断点续传）
   *
   * @param fileMd5 文件MD5字符串
   * @return 分片集合
   */
  @GetMapping("/check")
  public Result<Set<Integer>> checkUploadChunks(@RequestParam("fileMd5") String fileMd5) {
    CommonRespDto<Set<Integer>> respDto = fileService.getUploadedChunks(fileMd5);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 上传单个分片
   *
   * @param fileMd5     文件MD5字符串
   * @param chunkIndex  当前上传分片的索引
   * @param totalChunks 文件总分片数
   * @param fileDir     文件保存的目录 例如：  model/dataset
   * @param file        具体文件
   * @return
   */
  @PostMapping(value = "/uploadChunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Result<Integer> uploadChunk(@RequestParam("fileMd5") String fileMd5,
      @RequestParam("chunkIndex") Integer chunkIndex,
      @RequestParam("totalChunks") Integer totalChunks, @RequestParam("fileDir") String fileDir,
      @RequestParam("file") MultipartFile file) {
    CommonRespDto<Integer> respDto = fileService.uploadChunk(fileMd5, chunkIndex, totalChunks,
        fileDir, file);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 合并所有分片
   *
   * @param fileMd5  文件MD5字符串
   * @param fileName 最大文件的文件名称  例如： test.zip
   * @param fileDir  文件保存的目录 例如：  model/dataset
   * @return 文件地址和结构
   */
  @GetMapping("/merge")
  public Result<FileExtractEntityDto> mergeChunks(@RequestParam("fileMd5") String fileMd5,
      @RequestParam("fileName") String fileName, @RequestParam("fileDir") String fileDir) {
    CommonRespDto<FileExtractEntityDto> respDto = fileService.mergeChunks(fileMd5, fileName,
        fileDir);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), respDto.getMsg());
  }

  /**
   * 获取文件目录结构
   *
   * @param filePath
   * @return
   */
  @GetMapping("/build-tree")
  public Result<ZipNodeDto> buildTree(
      @RequestParam(value = "filePath", required = false) @NotNull(message = "文件夹路径不能为空") String filePath) {
    CommonRespDto<ZipNodeDto> respDto = fileService.buildTree(filePath);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


}
