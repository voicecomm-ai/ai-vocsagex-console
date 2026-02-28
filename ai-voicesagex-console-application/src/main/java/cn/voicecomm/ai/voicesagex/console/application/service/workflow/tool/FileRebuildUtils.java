package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.Variable;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.VariableEntityType;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.UploadFilesMapper;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start.StartNode;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.UploadFilesPo;
import cn.voicecomm.ai.voicesagex.console.util.util.FileTypeCheckUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RefreshScope
public class FileRebuildUtils {

  /**
   * 对话接口前缀
   */
  @Value("${voicesagexConsole.loginUrl}")
  private String loginUrl;

  private final UploadFilesMapper uploadFilesMapper;

  public FileRebuildUtils(UploadFilesMapper uploadFilesMapper) {
    this.uploadFilesMapper = uploadFilesMapper;
  }

  public static String getUrlPrefix(String loginUrl) {
    return loginUrl.substring(0, loginUrl.lastIndexOf("/"));
  }

  public String replaceStoragePath(String path) {
    return path.replaceAll("/data1", "/file");
  }

  /**
   * 为开始节点重建用户输入中的文件
   *
   * @param tenantId      租户ID
   * @param startNodeData 开始节点数据
   * @param userInputs    用户输入
   * @return 重建后的用户输入
   */
  public Map<String, Object> rebuildFileForUserInputsInStartNode(
      String tenantId,
      StartNode startNodeData,
      Map<String, Object> userInputs) {

    Map<String, Object> inputsCopy = new HashMap<>(userInputs);
    if (CollUtil.isEmpty(startNodeData.getVariables())) {
      return inputsCopy;
    }

    for (Variable variable : startNodeData.getVariables()) {
      // 只处理文件类型的变量
      if (!Objects.equals(variable.getType(), "file") &&
          !Objects.equals(variable.getType(), "file-list")) {
        continue;
      }

      // 检查变量是否在用户输入中
      if (!userInputs.containsKey(variable.getVariable())) {
        continue;
      }

      Object value = userInputs.get(variable.getVariable());
      Object file = rebuildSingleFile(tenantId, value,
          VariableEntityType.fromValue(variable.getType()));
      inputsCopy.put(variable.getVariable(), file);
    }

    return inputsCopy;
  }

  /**
   * 重建单个文件
   *
   * @param tenantId           租户ID
   * @param value              文件值
   * @param variableEntityType 变量实体类型
   * @return 重建后的文件对象
   */
  public Object rebuildSingleFile(String tenantId, Object value,
      VariableEntityType variableEntityType) {
    if (variableEntityType == VariableEntityType.FILE) {
      if (!(value instanceof Map)) {
        throw new IllegalArgumentException("Expected Map for file object, got " + value.getClass());
      }
      return buildFromMapping((Map<String, Object>) value, tenantId, null, false);
    } else if (variableEntityType == VariableEntityType.FILE_LIST) {
      if (!(value instanceof List)) {
        throw new IllegalArgumentException(
            "Expected List for file list object, got " + value.getClass());
      }
      List<?> valueList = (List<?>) value;
      if (valueList.isEmpty()) {
        return new ArrayList<>();
      }
      if (!(valueList.getFirst() instanceof Map)) {
        throw new IllegalArgumentException(
            "Expected Map for first element in the file list, got " + valueList.getFirst()
                .getClass());
      }
      return buildFromMappings((List<Map<String, Object>>) value, tenantId, null, false);
    } else {
      throw new IllegalStateException("Unreachable");
    }
  }


