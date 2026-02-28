//package cn.voicecomm.ai.voicesagex.console.user.config;
//
//import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//@Data
//@RefreshScope
//public class XxlJobConfig {
//
//  @Value("${xxl.job.admin.addresses}")
//  private String adminAddresses;
//
//  @Value("${xxl.job.accessToken}")
//  private String accessToken;
//
//  @Value("${xxl.job.executor.appname}")
//  private String appname;
//
//  @Value("${xxl.job.executor.address}")
//  private String address;
//
//  @Value("${xxl.job.executor.ip}")
//  private String ip;
//
//  @Value("${xxl.job.executor.port}")
//  private int port;
//
//  @Value("${xxl.job.executor.logpath}")
//  private String logPath;
//
//  @Value("${xxl.job.executor.logretentiondays}")
//  private int logRetentionDays;
//
//  @Bean
//  public XxlJobSpringExecutor xxlJobExecutor() {
//    log.info(">>>>>>>>>>> xxl-job 初始化 init.");
//    XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
//    xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
//    xxlJobSpringExecutor.setAppname(appname);
//    xxlJobSpringExecutor.setAddress(address);
//    xxlJobSpringExecutor.setIp(ip);
//    xxlJobSpringExecutor.setPort(port);
//    xxlJobSpringExecutor.setAccessToken(accessToken);
//    xxlJobSpringExecutor.setLogPath(logPath);
//    xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
//
//    return xxlJobSpringExecutor;
//  }
//}
