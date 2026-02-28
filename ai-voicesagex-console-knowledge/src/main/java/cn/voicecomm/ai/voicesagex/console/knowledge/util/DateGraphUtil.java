package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Date Util
 */
public class DateGraphUtil {


  public static String dateProcess(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 使用SimpleDateFormat的format方法来格式化Date对象
    return sdf.format(date);
  }

  public static Date dateProcess(String dateString) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // Use SimpleDateFormat's parse method to convert the string to a Date object
    return sdf.parse(dateString);
  }

  public static String dateProcess1(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    // 使用SimpleDateFormat的format方法来格式化Date对象
    return sdf.format(date);
  }


  public static String dateProcessTime(String input) {
    DateTimeFormatter parser = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy",
        Locale.ENGLISH).withZone(ZoneId.of("Asia/Shanghai"));
    LocalDateTime dateTime = LocalDateTime.parse(input, parser);
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public static String dateProcessTimeStamp(Long input) {
    Instant instant = Instant.ofEpochSecond(input);
    ZoneId sourceZone = ZoneId.systemDefault();
    ZoneId targetZone = ZoneId.of("Asia/Shanghai");
    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, sourceZone).atZone(sourceZone)
        .withZoneSameInstant(targetZone).toLocalDateTime();
    return dateTime.atZone(targetZone).withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }


  public static String TimeZoneConversion(String time) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalTime utcLocalTime = LocalTime.parse(time, timeFormatter);
    // 将UTC时间转换为北京时间（GMT+8）
    // 注意：这里我们简单地加上了8小时，没有考虑日期变更或夏令时
    LocalTime beijingLocalTime = utcLocalTime.plusHours(8);
    // 如果转换后的时间超过24小时，则需要调整（但在本例中不会发生）
    // 但在实际应用中，你可能需要更复杂的逻辑来处理跨日的情况
    return beijingLocalTime.format(timeFormatter);
  }


}
