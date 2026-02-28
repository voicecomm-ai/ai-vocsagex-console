package cn.voicecomm.ai.voicesagex.console.application.config;

import cn.hutool.json.JSONNull;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * 自定义序列化器：将Hutool的JSONNull转为JSON原生null 解决Jackson无法序列化JSONNull的问题
 *
 * @author: gaox
 * @date: 2026/1/28 14:04
 */
public class HutoolJSONNullSerializer extends JsonSerializer<JSONNull> {

  /**
   * 核心逻辑：把JSONNull序列化为JSON的null值
   */
  @Override
  public void serialize(JSONNull jsonNull, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {
    // 直接写入JSON原生null，前端拿到的就是正常的null
    jsonGenerator.writeNull();
  }

  /**
   * 指定序列化器适用的类型（避免类型匹配问题）
   */
  @Override
  public Class<JSONNull> handledType() {
    return JSONNull.class;
  }
}
