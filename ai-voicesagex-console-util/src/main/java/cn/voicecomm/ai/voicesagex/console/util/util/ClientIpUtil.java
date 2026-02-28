package cn.voicecomm.ai.voicesagex.console.util.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取ip工具类
 *
 * @author ryc
 * @date 2022-10-21 17:48
 */
@Slf4j
public class ClientIpUtil {

  /**
   * 获取ip
   *
   * @param request req
   * @return ip
   */
  public static String getClientIp(HttpServletRequest request) {
    // 获取客户端ip地址
    String clientIp = request.getHeader("x-forwarded-for");

    if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getHeader("WL-Proxy-Client-IP");
    }
    if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
      clientIp = request.getRemoteAddr();
    }
    return clientIp;
  }

  /**
   * 获取真实IP地址
   *
   * <p>使用getRealIP代替该方法
   *
   * @param request req
   * @return ip
   */
  public static String getClientPublicIp(HttpServletRequest request) {
    // 获取客户端ip地址
    String clientIp = getClientIp(request);

    /*
     * 对于获取到多ip的情况下，找到公网ip.
     */
    // 公网
    String pIP = null;
    // 内网
    String sIP = null;
    if (clientIp != null && !clientIp.contains("unknown")) {
      // 大于0 说明找到多个地址
      if (clientIp.indexOf(",") > 0) {
        String[] ipsz = clientIp.split(",");
        List<String> ipList = Arrays.asList(ipsz);
        // 内网ipList
        List<String> sIpList =
            ipList.stream().filter(a -> isInnerIp(a.trim())).collect(Collectors.toList());
        // 公网ipList
        List<String> pIpList =
            ipList.stream().filter(a -> !isInnerIp(a.trim())).collect(Collectors.toList());

        clientIp = "";

        // 拼接第一个公网ip
        if (pIpList != null && pIpList.size() > 0) {
          pIP = pIpList.get(0);
          clientIp = "(公网)" + pIP;
        }
        // 拼接第一个内网ip
        if (sIpList != null && sIpList.size() > 0) {
          sIP = sIpList.get(0);
          if (StrUtil.isNotBlank(clientIp)) {
            clientIp = clientIp + "," + "(内网)" + sIP;
          } else {
            clientIp = "(内网)" + sIP;
          }
        }
      } else {
        // 只有单个地址
        // 判断是公网还是内网
        clientIp = isInnerIp(clientIp) ? "(内网)" + clientIp : "(公网)" + clientIp;
      }
    }

    if (clientIp != null && clientIp.contains("unknown")) {
      clientIp = clientIp.replaceAll("unknown,", "");
      clientIp = clientIp.trim();
    }

    if ("".equals(clientIp) || null == clientIp) {
      clientIp = "(内网)" + "127.0.0.1";
    }
    return clientIp;
  }

  /**
   * 获取真实IP地址
   *
   * <p>使用getRealIP代替该方法
   *
   * @param request req
   * @return ip
   */
  public static void getClientPublicIp(HttpServletRequest request, Map<String, Object> map) {
    // 获取客户端ip地址
    String clientIp = getClientIp(request);
    /*
     * 对于获取到多ip的情况下，找到公网ip.
     */
    // 公网
    String pIP = null;
    // 内网
    String sIP = null;

    if (clientIp != null && !clientIp.contains("unknown")) {

      clientIp = clientIp.replaceAll("unknown,", "");
      clientIp = clientIp.trim();

      String[] ipsz = clientIp.split(",");
      List<String> ipList = Arrays.asList(ipsz);
      // 内网ipList
      List<String> sIpList =
          ipList.stream().filter(a -> isInnerIp(a.trim())).collect(Collectors.toList());
      // 公网ipList
      List<String> pIpList =
          ipList.stream().filter(a -> !isInnerIp(a.trim())).collect(Collectors.toList());

      // 拼接第一个公网ip
      if (pIpList != null && pIpList.size() > 0) {
        pIP = pIpList.get(0);
        map.put("public_ip", pIP);
      }
      // 拼接第一个内网ip
      if (sIpList != null && sIpList.size() > 0) {
        sIP = sIpList.get(0);
        map.put("intranet_ip", sIP);
      }
    }
    if (clientIp != null && clientIp.contains("unknown")) {
      clientIp = clientIp.replaceAll("unknown,", "");
      clientIp = clientIp.trim();
    }
    if ("".equals(clientIp) || null == clientIp) {
      map.put("intranet_ip", "127.0.0.1");
    }
  }

  /**
   * 判断IP是否是内网地址
   *
   * @param ipAddress ip地址
   * @return 是否是内网地址
   */
  private static boolean isInnerIp(String ipAddress) {
    boolean isInnerIp;
    long ipNum = getIpNum(ipAddress);
    /**
     * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类 192.168.0.0-192.168.255.255
     * 当然，还有127这个网段是环回地址
     */
    long aBegin = getIpNum("10.0.0.0");
    long aEnd = getIpNum("10.255.255.255");

    long bBegin = getIpNum("172.16.0.0");
    long bEnd = getIpNum("172.31.255.255");

    long cBegin = getIpNum("192.168.0.0");
    long cEnd = getIpNum("192.168.255.255");
    isInnerIp =
        isInner(ipNum, aBegin, aEnd)
            || isInner(ipNum, bBegin, bEnd)
            || isInner(ipNum, cBegin, cEnd)
            || "127.0.0.1".equals(ipAddress);
    return isInnerIp;
  }

  private static long getIpNum(String ipAddress) {
    String[] ip = ipAddress.split("\\.");
    long a = Integer.parseInt(ip[0]);
    long b = Integer.parseInt(ip[1]);
    long c = Integer.parseInt(ip[2]);
    long d = Integer.parseInt(ip[3]);

    return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
  }

  private static boolean isInner(long userIp, long begin, long end) {
    return (userIp >= begin) && (userIp <= end);
  }
}
