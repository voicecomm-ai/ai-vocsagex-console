package cn.voicecomm.ai.voicesagex.console.gateway;

import cn.voicecomm.ai.voicesagex.console.util.config.ControllerAdviceAutoConfiguration;
import cn.voicecomm.ai.voicesagex.console.util.config.MyBatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class, MyBatisAutoConfiguration.class,
    ControllerAdviceAutoConfiguration.class})
@EnableDiscoveryClient
public class VoicesagexConsoleGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(VoicesagexConsoleGatewayApplication.class, args);
  }

}
