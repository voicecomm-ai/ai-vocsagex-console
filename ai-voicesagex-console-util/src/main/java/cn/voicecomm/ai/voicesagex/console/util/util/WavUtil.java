package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * wav 工具
 *
 * @author GeCh
 * @version v1.0
 * @date 2022-11-03
 */
@Slf4j
public class WavUtil {

  private static final Integer WAV_HEADER_LENGTH = 44;

  /**
   * 每次读取2M
   */
  private static final Integer READ_DATA_BUFFER = 2048;
  /**
   * WAV 常规长度为 4字节，ID，SIZE TYPE 等
   */
  private static final Integer WAV_NORMAL_LENGTH = 4;
  /**
   * WAV 读取short int 为2字节
   */
  private static final Integer WAV_SHORT_LENGTH = 2;
  /**
   * 双声道
   */
  private static final Integer STEREO_NUM_CHANNEL = 2;
  /**
   * 左声道
   */
  private static final Integer CHANNEL_LEFT = 0;
  /**
   * 右声道
   */
  private static final Integer CHANNEL_RIGHT = 1;

  private static final Integer BITS_PER_SAMPLE = 8;

  private static final String WAV_HEADER_LIST = "LIST";
  private static final String WAV_HEADER_DATA = "DATA";

  /**
   * 采用位数，采样率 audio_format 1代表wav
   */
  private static final Integer AUDIO_FORMAT = 1;

  private static final Integer PRECISION = 16;
  private static final Integer SAMPLE_RATE = 8000;

  /**
   * byte[]转int
   *
   * @param bytes 需要转换成int的数组
   * @return int值
   */
  private static int byteArrayToInt(byte[] bytes) {
    int value = 0;
    for (int i = 0; i < 4; i++) {
      int shift = (3 - i) * 8;
      value += (bytes[i] & 0xFF) << shift;
    }
    return value;
  }

  private static String byteToString(byte[] b, int s) {
    return byteToString(b, s, WAV_NORMAL_LENGTH);
  }

