package cn.voicecomm.ai.voicesagex.console.util.util;

import static java.lang.Math.abs;

import cn.voicecomm.ai.voicesagex.console.util.vo.RequestAuthBean;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * API 鉴权 Util
 *
 * @author GeCh
 * @version v1.0
 * @date 2021-02-19
 */
public class ApiAuthHelp {

  /**
   * 根据 key, secret 生成bean
   *
   * @param appKey    key
   * @param appSecret secret
   * @return bean
   */
  public static RequestAuthBean authEncrypt(String appKey, String appSecret) {
    String nonce = RandomStringUtils.random(8, true, true);
    String timestamp = String.valueOf(System.currentTimeMillis());

    String s = String.format("%s%s%s%s", appKey, nonce, timestamp, appSecret);
    String sign = null;
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(s.getBytes());
      sign = Hex.encodeHexString(digest);
    } catch (NoSuchAlgorithmException ignored) {
    }
    return RequestAuthBean.builder()
        .timestamp(timestamp)
        .appKey(appKey)
        .nonce(nonce)
        .sign(sign)
        .build();
  }

  /**
   * 校验 auth bean
   *
   * @param requestAuthBean bean
   * @throws RuntimeException 失败时抛出异常
   */
  public static void authValidate(RequestAuthBean requestAuthBean) throws RuntimeException {
    if (StringUtils.isEmpty(requestAuthBean.getAppKey())) {
      throw new RuntimeException("API: app key is null");
    }

    if (StringUtils.isEmpty(requestAuthBean.getNonce())
        || requestAuthBean.getNonce().length() != 8) {
      throw new RuntimeException("API: nonce error");
    }

    if (requestAuthBean.isNonceExist()) {
      throw new RuntimeException("API: nonce exist");
    }

    if (StringUtils.isEmpty(requestAuthBean.getAppSecret())) {
      throw new RuntimeException("API: not found app key");
    }

    try {
      long aLong = Long.parseLong(requestAuthBean.getTimestamp());
      long now = System.currentTimeMillis();
      if (abs(now - aLong) > 5000) {
        throw new RuntimeException("API: timestamp is invalid");
      }
    } catch (NumberFormatException ignore) {
      throw new RuntimeException("API: timestamp is error");
    }

    String s =
        String.format(
            "%s%s%s%s",
            requestAuthBean.getAppKey(),
            requestAuthBean.getNonce(),
            requestAuthBean.getTimestamp(),
            requestAuthBean.getAppSecret());
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(s.getBytes());
      String sign = Hex.encodeHexString(digest);
      if (sign.equals(requestAuthBean.getSign())) {
        return;
      }
    } catch (NoSuchAlgorithmException ignored) {
    }
    throw new RuntimeException("API: sign is invalid");
  }
}
