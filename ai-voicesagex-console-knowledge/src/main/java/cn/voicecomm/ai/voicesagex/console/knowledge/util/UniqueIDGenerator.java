package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

public class UniqueIDGenerator {


    public static String generateUniqueID() {
        // 1. 获取当前时间戳（毫秒级，13位）
        long timestamp = Instant.now().toEpochMilli();

        // 2. 生成随机UUID并生成哈希值
        String randomUUID = UUID.randomUUID().toString();
        String hash = generateHash(randomUUID);

        // 3. 截取哈希值的前19位，加上时间戳，拼接成32位唯一ID
        String uniqueID = timestamp + hash.substring(0, 19);

        return uniqueID;
    }

    private static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

}
