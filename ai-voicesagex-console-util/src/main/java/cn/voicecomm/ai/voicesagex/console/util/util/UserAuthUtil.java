package cn.voicecomm.ai.voicesagex.console.util.util;

import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * 用户信息工具类
 *
 * @author wangfan
 * @date 2023/05/16
 */
@Slf4j
public final class UserAuthUtil {

  private static Object getAttachmentByJwt(String key) {

    // ** 添加我们需要放置的用户信息
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // 不为空
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        return claims.get(key);
      }
    }
    return null;
  }


  private static Object getAttachment(String key) {
    return Optional.ofNullable(RpcContext.getServiceContext().getObjectAttachments())
        .map(map -> map.get(key))
        .orElse(null);
  }


  public static void setAttachment(String key, Object value) {
    RpcContext.getServiceContext().setObjectAttachment(key, value);
  }

  public static void setAttachmentUserId(Integer userId) {
    setAttachment("user_id", userId);
  }

  /**
   * 获取当前用户的id
   *
   * @return
   */
  public static Integer getUserId() {
    try {
      Object userId = getAttachment("user_id");
      if (userId == null) {
        userId = getAttachmentByJwt("user_id");
      }
      if (userId == null) {
        return 1;
      }
      return Integer.valueOf(userId.toString());
    } catch (Exception e) {
      return 1;
    }
  }

  public static String getUserLoginType() {
    Object deviceType = getAttachment("deviceType");
    if (deviceType == null) {
      deviceType = getAttachmentByJwt("deviceType");
    }
    if (deviceType == null) {
      return "pc";
    }
    return deviceType.toString();
  }

  /**
   * 获取数据权限
   *
   * @return
   */
  public static Integer getDataPermissionType() {
    Object dataPermission = getAttachment("data_permission");
    if (dataPermission == null) {
      dataPermission = getAttachmentByJwt("data_permission");
    }
    return Integer.valueOf(dataPermission.toString());
  }


  /**
   * 获取用户名
   *
   * @return
   */
  public static String getUserName() {
//    return "admin";
    return (String) getAttachment("user_name");
  }

  /**
   * 获取ip
   *
   * @return
   */
  public static String getUserIp() {
    return "127.0.0.1";
//    return (String) getAttachment("user_ip");
  }

  /**
   * 获取公网ip
   *
   * @param
   * @return
   */
  public static String getPublicIp() {
    return "";
//    return (String) getAttachment("public_ip");
  }

  /**
   * 获取内网ip
   *
   * @param
   * @return
   */
  public static String getIntranetIp() {
    return "127.0.0.1";
//    return (String) getAttachment("intranet_ip");
  }
}
