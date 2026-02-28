package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.UploadFilesService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.UploadFilesResp;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.UploadFilesMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.UploadFilesPo;
import cn.voicecomm.ai.voicesagex.console.util.util.FileTypeCheckUtils;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class UploadFilesServiceImpl extends ServiceImpl<UploadFilesMapper, UploadFilesPo> implements
    UploadFilesService {

  /**
   * 文档扩展名列表
   */
  private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList("txt", "pdf", "doc", "docx",
      "xls", "xlsx", "ppt", "pptx", "csv");

  /**
   * 无效字符列表
   */
  private static final List<Character> INVALID_CHARS = Arrays.asList('/', '\\', ':', '*', '?', '"',
      '<', '>', '|');

  /**
   * 最大文件名长度
   */
  private static final int MAX_FILENAME_LENGTH = 200;

  /**
   * 文件上传路径前缀
   */
  @Value("${file.upload}")
  private String uploadDir;


  @Override
  public CommonRespDto<UploadFilesResp> upload(MultipartFile file) {
    log.info("开始上传文件: {}", file.getOriginalFilename());

    if (file.isEmpty()) {
      log.warn("上传文件为空");
      return CommonRespDto.error("文件不能为空");
    }

    if (StrUtil.isBlank(file.getContentType())) {
      log.warn("文件类型为空: {}", file.getOriginalFilename());
      return CommonRespDto.error("不支持的文件类型");
    }

    if (StrUtil.isBlank(file.getName())) {
      log.warn("文件名称不存在");
      return CommonRespDto.error("文件名称不存在");
    }

    try {
      log.info("处理文件上传: {}, 大小: {} bytes, 类型: {}", file.getOriginalFilename(),
          file.getSize(), file.getContentType());

      UploadFilesPo uploadFile = uploadFile(file.getOriginalFilename(), file.getBytes(),
          file.getContentType(), null, null);

      log.info("文件上传成功: {}, 文件ID: {}", file.getOriginalFilename(), uploadFile.getId());

      UploadFilesResp uploadFilesResp = new UploadFilesResp();
      BeanUtil.copyProperties(uploadFile, uploadFilesResp);

      // 添加JSON格式的日志输出
      log.info("返回的UploadFilesResp实体信息: {}", JSONUtil.toJsonStr(uploadFilesResp));

      return CommonRespDto.success(uploadFilesResp);
    } catch (Exception e) {
      log.error("上传文件失败，文件名: {}", file.getOriginalFilename(), e);
      return CommonRespDto.error("上传失败");
    }
  }

  @Override
  public CommonRespDto<UploadFilesResp> remoteFileUpload(String url) {
    try {
      log.info("开始远程文件上传: {}", url);

      HttpResponse resp;
      HttpRequest request;
      request = HttpUtil.createRequest(Method.HEAD, url);
      log.info("发送HEAD请求到: {}", url);
      resp = request.execute();
      log.info("HEAD请求结果：状态码:{},body:{}", resp.getStatus(), resp.body());
      if (!resp.isOk()) {
        log.warn("HEAD请求失败，状态码:{},body:{}，尝试GET请求", resp.getStatus(), resp.body());
        request = HttpUtil.createGet(url);
        resp = request.execute();
        log.info("GET请求结果：状态码:{},body:{}", resp.getStatus(), resp.body());
      }

      if (!resp.isOk()) {
        log.error("远程文件获取失败，URL: {}, 状态码:{},body:{}", url, resp.getStatus(),
            resp.body());
        return CommonRespDto.error("无法获取远程文件");
      }

      UploadFilesPo uploadFilesPo = guessFileInfoFromResponse(resp, url);
      log.info("解析远程文件信息: 文件名={}, MIME类型={}, 大小={}", uploadFilesPo.getName(),
          uploadFilesPo.getMime_type(), uploadFilesPo.getSize());

      log.info("返回的uploadFilesPo实体信息: {}", JSONUtil.toJsonStr(uploadFilesPo));

      // 获取文件内容
      byte[] content;
      if ("GET".equals(request.getMethod().name())) {
        content = resp.bodyBytes();
      } else {
        log.info("再次发送GET请求获取文件内容: {}", url);
        content = HttpUtil.createGet(url).execute().bodyBytes();
      }

      log.info("获取远程文件内容完成，大小: {} bytes", content.length);

      // 上传文件
      UploadFilesPo uploadedFile = uploadFile(uploadFilesPo.getName(), content,
          uploadFilesPo.getMime_type(), null, url);

      log.info("远程文件上传完成: {}, 文件ID: {}", url, uploadedFile.getId());

      UploadFilesResp uploadFilesResp = new UploadFilesResp();
      BeanUtil.copyProperties(uploadedFile, uploadFilesResp);

      log.info("返回的实体信息: {}", JSONUtil.toJsonStr(uploadFilesResp));

      return CommonRespDto.success(uploadFilesResp);
    } catch (Exception e) {
      if (StrUtil.contains(e.getMessage(),"UnknownHostException")) {
        return CommonRespDto.error("url不正确", null);
      }
      log.error("远程文件上传失败，URL: {}", url, e);
      return CommonRespDto.error(e.getMessage(), null);
    }
  }


  /**
   * 上传文件
   *
   * @param filename  文件名
   * @param content   文件内容
   * @param mimetype  MIME类型
   * @param source    来源（可选，用于数据集）
   * @param sourceUrl 源URL（可选）
   * @return UploadFile 上传文件实体
   */
  public UploadFilesPo uploadFile(String filename, byte[] content, String mimetype, String source,
      String sourceUrl) {
    log.info("开始处理文件上传: 文件名={}, MIME类型={}, 来源={}, 是否为远程文件={}", filename,
        mimetype, source, StrUtil.isNotBlank(sourceUrl));

    // 获取文件扩展名
    String extension = getFileExtension(filename);
    log.info("文件扩展名: {}", extension);

    // 检查文件名是否包含无效字符
    if (containsInvalidCharacters(filename)) {
      log.warn("文件名包含无效字符: {}", filename);
      throw new IllegalArgumentException("Filename contains invalid characters");
    }

    // 限制文件名长度
    if (filename.length() > MAX_FILENAME_LENGTH) {
      log.info("文件名过长，进行截断: 原始长度={}", filename.length());
      filename = truncateFilename(filename, extension);
      log.info("截断后文件名: {}", filename);
    }

    // 数据集文件类型验证
    if ("datasets".equals(source) && !DOCUMENT_EXTENSIONS.contains(extension)) {
      log.warn("数据集不支持的文件类型: {}", extension);
      throw new RuntimeException("Unsupported file type for datasets");
    }

    // 获取文件大小
    int fileSize = content.length;
    log.info("文件大小: {} bytes", fileSize);

    // 检查文件大小是否超限
    if (!isFileSizeWithinLimit(extension, fileSize)) {
      log.warn("文件大小超出限制: 文件大小={}, 扩展名={}", fileSize, extension);
      throw new RuntimeException("File size exceeds limit");
    }

    // 生成文件UUID
    String fileUuid = UUID.randomUUID().toString();
    log.info("生成文件UUID: {}", fileUuid);

    // 获取租户ID
    String currentTenantId = "";
    log.info("当前租户ID: {}", currentTenantId);

    // 生成文件键
    String fileKey = String.format(uploadDir + "node_file%s%s.%s",
        StrUtil.isNotBlank(currentTenantId) ? ("/" + currentTenantId) : "", "/" + fileUuid,
        extension);
    log.info("生成文件存储路径: {}", fileKey);

    // 保存文件到存储
    log.info("开始保存文件到存储系统");
    FileUtil.writeBytes(content, fileKey);
    log.info("文件保存完成");

    // 创建UploadFile实体
    UploadFilesPo uploadFile = createUploadFile(currentTenantId, fileKey, filename, fileSize,
        extension, mimetype, content, sourceUrl);

    // 保存到数据库
    log.info("开始保存文件信息到数据库");
    this.save(uploadFile);
    log.info("文件信息保存完成，记录ID: {}", uploadFile.getId());

    return uploadFile;
  }


  /**
   * 获取文件扩展名
   */
  private String getFileExtension(String filename) {
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1).toLowerCase();
  }

  /**
   * 检查文件名是否包含无效字符
   */
  private boolean containsInvalidCharacters(String filename) {
    return filename.chars().mapToObj(ch -> (char) ch).anyMatch(INVALID_CHARS::contains);
  }

  /**
   * 截断文件名
   */
  private String truncateFilename(String filename, String extension) {
    int maxNameLength = MAX_FILENAME_LENGTH - extension.length() - 1; // -1 for dot
    String nameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
    return nameWithoutExtension.substring(0, Math.min(nameWithoutExtension.length(), maxNameLength))
        + "." + extension;
  }

  /**
   * 检查文件大小是否在限制内
   */
  private boolean isFileSizeWithinLimit(String extension, int fileSize) {
    long fileSizeLimit = getFileSizeLimit(extension);
    return fileSize <= fileSizeLimit;
  }

  /**
   * 获取文件大小限制
   */
  private long getFileSizeLimit(String extension) {
    if (FileTypeCheckUtils.isImageExtension(extension)) {
      return FileSizeConfig.uploadImageFileSizeLimit * 1024 * 1024;
    } else if (FileTypeCheckUtils.isVideoExtension(extension)) {
      return FileSizeConfig.uploadVideoFileSizeLimit * 1024 * 1024;
    } else if (FileTypeCheckUtils.isAudioExtension(extension)) {
      return FileSizeConfig.uploadAudioFileSizeLimit * 1024 * 1024;
    } else {
      return FileSizeConfig.uploadFileSizeLimit * 1024 * 1024;
    }
  }

  /**
   * 创建UploadFile实体
   */
  private UploadFilesPo createUploadFile(String tenantId, String fileKey, String filename,
      int fileSize, String extension, String mimetype, byte[] content, String sourceUrl) {
    UploadFilesPo uploadFile = new UploadFilesPo();
    uploadFile.setTenant_id(tenantId != null ? tenantId : "");
    uploadFile.setStorage_type(
        StrUtil.isNotBlank(sourceUrl) ? "" : FileSizeConfig.localStorageType);
    uploadFile.setKey(fileKey);
    uploadFile.setName(filename);
    uploadFile.setSize(fileSize);
    uploadFile.setExtension(extension);
    uploadFile.setMime_type(mimetype);
    uploadFile.setCreateTime(LocalDateTime.now());
    uploadFile.setCreateBy(UserAuthUtil.getUserId());
    uploadFile.setUsed(false);
    uploadFile.setHash(calculateHash(content));
    uploadFile.setSource_url(sourceUrl);

    return uploadFile;
  }


  /**
   * 计算文件哈希值
   */
  private String calculateHash(byte[] content) {
    log.info("开始计算文件哈希值，内容大小: {} bytes", content.length);
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA3-256");
      byte[] hashBytes = digest.digest(content);
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      log.info("文件哈希值计算完成");
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      log.error("计算文件哈希值失败", e);
      return "";
    }
  }


  /**
   * 从HTTP响应中猜测文件信息
   *
   * @param response HTTP响应
   * @return 文件信息
   */
  public static UploadFilesPo guessFileInfoFromResponse(HttpResponse response, String url) {

    // 1. 从URL解析文件名
    String filename = extractFilenameFromUrl(url);

    // 2. 如果无法从URL获取，尝试Content-Disposition头
    if (StrUtil.isBlank(filename)) {
      String contentDisposition = response.header("Content-Disposition");
      if (StrUtil.isBlank(contentDisposition)) {
        filename = extractFilenameFromContentDisposition(contentDisposition);
      }
    }

    // 3. 如果仍然没有文件名，生成唯一名称
    if (StrUtil.isBlank(filename)) {
      filename = java.util.UUID.randomUUID().toString();
    }

    // 4. 猜测MIME类型
    String mimetype = guessMimeType(filename, url, response);

    // 5. 处理文件扩展名
    String extension = extractExtension(filename);
    if (StrUtil.isBlank(extension) || extension.isEmpty()) {
      extension = guessExtensionFromMimeType(mimetype);
      if (StrUtil.isBlank(extension)) {
        extension = ".bin";
      }
      filename = filename + extension;
    }

    // 6. 获取文件大小
    long size = -1;
    String contentLength = response.header("Content-Length");
    if (contentLength != null) {
      try {
        size = Long.parseLong(contentLength);
      } catch (NumberFormatException e) {
        // 忽略解析错误，使用默认值
      }
    }

    return UploadFilesPo.builder().name(filename).extension(extension)
        .mime_type(mimetype).size((int) size).build();
  }

  /**
   * 从URL中提取文件名
   */
  private static String extractFilenameFromUrl(String url) {
    try {
      URL urlObj = URI.create(url).toURL();
      String path = urlObj.getPath();
      if (path != null && !path.isEmpty()) {
        return path.substring(path.lastIndexOf('/') + 1);
      }
    } catch (Exception e) {
      // 忽略异常
    }
    return null;
  }

  /**
   * 从Content-Disposition头中提取文件名
   */
  private static String extractFilenameFromContentDisposition(String contentDisposition) {
    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("filename=\"?([^\"]+)\"?");
    java.util.regex.Matcher matcher = pattern.matcher(contentDisposition);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  /**
   * 猜测MIME类型
   */
  private static String guessMimeType(String filename, String url, HttpResponse response) {
    // 1. 从文件名猜测
    String mimetype = guessMimeTypeFromFilename(filename);

    // 2. 如果失败，从URL猜测
    if (StrUtil.isBlank(mimetype)) {
      mimetype = guessMimeTypeFromFilename(url);
    }

    // 3. 如果仍然失败，使用Content-Type头
    if (StrUtil.isBlank(mimetype)) {
      mimetype = response.header("Content-Type");
      if (StrUtil.isBlank(mimetype)) {
        mimetype = "application/octet-stream";
      }
    }

    return mimetype;
  }

  /**
   * 从文件名猜测MIME类型
   */
  private static String guessMimeTypeFromFilename(String filename) {
    if (filename == null) {
      return null;
    }

    String extension = extractExtension(filename);
    if (extension == null) {
      return null;
    }

// 简单的MIME类型映射
    return switch (extension.toLowerCase()) {
      case ".jpg", ".jpeg" -> "image/jpeg";
      case ".png" -> "image/png";
      case ".gif" -> "image/gif";
      case ".bmp" -> "image/bmp";
      case ".webp" -> "image/webp";
      case ".svg" -> "image/svg+xml";
      case ".pdf" -> "application/pdf";
      case ".txt" -> "text/plain";
      case ".html", ".htm" -> "text/html";
      case ".css" -> "text/css";
      case ".js" -> "application/javascript";
      case ".json" -> "application/json";
      case ".xml" -> "application/xml";
      case ".zip" -> "application/zip";
      case ".rar" -> "application/x-rar-compressed";
      case ".7z" -> "application/x-7z-compressed";
      case ".mp3" -> "audio/mpeg";
      case ".wav" -> "audio/wav";
      case ".mp4" -> "video/mp4";
      case ".avi" -> "video/x-msvideo";
      case ".mov" -> "video/quicktime";
      case ".doc" -> "application/msword";
      case ".docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
      case ".xls" -> "application/vnd.ms-excel";
      case ".xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
      case ".ppt" -> "application/vnd.ms-powerpoint";
      case ".pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
      default -> "";
    };
  }

  /**
   * 提取文件扩展名
   */
  private static String extractExtension(String filename) {
    if (filename == null) {
      return null;
    }
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex > 0) {
      return filename.substring(lastDotIndex);
    }
    return null;
  }

  /**
   * 从MIME类型猜测扩展名
   */
  private static String guessExtensionFromMimeType(String mimetype) {
    if (mimetype == null) {
      return null;
    }

    return switch (mimetype.toLowerCase()) {
      case "image/jpeg" -> ".jpg";
      case "image/png" -> ".png";
      case "image/gif" -> ".gif";
      case "application/pdf" -> ".pdf";
      case "text/plain" -> ".txt";
      case "application/json" -> ".json";
      case "application/xml" -> ".xml";
      case "text/html" -> ".html";
      case "text/css" -> ".css";
      case "application/javascript" -> ".js";
      default -> null;
    };
  }

  public static class FileSizeConfig {

    public static String localStorageType = "local";
    public static String remoteStorageType = "local";
    public static Integer uploadFileSizeLimit = 100; // MB
    public static Integer uploadImageFileSizeLimit = 10; // MB
    public static Integer uploadVideoFileSizeLimit = 500; // MB
    public static Integer uploadAudioFileSizeLimit = 50; // MB
  }
}