  /**
   * 从映射构建文件对象
   *
   * @param mapping              文件映射信息
   * @param tenantId             租户ID
   * @param config               文件上传配置
   * @param strictTypeValidation 是否严格类型验证
   * @return 文件对象
   */
  public File buildFromMapping(
      Map<String, Object> mapping,
      String tenantId,
      FileUploadConfig config,
      boolean strictTypeValidation) {

    // 1. 获取传输方法
    String transferMethodStr = (String) mapping.get("transfer_method");
    FileTransferMethod transferMethod = FileTransferMethod.fromValue(transferMethodStr);

    // 2. 定义构建函数映射
    Map<FileTransferMethod, Function<BuildContext, File>> buildFunctions = new HashMap<>();
    buildFunctions.put(FileTransferMethod.LOCAL_FILE, this::buildFromLocalFile);
    buildFunctions.put(FileTransferMethod.REMOTE_URL, this::buildFromRemoteUrl);
    buildFunctions.put(FileTransferMethod.TOOL_FILE, this::buildFromToolFile);

    // 3. 获取对应的构建函数
    Function<BuildContext, File> buildFunc = buildFunctions.get(transferMethod);
    if (buildFunc == null) {
      throw new IllegalArgumentException("Invalid file transfer method: " + transferMethod);
    }

    // 4. 创建构建上下文
    BuildContext context = new BuildContext(
        mapping,
        tenantId,
        transferMethod,
        strictTypeValidation
    );

    // 5. 构建文件对象
    File file = buildFunc.apply(context);

    // 6. 验证文件配置
    if (config != null && !isFileValidWithConfig(
        (FileType) mapping.getOrDefault("type", FileType.CUSTOM),
        file.getType() != null ? file.getExtension() : "",
        file.getTransfer_method(),
        config)) {
      throw new IllegalArgumentException("File validation failed for file: " + file.getFilename());
    }

    return file;
  }


  public List<File> buildFromMappings(
      List<Map<String, Object>> mapping,
      String tenantId,
      FileUploadConfig config,
      boolean strictTypeValidation) {
    return mapping.stream()
        .map(m -> buildFromMapping(m, tenantId, config, strictTypeValidation))
        .toList();
  }

  public boolean isFileMapping(Object inputValue) {
    if (!(inputValue instanceof Map)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>) inputValue;
    return map.containsKey("type") && map.containsKey("transfer_method");
  }

  public boolean isFileMappingList(Object inputValue) {
    if (!(inputValue instanceof List)) {
      return false;
    }

    @SuppressWarnings("unchecked")
    List<?> list = (List<?>) inputValue;

    // 检查所有元素是否都是字典且包含type和transfer_method
    return list.stream().allMatch(item -> {
      if (!(item instanceof Map)) {
        return false;
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) item;
      return map.containsKey("type") && map.containsKey("transfer_method");
    });
  }

  /**
   * 构建上下文类
   *
   * @param mapping Getters
   */
  private record BuildContext(Map<String, Object> mapping, String tenantId,
                              FileTransferMethod transferMethod, boolean strictTypeValidation) {

  }

  /**
   * 从本地文件构建
   */
  private File buildFromLocalFile(BuildContext context) {
    Map<String, Object> mapping = context.mapping();
    String tenantId = context.tenantId();
    FileTransferMethod transferMethod = context.transferMethod();
    boolean strictTypeValidation = context.strictTypeValidation();

    Integer uploadFileId = (Integer) mapping.get("upload_file_id");
    if (uploadFileId == null) {
      throw new IllegalArgumentException("Invalid upload file id");
    }

    // 从数据库获取上传文件信息
    UploadFilesPo uploadFile = getUploadFileById(uploadFileId, tenantId);
    if (uploadFile == null) {
      throw new IllegalArgumentException("Invalid upload file");
    }

    // 检测文件类型
    FileType detectedFileType = standardizeFileType(
        "." + uploadFile.getExtension(),
        uploadFile.getMime_type()
    );

    // 获取指定类型
    String specifiedTypeStr = (String) mapping.get("type");
    FileType specifiedType = FileType.fromValue(specifiedTypeStr) != null ?
        FileType.fromValue(specifiedTypeStr) : FileType.CUSTOM;

    // 严格类型验证
    if (strictTypeValidation && !detectedFileType.equals(specifiedType)) {
      throw new IllegalArgumentException(
          "Detected file type does not match the specified type. Please verify the file.");
    }

    FileType fileType = !FileType.CUSTOM.equals(specifiedType) ?
        specifiedType : detectedFileType;

    // 构建文件对象
    return File.builder()
        .id((String) mapping.get("id"))
        .filename(uploadFile.getName())
        .extension("." + uploadFile.getExtension())
        .mime_type(uploadFile.getMime_type())
        .tenant_id(tenantId)
        .type(fileType.getValue())
        .transfer_method(transferMethod)
        .remote_url(uploadFile.getSource_url())
        .related_id(String.valueOf(uploadFileId))
        .size(uploadFile.getSize())
        .storage_key(uploadFile.getKey())
        .url(getUrlPrefix(loginUrl) + replaceStoragePath(uploadFile.getKey()))
        .build();
  }

