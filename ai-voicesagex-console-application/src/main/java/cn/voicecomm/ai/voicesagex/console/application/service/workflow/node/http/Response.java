package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.http;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.Data;

/**
 * @author: gaox
 * @date: 2025/9/8 17:25
 */
@Data
public class Response {

  private final HttpResponse<byte[]> response;
  private final Map<String, String> headers;// 假设这是一个HTTP响应接口或类

  public Response(HttpResponse<byte[]> response) {
    this.response = response;
    this.headers = new HashMap<>();
    response.headers().map().forEach((k, v) -> this.headers.put(k, String.join(",", v)));
  }

  public String getContentType() {
    return headers.getOrDefault("content-type", "").toLowerCase(Locale.ROOT);
  }

  public String getContentDisposition() {
    return headers.getOrDefault("content-disposition", "");
  }

  public boolean isFile() {
    String contentType = getContentType();
    if (contentType.contains(";")) {
      contentType = contentType.split(";", 2)[0].trim();
    }

    String contentDisposition = getContentDisposition();

    // 1. 如果 header 里有 Content-Disposition: attachment / filename
    if (!contentDisposition.isEmpty()) {
      if (contentDisposition.toLowerCase(Locale.ROOT).contains("attachment") ||
          contentDisposition.toLowerCase(Locale.ROOT).contains("filename=")) {
        return true;
      }
    }

    // 2. 对于 text 类型
    if (contentType.startsWith("text/") && !contentType.contains("csv")) {
      return false;
    }

    // 3. 对于 application/* 类型
    if (contentType.startsWith("application/")) {
      String[] textBased = {
          "json", "xml", "javascript", "x-www-form-urlencoded", "yaml", "graphql"
      };
      for (String t : textBased) {
        if (contentType.contains(t)) {
          return false;
        }
      }

      // 尝试取前 1024 bytes 判断是不是可解码文本
      byte[] contentSample = Arrays.copyOfRange(response.body(), 0,
          Math.min(1024, response.body().length));
      try {
        String decoded = new String(contentSample, StandardCharsets.UTF_8);
        // 常见文本 marker
        String[] markers = {"{", "[", "<", "function", "var ", "const ", "let "};
        for (String marker : markers) {
          if (decoded.contains(marker)) {
            return false;
          }
        }
      } catch (Exception e) {
        // 不能 decode = 二进制文件
        return true;
      }
    }

    // 4. MIME 分析 (利用文件扩展名推断)
    String guessedExt = null;
    try {
      guessedExt = contentType.contains("/") ? contentType.split("/")[1] : null;
    } catch (Exception ignored) {
    }
    if (guessedExt != null) {
      if (contentType.startsWith("application/") ||
          contentType.startsWith("image/") ||
          contentType.startsWith("audio/") ||
          contentType.startsWith("video/")) {
        return true;
      }
    }

    // 5. fallback 判断 media 类型
    return contentType.startsWith("image/") ||
        contentType.startsWith("audio/") ||
        contentType.startsWith("video/");
  }

  public int getSize() {
    byte[] body = response.body();
    if (body == null) {
      return 0;
    }
    return body.length;
  }

  /**
   * 获取可读的文件大小格式
   *
   * @return 格式化的文件大小字符串
   */
  public String getReadableSize() {
    int size = getSize();
    if (size < 1024) {
      return size + " bytes";
    } else if (size < 1024 * 1024) {
      return String.format("%.2f KB", (double) size / 1024);
    } else {
      return String.format("%.2f MB", (double) size / (1024 * 1024));
    }
  }

  /**
   * parsed_content_disposition.get_filename()
   */
  public String getContentDispositionFilename() {
    String contentDisposition = getParsedContentDisposition();
    if (contentDisposition == null || contentDisposition.isEmpty()) {
      return null;
    }

    // 简单解析 filename="xxx" 的形式
    int idx = contentDisposition.toLowerCase().indexOf("filename=");
    if (idx < 0) {
      return null;
    }

    String filenamePart = contentDisposition.substring(idx + 9).trim();

    // 去掉可能的双引号
    if (filenamePart.startsWith("\"") && filenamePart.endsWith("\"")) {
      filenamePart = filenamePart.substring(1, filenamePart.length() - 1);
    }

    // 如果还有 ; 号，截断
    int semicolonIdx = filenamePart.indexOf(";");
    if (semicolonIdx >= 0) {
      filenamePart = filenamePart.substring(0, semicolonIdx).trim();
    }

    return filenamePart.isEmpty() ? null : filenamePart;
  }

  /**
   * parsed_content_disposition
   *
   * @return Content-Disposition 字符串，如果不存在返回 null
   */
  private String getParsedContentDisposition() {
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      if ("content-disposition".equalsIgnoreCase(entry.getKey())) {
        return entry.getValue();
      }
    }
    return null;
  }
}

