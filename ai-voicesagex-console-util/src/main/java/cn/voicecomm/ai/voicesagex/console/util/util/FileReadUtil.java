package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.EnumerationIter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

/**
 * ·文件读取工具
 *
 * @author wangfan
 * @date 2024/2/20 下午 3:35
 */
@Slf4j
public class FileReadUtil {

  private static ExecutorService executor = Executors.newCachedThreadPool();

  private static final List<String> SAFE_ROOTS = List.of("/data1/voicesagex-console/upload/");

  /**
   * 读取文件中的文本
   *
   * @param file
   * @return
   */
  public static String readFileText(File file) {
    String fileType = getFileType(file.getPath());
    if (".txt".equalsIgnoreCase(fileType)) {
      return FileUtil.readString(file, CharsetUtil.CHARSET_UTF_8);
    }
    if (".pdf".equalsIgnoreCase(fileType)) {
      // 创建文档对象
      PDDocument doc;
      String content = StrUtil.EMPTY;
      try {
        // 加载一个pdf对象
        doc = PDDocument.load(file);
        // 获取一个PDFTextStripper文本剥离对象
        PDFTextStripper textStripper = new PDFTextStripper();
        content = textStripper.getText(doc);
        // 关闭文档
        doc.close();
        return content;
      } catch (Exception e) {
        log.info("读取pdf失败", e);
        throw new RuntimeException("读取pdf失败");
      }
    }

    if (".doc".equalsIgnoreCase(fileType) || ".docx".equalsIgnoreCase(fileType)) {
      try {
        FileInputStream fileInputStream = new FileInputStream(file);
        XWPFDocument document = new XWPFDocument(fileInputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);

        String text = extractor.getText();

        extractor.close();
        document.close();
        fileInputStream.close();
        return text;
      } catch (Exception e) {
        if (e instanceof OLE2NotOfficeXmlFileException) {
          try (FileInputStream fileInputStream = new FileInputStream(
              file); HWPFDocument document = new HWPFDocument(fileInputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
          } catch (Exception exc) {
            log.info("读取word失败", exc);
            throw new RuntimeException("读取word失败");
          }
        }
        log.info("读取word失败", e);
        throw new RuntimeException("读取word失败");
      }
    }
    if (".xls".equalsIgnoreCase(fileType) || ".xlsx".equalsIgnoreCase(fileType)) {
      return ExcelUtil.getReader(file).getExtractor().getText();
    }
    return StrUtil.EMPTY;
  }

  /**
   * 获取zip中的文件
   *
   * @param file
   * @return
   */
  public static List<String> getAllZipFileNames(File file) {
    ZipFile zipFile = ZipUtil.toZipFile(file, CharsetUtil.CHARSET_GBK);
    List<String> zipFileNames = new ArrayList<>();
    for (ZipEntry entry : new EnumerationIter<>(zipFile.entries())) {
      String name = entry.getName();
      zipFileNames.add(name);
    }
    return zipFileNames;
  }

  /**
   * 获取符合条件的文件(word,excel,txt,pdf)
   *
   * @param fileNameList
   * @return
   */
  public static List<String> getCheckedZipFileNames(List<String> fileNameList) {
    return fileNameList.stream().filter(e -> checkFileType(getFileType(e)))
        .collect(Collectors.toList());
  }

  /**
   * 筛选文件
   *
   * @param fileNameList
   * @return
   */
  public static List<String> filterZipFileNames(List<String> fileNameList) {
    return fileNameList.stream().filter(e -> !e.endsWith("/")).collect(Collectors.toList());
  }

  /**
   * 获取文件类型
   *
   * @param fileName
   * @return
   */
  public static String getFileType(String fileName) {
    int index = fileName.lastIndexOf(".");
    return fileName.substring(index);
  }

  /**
   * 检查文件格式
   *
   * @param fileType
   * @return
   */
  public static boolean checkFileType(String fileType) {
    if (StrUtil.isBlank(fileType)) {
      return false;
    }
    return ".txt".equalsIgnoreCase(fileType) || ".pdf".equalsIgnoreCase(fileType)
        || ".doc".equalsIgnoreCase(fileType) || ".docx".equalsIgnoreCase(fileType)
        || ".xlsx".equalsIgnoreCase(fileType) || ".xls".equalsIgnoreCase(fileType);
  }

  public static ZipNodePo buildZipTree(String zipPath) {
    if (StrUtil.isBlank(zipPath)) {
      log.warn("文件路径为空");
      return null;
    }
    zipPath = replaceFirstFile(zipPath);
    File file = new File(zipPath);
    if (!file.exists()) {
      log.warn("文件路径错误");
      return null;
    }
    try (FileInputStream fis = new FileInputStream(file)) {
      return buildZipTree(fis, extractNameAfterUnderscore(file.getName()),
          formatSize(file.length()), "");
    } catch (IOException e) {
      log.error("构建 zip 树失败", e);
      return null;
    }
  }

  /**
   * 构造压缩包的树结构递归
   */
  public static ZipNodePo buildZipTree(InputStream inputStream, String zipName, String size,
      String pathPrefix) {
    ZipNodePo root = ZipNodePo.builder().name(zipName).type("zip").size(size).fullPath(pathPrefix)
        .children(new ArrayList<>()).build();
    Map<String, ZipNodePo> pathMap = new HashMap<>();
    pathMap.put("", root);

    try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream),
        Charset.forName("GBK"))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        String entryName = entry.getName();
        String[] parts = Arrays.stream(entryName.split("/")).filter(p -> !p.isEmpty())
            .toArray(String[]::new);