  /**
   * 从远程URL构建
   */
  private File buildFromRemoteUrl(BuildContext context) {
    Map<String, Object> mapping = context.mapping();
    String tenantId = context.tenantId();
    FileTransferMethod transferMethod = context.transferMethod();
    boolean strictTypeValidation = context.strictTypeValidation();

    // 检查是否有upload_file_id
    Integer uploadFileId = (Integer) mapping.get("upload_file_id");
    if (uploadFileId != null) {

      UploadFilesPo uploadFile = getUploadFileById(uploadFileId, tenantId);
      if (uploadFile == null) {
        throw new IllegalArgumentException("Invalid upload file");
      }

      FileType detectedFileType = standardizeFileType(
          "." + uploadFile.getExtension(),
          uploadFile.getMime_type()
      );

      String specifiedTypeStr = (String) mapping.get("type");
      if (strictTypeValidation && specifiedTypeStr != null &&
          !detectedFileType.equals(FileType.fromValue(specifiedTypeStr))) {
        throw new IllegalArgumentException(
            "Detected file type does not match the specified type. Please verify the file.");
      }

      FileType fileType = (specifiedTypeStr != null && !specifiedTypeStr.equals("custom")) ?
          FileType.fromValue(specifiedTypeStr) : detectedFileType;

      return File.builder()
          .id((String) mapping.get("id"))
          .filename(uploadFile.getName())
          .extension("." + uploadFile.getExtension())
          .mime_type(uploadFile.getMime_type())
          .tenant_id(tenantId)
          .type(fileType.getValue())
          .transfer_method(transferMethod)
          .remote_url(uploadFile.getSource_url())
          .related_id(String.valueOf(uploadFileId))
          .size(uploadFile.getSize())
          .storage_key(uploadFile.getKey())
          .url(getUrlPrefix(loginUrl) + replaceStoragePath(uploadFile.getKey()))
          .build();
    }
    return null;
//
//    // 处理远程URL
//    String url = (String) mapping.get("url");
//    if (url == null) {
//      url = (String) mapping.get("remote_url");
//    }
//    if (url == null || url.trim().isEmpty()) {
//      throw new IllegalArgumentException("Invalid file url");
//    }
//
//    // 获取远程文件信息
//    java.io.File fileInfo = getRemoteFileInfo(url);
//    String extension = guessExtensionFromMimeType(fileInfo.getMimeType());
//    if (extension == null) {
//      extension = ".bin";
//    }
//
//    FileType fileType = standardizeFileType(extension, fileInfo.getMimeType());
//    String specifiedTypeStr = (String) mapping.get("type");
//    if (specifiedTypeStr != null && !fileType.equals(FileType.valueOf(specifiedTypeStr))) {
//      throw new IllegalArgumentException(
//          "Detected file type does not match the specified type. Please verify the file.");
//    }
//
//    return File.builder()
//        .id((String) mapping.get("id"))
//        .filename(fileInfo.getFilename())
//        .tenantId(tenantId)
//        .type(fileType)
//        .transferMethod(transferMethod)
//        .remoteUrl(url)
//        .mimeType(fileInfo.getMimeType())
//        .extension(extension)
//        .size(fileInfo.getSize())
//        .storageKey("")
//        .build();
  }

