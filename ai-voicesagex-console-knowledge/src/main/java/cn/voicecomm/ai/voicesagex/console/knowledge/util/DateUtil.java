package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import com.vesoft.nebula.DateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {


  public static boolean dateTimeComparison(DateTime input) {
    // 将com.vesoft.nebula.DateTime转换为java.time.LocalDateTime
    LocalDateTime dateTime = LocalDateTime.of(input.getYear(), input.getMonth(), input.getDay(),
        input.getHour(), input.getMinute(), input.getSec(), input.getMicrosec());

    LocalDateTime now = LocalDateTime.now();
    return dateTime.isBefore(now);
  }

  public static String dateTimetoString(DateTime input) {
    // 将com.vesoft.nebula.DateTime转换为java.time.LocalDateTime
    LocalDateTime dateTime = LocalDateTime.of(input.getYear(), input.getMonth(), input.getDay(),
        input.getHour(), input.getMinute(), input.getSec(), input.getMicrosec());

    // 使用DateTimeFormatter格式化为yyyy-MM-dd HH:mm:ss格式的字符串
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedDateTime = dateTime.format(formatter);

    return formattedDateTime;
  }

  public static String nowDateString() {
    // Get the current date and time
    LocalDateTime currentDateTime = LocalDateTime.now();

    // Define the desired format
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Format the current date and time
    String formattedDate = currentDateTime.format(formatter);

    return formattedDate;
  }
}
