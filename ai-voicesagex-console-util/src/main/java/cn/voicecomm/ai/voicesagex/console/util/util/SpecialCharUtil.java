package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.util.constant.SpaceConstant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 特殊字符处理
 *
 * @author ryc
 * @date 2023/6/21
 */
public class SpecialCharUtil {

  /**
   * 将特殊符号进行转义
   *
   * @param str
   * @return
   */
  public static String transfer(String str) {
    if (StrUtil.isBlank(str)) {
      return str;
    }
    String regex = "([\\\\$\\(\\)\\*\\+\\.\\[\\]\\?\\^\\{\\}\\|\\_\\%\\'])";
    String replaceStr = "\\\\$0";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(str);
    if (matcher.find()) {
      str = str.replaceAll(regex, replaceStr);
    }
    return str;
  }

  /**
   * 本中不能包含中文符号但是可以有汗字而且如果出现英文符号，只能有以下[]|()*^$个
   *
   * @param text
   * @return ture：文本符合要求； false：文本不符合要求
   */
  public static boolean textValidator(String text) {
    if (StrUtil.isBlank(text)) {
      return true;
    }
    // 正则表达式
    String regex = "^[^\\p{P}]*[\\p{InCJKUnifiedIdeographs}&&[^\\p{P}]]*([\\[\\]|()*^$][^\\p{P}]*)*$";
    // 创建 Pattern 对象
    Pattern pattern = Pattern.compile(regex);
    // 创建 Matcher 对象
    Matcher matcher = pattern.matcher(text);
    // 进行匹配
    return matcher.matches();
  }

  public static boolean specialCharValid(String text) {
    if (StrUtil.isBlank(text)) {
      return true;
    }
    String regEx = "[\\u4e00-\\u9fa5\\w-._()（）:： ]*";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(text);
    return m.matches();
  }

  /**
   * 转义正则特殊字符 （$()*+.[]?\^{},|）
   */
  public static String replaceSpecialWord(String keyword) {
    if (StrUtil.isEmpty(keyword)) {
      return StrUtil.EMPTY;
    }
    String[] specialWords = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|",
        "_", "%"};
    for (String key : specialWords) {
      if (keyword.contains(key)) {
        keyword = keyword.replace(key, "\\" + key);
      }
    }
    return keyword;
  }

  public static boolean containsSpecialCharacters(String input) {
    if (StrUtil.isBlank(input)) {
      return false;
    }
    // 如果找到匹配项，则返回true
    Matcher matcher = Pattern.compile(SpaceConstant.PATTERN).matcher(input);
    return matcher.matches();
  }

  public static String sub(String str, Integer length) {
    if (StrUtil.isBlank(str)) {
      return str;
    }
    if (str.length() > length) {
      return str.substring(0, length);
    }
    return str;
  }

  /**
   * 将第一个data1 替换为空字符串
   *
   * @param data
   * @return
   */
  public static String replaceFirstDataToEmptyStr(String data) {
    if (CharSequenceUtil.isBlank(data)) {
      return data;
    }
    return data.replaceFirst("^/data1/", "");
  }
}
