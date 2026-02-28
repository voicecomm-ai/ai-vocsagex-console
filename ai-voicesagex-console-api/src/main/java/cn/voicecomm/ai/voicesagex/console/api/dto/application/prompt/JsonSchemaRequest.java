package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.hutool.json.JSONObject;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class JsonSchemaRequest implements Serializable {

  /**
   * JSON Schema的自然语言描述（必填）
   */
  private String description;

  /**
   * CHAT模型调用参数
   */
  private JSONObject model_parameters;

  /**
   * 模型供应商（必填）
   */
  private String model_instance_provider = "ollama";

  /**
   * 模型配置信息（必填）
   */
  private JSONObject model_instance_config;

}
