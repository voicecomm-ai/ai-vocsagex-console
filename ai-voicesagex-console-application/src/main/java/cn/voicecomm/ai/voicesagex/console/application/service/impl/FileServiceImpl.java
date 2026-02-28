package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.file.FileService;
import cn.voicecomm.ai.voicesagex.console.api.constant.RedisConstant;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.FileExtractEntityDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.FileConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelConverter;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
import cn.voicecomm.ai.voicesagex.console.util.util.FileReadUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author adminst
 */
@Service
@Slf4j
@DubboService
@RefreshScope
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileConverter fileConverter;

  private final ModelConverter modelConverter;

  private final RedissonClient redissonClient;

  /**
   * 文件上传路径前缀
   */
  @Value("${file.upload}")
  private String uploadDir;

  private static final String TEMP_DIR = "tempChunks";

  /**
   * 常见图片的文件扩展名列表
   */
  private static final List<String> ZIP_EXTENSIONS = Arrays.asList("zip");


  @Override
  public CommonRespDto<String> uploadZip(String fileDir, MultipartFile file) {
    if (file.isEmpty()) {
      return CommonRespDto.error("请选择文件");
    }
    String extName = FileUtil.extName(file.getOriginalFilename()).toLowerCase();
    if (!ZIP_EXTENSIONS.contains(extName)) {
      return CommonRespDto.error("图片格式不正确");
    }
    if (file.getSize() > 200 * 1024 * 1024) {
      return CommonRespDto.error("压缩包大小不能超过200MB");
    }
    try {
      String dataFormat = DatePattern.PURE_DATE_FORMAT.format(new Date());
      String formatted = DatePattern.PURE_DATETIME_MS_FORMAT.format(new Date());
      String randomString = RandomUtil.randomString(4);
      // 保存到文件服务器
      String filePath = String.join("/", fileDir, dataFormat, formatted + randomString,
          file.getOriginalFilename());
      String path = uploadDir + filePath;
      FileUtil.touch(path);
      FileUtil.writeBytes(file.getBytes(), path);
      String realPath = StrUtil.replaceFirst(path, "data1", "file");
      return CommonRespDto.success(realPath);
    } catch (IOException e) {
      log.error("上传图片文件失败，文件名: {}", file.getOriginalFilename(), e);
      return CommonRespDto.error("上传失败");
    }
  }

  @Override
  public CommonRespDto<String> upload(String fileDir, MultipartFile file) {
    if (file.isEmpty()) {
      return CommonRespDto.error("请选择文件");
    }
    if (file.getSize() > 200 * 1024 * 1024) {
      return CommonRespDto.error("文件大小不能超过200MB");
    }
    try {
      // 保存到文件服务器
      String dataFormat = DatePattern.PURE_DATE_FORMAT.format(new Date());
      String formatted = DatePattern.PURE_DATETIME_MS_FORMAT.format(new Date());
      String randomString = RandomUtil.randomString(4);
      // 保存到文件服务器
      String filePath = String.join("/", fileDir, dataFormat, formatted + randomString,
          file.getOriginalFilename());
      String path = uploadDir + filePath;
      FileUtil.touch(path);
      FileUtil.writeBytes(file.getBytes(), path);
      String realPath = StrUtil.replaceFirst(path, "data1", "file");
      return CommonRespDto.success(realPath);
    } catch (IOException e) {
      log.error("上传图片文件失败，文件名: {}", file.getOriginalFilename(), e);
      return CommonRespDto.error("上传失败");
    }
  }

  @Override
  public CommonRespDto<Void> downloadEntry(String zipPath, String entryPath,
      HttpServletResponse response) {
    zipPath = FileReadUtil.replaceFirstFile(zipPath);
    File zipFileOnDisk = new File(zipPath);
    if (!zipFileOnDisk.exists()) {
      return CommonRespDto.error("文件不存在");
    }
    if (StrUtil.isBlank(entryPath)) {
      return downloadFullZip(zipFileOnDisk, response);
    }

    List<File> tempFiles = new ArrayList<>();
    try {
      File currentZipFile = zipFileOnDisk;
      String currentEntryPath = entryPath;

      while (true) {
        try (ZipFile zipFile = new ZipFile(currentZipFile, "GBK")) {
          // 找到第一级路径的第一个zip文件位置
          int nestedZipIndex = currentEntryPath.toLowerCase().indexOf(".zip/");
          if (nestedZipIndex == -1) {
            // 说明是最里面的文件或文件夹
            ZipArchiveEntry entry = zipFile.getEntry(currentEntryPath);
            if (entry == null) {
              return CommonRespDto.error("条目不存在");
            }
            if (entry.isDirectory()) {
              return downloadSubFolder(zipFile, currentEntryPath, response);
            } else {
              return downloadSingleFile(zipFile, entry, response);
            }
          }

          // 外层zip里的zip文件路径（带.zip）
          String outerZipEntry = currentEntryPath.substring(0, nestedZipIndex + 4);
          ZipArchiveEntry outerEntry = zipFile.getEntry(outerZipEntry);
          if (outerEntry == null) {
            return CommonRespDto.error("条目不存在");
          }
          if (outerEntry.isDirectory()) {
            return downloadSubFolder(zipFile, outerZipEntry, response);
          }

          // 解压外层zip内的zip到临时文件
          File nestedZipFile = File.createTempFile("nested-", ".zip");
          tempFiles.add(nestedZipFile);
          try (InputStream is = zipFile.getInputStream(
              outerEntry); OutputStream os = new FileOutputStream(nestedZipFile)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
              os.write(buffer, 0, len);
            }
          }

          // 准备下一轮循环，继续解析内部zip
          currentZipFile = nestedZipFile;
          // 更新路径，去掉前面已解开的zip部分
          currentEntryPath = currentEntryPath.substring(nestedZipIndex + 5);
        }
      }
    } catch (IOException e) {
      log.error("文件下载失败", e);
      return CommonRespDto.error("文件下载失败");
    } finally {
      // 删除临时文件
      for (File file : tempFiles) {
        boolean delete = file.delete();
        log.warn("临时文件地址：{}删除状态：{}", file.getAbsolutePath(), delete);
        if (file.exists() && !delete) {
          log.warn("未能删除临时文件: " + file.getAbsolutePath());
        }
      }
    }
  }

  @Override
  public CommonRespDto<FileExtractEntityDto> uploadExtractEntityFiles(String fileDir,
      MultipartFile file) {
    FileExtractEntityDto fileExtractEntityDto = FileExtractEntityDto.builder().build();
    CommonRespDto<String> commonRespDto = this.upload(fileDir, file);
    if (commonRespDto.isOk()) {
      fileExtractEntityDto.setFilePath(commonRespDto.getData());
      // 文件解析结构
      fileExtractEntityDto.setZipNodeList(
          fileConverter.zipNodePoListToDtoList(FileReadUtil.extractEntity(file)));
    } else {
      return CommonRespDto.error(commonRespDto.getMsg());
    }
    return CommonRespDto.success(fileExtractEntityDto);
  }

  @Override
  public CommonRespDto<Set<Integer>> getUploadedChunks(String fileMd5) {
    log.info("获取上传分片记录：{}", fileMd5);
    Integer userId = UserAuthUtil.getUserId();
    return CommonRespDto.success(
        redissonClient.getSet(RedisConstant.UPLOAD_CHUNKS_KEY + fileMd5 + "_" + userId));
  }

  @Override
  public CommonRespDto<Integer> uploadChunk(String fileMd5, Integer chunkIndex, Integer totalChunks,
      String fileDir, MultipartFile file) {
    log.info("----上传分片开始----MD5：{}, 当前第{}个分片，总分片数{}", fileMd5, chunkIndex,
        totalChunks);
    Integer userId = UserAuthUtil.getUserId();
    String lockKey = RedisConstant.UPLOAD_LOCK + fileMd5 + "_" + userId + "_" + chunkIndex;
    RLock lock = redissonClient.getLock(lockKey);
    try {
      boolean locked = lock.tryLock(10, 120, TimeUnit.SECONDS);
      if (!locked) {
        return CommonRespDto.error("该文件正在上传，请稍后重试");
      }
      // 临时分片保存路径
      String tempPath = String.join("/", uploadDir, fileDir, TEMP_DIR, fileMd5 + "_" + userId);
      File chunkFolder = new File(tempPath);
      if (!chunkFolder.exists()) {
        chunkFolder.mkdirs();
      }
      File chunkFile = new File(chunkFolder, chunkIndex + ".part");
      file.transferTo(chunkFile);
      // 使用 Redis Set 记录已上传分片
      String redisKey = RedisConstant.UPLOAD_CHUNKS_KEY + fileMd5 + "_" + userId;
      RSet<Integer> chunkSet = redissonClient.getSet(redisKey);
      chunkSet.add(chunkIndex);
      // 设置两天的过期时间
      chunkSet.expire(Duration.ofDays(2));
      return CommonRespDto.success(chunkIndex);
    } catch (InterruptedException e) {
      log.error("上传分片获取锁异常", e);
      Thread.currentThread().interrupt();
      return CommonRespDto.error("上传分片获取锁异常");
    } catch (Exception e) {
      log.error("第{}个分片上传失败", chunkIndex, e);
      return CommonRespDto.error(
          StrUtil.format("第{}个分片上传失败", chunkIndex));
    } finally {
      // 只释放当前线程持有的锁，避免非法释放
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }


  @Override
  public CommonRespDto<FileExtractEntityDto> mergeChunks(String fileMd5, String fileName,
      String fileDir) {
    log.info("----开始合并文件夹----，MD5：{}，文件名称：{}，文件目录：{}", fileMd5, fileName, fileDir);
    FileExtractEntityDto fileExtractEntityDto = new FileExtractEntityDto();
    Integer userId = UserAuthUtil.getUserId();
    RLock lock = redissonClient.getLock(RedisConstant.UPLOAD_LOCK + fileMd5 + "_" + userId);
    try {
      // 尝试获取锁
      if (!lock.tryLock(1, 5, TimeUnit.MINUTES)) {
        return CommonRespDto.error("该文件正在合并，请稍后重试");
      }
      // 检查 Redis 分片记录
      String redisKey = RedisConstant.UPLOAD_CHUNKS_KEY + fileMd5 + "_" + userId;
      Set<Integer> chunks = redissonClient.getSet(redisKey);
      if (chunks == null || chunks.isEmpty()) {
        return CommonRespDto.error("没有找到分片记录");
      }

      // 分片目录
      String chunkFolderPath = String.join("/", uploadDir, fileDir, TEMP_DIR,
          fileMd5 + "_" + userId);
      File chunkFolder = new File(chunkFolderPath);
      File[] files = chunkFolder.listFiles((dir, name) -> name.endsWith(".part"));
      if (files == null || files.length != chunks.size()) {
        return CommonRespDto.error("分片数量不完整，无法合并");
      }
      // 按分片序号排序
      List<File> sortedChunks = Arrays.stream(files)
          .sorted(Comparator.comparingInt(f -> Integer.parseInt(f.getName().replace(".part", ""))))
          .collect(Collectors.toList());
      // 生成最终文件路径
      String dataFormat = DatePattern.PURE_DATE_FORMAT.format(new Date());
      String formatted = DatePattern.PURE_DATETIME_MS_FORMAT.format(new Date());
      String randomString = RandomUtil.randomString(4);

      String finalFilePath = String.join("/", fileDir, dataFormat, formatted + randomString,
          fileName);
      File finalFile = new File(uploadDir, finalFilePath);
      finalFile.getParentFile().mkdirs();
      // 缓冲区大小（8MB）
      int bufferSize = 8 * 1024 * 1024;
      ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
      // 输出 FileChannel
      try (FileChannel outChannel = FileChannel.open(finalFile.toPath(), StandardOpenOption.CREATE,
          StandardOpenOption.WRITE)) {
        for (File chunk : sortedChunks) {
          try (FileChannel inChannel = FileChannel.open(chunk.toPath(), StandardOpenOption.READ)) {
            buffer.clear();
            while (inChannel.read(buffer) > 0) {
              buffer.flip();
              while (buffer.hasRemaining()) {
                outChannel.write(buffer);
              }
              buffer.clear();
            }
          }
          // 删除已合并分片
          Files.deleteIfExists(chunk.toPath());
        }
      }
      // 删除分片目录
      Files.deleteIfExists(chunkFolder.toPath());
      // 清理 Redis 分片记录
      redissonClient.getSet(redisKey).delete();
      // 返回文件路径
      String realPath = StrUtil.replaceFirst(finalFile.getAbsolutePath(), "data1", "file");
      log.info("合并完成的文件路径：{}", realPath);
      fileExtractEntityDto.setFilePath(realPath);
      return CommonRespDto.success(fileExtractEntityDto);
    } catch (InterruptedException e) {
      log.error("合并分片获取锁异常", e);
      Thread.currentThread().interrupt();
      return CommonRespDto.error("合并分片获取锁异常");
    } catch (Exception e) {
      log.error("文件合并失败", e);
      return CommonRespDto.error("文件合并失败: " + e.getMessage());
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

  @Override
  public CommonRespDto<ZipNodeDto> buildTree(String filePath) {
    ZipNodePo zipNodePo = FileReadUtil.buildZipTree(filePath);
    return CommonRespDto.success(modelConverter.zipNodePoToDto(zipNodePo));
  }


  /**
   * 下载整个文件夹
   *
   * @param zipFile
   * @param response
   * @return
   */
  private CommonRespDto<Void> downloadFullZip(File zipFile, HttpServletResponse response) {
    try {
      response.setContentType("application/zip");
      String encoded = URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8)
          .replaceAll("\\+", "%20");
      response.setHeader("Content-Disposition",
          "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
      response.setHeader("Content-Length", String.valueOf(zipFile.length()));

      try (InputStream in = new FileInputStream(
          zipFile); OutputStream out = response.getOutputStream()) {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
          out.write(buffer, 0, len);
        }
        out.flush();
      }
      return CommonRespDto.success();
    } catch (IOException e) {
      log.error("主压缩包下载失败", e);
      return CommonRespDto.error("主压缩包下载失败");
    }
  }

  /**
   * 下载 ZIP 中的文件夹
   *
   * @param zipFile
   * @param folderPath
   * @param response
   * @return
   * @throws IOException
   */
  private CommonRespDto<Void> downloadSubFolder(ZipFile zipFile, String folderPath,
      HttpServletResponse response) throws IOException {
    // 去掉末尾的 `/`，避免拼出来的文件名为“xxx/.zip”
    String cleanedFolderPath = CharSequenceUtil.removeSuffix(folderPath, "/");
    String folderName = new File(cleanedFolderPath).getName();
    String encodedName = encodeFileName(folderName);
    response.setContentType("application/zip");
    response.setHeader("Content-Disposition",
        "attachment; filename=\"" + encodedName + "\"; filename*=UTF-8''" + encodedName);
    try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(response.getOutputStream())) {
      Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
      while (entries.hasMoreElements()) {
        ZipArchiveEntry entry = entries.nextElement();
        if (entry.getName().startsWith(folderPath) && !entry.isDirectory()) {
          String relativeName = entry.getName().substring(folderPath.length());
          ZipArchiveEntry newEntry = new ZipArchiveEntry(relativeName);
          zos.putArchiveEntry(newEntry);
          IOUtils.copy(zipFile.getInputStream(entry), zos);
          zos.closeArchiveEntry();
        }
      }
      zos.finish();
    }
    return CommonRespDto.success();
  }

  /**
   * 对下载文件名进行清洗和编码，自动补后缀
   *
   * @param name 原始文件名（不带后缀）
   * @return 编码后的文件名字符串
   */
  private String encodeFileName(String name) {
    boolean endsWithZip = name.toLowerCase().endsWith(".zip");
    String baseName;
    String extension = ".zip";

    if (endsWithZip) {
      baseName = name.substring(0, name.length() - 4); // 去掉 ".zip"
    } else {
      baseName = name;
    }
    // 清洗非法字符
    // Windows 非法字符
    // HTTP header 可能冲突字符
    // 替换空格
    // 去掉结尾点
    String safeName = baseName.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("[',;=]", "_")
        .replaceAll("\\s+", "").replaceAll("\\.", "_");

    // 编码为 UTF-8 且空格转义
    return URLEncoder.encode(safeName + extension, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
  }


  /**
   * 下载 ZIP 中的单文件
   *
   * @param zipFile
   * @param entry
   * @param response
   * @return
   * @throws IOException
   */
  private CommonRespDto<Void> downloadSingleFile(ZipFile zipFile, ZipArchiveEntry entry,
      HttpServletResponse response) throws IOException {
    response.setContentType("application/octet-stream");
    String fileName = new File(entry.getName()).getName();
    String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

    response.setHeader("Content-Disposition",
        "attachment; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded);
    response.setHeader("Content-Length", String.valueOf(entry.getSize()));

    try (InputStream is = zipFile.getInputStream(
        entry); OutputStream os = response.getOutputStream()) {
      byte[] buffer = new byte[4096];
      int len;
      while ((len = is.read(buffer)) != -1) {
        os.write(buffer, 0, len);
      }
      os.flush();
    }
    return CommonRespDto.success();
  }


}
