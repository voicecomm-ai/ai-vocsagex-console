package cn.voicecomm.ai.voicesagex.console.util.util;

import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类
 */
public class FileTypeCheckUtils {

  public static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
      "jpg", "jpeg", "png", "gif", "bmp", "webp"
  );

  public static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
      "mp4", "avi", "mov", "wmv", "flv", "webm"
  );

  public static final List<String> AUDIO_EXTENSIONS = Arrays.asList(
      "mp3", "wav", "flac", "aac", "ogg"
  );
  public static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
      "txt", "markdown", "md", "mdx", "pdf", "html", "htm", "xlsx", "xls", "vtt", "properties",
      "doc", "docx", "csv", "eml", "msg", "pptx", "xml", "epub", "ppt"
  );

  /**
   * 检查是否为图片扩展名
   */
  public static boolean isImageExtension(String extension) {
    return IMAGE_EXTENSIONS.contains(extension.toLowerCase());
  }

  /**
   * 检查是否为视频扩展名
   */
  public static boolean isVideoExtension(String extension) {
    return VIDEO_EXTENSIONS.contains(extension.toLowerCase());
  }

  /**
   * 检查是否为音频扩展名
   */
  public static boolean isAudioExtension(String extension) {
    return AUDIO_EXTENSIONS.contains(extension.toLowerCase());
  }

  /**
   * 检查是否为文档扩展名
   */
  public static boolean isDocumentExtension(String extension) {
    return DOCUMENT_EXTENSIONS.contains(extension.toLowerCase());
  }


  /**
   * 获取签名文件URL
   */
  public static String getSignedFileUrl(Integer uploadFileId) {
    // todo 实现签名URL生成逻辑
    return "https://example.com/files/" + uploadFileId + "?sign=...";
  }
}