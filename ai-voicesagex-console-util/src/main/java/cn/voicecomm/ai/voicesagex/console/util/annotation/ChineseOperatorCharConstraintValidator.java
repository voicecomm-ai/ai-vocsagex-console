package cn.voicecomm.ai.voicesagex.console.util.annotation;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验特殊字符实现
 *
 * @author ryc
 * @date 2023/5/15
 */
public class ChineseOperatorCharConstraintValidator implements
    ConstraintValidator<ChineseOperatorChar, String> {

  @Override
  public void initialize(ChineseOperatorChar constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String str, ConstraintValidatorContext context) {
    if (CharSequenceUtil.isBlank(str)) {
      return Boolean.TRUE;
    }
    String regEx = "^[\\u4E00-\\u9FFF|]+$";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(str);
    return m.matches();
  }

}
