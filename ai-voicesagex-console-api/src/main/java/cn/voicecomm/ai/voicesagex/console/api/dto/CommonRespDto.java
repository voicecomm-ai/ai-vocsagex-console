package cn.voicecomm.ai.voicesagex.console.api.dto;


import cn.voicecomm.ai.voicesagex.console.api.enums.CommonEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.ICommonEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommonRespDto<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -479424010664469493L;

  /**
   * 是否成功
   */
  private boolean isOk;

  /**
   * 状态码
   */
  private Integer code;

  /**
   * 返回信息
   */
  private String msg;

  /**
   * 返回数据
   */
  private T data;


  public static <T> CommonRespDto<T> success() {
    return success(null);
  }

  public static <T> CommonRespDto<T> success(T data) {
    return success("", data);
  }

  public static <T> CommonRespDto<T> of(boolean isOk, String msg, T data) {
    if (Boolean.TRUE.equals(isOk)) {
      return success("", data);
    } else {
      return error(msg, data);
    }
  }

  public static <T> CommonRespDto<T> of(boolean isOk, String msg) {
    if (Boolean.TRUE.equals(isOk)) {
      return success();
    } else {
      return error(msg);
    }
  }

  public static <T> CommonRespDto<T> of(boolean isOk) {
    if (Boolean.TRUE.equals(isOk)) {
      return success();
    } else {
      return error();
    }
  }

  public static <T> CommonRespDto<T> success(String message, T data) {
    return of(CommonEnum.SUCCESS, message, data);
  }

  public static <T> CommonRespDto<T> error() {
    return of(CommonEnum.ERROR, null, null);
  }

  public static <T> CommonRespDto<T> error(String message) {
    return error(CommonEnum.ERROR, message);
  }

  public static <T> CommonRespDto<T> error(String message, T data) {
    return error(CommonEnum.ERROR, message, data);
  }

  public static <T> CommonRespDto<T> error(ICommonEnum commonEnum, String message) {
    return error(commonEnum, message, null);
  }

  public static <T> CommonRespDto<T> error(ICommonEnum commonEnum, T data) {
    return error(commonEnum, null, data);
  }

  public static <T> CommonRespDto<T> error(ICommonEnum commonEnum, String message, T data) {
    return of(commonEnum, message, data);
  }

  public static <T> CommonRespDto<T> of(ICommonEnum commonEnum, String message, T data) {
    CommonRespDto<T> commonRespDTO = new CommonRespDto<>();
    commonRespDTO.setCode(commonEnum.getCode());
    // code 为1000时isOk表示成功，其他表示失败
    commonRespDTO.setOk(CommonEnum.SUCCESS.getCode().equals(commonEnum.getCode()));
    if (message == null || message.isEmpty()) {
      commonRespDTO.setMsg(commonEnum.getMessage());
    } else {
      commonRespDTO.setMsg(message);
    }
    commonRespDTO.setData(data);

    return commonRespDTO;
  }

  public CommonRespDto(T data) {
    this(CommonEnum.SUCCESS, null, data);
  }

  public CommonRespDto(String msg, T data) {
    this(CommonEnum.SUCCESS, msg, data);
  }

  public CommonRespDto(ICommonEnum commonEnum, String msg) {
    this(commonEnum, msg, null);
  }

  public CommonRespDto(ICommonEnum commonEnum, T data) {
    this(commonEnum, null, data);
  }

  public CommonRespDto(ICommonEnum commonEnum) {
    this(commonEnum, null, null);
  }

  public CommonRespDto(ICommonEnum commonEnum, String msg, T data) {
    // code 为1000时isOk表示成功，其他表示失败
    code = commonEnum.getCode();
    isOk = CommonEnum.SUCCESS.getCode().equals(code);
    if (msg == null || msg.isEmpty()) {
      msg = commonEnum.getMessage();
    }
    this.msg = msg;
    this.data = data;
  }

  public CommonRespDto() {
    code = CommonEnum.SUCCESS.getCode();
    msg = CommonEnum.SUCCESS.getMessage();
    isOk = true;
  }

}
