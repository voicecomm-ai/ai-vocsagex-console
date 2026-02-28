package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class PropertyValidator {

    // 校验整数类型的正则表达式
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^-?\\d+$");

    // 校验15位浮点数的正则表达式
    private static final Pattern FLOAT_15_DIGITS_PATTERN = Pattern.compile("^-?\\d+(\\.\\d{1,14})?$");

    // 校验时间的正则表达式
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?\\d|2[0-3]):([0-5]?\\d):([0-5]?\\d)$");

    // 校验日期的正则表达式
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    private static final Pattern DATA = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
    private static final Pattern DATA_FIX = Pattern.compile("\\d{4}/\\d{1,2}/\\d{1,2}");

    // 校验日期时间的正则表达式
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String DATE_PATTERN_CHAK = "yyyy-MM-dd";
    private static final String DATE_FIX_PATTERN_CHAK = "yyyy/MM/dd";


    public static boolean validatePropertyType(String propertyType, String propertyValue) {
        switch (propertyType) {
            case "INT8":
                return  isInt8(propertyValue);
            case "INT16":
                return  isInt16(propertyValue);
            case "INT32":
                return isInt32(propertyValue);
            case "INT64":
                return isInt64(propertyValue);
            case "DOUBLE":
            case "FLOAT":
                return isFloat15Digits(propertyValue);
            case "STRING":
            case "FIXED_STRING":
                return isString(propertyValue);
            case "TIME":
                return isTime(propertyValue);
            case "DATE":
                return isDate(propertyValue);
            case "DATETIME":
                return isDateTime(propertyValue);
            case "TIMESTAMP":
                return isDateTime(propertyValue);
            case "BOOL":
                return isBool(propertyValue);
            default:
                // 未知类型，可以抛出异常或返回false
                return false;
        }
    }

    public static boolean validateProperty(String propertyType, String propertyValue) {
        switch (propertyType) {
            case "INT8":
                return  isInt8(propertyValue);
            case "INT16":
                return  isInt16(propertyValue);
            case "INT32":
                return isInt32(propertyValue);
            case "INT64":
                return isInt64(propertyValue);
            case "FIXED_STRING":
                return isString(propertyValue);
            default:
                return  true;
        }
    }


    private static boolean isInt32(String propertyValue) {
      try {
        return  (Long.valueOf(propertyValue) >= -2147483648) && (Long.valueOf(propertyValue) <= 2147483647);
      } catch (NumberFormatException e) {
        return false;
      }
    }


    private static boolean isInt16(String propertyValue) {
      try {
        return (Long.valueOf(propertyValue) > -32769) && (Long.valueOf(propertyValue) < 32768);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    private static boolean isInt64(String propertyValue) {
      try {
        return (Long.valueOf(propertyValue) >= -9223372036854775808L) && (Long.valueOf(propertyValue) <= 9223372036854775807L);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    private static boolean isInt8(String propertyValue) {
        try {
            return (Long.valueOf(propertyValue) > -129) && (Long.valueOf(propertyValue) < 128);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private static boolean isInteger(String value) {
        return true;
    }

    private static boolean isFloat15Digits(String value) {
        return FLOAT_15_DIGITS_PATTERN.matcher(value).matches();
    }

    private static boolean isString(String value) {
        // 字符串类型不需要复杂的校验，只要不是null或者空字符串就可以
        return value != null && !value.isEmpty();
    }

    private static boolean isTime(String value) {
        return TIME_PATTERN.matcher(value).matches();
    }

    private static boolean isDate(String value) {
        if (DATA.matcher(value).matches() || DATA_FIX.matcher(value).matches()) {
            return isDateCheck(value);
        }
        return false;
    }

    private static boolean isDateTime(String value) {
        try {
            DATETIME_FORMAT.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }




    public static boolean isDateCheck(String value) {
        try {

            // 尝试使用yyyy-MM-dd格式解析
            LocalDate.parse(value, java.time.format.DateTimeFormatter.ofPattern(DATE_PATTERN_CHAK));
            return true;
        } catch (DateTimeParseException e) {
            // 如果失败，尝试使用yyyy/MM/dd格式解析
            try {
                LocalDate.parse(value, java.time.format.DateTimeFormatter.ofPattern(DATE_FIX_PATTERN_CHAK));
                return true;
            } catch (DateTimeParseException ex) {
                // 如果两种格式都失败，则不是有效的日期
                return false;
            }
        }
    }





    private static boolean isBool(String value) {
        return "TRUE".equalsIgnoreCase(value) || "FALSE".equalsIgnoreCase(value);
    }
}
