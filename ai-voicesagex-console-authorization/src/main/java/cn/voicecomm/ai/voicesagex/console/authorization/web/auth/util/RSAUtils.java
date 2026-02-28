package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.util;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import javax.crypto.Cipher;

/**
 * @see <a
 * href="https://blog.csdn.net/u011627980/article/details/50800474/">https://blog.csdn.net/u011627980/article/details/50800474/</a>
 * https://blog.csdn.net/dwj7758520/article/details/51014282
 * <p>author charles.shi date 12/2/2019 3:39 PM
 */
public class RSAUtils {

  // 一个固定的RSA系数，被Public/Private 公钥、私钥所共享
  private static final String RSA_KEY_MODULUS =
      "your RSA_KEY_MODULUS";

  // 公钥私钥指数
  private static final String PUBLIC_KEY_EXPONENT = "your PUBLIC_KEY_EXPONENT";
  private static final String PRIVATE_KEY_EXPONENT =
      "your PRIVATE_KEY_EXPONENT";

  // for 前端JS设置 (由于js通过模和公钥指数获取公钥对字符串进行加密，注意必须转为16进制，所以前端需要用下面两个参数)
  private static final String RSA_KEY_MODULUS_JS =
      "your RSA_KEY_MODULUS_JS";
  private static final String PUBLIC_KEY_EXPONENT_JS = "your PUBLIC_KEY_EXPONENT_JS";

