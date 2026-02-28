package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * ·多音字工具类
 *
 * @author wangfan
 * @date 2023/11/22 9:34
 */
public class MultiToneUtil {

  public static final String REGEX = "<[a-z]+[1-5]>";

  /**
   * 处理多音字
   *
   * @param text
   * @return
   */
  public static String multiToneHandle(String text) {
    // 多音字检测
    Pattern multyVoicepattern = Pattern.compile(REGEX);
    Matcher matcher = multyVoicepattern.matcher(text);
    List<String> groupList = new ArrayList<>();
    while (matcher.find()) {
      groupList.add(matcher.group());
    }
    if (CollUtil.isNotEmpty(groupList)) {
      StringBuilder sb = new StringBuilder("<speak>");
      String[] textSplit = text.split(REGEX);
      IntStream.range(0, textSplit.length)
          .forEach(
              index -> {
                String segText = textSplit[index];
                String preText = segText.substring(0, segText.length() - 1);
                String lastText = segText.substring(segText.length() - 1);
                if (index <= groupList.size() - 1) {
                  String severalVoiceTextDesc =
                      groupList.get(index).replace("<", "").replace(">", "");
                  String severalVoiceTextScript =
                      "<phoneme alphabet=\"py\" ph=\""
                          + severalVoiceTextDesc
                          + "\">"
                          + lastText
                          + "</phoneme>";
                  sb.append(preText).append(severalVoiceTextScript);

                } else {
                  sb.append(segText);
                }
                if (index == textSplit.length - 1) {
                  sb.append("</speak>");
                }
              });
      text = sb.toString();
    }
    return text;
  }

  /**
   * 多音字配置标签去除
   *
   * @param text
   * @return
   */
  public static String multiToneLabelClear(String text) {
    return text.replaceAll(REGEX, "");
  }
}
