package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Token生成校验工具
 *
 * @author: zhaot
 * @date: 2020/12/1 10:01
 */
@Slf4j
public class TokenHelp {

  /**
   * 创建秘钥
   */
  private static final byte[] SECRET = "6MNSobBRCHGIO0fS6MNSobBRCHGIO0fS".getBytes();

  private static final String PREFIX = "sk-";

  public static Result<String> tokenValidate(String token) {
    try {
      SignedJWT jwt = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(SECRET);
      // 校验是否有效
      if (!jwt.verify(verifier)) {
        log.error("校验失效");
        return Result.error("验证失败");
      }

      // 获取载体中的数据
      Object userId = jwt.getJWTClaimsSet().getClaim("userId");
      // 是否有openUid
      if (Objects.isNull(userId)) {
        log.error("userId为空");
        return Result.error("验证失败");
      }
      return Result.success(userId.toString());
    } catch (ParseException e) {
      log.error("", e);
    } catch (JOSEException e) {
      log.error("", e);
    }
    return Result.error("验证失败");
  }

  public static String getToken(Integer userId) {
    try {
      /** 1.创建一个32-byte的密匙 */
      MACSigner macSigner = new MACSigner(SECRET);
      /** 2. 建立payload 载体 */
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject("voicecomm")
          .jwtID(String.valueOf(UUID.randomUUID())).claim("userId", userId).build();

      /** 3. 建立签名 */
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(macSigner);

      /** 4. 生成token */
      String token = signedJWT.serialize();
      return token;
    } catch (KeyLengthException e) {
      log.error("", e);
    } catch (JOSEException e) {
      log.error("", e);
    }
    return null;
  }

  public static String generateToken() {
    // 16 bytes = 128 bits
    byte[] randomBytes = new byte[16];
    new SecureRandom().nextBytes(randomBytes);

    // 转成32位16进制字符串
    StringBuilder sb = new StringBuilder(PREFIX);
    for (byte b : randomBytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
