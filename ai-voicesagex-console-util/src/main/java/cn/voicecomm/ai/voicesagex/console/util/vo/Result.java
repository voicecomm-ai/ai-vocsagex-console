package cn.voicecomm.ai.voicesagex.console.util.vo;

import cn.voicecomm.ai.voicesagex.console.util.enums.ResultCodeEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jiwh
 * @date 2024/5/30 14:12
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

  /**
   * 状态码，1000为成功，其他为失败
   */
  private Integer code;

  /**
   * 状态码的描述
   */
  private String msg;

  /**
   * 返回的数据
   */
  private T data;

  public static <T> Result<T> success() {
    return of(ResultCodeEnum.SUCCESS, null, null);
  }

  public static <T> Result<T> success(T data) {
    return of(ResultCodeEnum.SUCCESS, null, data);
  }


  public boolean isOk() {
    return this.code == ResultCodeEnum.SUCCESS.getCode();
  }

  public static <T> Result<T> successMsg(String msg) {
    return of(ResultCodeEnum.SUCCESS, msg, null);
  }

  public static <T> Result<T> success(T data, String msg) {
    return of(ResultCodeEnum.SUCCESS, msg, data);
  }
  public static <T> Result<T> error() {
    return of(ResultCodeEnum.FAILED, null, null);
  }
  public static <T> Result<T> error(String msg) {
    return of(ResultCodeEnum.FAILED, msg, null);
  }

  public static <T> Result<T> error(String msg, T data) {
    return of(ResultCodeEnum.FAILED, msg, data);
  }

  public static <T> Result<T> error(ResultCodeEnum resultCodeEnum, String msg, T data) {
    return of(resultCodeEnum, msg, data);
  }

  public static <T> Result<T> error(Integer code, String msg, T data) {
    return new Result<>(code, msg, data);
  }

  public static <T> Result<T> of(ResultCodeEnum code) {
    return of(code, null, null);
  }

  public static <T> Result<T> of(ResultCodeEnum code, String msg) {
    return of(code, msg, null);
  }

  public static <T> Result<T> of(ResultCodeEnum code, String msg, T data) {
    String finalMsg = StringUtils.isEmpty(msg) ? code.getMsg() : msg;
    return new Result<>(code.getCode(), finalMsg, data);
  }
}