  private static String byteToString(byte[] b, int s, int size) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < size; i++) {
      builder.append((char) b[s + i]);
    }
    return builder.toString();
  }

  private static int byteToInt(byte[] b, int s) {
    return ((b[s + 3] << 24) + (b[s + 2] << 16) + (b[s + 1] << 8) + (b[s + 0] << 0));
  }

  private static int byteToShort(byte[] b, int s) {
    return (short) ((b[s + 1] << 8) + (b[s + 0] << 0));
  }


  public static int getWavHeaderLength(byte[] bytes) {
    if (bytes == null || bytes.length < WAV_HEADER_LENGTH) {
      // 数据不够
      return -1;
    }

    WavData data = WavData.parse(bytes);
    int offset = WAV_HEADER_LENGTH;
    while (!data.isData()) {
      offset += data.size;
      // 读8个byte
      data = WavData.parse(bytes, offset);
      offset += 8;
    }
    return offset;
  }

  private static final byte[] headers_8k = new byte[]{82, 73, 70, 70, -102, 117, 1, 0, 87, 65, 86,
      69, 102, 109, 116,
      32, 16, 0, 0, 0, 1, 0, 1, 0, 64, 31, 0, 0, -128, 62, 0, 0, 2, 0, 16, 0, 100, 97, 116, 97, 84,
      117, 1, 0};
  private static final byte[] headers_16k = new byte[]{82, 73, 70, 70, -10, 42, 3, 0, 87, 65, 86,
      69, 102, 109, 116, 32,
      16, 0, 0, 0, 1, 0, 1, 0, -128, 62, 0, 0, 0, 125, 0, 0, 2, 0, 16, 0, 100, 97, 116, 97, -46, 42,
      3, 0};


  private static byte[] makeHeaders(byte[] headers, int dataSize) {
    byte[] clone = headers.clone();
    // data 的数据大小
    intToByte(clone, dataSize, 40);
    // riff size 大小
    intToByte(clone, dataSize + 36, 4);
    return clone;
  }


  /**
   * 写入pcm，到文件
   *
   * @param destPath
   * @param bytesList
   * @return
   */
  public static int writeWavBytesListByPcm(String destPath, List<byte[]> bytesList) {
    if (StrUtil.isEmpty(destPath) || bytesList.isEmpty()) {
      return 0;
    }

    File file = new File(destPath).getParentFile();
    if (!file.exists()) {
      boolean mks = file.mkdirs();
      log.info("mkdirs [{}] {}", mks, file.getAbsolutePath());
    }

    int dataSize = bytesList.stream().mapToInt(s -> s.length).sum();

    try (FileOutputStream stream = new FileOutputStream(destPath)) {
      stream.write(makeHeaders(headers_16k, dataSize));
      for (byte[] bytes : bytesList) {
        stream.write(bytes);
      }

      return 1;
    } catch (Exception e) {
      log.error("write error", e);
      return 0;
    }
  }

  public static int writeWavBytesList(String destPath, List<byte[]> bytesList) {
    if (StrUtil.isEmpty(destPath) || bytesList.isEmpty()) {
      return 0;
    }

    File file = new File(destPath).getParentFile();
    if (!file.exists()) {
      boolean mks = file.mkdirs();
      log.info("mkdirs [{}] {}", mks, file.getAbsolutePath());
    }
    try (FileOutputStream stream = new FileOutputStream(destPath)) {
      // WAV 写入文件
      if (bytesList.size() == 1) {
        stream.write(bytesList.get(0));
        return 1;
      }

      // 合并多个
      List<Integer> headerSizeList = bytesList.stream().map(WavUtil::getWavHeaderLength).toList();
      int dataSize = 0;

      for (int i = 0; i < bytesList.size(); i++) {
        Integer header = headerSizeList.get(i);
        if (header == -1) {
          // 异常数据
          continue;
        }
        int wavSize = bytesList.get(i).length;
        dataSize += wavSize - header;
      }

      for (int i = 0; i < bytesList.size(); i++) {
        byte[] bytes = bytesList.get(i);
        Integer header = headerSizeList.get(i);
        if (header == -1) {
          // 异常数据
          continue;
        }

        if (i == 0) {
          // data 的数据大小
          intToByte(bytes, dataSize, header - 4);
          // riff size 大小
          intToByte(bytes, dataSize + header - 8, 4);
          stream.write(bytes);
          continue;
        }
        stream.write(bytes, header, bytes.length - header);
      }
      return 1;
    } catch (Exception e) {
      log.error("write error", e);
      return 0;
    }
  }

  private static void shortToByte(byte[] b, int value, int position) {
    b[position + 0] = intToByte(value, 0);
    b[position + 1] = intToByte(value, 1);
  }

  private static void intToByte(byte[] b, int value, int position) {
    b[position + 0] = intToByte(value, 0);
    b[position + 1] = intToByte(value, 1);
    b[position + 2] = intToByte(value, 2);
    b[position + 3] = intToByte(value, 3);
  }

  private static byte intToByte(int i, int bit) {
    return (byte) ((i >> (bit * 8)) & 0xFF);
  }

  private static int writeWav(byte[] headers, byte[] readBytes, String destPath) {
    if (StrUtil.isEmpty(destPath)) {
      return 0;
    }
    File file = new File(destPath).getParentFile();
    if (!file.exists()) {
      boolean mks = file.mkdirs();
      log.info("mkdirs [{}] {}", mks, file.getAbsolutePath());
    }

    try (FileOutputStream stream = new FileOutputStream(destPath)) {

      // WAV 写入文件
      stream.write(headers);
      stream.write(readBytes);
      return 1;
    } catch (Exception e) {
      log.error("write error", e);
      return 0;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class WavRiff {

    // 长度: 4
    private String id;
    // 长度: 4 整个文件的长度减去ID和Size的长度
    private int size;
    // 长度: 4
    private String type;

    public static WavRiff parse(byte[] b) {
      // 12
      return new WavRiff(byteToString(b, 0), byteToInt(b, 4), byteToString(b, 8));
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class WavFormat {

    // 24 长度: 4
    private String id;
    // 长度: 4 表示该区块数据的长度（不包含ID和Size的长度）值为 16
    private int size;
    // 长度: 2 表示Data区块存储的音频数据的格式，PCM音频数据的值为1
    private int audioFormat;
    // 长度: 2 重要 音频数据的声道数，1：单声道，2：双声道
    private int numChannels;
    // 长度: 4 重要 音频数据的采样率
    private int sampleRate;
    // 长度: 4 每秒数据字节数  = SampleRate * NumChannels * BitsPerSample / 8
    private int byteRate;
    // 长度: 2 每个采样所需的字节数 = NumChannels * BitsPerSample / 8
    private int blockAlign;
    // 长度: 2 重要 每个采样存储的bit数，8：8bit，16：16bit，32：32bit
    private int bitsPerSample;

    public static WavFormat parse(byte[] b) {
      int offset = 12;
      // 24
      WavFormatBuilder builder =
          WavFormat.builder()
              .id(byteToString(b, offset))
              .size(byteToInt(b, offset + 4))
              .audioFormat(byteToShort(b, offset + 8))
              .numChannels(byteToShort(b, offset + 10))
              .sampleRate(byteToInt(b, offset + 12))
              .byteRate(byteToInt(b, offset + 16))
              .blockAlign(byteToShort(b, offset + 20))
              .bitsPerSample(byteToShort(b, offset + 22));
      WavFormat wavFormat = builder.build();

      // 优化参数
      wavFormat.setBlockAlign(
          wavFormat.getNumChannels() * wavFormat.getBitsPerSample() / BITS_PER_SAMPLE);
      wavFormat.setByteRate(wavFormat.getBlockAlign() * wavFormat.getSampleRate());
      return wavFormat;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class WavData {

    // 长度: 4
    private String id;
    // 长度: 4 音频数据的长度，N = ByteRate * seconds
    private int size;

    public boolean isData() {
      return WAV_HEADER_DATA.equalsIgnoreCase(id);
    }

    public static WavData parse(byte[] b) {
      return parse(b, 36);
    }

    public static WavData parse(byte[] b, int offset) {
      return new WavData(byteToString(b, offset), byteToInt(b, offset + 4));
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class WavList {

    // 长度: 4
    private String id;
    // 长度: 4
    private int type;
    // 长度: 4
    private int size;
    private String data;

    public static WavData parse(byte[] b) {
      int offset = 36;
      return new WavData(byteToString(b, offset), byteToInt(b, offset + 4));
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SegmentParams {

    /**
     * 开始时间 毫秒
     */
    private Integer startTime;

    /**
     * 结束时间 毫秒
     */
    private Integer endTime;

    /**
     * 左 右 声道 0/1
     */
    private Integer channel;

    /**
     * 保存的路径
     */
    private String destPath;
  }
}
