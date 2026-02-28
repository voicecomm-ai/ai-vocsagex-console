package cn.voicecomm.ai.voicesagex.console.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gml
 * @date 2024/7/9 14:49
 */
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
