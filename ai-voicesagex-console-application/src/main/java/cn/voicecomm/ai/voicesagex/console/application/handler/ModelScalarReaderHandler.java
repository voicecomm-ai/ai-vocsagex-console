package cn.voicecomm.ai.voicesagex.console.application.handler;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.CoordinateDto;
import cn.voicecomm.ai.voicesagex.console.util.util.FileReadUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.tensorflow.proto.util.Event;

/**
 * 模型监控
 *
 * @author ryc
 * @description
 * @date 2025/12/9 10:40
 */
@Slf4j
public class ModelScalarReaderHandler {

//  public static void main(String[] args) {
////    Map<String, List<CoordinateDto>> stringListMap = ModelScalarReaderHandler.readOtherMonitoring(
////        "C:\\Users\\adminst\\Desktop\\source\\events.out.tfevents.1767513757"
////            + ".qe-job-4c7c7383-bcbd-43c6-bf53-2b06b956c799-master-0.1.0");
////    System.out.println(JSONUtil.toJsonStr(stringListMap));
//
////    Object stringListMap = ModelScalarReaderHandler.readOllamaMonitoring(
////        "C:\\Users\\adminst\\Downloads\\日志示例文件\\llama" + "-factory\\train\\trainer_log.jsonl");
////    System.out.println(stringListMap);
//
//    Map<String, Object> stringObjectMap = FileReadUtil.readFileToMap(
//        "C:\\Users\\adminst\\Downloads\\日志示例文件\\llama" + "-factory\\train\\trainer_log.jsonl");
//
//    System.out.println(stringObjectMap);
//
//    System.out.println(JSONUtil.parseObj(stringObjectMap));
//
//  }

  public static Map<String, List<CoordinateDto>> readOtherMonitoring(String logPath) {
    log.info("获取其他类型的监控文件：{}", logPath);
    Map<String, List<CoordinateDto>> result = new HashMap<>();
    try (FileInputStream fis = new FileInputStream(logPath)) {
      while (true) {
        byte[] lenBytes = fis.readNBytes(8);
        if (lenBytes.length < 8) {
          break;
        }
        long length = ByteBuffer.wrap(lenBytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
        // crc
        fis.skipNBytes(4);
        byte[] eventBytes = fis.readNBytes((int) length);
        if (eventBytes.length < length) {
          break;
        }
        Event event;
        try {
          event = Event.parseFrom(eventBytes);
        } catch (InvalidProtocolBufferException e) {
          fis.skipNBytes(4);
          continue;
        }
        if (event.hasSummary()) {
          event.getSummary().getValueList().forEach(v -> {
            String tag = v.getTag();
            long step = event.getStep();
            double value;
            // 优先 tensor
            if (v.hasTensor() && v.getTensor().getFloatValCount() > 0) {
              value = v.getTensor().getFloatVal(0);
            } else {
              value = v.getSimpleValue();
            }
            CoordinateDto point = CoordinateDto.builder().xaxis(step).yaxis(value).build();
            // 按 tag 分组
            result.computeIfAbsent(tag, k -> new ArrayList<>()).add(point);
          });
        }
        // crc
        fis.skipNBytes(4);
      }
    } catch (Exception e) {
      log.error("读取其他方式的监控出错", e);
      return new HashMap<>();
    }
    result.entrySet().removeIf(entry -> {
      List<CoordinateDto> list = entry.getValue();
      if (list.size() != 1) {
        return false;
      }
      CoordinateDto p = list.get(0);
      return p.getXaxis() == 0 && p.getYaxis() == 0;
    });
    return result;
  }

  /**
   * 读取ollama的配置
   *
   * @param logPath
   * @return
   */
  public static Map<String, List<CoordinateDto>> readOllamaMonitoring(String logPath) {
    log.info("获取ollama类型的监控文件：{}", logPath);
    Map<String, List<CoordinateDto>> coordinateMap = new HashMap<>();
    List<Map<String, Object>> dataList = FileReadUtil.readJsonlToList(logPath);
    if (CollUtil.isEmpty(dataList)) {
      return coordinateMap;
    }
    // 映射到 CoordinateDto（current_steps -> xAxis, loss -> yAxis）
    List<CoordinateDto> coordinates = dataList.stream().map(data -> CoordinateDto.builder()
            .xaxis(((Number) data.getOrDefault("current_steps", 0L)).longValue())
            .yaxis(((Number) data.getOrDefault("loss", 0)).doubleValue()).build())
        .collect(Collectors.toList());
    // 按 xAxis (loss) 从小到大排序
    List<CoordinateDto> sortedCoordinates = coordinates.stream()
        .sorted(Comparator.comparingDouble(CoordinateDto::getXaxis)).collect(Collectors.toList());
    coordinateMap.put("loss", sortedCoordinates);
    coordinateMap.entrySet().removeIf(entry -> {
      List<CoordinateDto> list = entry.getValue();
      if (list.size() != 1) {
        return false;
      }
      CoordinateDto p = list.get(0);
      return p.getXaxis() == 0 && p.getYaxis() == 0;
    });
    return coordinateMap;
  }

  public static String getLatestFileFullPath(String dirPath) {
    log.info("获取文件夹中最新一个文件路径：{}", dirPath);
    try {
      Path dir = Paths.get(dirPath);
      try (Stream<Path> stream = Files.list(dir)) {
        Optional<Path> latest = stream.filter(p -> !Files.isDirectory(p))
            .max(Comparator.comparingLong(p -> {
              try {
                return Files.getLastModifiedTime(p).toMillis();
              } catch (IOException e) {
                return 0L;
              }
            }));
        // Path.toAbsolutePath() 本身就是全路径
        return latest.map(p -> p.toAbsolutePath().toString()).orElse(null);
      }
    } catch (Exception e) {
      log.error("文件获取失败：{}", e.getMessage());
      return null;
    }
  }

}
