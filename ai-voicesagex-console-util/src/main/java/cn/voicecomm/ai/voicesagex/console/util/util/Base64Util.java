package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.text.CharSequenceUtil;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * Base64字符加密解密
 *
 * @author ryc
 * @date 2023/6/21
 */
@Slf4j
public class Base64Util {

  /**
   * Base64加密
   *
   * @param value      明文
   * @param key        秘钥
   * @param initVector 初始向量
   * @return 密文
   * @throws Exception 异常
   */
  public static String encrypt(String value, String key, String initVector) {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encrypted);
    } catch (Exception e) {
      log.error("Base64加密错误：{}", e);
    }
    return CharSequenceUtil.EMPTY;
  }

  /**
   * Base64解密
   *
   * @param ciphertext 密文
   * @param key        秘钥
   * @param initVector 初始向量
   * @return 明文
   * @throws Exception 异常
   */
  public static String decrypt(String ciphertext, String key, String initVector) {
    try {
      IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
      SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
      byte[] original = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
      return new String(original, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Base64解密错误：{}", e);
    }
    return CharSequenceUtil.EMPTY;
  }
}