        StringBuilder currentPath = new StringBuilder();
        ZipNodePo parent = root;

        for (int i = 0; i < parts.length; i++) {
          currentPath.append(parts[i]);
          String key = currentPath.toString();

          if (!pathMap.containsKey(key)) {
            boolean isDir = (i != parts.length - 1) || entry.isDirectory();
            String type;
            String sizeStr = null;

            if (isDir) {
              type = "folder";
            } else if (parts[i].toLowerCase().endsWith(".zip")) {
              type = "zip";
            } else {
              type = "file";
            }
            String fullPath = key + ("folder".equals(type) ? "/" : "");
            if (StrUtil.isNotBlank(pathPrefix)) {
              fullPath = pathPrefix + "/" + fullPath;
            }
            ZipNodePo node = ZipNodePo.builder().name(parts[i]).type(type).fullPath(fullPath)
                .children(isDir ? new ArrayList<>() : null).build();

            if (!isDir) {
              long rawSize = entry.getSize();
              if (rawSize > 0) {
                sizeStr = formatSize(rawSize);
              } else {
                // 尝试手动读取数据并计算大小
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = zis.read(buffer)) != -1) {
                  baos.write(buffer, 0, len);
                }
                byte[] bytes = baos.toByteArray();
                sizeStr = formatSize((long) bytes.length);
              }
              node.setSize(sizeStr);
            }

