package cn.voicecomm.ai.voicesagex.console.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class GlusterProperties {

  private String baseDir;

  private String export;

  private String upload;

  private String logExport;
}
