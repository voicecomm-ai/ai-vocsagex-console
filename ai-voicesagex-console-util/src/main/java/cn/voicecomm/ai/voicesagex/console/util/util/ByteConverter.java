package cn.voicecomm.ai.voicesagex.console.util.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author jiwh
 * @date 2024/6/5 15:44
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ByteConverter {

  private static final long KILOBYTE = 1024;
  private static final long MEGABYTE = KILOBYTE * 1024;
  private static final long GIGABYTE = MEGABYTE * 1024;

  public static String convertBytes(long bytes) {
    if (bytes < KILOBYTE) {
      return bytes + "B";
    } else if (bytes < MEGABYTE) {
      return String.format("%.1fKB", (double) bytes / KILOBYTE);
    } else if (bytes < GIGABYTE) {
      return String.format("%.1fMB", (double) bytes / MEGABYTE);
    } else {
      return String.format("%.1fGB", (double) bytes / GIGABYTE);
    }
  }

}
