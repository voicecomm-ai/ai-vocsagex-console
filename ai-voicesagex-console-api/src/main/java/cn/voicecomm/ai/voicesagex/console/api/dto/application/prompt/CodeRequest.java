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
public class CodeRequest implements Serializable {

  /**
   * 用户指令
   */
  private String instruction;
  /**
   * 代码语言，python3，javascript
   */
  private String language;

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

  /**
   * 是否流式，默认false，开启流式时，响应将为Event-Stream
   */
  private Boolean stream = false;

}
