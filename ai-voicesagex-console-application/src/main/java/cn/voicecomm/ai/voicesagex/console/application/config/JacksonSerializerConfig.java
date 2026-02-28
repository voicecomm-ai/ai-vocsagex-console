package cn.voicecomm.ai.voicesagex.console.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.text.SimpleDateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson全局配置：注册自定义序列化器
 *
 * @author: gaox
 * @date: 2026/1/28 14:05
 */
@Configuration
public class JacksonSerializerConfig {

  /**
   * 覆盖Spring默认的ObjectMapper，添加JSONNull序列化器
   */
  @Bean
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    // 1. 创建Jackson模块，用于注册自定义序列化/反序列化器
    SimpleModule hutoolJsonModule = new SimpleModule();

    // 2. 注册JSONNull序列化器（核心步骤）
    hutoolJsonModule.addSerializer(new HutoolJSONNullSerializer());

    // 3. 构建ObjectMapper并注册模块
    ObjectMapper objectMapper = builder.build();
    objectMapper.registerModule(hutoolJsonModule);

    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    return objectMapper;
  }
}
