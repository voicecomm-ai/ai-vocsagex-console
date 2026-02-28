package cn.voicecomm.ai.voicesagex.console.util.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加密工具类
 *
 * @author wangf
 * @date 2025/10/28 上午 10:20
 */
public class IntAesEncoder {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "voicecomm1234567".getBytes(); // 16 bytes for AES-128

    public static String encrypt(int id) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
        byte[] encrypted = cipher.doFinal(toBytes(id));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
    }

    public static int decrypt(String encoded) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, ALGORITHM));
        byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(encoded));
        return fromBytes(decrypted);
    }

    private static byte[] toBytes(int value) {
        return new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    private static int fromBytes(byte[] bytes) {
        return (bytes[0] << 24) | ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
}