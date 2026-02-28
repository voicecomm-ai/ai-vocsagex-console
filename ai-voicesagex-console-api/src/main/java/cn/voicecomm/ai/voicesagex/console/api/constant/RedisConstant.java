package cn.voicecomm.ai.voicesagex.console.api.constant;

public class RedisConstant {

  public static final String ALGORITHM_MODEL_TRAIN_KEY = "lock:algorithm_model:train";

  public static final String ALGORITHM_MODEL_EVAL_KEY = "lock:algorithm_model:eval";

  public static final String PRE_MODEL_FINETUNE_KEY = "lock:pre_model:finetune";


  public static final String CODE_UPLOAD_KEY = "code_upload:";

  public static final String UPLOAD_LOCK = "upload_lock:";

  public static final String UPLOAD_CHUNKS_KEY = "upload_chunks:";


  /**
   * 订单完成key
   */
  public static final String ORDER_FINISH_KEY = "order_finish:";


  public static final String ORDER_ACCEPT_KEY = "order_accept:";


  /**
   * 订单支付通知key前缀
   */
  public static final String ORDER_PAY_NOTIFY_KEY = "order_pay_notify:";

  /**
   * 订单支付通知key前缀-富友
   */
  public static final String ORDER_PAY_NOTIFY_FUIOU_KEY = "order_pay_notify_fuiou:";


  public static final String ORDER_PAY_NOTIFY_FINISHED_KEY = "order_pay_notify_finished_key:";


  public static final String ORDER_PAY_NOTIFY_FINISHED_FUIOU_KEY = "order_pay_notify_finished_fuiou_key:";


  /**
   * 订单退款通知key前缀
   */
  public static final String ORDER_REFUND_NOTIFY_KEY = "order_refund_notify:";

  public static final String ORDER_REFUND_NOTIFY_FINISHED_KEY = "order_refund_finished_notify:";

  // APP在线监控
  public static final String APP_DEVICE_MONITOR = "app_device_monitor:";
  public static final String DRAWING_APP_MONITOR = "drawing_app_monitor:";

  public static final String APP_STATISTICAL_RECORD = "app_device_statistical_record:";

  // 临时key
  public static final String APP_STATISTICAL_TEMP_RECORD = "app_device_statistical_temp_record:";
  public static final String APP_STATISTICAL_RECORD_UUID = "app_device_statistical_record_uuid:";
  public static final String APP_STATISTICAL_TEMPLATE_RECORD = "app_device_statistical_template_record:";

  public static final String APPEND_CHARGE = ":charge:";


  public static final String DRAW_PHOTO_QRDATA = "draw_photo_qrdata:";

  public static final String PICTURE_DOWNLOAD_KEY = "picture_download:";


  /**
   * 退款完成key -- 富友
   */
  public static final String REFUND_FINISHED_FUIOU_KEY = "refund_finished_fuiou:";


}
