package cn.voicecomm.ai.voicesagex.console.application;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("cn.voicecomm.ai.voicesagex.console.application.dao.mapper")
public class VoicesagexConsoleApplication {

  public static void main(String[] args) {
    SpringApplication.run(VoicesagexConsoleApplication.class, args);
  }

}
