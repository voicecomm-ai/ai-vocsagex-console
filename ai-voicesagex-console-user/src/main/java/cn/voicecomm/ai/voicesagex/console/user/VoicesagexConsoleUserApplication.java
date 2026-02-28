package cn.voicecomm.ai.voicesagex.console.user;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("cn.voicecomm.ai.voicesagex.console.user.dao.mapper")
public class VoicesagexConsoleUserApplication {

  public static void main(String[] args) {
    SpringApplication.run(VoicesagexConsoleUserApplication.class, args);
  }
}
