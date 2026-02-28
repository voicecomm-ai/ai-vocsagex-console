package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 数学运算验证码文本生成
 */
public class CaptchaTextCreatorUtil extends DefaultTextCreator {

  private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

  @Override
  public String getText() {
    int result;
    Random random = new SecureRandom();
    int x = random.nextInt(10);
    int y = random.nextInt(10);
    StringBuilder suChinese = new StringBuilder();
    int randomOperands = (int) Math.round(Math.random() * 2);
    if (randomOperands == 0) {
      result = x * y;
      suChinese.append(NUMBERS[x]);
      suChinese.append("*");
      suChinese.append(NUMBERS[y]);
    } else if (randomOperands == 1) {
      if (!(x == 0) && y % x == 0) {
        result = y / x;
        suChinese.append(NUMBERS[y]);
        suChinese.append("÷");
        suChinese.append(NUMBERS[x]);
      } else {
        result = x + y;
        suChinese.append(NUMBERS[x]);
        suChinese.append("+");
        suChinese.append(NUMBERS[y]);
      }
    } else if (randomOperands == 2) {
      if (x >= y) {
        result = x - y;
        suChinese.append(NUMBERS[x]);
        suChinese.append("-");
        suChinese.append(NUMBERS[y]);
      } else {
        result = y - x;
        suChinese.append(NUMBERS[y]);
        suChinese.append("-");
        suChinese.append(NUMBERS[x]);
      }
    } else {
      result = x + y;
      suChinese.append(NUMBERS[x]);
      suChinese.append("+");
      suChinese.append(NUMBERS[y]);
    }
    suChinese.append("=?@").append(result);
    return suChinese.toString();
  }
}
