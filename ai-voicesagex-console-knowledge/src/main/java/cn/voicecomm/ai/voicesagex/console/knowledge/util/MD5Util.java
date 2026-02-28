package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    // 生成 MD5 哈希值
    public static String generateMD5(String input) {
        try {
            // 获取 MD5 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 计算哈希值
            byte[] digest = md.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                // 转换每个字节为两位的十六进制数
                String hex = Integer.toHexString(0xff & b);
                // 如果十六进制数不足两位，则在前面补零
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 捕获异常并打印堆栈跟踪
            e.printStackTrace();
            return null;
        }
    }

}
