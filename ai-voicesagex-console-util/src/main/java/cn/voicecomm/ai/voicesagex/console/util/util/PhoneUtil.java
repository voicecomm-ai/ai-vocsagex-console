package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.lang.Validator;
import java.util.regex.Pattern;

/**
 * 手机号校验
 *
 * @author GeCh
 * @version v1.0.0
 * @date 2023-05-12
 */
public class PhoneUtil {

  private static final String REG_MOBILE =
      "(13\\d|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18\\d|19[0-35-9])\\d{8}";

  /**
   * 移动电话
   */
  public static final Pattern MOBILE = Pattern.compile(REG_MOBILE);

  /**
   * 验证是否为手机号码（中国大陆）
   *
   * @param value 值
   * @return 是否为手机号码（中国大陆）
   * @since v1.0.0
   */
  public static boolean isMobile(CharSequence value) {
    return Validator.isMatchRegex(MOBILE, value);
  }
}