  /**
   * 生成公钥和私钥
   *
   * @throws NoSuchAlgorithmException
   */
  public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException {
    HashMap<String, Object> map = new HashMap<>();
    KeyPairGenerator keyPairGen =
        KeyPairGenerator.getInstance(
            "RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
    keyPairGen.initialize(1024);
    KeyPair keyPair = keyPairGen.generateKeyPair();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    map.put("public", publicKey);
    map.put("private", privateKey);
    return map;
  }

  /**
   * 使用模和指数生成RSA公钥
   *
   * @param modulus  模
   * @param exponent 指数
   * @return
   */
  public static RSAPublicKey getPublicKey(String modulus, String exponent) {
    try {
      BigInteger b1 = new BigInteger(modulus);
      BigInteger b2 = new BigInteger(exponent);
      KeyFactory keyFactory =
          KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
      RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
      return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取RSA公钥
   *
   * <p>/None/NoPadding】
   *
   * @return
   */
  public static RSAPublicKey getPublicKey() {
    return getPublicKey(RSA_KEY_MODULUS, PUBLIC_KEY_EXPONENT);
  }

  /**
   * 使用模和指数生成RSA私钥
   *
   * <p>/None/NoPadding】
   *
   * @param modulus  模
   * @param exponent 指数
   * @return
   */
  public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
    try {
      BigInteger b1 = new BigInteger(modulus);
      BigInteger b2 = new BigInteger(exponent);
      KeyFactory keyFactory =
          KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
      RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
      return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获取RSA私钥
   *
   * <p>/None/NoPadding】
   *
   * @return
   */
  public static RSAPrivateKey getPrivateKey() {
    return getPrivateKey(RSA_KEY_MODULUS, PRIVATE_KEY_EXPONENT);
  }

  /**
   * 公钥加密
   *
   * @param data
   * @param publicKey
   * @return
   * @throws Exception
   */
  public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    // 模长
    int key_len = publicKey.getModulus().bitLength() / 8;
    // 加密数据长度 <= 模长-11
    String[] datas = splitString(data, key_len - 11);
    String mi = "";
    // 如果明文长度大于模长-11则要分组加密
    for (String s : datas) {
      mi += bcd2Str(cipher.doFinal(s.getBytes()));
    }
    return mi;
  }

  /**
   * 私钥解密
   *
   * @param data
   * @param privateKey
   * @return
   * @throws Exception
   */
  public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    // 模长
    int key_len = privateKey.getModulus().bitLength() / 8;
    byte[] bytes = data.getBytes();
    byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
    // System.err.println(bcd.length);
    // 如果密文长度大于模长则要分组解密
    String ming = "";
    byte[][] arrays = splitArray(bcd, key_len);
    for (byte[] arr : arrays) {
      ming += new String(cipher.doFinal(arr));
    }
    return ming;
  }

  /**
   * ASCII码转BCD码
   */
  public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
    byte[] bcd = new byte[asc_len / 2];
    int j = 0;
    for (int i = 0; i < (asc_len + 1) / 2; i++) {
      bcd[i] = asc_to_bcd(ascii[j++]);
      bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
    }
    return bcd;
  }

  public static byte asc_to_bcd(byte asc) {
    byte bcd;

    if ((asc >= '0') && (asc <= '9')) {
      bcd = (byte) (asc - '0');
    } else if ((asc >= 'A') && (asc <= 'F')) {
      bcd = (byte) (asc - 'A' + 10);
    } else if ((asc >= 'a') && (asc <= 'f')) {
      bcd = (byte) (asc - 'a' + 10);
    } else {
      bcd = (byte) (asc - 48);
    }
    return bcd;
  }

  /**
   * BCD转字符串
   */
  public static String bcd2Str(byte[] bytes) {
    char[] temp = new char[bytes.length * 2];
    char val;

    for (int i = 0; i < bytes.length; i++) {
      val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
      temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

      val = (char) (bytes[i] & 0x0f);
      temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
    }
    return new String(temp);
  }

  /**
   * 拆分字符串
   */
  public static String[] splitString(String string, int len) {
    int x = string.length() / len;
    int y = string.length() % len;
    int z = 0;
    if (y != 0) {
      z = 1;
    }
    String[] strings = new String[x + z];
    String str = "";
    for (int i = 0; i < x + z; i++) {
      if (i == x + z - 1 && y != 0) {
        str = string.substring(i * len, i * len + y);
      } else {
        str = string.substring(i * len, i * len + len);
      }
      strings[i] = str;
    }
    return strings;
  }

  /**
   * 拆分数组
   */
  public static byte[][] splitArray(byte[] data, int len) {
    int x = data.length / len;
    int y = data.length % len;
    int z = 0;
    if (y != 0) {
      z = 1;
    }
    byte[][] arrays = new byte[x + z][];
    byte[] arr;
    for (int i = 0; i < x + z; i++) {
      arr = new byte[len];
      if (i == x + z - 1 && y != 0) {
        System.arraycopy(data, i * len, arr, 0, y);
      } else {
        System.arraycopy(data, i * len, arr, 0, len);
      }
      arrays[i] = arr;
    }
    return arrays;
  }

  public static String encrypt(String text) throws Exception {
    String encryptText =
        RSAUtils.encryptByPublicKey(
            text, RSAUtils.getPublicKey(RSA_KEY_MODULUS, PUBLIC_KEY_EXPONENT));
    return encryptText;
  }

  public static String decrypt(String encryptText) throws Exception {
    String text =
        RSAUtils.decryptByPrivateKey(
            encryptText, RSAUtils.getPrivateKey(RSA_KEY_MODULUS, PRIVATE_KEY_EXPONENT));
    return text;
  }

  public static PublicKey getPublicKeyBS(String modulus, String exponent) {
    try {
      BigInteger b1 = new BigInteger(modulus, 16);
      BigInteger b2 = new BigInteger(exponent, 16);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String encryptByPublicKeyBS(byte[] data, PublicKey publicKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    int inputLen = data.length;

    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      int offSet = 0;
      byte[] cache;
      int i = 0;
      // 对数据分段加密
      while (inputLen - offSet > 0) {
        if (inputLen - offSet > 0x75) {
          cache = cipher.doFinal(data, offSet, 0x75);
        } else {
          cache = cipher.doFinal(data, offSet, inputLen - offSet);
        }
        out.write(cache, 0, cache.length);
        i++;
        offSet = i * 0x75;
      }
      byte[] bytes = out.toByteArray();
      char[] ret = new char[bytes.length * 2];
      char val;

      for (int j = 0; j < bytes.length; j++) {
        val = (char) (((bytes[j] & 0xf0) >> 4) & 0x0f);
        ret[j * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        val = (char) (bytes[j] & 0x0f);
        ret[j * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
      }
      return new String(ret);
    }
  }
}
