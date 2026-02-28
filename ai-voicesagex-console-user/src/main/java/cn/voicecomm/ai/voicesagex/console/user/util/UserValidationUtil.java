package cn.voicecomm.ai.voicesagex.console.user.util;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import static cn.voicecomm.ai.voicesagex.console.api.constant.user.RegexConstant.ACCOUNT_REGEX;
import static cn.voicecomm.ai.voicesagex.console.api.constant.user.RegexConstant.PASSWORD_REGEX;
import static cn.voicecomm.ai.voicesagex.console.api.constant.user.UserConstant.STATUS;
import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@NoArgsConstructor(access = PRIVATE)
public final class UserValidationUtil {

  public enum ValidationField {
    ACCOUNT,
    USERNAME,
    PASSWORD,
    STATUS
  }

  /**
   * 验证账号是否合法：长度不超过15
   *
   * @param account 账号
   * @return true 合法，false 不合法
   */
  public static boolean isValidAccount(String account) {
    if (StringUtils.isEmpty(account)) {
      return false;
    }
    return Pattern.compile(ACCOUNT_REGEX).matcher(account).matches();
  }


  /**
   * 验证用户名是否合法：长度不超过50
   *
   * @param username 用户名
   * @return true 合法，false 不合法
   */
  public static boolean isValidUsername(String username) {
    return username != null && username.length() <= 50;
  }

  /**
   * 验证密码是否合法：数字、英文大小写和特殊字符，长度12-18
   *
   * @param password 密码
   * @return true 合法，false 不合法
   */
  public static boolean isValidPassword(String password) {
    if (StringUtils.isEmpty(password)) {
      return false;
    }
    return Pattern.compile(PASSWORD_REGEX).matcher(password).matches();
  }

  public static boolean isValidStatus(Byte status) {
    return ArrayUtils.contains(STATUS, status);
  }

  public static CommonRespDto<List<String>> isValidUser(
    BackendUserDto user, ValidationField... ignoreFields) {
    List<String> errorMessages = new ArrayList<>();

    List<ValidationField> remain = new ArrayList<>(Arrays.asList(ValidationField.values()));
    if (Objects.nonNull(ignoreFields)) {
      remain.removeAll(Arrays.asList(ignoreFields));
    }

    if (remain.contains(ValidationField.ACCOUNT) && !isValidAccount(user.getAccount())) {
      errorMessages.add("账号格式不正确");
    }

    if (remain.contains(ValidationField.USERNAME) && !isValidUsername(user.getUsername())) {
      errorMessages.add("用户名格式不正确");
    }

    if (remain.contains(ValidationField.PASSWORD) && !isValidPassword(user.getPassword())) {
      errorMessages.add("密码格式不正确");
    }

    if (remain.contains(ValidationField.STATUS) && !isValidStatus(user.getStatus())) {
      errorMessages.add("不存在的状态");
    }

    if (errorMessages.isEmpty()) {
      return CommonRespDto.success();
    } else {
      return CommonRespDto.error(errorMessages.get(0), errorMessages);
    }
  }
}
