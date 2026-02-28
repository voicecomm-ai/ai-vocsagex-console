package cn.voicecomm.ai.voicesagex.console.application.handler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.util.util.FileReadUtil;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author ryc
 * @description
 * @date 2025/7/11 15:55
 */
@Component
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class ModelTrainDeployHandler {

  private static final List<String> SAFE_ROOTS = List.of("/data1/voicesagex-console/upload/");

//  private static final List<String> SAFE_ROOTS = List.of("D:\\test");

//  public static void main(String[] args) {
//    ModelTrainDeployHandler modelTrainDeployHandler = new ModelTrainDeployHandler();
//
//    CommonRespDto<String> respDto = modelTrainDeployHandler.unzip("D:\\test\\source.zip");
//    System.out.println(respDto.getData());
//
////    CommonRespDto<String> respDto = modelTrainDeployHandler.ensureUnzipped("D:\\test\\source.zip");
////    System.out.println(respDto.getData());
//
////    CommonRespDto<String> respDto = modelTrainDeployHandler.createEmptyFolder("D:\\test\\target\\");
////    System.out.println(respDto.getData());
//
////    CommonRespDto<String> respDto = modelTrainDeployHandler.moveFolder(
////        "D:\\test\\source - 副本.zip", "D:\\test\\target2");
////    System.out.println(respDto.getData());
//
////    CommonRespDto<String> respDto = modelTrainDeployHandler.copyFolder("D:\\test\\target4\\",
////        "D:\\test\\target5");
////    System.out.println(respDto.getData());
//
////    CommonRespDto<String> respDto = modelTrainDeployHandler.deleteFolderOrFile(
////        "D:\\test\\target4\\source.zip");
////    System.out.println(respDto.getData());
//
////    System.out.println(modelTrainDeployHandler.getFirstSubDirName("D:\\test\\target4"));
//
//  }

  /**
   * 解压文件夹
   *
   * @param filePath
   * @return
   */
  public CommonRespDto<String> unzip(String filePath) {
    // 替换/file为/data1
    filePath = FileReadUtil.replaceFirstFile(filePath);
    String modelFilePath = CharSequenceUtil.EMPTY;
    try {
      CommonRespDto<String> modelRespDto = unzipFile(filePath);
      if (!modelRespDto.isOk()) {
        log.error("解压文件失败：{}", modelRespDto.getMsg());
        return CommonRespDto.error(modelRespDto.getMsg(), modelFilePath);
      }
      return CommonRespDto.success(modelRespDto.getData());
    } catch (Exception e) {
      log.info("解压失败：{}", e.getMessage(), e);
      return CommonRespDto.error(e.getMessage(), modelFilePath);
    }
  }

  /**
   * 解压前判断是否已经解压
   *
   * @param filePath
   * @return
   */
  public CommonRespDto<String> ensureUnzipped(String filePath) {
    // 替换/file为/data1
    filePath = FileReadUtil.replaceFirstFile(filePath);
    Path zipPath = Paths.get(filePath);
    if (!Files.exists(zipPath)) {
      log.warn("文件不存在: " + filePath);
      return CommonRespDto.error("文件不存在: " + filePath);
    }
    String targetDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
    try {
      // 1. 查询目录下是否已有子目录（表示已解压）
      String firstSubDirName = getFirstSubDirName(targetDir);
      String modelFilePath;
      if (StrUtil.isNotBlank(firstSubDirName)) {
        modelFilePath = targetDir + File.separator + firstSubDirName + File.separator;
        log.info("存在解压目录：{}", modelFilePath);
      } else {
        // 2. 解压 zip 文件
        CommonRespDto<String> modelRespDto = unzipFile(filePath);
        if (!modelRespDto.isOk()) {
          log.warn("文件解压异常：{}", modelRespDto.getMsg());
          return CommonRespDto.error(modelRespDto.getMsg(), filePath);
        }
        modelFilePath = modelRespDto.getData();
      }
      return CommonRespDto.success(modelFilePath);
    } catch (Exception e) {
      log.info("解压失败：{}", e.getMessage(), e);
      return CommonRespDto.error(e.getMessage(), filePath);
    }
  }

  /**
   * 解压模型文件
   *
   * @param zipFile
   * @return
   */
  public CommonRespDto<String> unzipFile(String zipFile) {
    log.info("------开始解压压缩包：{}------", zipFile);
    if (StrUtil.isBlank(zipFile)) {
      log.warn("需要解压的文件为空");
      return CommonRespDto.error("需要解压的文件为空");
    }
    // 将要解压的目标文件夹地址
    try {
      File zip = new File(zipFile);
      if (!zip.exists()) {
        log.warn("压缩文件不存在：" + zipFile);
        return CommonRespDto.error("压缩文件不存在：" + zipFile);
      }
      // 解压到 zipFile 同目录
      String targetDir = zip.getParent();
      Path targetPath = Paths.get(targetDir);
      log.info("解压目标路径：{}", targetDir);
      Path zipPath = zip.toPath();
      try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath),
          Charset.forName("GBK"))) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
          Path newPath = targetPath.resolve(entry.getName());
          if (entry.isDirectory()) {
            Files.createDirectories(newPath);
          } else {
            Files.createDirectories(newPath.getParent());
            Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
          }
          zis.closeEntry();
        }
      }
      // 获取解压后的第一个子目录
      String firstSubDirName = getFirstSubDirName(targetDir);
      if (firstSubDirName == null) {
        return CommonRespDto.error("解压后未找到有效子目录");
      }
      String resultFileName = targetDir + File.separator + firstSubDirName + File.separator;
      log.info("最终文件解压后的文件夹目录：{}", resultFileName);
      // 返回完整的路径
      return CommonRespDto.success(resultFileName);
    } catch (Exception e) {
      log.error("文件解压失败:{},原因：{}", zipFile, e.getMessage(), e);
      return CommonRespDto.error("文件解压失败:" + e.getMessage());
    }
  }

  /**
   * 创建空文件夹
   *
   * @param folderPath
   * @return
   */
  public CommonRespDto<String> createEmptyFolder(String folderPath) {
    log.info("------创建空文件夹：{}------", folderPath);
    if (StrUtil.isBlank(folderPath)) {
      log.warn("需要创建的目录为空");
      return CommonRespDto.error("需要创建的目录为空");
    }
    try {
      Path path = Paths.get(folderPath);
      // 等价于 mkdir -p —— 自动创建所有不存在的目录
      Files.createDirectories(path);
      log.info("文件夹创建成功：{}", folderPath);
      return CommonRespDto.success(folderPath);
    } catch (Exception e) {
      log.error("文件夹创建失败:{}, 原因：{}", folderPath, e.getMessage(), e);
      return CommonRespDto.error("文件夹创建失败: " + e.getMessage());
    }
  }

  /**
   * 移动文件
   *
   * @param sourcePath
   * @param targetDir
   * @return
   */
  public CommonRespDto<String> moveFolder(String sourcePath, String targetDir) {
    log.info("------移动文件夹，源路径：{}，目标路径：{}------", sourcePath, targetDir);
    if (StrUtil.isBlank(sourcePath) || StrUtil.isBlank(targetDir)) {
      log.warn("空文件夹地址为空");
      return CommonRespDto.error("空文件夹地址为空");
    }
    sourcePath = FileReadUtil.replaceFirstFile(sourcePath);
    targetDir = FileReadUtil.replaceFirstFile(targetDir);
    Path source = Paths.get(sourcePath);
    Path targetDirectory = Paths.get(targetDir);
    // 将要解压的目标文件夹地址
    try {
      // 1. 校验路径是否安全
      boolean safeSource = SAFE_ROOTS.stream().anyMatch(sourcePath::startsWith);
      boolean safeTarget = SAFE_ROOTS.stream().anyMatch(targetDir::startsWith);
      if (!safeSource || !safeTarget) {
        log.warn("路径不安全，拒绝执行: " + sourcePath + " -> " + targetDir);
        return CommonRespDto.error("路径不安全，拒绝执行: " + sourcePath + " -> " + targetDir);
      }
      // 2. 检查源是否存在
      if (!Files.exists(source)) {
        log.warn("源路径不存在: " + sourcePath);
        return CommonRespDto.error("源路径不存在: " + sourcePath);
      }
      // 3. 创建目标目录
      if (!Files.exists(targetDirectory)) {
        Files.createDirectories(targetDirectory);
      }
      // 4. 目标路径（目录 + 源名称）
      Path target = targetDirectory.resolve(source.getFileName());
      // 5. 执行移动（覆盖）
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
      String movedFullPath = target.toAbsolutePath().toString();
      log.info("移动成功：{} -> {}", source, movedFullPath);
      // ⭐ 返回移动后的文件完整路径
      return CommonRespDto.success(movedFullPath);
    } catch (Exception e) {
      log.error("移动文件夹失败,原因：{}", e.getMessage(), e);
      return CommonRespDto.error("移动文件夹失败:" + e.getMessage());
    }
  }

  /**
   * 移动文件夹或文件
   *
   * @param sourcePathOrDir
   * @param targetDir
   * @return
   */
  public CommonRespDto<String> copyFolderOrFile(String sourcePathOrDir, String targetDir) {
    log.info("------拷贝文件夹，源路径：{}，目标路径：{}------", sourcePathOrDir, targetDir);
    if (StrUtil.isBlank(sourcePathOrDir) || StrUtil.isBlank(targetDir)) {
      log.warn("空文件夹地址为空");
      return CommonRespDto.error("空文件夹地址为空");
    }
    sourcePathOrDir = FileReadUtil.replaceFirstFile(sourcePathOrDir);
    targetDir = FileReadUtil.replaceFirstFile(targetDir);
    // 校验安全
    boolean safeSource = SAFE_ROOTS.stream().anyMatch(sourcePathOrDir::startsWith);
    boolean safeTarget = SAFE_ROOTS.stream().anyMatch(targetDir::startsWith);

    if (!safeSource || !safeTarget) {
      log.warn("路径不安全：" + sourcePathOrDir + " -> " + targetDir);
      return CommonRespDto.error("路径不安全：" + sourcePathOrDir + " -> " + targetDir);
    }
    Path source = Paths.get(sourcePathOrDir);
    Path target = Paths.get(targetDir);
    // 将要解压的目标文件夹地址
    try {
      if (!Files.exists(source)) {
        log.warn("源路径不存在：" + sourcePathOrDir);
        return CommonRespDto.error("源路径不存在：" + sourcePathOrDir);
      }
      // 确保目标目录存在
      if (!Files.exists(target)) {
        Files.createDirectories(target);
      }
      // 返回的最终路径
      String resultPath;

      if (Files.isDirectory(source)) {
        // 复制目录
        try (Stream<Path> stream = Files.walk(source)) {
          stream.forEach(path -> {
            Path relative = source.relativize(path);
            Path targetPath = target.resolve(relative);
            try {
              if (Files.isDirectory(path)) {
                if (!Files.exists(targetPath)) {
                  Files.createDirectories(targetPath);
                }
              } else {
                Files.copy(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
              }
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          });
        }
        resultPath = target + File.separator;
      } else {
        Path targetFile = target.resolve(source.getFileName());
        // 复制文件
        Files.copy(source, target.resolve(source.getFileName()),
            StandardCopyOption.REPLACE_EXISTING);
        resultPath = targetFile.toString();
      }
      log.info("拷贝完成：{} -> {}", sourcePathOrDir, targetDir);
      return CommonRespDto.success(resultPath);
    } catch (Exception e) {
      log.error("拷贝文件夹失败,原因：{}", e.getMessage(), e);
      return CommonRespDto.error("拷贝文件夹失败:" + e.getMessage());
    }
  }


  /**
   * 删除模型文件夹
   *
   * @param folderPath
   * @return
   */
  public CommonRespDto<String> deleteFolderOrFile(String folderPath) {
    log.info("------删除模型文件夹：{}------", folderPath);
    if (StrUtil.isBlank(folderPath)) {
      log.warn("模型文件夹地址为空");
      return CommonRespDto.error("模型文件夹地址为空");
    }
    // 将要解压的目标文件夹地址
    try {
      boolean safe = SAFE_ROOTS.stream().anyMatch(folderPath::startsWith);
      if (!safe) {
        log.warn("路径不安全：" + folderPath);
        return CommonRespDto.error("路径不安全：" + folderPath);
      }
      Path targetPath = Paths.get(folderPath);
      if (!Files.exists(targetPath)) {
        log.warn("文件夹不存在：" + folderPath);
        return CommonRespDto.error("文件夹不存在：" + folderPath);
      }
      // 递归删除目录
      // 使用 try-with-resources 自动关闭 Stream
      try (Stream<Path> walk = Files.walk(targetPath)) {
        walk.sorted(Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.delete(path);
          } catch (IOException e) {
            log.error("删除失败: " + path + "，原因：" + e.getMessage(), e);
            throw new RuntimeException("删除失败: " + path + "，原因：" + e.getMessage(), e);
          }
        });
      }
      log.info("删除完成：{}", folderPath);
      // 返回完整的路径
      return CommonRespDto.success(folderPath);
    } catch (Exception e) {
      log.error("删除模型文件夹失败:{},原因：{}", folderPath, e.getMessage(), e);
      return CommonRespDto.error("删除模型文件夹失败:" + e.getMessage());
    }
  }


  /**
   * 获取文件夹中第一个文件夹
   *
   * @param targetDir
   * @return
   */
  public String getFirstSubDirName(String targetDir) {
    File dir = new File(targetDir);
    if (!dir.exists() || !dir.isDirectory()) {
      return null;
    }
    File[] subDirs = dir.listFiles(File::isDirectory);
    if (subDirs == null || subDirs.length == 0) {
      return null;
    }
    // 取第一个子目录（与 head -n 1 一样）
    return subDirs[0].getName();
  }

  /**
   * 获取第一个压缩文件
   *
   * @param targetDir
   * @return
   */
  public String getFirstZipFileName(String targetDir) {
    File dir = new File(targetDir);
    if (!dir.exists() || !dir.isDirectory()) {
      return null;
    }
    File[] files = dir.listFiles(f -> f.isFile() && f.getName().toLowerCase().contains(".zip"));
    if (files == null || files.length == 0) {
      return null;
    }
    return files[0].getName();
  }

}