  /**
   * 从工具文件构建
   */
  private File buildFromToolFile(BuildContext context) {
    Map<String, Object> mapping = context.mapping();
    String tenantId = context.tenantId();
    FileTransferMethod transferMethod = context.transferMethod();
    boolean strictTypeValidation = context.strictTypeValidation();

    Integer toolFileId = (Integer) mapping.get("tool_file_id");
    UploadFilesPo toolFile = getUploadFileById(toolFileId, tenantId);
    if (toolFile == null) {
      throw new IllegalArgumentException("ToolFile " + toolFileId + " not found");
    }

    String extension = "";
    if (toolFile.getKey().contains(".")) {
      extension = "." + toolFile.getKey().substring(toolFile.getKey().lastIndexOf(".") + 1);
    } else {
      extension = ".bin";
    }

    FileType detectedFileType = standardizeFileType(extension, toolFile.getMime_type());
    String specifiedTypeStr = (String) mapping.get("type");

    if (strictTypeValidation && specifiedTypeStr != null &&
        !detectedFileType.equals(FileType.valueOf(specifiedTypeStr))) {
      throw new IllegalArgumentException(
          "Detected file type does not match the specified type. Please verify the file.");
    }

    FileType fileType = (specifiedTypeStr != null && !specifiedTypeStr.equals("custom")) ?
        FileType.valueOf(specifiedTypeStr) : detectedFileType;

    return File.builder()
        .id((String) mapping.get("id"))
        .tenant_id(tenantId)
        .filename(toolFile.getName())
        .type(fileType.getValue())
        .transfer_method(transferMethod)
        .remote_url(toolFile.getSource_url())
        .related_id(toolFile.getId().toString())
        .extension(extension)
        .mime_type(toolFile.getMime_type())
        .size(toolFile.getSize())
        .storage_key(toolFile.getKey())
        .build();
  }

  /**
   * 检查文件是否与配置有效
   */
  private static boolean isFileValidWithConfig(
      FileType inputFileType,
      String fileExtension,
      FileTransferMethod fileTransferMethod,
      FileUploadConfig config) {

    // 检查文件类型是否在允许的范围内
    if (!config.getAllowedFileTypes().contains(inputFileType)) {
      return false;
    }

    // 检查文件扩展名是否在允许的范围内
    if (!config.getAllowedFileExtensions().contains(fileExtension.toLowerCase())) {
      return false;
    }

    // 检查传输方法是否在允许的范围内
    if (!config.getAllowedFileUploadMethods().contains(fileTransferMethod)) {
      return false;
    }

    return true;
  }

  // 辅助方法（需要根据实际实现补充）
  private UploadFilesPo getUploadFileById(Integer uploadFileId, String tenantId) {
    // 实现从数据库获取UploadFile的逻辑
    return uploadFilesMapper.selectById(uploadFileId);
  }

  private static FileType standardizeFileType(String extension, String mimeType) {
    FileType guessedType = null;

    if (extension != null && !extension.isEmpty()) {
      guessedType = getFileTypeByExtension(extension);
    }

    if (guessedType == null && mimeType != null && !mimeType.isEmpty()) {
      guessedType = getFileTypeByMimeType(mimeType);
    }

    return guessedType != null ? guessedType : FileType.CUSTOM;
  }


  public static FileType getFileTypeByExtension(String extension) {
    // 移除开头的点号
    extension = extension.startsWith(".") ? extension.substring(1) : extension;

    if (FileTypeCheckUtils.isImageExtension(extension)) {
      return FileType.IMAGE;
    } else if (FileTypeCheckUtils.isVideoExtension(extension)) {
      return FileType.VIDEO;
    } else if (FileTypeCheckUtils.isAudioExtension(extension)) {
      return FileType.AUDIO;
    } else if (FileTypeCheckUtils.isDocumentExtension(extension)) {
      return FileType.DOCUMENT;
    }
    return null;
  }

  public static FileType getFileTypeByMimeType(String mimeType) {
    if (mimeType.contains("image")) {
      return FileType.IMAGE;
    } else if (mimeType.contains("video")) {
      return FileType.VIDEO;
    } else if (mimeType.contains("audio")) {
      return FileType.AUDIO;
    } else if (mimeType.contains("text") || mimeType.contains("pdf")) {
      return FileType.DOCUMENT;
    } else {
      return FileType.CUSTOM;
    }
  }

  private static java.io.File getRemoteFileInfo(String url) {
    // 实现获取远程文件信息的逻辑
    return FileUtil.touch(url);
  }

  private static String getSignedFileUrl(String uploadFileId) {
    // 实现获取签名文件URL的逻辑
    return null;
  }

  @Data
  public static class FileUploadConfig {

    private ImageConfig imageConfig;
    private Set<FileType> allowedFileTypes;
    private Set<String> allowedFileExtensions;
    private Set<FileTransferMethod> allowedFileUploadMethods;
    private int numberLimits;
  }

  @Data
  public static class ImageConfig {

    private int numberLimits = 0;
    private List<FileTransferMethod> transferMethods = Collections.emptyList();
    private String detail;
  }
}

