package cn.voicecomm.ai.voicesagex.console.api.enums;

/**
 * @author jiwh
 * @date 2023/3/15 15:25
 */
public enum CommonEnum implements ICommonEnum {
  /**
   * 请求成功
   */
  SUCCESS(1000, "请求成功"),
  /**
   * 请求失败
   */
  ERROR(2000, "请求失败"),


  ORDER_STATUS_ERROR(2021, "该订单已非待接单状态"),

  ORDER_STATUS_CHANGE(2022, "该订单状态已变更"),

  NOT_REACH_TIME(2023, "未到预约时间"),

  EXIST_SERVICING_ORDER(2024, "您有正在服务中的订单，请完成服务后再开始下一单！"),

  /**
   * 完成配置失败
   */
  PROJECT_CONFIG_FAIL(3001, "超出限制"),

  /**
   * 模型下载成功
   */
  MODEL_DOWNLOAD_SUCCESS(4001, "模型下载成功");

  private final Integer code;

  private final String message;

  CommonEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public Integer getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
