package cn.voicecomm.ai.voicesagex.console.util.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件后缀名工具类
 *
 * @author gaox
 * @date 2024/6/12
 */
public class FileExtensionUtil {

  private static final Map<String, String> EXTENSION_MAP = new HashMap<>();

  static {
    // 文档文本类文件
    EXTENSION_MAP.put("txt", "text/plain");
    EXTENSION_MAP.put("doc", "application/msword");
    EXTENSION_MAP.put("docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    EXTENSION_MAP.put("pdf", "application/pdf");
    EXTENSION_MAP.put("xls", "application/vnd.ms-excel");
    EXTENSION_MAP.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    EXTENSION_MAP.put("ppt", "application/vnd.ms-powerpoint");
    EXTENSION_MAP.put("pptx",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    EXTENSION_MAP.put("rtf", "application/rtf");

    // 图像类文件
    EXTENSION_MAP.put("bmp", "image/bmp");
    EXTENSION_MAP.put("gif", "image/gif");
    EXTENSION_MAP.put("jpg", "image/jpeg");
    EXTENSION_MAP.put("jpeg", "image/jpeg");
    EXTENSION_MAP.put("png", "image/png");

    // 音频视频类文件
    EXTENSION_MAP.put("mp3", "audio/mpeg");
    EXTENSION_MAP.put("wav", "audio/x-wav");
    EXTENSION_MAP.put("wma", "audio/x-ms-wma");
    EXTENSION_MAP.put("mp4", "video/mp4");
    EXTENSION_MAP.put("avi", "video/x-msvideo");
    EXTENSION_MAP.put("mpeg", "video/mpeg");
    EXTENSION_MAP.put("wmv", "video/x-ms-wmv");

    // 压缩解压缩文件
    EXTENSION_MAP.put("rar", "application/x-rar-compressed");
    EXTENSION_MAP.put("zip", "application/zip");
  }

  public static Map<String, String> getExtensionMap() {
    return EXTENSION_MAP;
  }
}
