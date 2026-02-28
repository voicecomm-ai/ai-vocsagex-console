package cn.voicecomm.ai.voicesagex.console.util.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 鉴权的bean
 *
 * @author GeCh
 * @version v1.0
 * @date 2021-02-19
 */
@Data
@Builder
public class RequestAuthBean {

  /**
   * 随机字符串 8位
   */
  private String nonce;

  /**
   * 随机字符串是否已存在
   */
  private boolean nonceExist;

  /**
   * 鉴权的key
   */
  private String appKey;

  /**
   * 鉴权的secret
   */
  private String appSecret;

  /**
   * 当前时间戳 ms 13位
   */
  private String timestamp;

  /**
   * 最后的签名
   */
  private String sign;
}