            // 处理嵌套 zip
            if ("zip".equals(type)) {
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              byte[] buffer = new byte[4096];
              int len;
              while ((len = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
              }
              byte[] zipBytes = baos.toByteArray();
              ZipNodePo nested = buildZipTree(new ByteArrayInputStream(zipBytes), parts[i],
                  formatSize((long) zipBytes.length), node.getFullPath());
              node.setSize(formatSize((long) zipBytes.length));
              node.setChildren(nested.getChildren());
            }
            pathMap.put(key, node);
            if (parent.getChildren() == null) {
              parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(node);
          }

          parent = pathMap.get(key);
          currentPath.append("/");
        }
      }
    } catch (Exception e) {
      log.error("解析 zip 流失败", e);
    }
    return root;
  }

  public static List<ZipNodePo> extractEntity(MultipartFile file) {
    try (InputStream fis = file.getInputStream()) {
      return extractEntityFiles(fis, "");
    } catch (IOException e) {
      log.error("路径错误", e);
      return Collections.emptyList();
    }
  }

  public static List<ZipNodePo> extractEntity(String zipFilePath) throws IOException {
    zipFilePath = replaceFirstFile(zipFilePath);
    try (FileInputStream fis = new FileInputStream(zipFilePath)) {
      return extractEntityFiles(fis, "");
    } catch (IOException e) {
      throw new IOException("解析文件失败: " + zipFilePath, e);
    }
  }

  public static List<ZipNodePo> extractEntityFiles(InputStream zipInputStream, String pathPrefix)
      throws IOException {
    List<ZipNodePo> result = new ArrayList<>();
    try (ZipInputStream zis = new ZipInputStream(zipInputStream, Charset.forName("GBK"))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        String currentPath = pathPrefix + entry.getName();

        if (entry.isDirectory()) {
          continue;
        }

        if (entry.getName().toLowerCase().endsWith(".zip")) {
          try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] temp = new byte[4096];
            int n;
            while ((n = zis.read(temp)) > 0) {
              buffer.write(temp, 0, n);
            }
            byte[] nestedZipBytes = buffer.toByteArray();

            try (ByteArrayInputStream nestedStream = new ByteArrayInputStream(nestedZipBytes)) {
              result.addAll(extractEntityFiles(nestedStream, currentPath + "/"));
            }
          }
        } else {
          String fileName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
          String sizeStr;
          long rawSize = entry.getSize();
          if (rawSize > 0) {
            sizeStr = formatSize(rawSize);
          } else {
            // 尝试手动读取数据并计算大小
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while ((len = zis.read(buffer)) != -1) {
              baos.write(buffer, 0, len);
            }
            byte[] bytes = baos.toByteArray();
            sizeStr = formatSize((long) bytes.length);
          }
          ZipNodePo node = ZipNodePo.builder().name(fileName).type("folder").size(sizeStr)
              .fullPath(currentPath).build();
          result.add(node);
        }
      }
    }
    return result;
  }

  public static String extractConfigYaml(String zipFilePath, String configName) {
    log.info("文件地址：{}，配置文件名称：{}", zipFilePath, configName);
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(replaceFirstFile(zipFilePath)),
        Charset.forName("GBK"))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        String fileName = Paths.get(entry.getName()).getFileName().toString();
        // 只匹配 reco/config.yaml
        if (!entry.isDirectory() && fileName.equals(configName)) {
          return new String(zis.readAllBytes(), StandardCharsets.UTF_8);
        }
      }
    } catch (Exception e) {
      log.error("文件解析失败", e);
    }
    return null;
  }


  /**
   * 提取 zip 文件名中第一个 "_" 后的部分
   */
  public static String extractNameAfterUnderscore(String fileName) {
    int underscoreIndex = fileName.indexOf('_');
    if (underscoreIndex != -1 && underscoreIndex < fileName.length() - 1) {
      return fileName.substring(underscoreIndex + 1);
    }
    return fileName;
  }

  /**
   * 替换第一个file
   *
   * @param path
   * @return
   */
  public static String replaceFirstFile(String path) {
    return path.replaceFirst("^/file", "/data1");
  }

  /**
   * 替换第一个data
   *
   * @param path
   * @return
   */
  public static String replaceFirstData(String path) {
    return path.replaceFirst("^/data1", "/file");
  }

  public static String formatSize(Long bytes) {
    if (bytes == null || bytes < 0) {
      return null;
    }
    double kb = bytes / 1024.0;
    if (kb < 1024) {
      return String.format("%.2f KB", kb);
    }
    double mb = kb / 1024.0;
    if (mb < 1024) {
      return String.format("%.2f MB", mb);
    }
    double gb = mb / 1024.0;
    return String.format("%.2f GB", gb);
  }

  public static void deleteBatchFile(List<String> filePathList) {
    if (CollUtil.isEmpty(filePathList)) {
      return;
    }
    executor.submit(() -> filePathList.forEach(path -> {
      try {
        path = replaceFirstFile(path);
        log.info("开始删除文件：{}", path);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
          boolean deleted = file.delete();
          log.info("{} 删除结果：{}", path, deleted);
        } else {
          log.warn("{} 文件不存在或不是文件", path);
        }
      } catch (Exception e) {
        log.error("删除文件 {} 时发生异常", path, e);
      }
    }));
  }

  /**
   * 读取 JSONL 格式日志文件
   *
   * @param filePath
   * @return
   */
  public static List<Map<String, Object>> readJsonlToList(String filePath) {
    log.info("读取监控配置文件路径：{}", filePath);
    File logFile = new File(filePath);
    if (!logFile.exists() || !logFile.isFile() || !logFile.canRead()) {
      log.warn("日志文件不存在或不可读: " + filePath);
      return new ArrayList<>();
    }
    List<Map<String, Object>> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue; // 跳过空行
        }
        // 使用 Hutool 的 JSONUtil 解析 JSON 字符串为 Map
        Map<String, Object> map = JSONUtil.toBean(line, Map.class);
        list.add(map);
      }
    } catch (Exception e) {
      log.error("读取训练日志失败: " + filePath, e);
      return new ArrayList<>();
    }
    return list;
  }

  public static Map<String, Object> readFileToMap(String filePath) {
    log.info("读取监控配置文件路径：{}", filePath);
    File file = new File(filePath);
    if (!file.exists() || !file.isFile() || !file.canRead()) {
      log.warn("文件不存在或不可读: {}", filePath);
      return new HashMap<>();
    }
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line.trim());
      }
      if (content.length() == 0) {
        return Collections.emptyMap();
      }
      // 解析整个文件为 JSON 对象
      Map<String, Object> map = JSONUtil.toBean(content.toString(), Map.class);
      return map;
    } catch (Exception e) {
      log.error("读取文件失败: {}", filePath, e);
      return new HashMap<>();
    }
  }

  /**
   * 删除文件或者文件夹
   *
   * @param folderPath
   * @return
   */
  public static void deleteFolderOrFile(String folderPath) {
    log.info("------删除文件或文件夹：{}------", folderPath);
    if (StrUtil.isBlank(folderPath)) {
      log.warn("模型文件夹地址为空");
      return;
    }
    // 将要解压的目标文件夹地址
    try {
      boolean safe = SAFE_ROOTS.stream().anyMatch(folderPath::startsWith);
      if (!safe) {
        log.warn("路径不安全：" + folderPath);
        return;
      }
      Path targetPath = Paths.get(folderPath);
      if (!Files.exists(targetPath)) {
        log.warn("文件夹不存在：" + folderPath);
        return;
      }
      // 递归删除目录
      // 使用 try-with-resources 自动关闭 Stream
      try (Stream<Path> walk = Files.walk(targetPath)) {
        walk.sorted(Comparator.reverseOrder()).forEach(path -> {
          try {
            Files.delete(path);
          } catch (IOException e) {
            log.error("删除失败: " + path + "，原因：" + e.getMessage(), e);
          }
        });
      }
      log.info("删除完成：{}", folderPath);
      // 返回完整的路径
    } catch (Exception e) {
      log.error("删除模型文件夹失败:{},原因：{}", folderPath, e.getMessage(), e);
    }
  }

  /**
   * 解压模型文件
   *
   * @param zipFile
   * @return
   */
  public static String unzipFile(String zipFile) {
    String resultFileName = StrUtil.EMPTY;
    log.info("------开始解压压缩包：{}------", zipFile);
    if (StrUtil.isBlank(zipFile)) {
      log.warn("需要解压的文件为空");
      return resultFileName;
    }
    // 将要解压的目标文件夹地址
    zipFile = FileReadUtil.replaceFirstFile(zipFile);
    try {
      File zip = new File(zipFile);
      if (!zip.exists()) {
        log.warn("压缩文件不存在：" + zipFile);
        return resultFileName;
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
        return resultFileName;
      }
      resultFileName = targetDir + File.separator + firstSubDirName + File.separator;
      log.info("最终文件解压后的文件夹目录：{}", resultFileName);
      // 返回完整的路径
      return resultFileName;
    } catch (Exception e) {
      log.error("文件解压失败:{},原因：{}", zipFile, e.getMessage(), e);
      return resultFileName;
    }
  }

  /**
   * 解压前判断是否已经解压
   *
   * @param filePath
   * @return
   */
  public static String ensureUnzipped(String filePath) {
    String modelFilePath = StrUtil.EMPTY;
    // 替换/file为/data1
    filePath = FileReadUtil.replaceFirstFile(filePath);
    Path zipPath = Paths.get(filePath);
    if (!Files.exists(zipPath)) {
      log.warn("文件不存在: " + filePath);
      return modelFilePath;
    }
    String targetDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
    try {
      // 1. 查询目录下是否已有子目录（表示已解压）
      String firstSubDirName = getFirstSubDirName(targetDir);
      if (StrUtil.isNotBlank(firstSubDirName)) {
        modelFilePath = targetDir + File.separator + firstSubDirName + File.separator;
        log.info("存在解压目录：{}", modelFilePath);
      } else {
        // 2. 解压 zip 文件
        modelFilePath = unzipFile(filePath);
      }
      return modelFilePath;
    } catch (Exception e) {
      log.info("解压失败：{}", e.getMessage(), e);
      return modelFilePath;
    }
  }

  /**
   * 移动文件
   *
   * @param sourcePath
   * @param targetDir
   * @return
   */
  public static String moveFolder(String sourcePath, String targetDir) {
    log.info("------移动文件夹，源路径：{}，目标路径：{}------", sourcePath, targetDir);
    String movedFullPath = StrUtil.EMPTY;
    if (StrUtil.isBlank(sourcePath) || StrUtil.isBlank(targetDir)) {
      log.warn("空文件夹地址为空");
      return movedFullPath;
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
        return movedFullPath;
      }
      // 2. 检查源是否存在
      if (!Files.exists(source)) {
        log.warn("源路径不存在: " + sourcePath);
        return movedFullPath;
      }
      // 3. 创建目标目录
      if (!Files.exists(targetDirectory)) {
        Files.createDirectories(targetDirectory);
      }
      // 4. 目标路径（目录 + 源名称）
      Path target = targetDirectory.resolve(source.getFileName());
      // 5. 执行移动（覆盖）
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
      movedFullPath = target.toAbsolutePath().toString();
      log.info("移动成功：{} -> {}", source, movedFullPath);
      // ⭐ 返回移动后的文件完整路径
      return movedFullPath;
    } catch (Exception e) {
      log.error("移动文件夹失败,原因：{}", e.getMessage(), e);
      return movedFullPath;
    }
  }

  /**
   * 获取文件夹中第一个文件夹
   *
   * @param targetDir
   * @return
   */
  public static String getFirstSubDirName(String targetDir) {
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
   * 获取第一个jsonl文件
   *
   * @param path
   * @return
   */
  public static Path getFirstJsonlFileOrNull(String path) {
    Path dir = Paths.get(path);
    try (Stream<Path> stream = Files.list(dir)) {
      return stream.filter(p -> p.getFileName().toString().endsWith(".jsonl")).findFirst()
          .orElse(null);
    } catch (IOException e) {
      log.error("获取 jsonl 文件失败", e);
      return null;
    }
  }


  /**
   * 统计jsonl / txt按换行符统计
   *
   * @param file
   * @return
   */
  public static long countByNewLine(Path file) {
    long count = 0;
    try (InputStream in = Files.newInputStream(file)) {
      byte[] buffer = new byte[8192];
      int read;
      boolean lastIsNewLine = false;

      while ((read = in.read(buffer)) != -1) {
        for (int i = 0; i < read; i++) {
          if (buffer[i] == '\n') {
            count++;
            lastIsNewLine = true;
          } else {
            lastIsNewLine = false;
          }
        }
      }
      // 文件非空，且最后一行没有换行符，也算一条数据
      if (!lastIsNewLine && Files.size(file) > 0) {
        count++;
      }
      return count;
    } catch (IOException e) {
      log.error("按换行符统计失败: {}", file, e);
      return 0L;
    }
  }

  /**
   * csv / tsv —— 按“行”统计
   *
   * @param file
   * @return
   */
  public static long countCsvOrTsv(Path file) {
    try (Stream<String> lines = Files.lines(file)) {
      return lines.filter(line -> !line.isBlank()).count();
    } catch (IOException e) {
      log.error("统计 CSV/TSV 失败: {}", file, e);
      return 0L;
    }
  }

  /**
   * json —— 统计数组元素个数
   *
   * @param file
   * @return
   */
  public static long countJsonNum(Path file) {
    ObjectMapper mapper = new ObjectMapper();
    long count = 0;
    try (InputStream in = Files.newInputStream(file); JsonParser parser = mapper.getFactory()
        .createParser(in)) {
      JsonToken firstToken = parser.nextToken();
      // ===== 1️⃣ JSON Array：保持你现在的逻辑 =====
      if (firstToken == JsonToken.START_ARRAY) {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
          parser.skipChildren();
          count++;
        }
        return count;
      }
      // ===== 2️⃣ JSON Object：有字段就算 1 条 =====
      if (firstToken == JsonToken.START_OBJECT) {
        // 读下一个 token，看是不是 END_OBJECT
        JsonToken next = parser.nextToken();
        if (next != JsonToken.END_OBJECT) {
          // 对象里至少有一个字段
          return 1L;
        } else {
          // 空对象 {}
          return 0L;
        }
      }
      log.warn("JSON 文件既不是数组也不是对象: {}", file);
      return 0L;
    } catch (Exception e) {
      log.error("统计 JSON 数据量失败: {}", file, e);
      return 0L;
    }
  }


}
