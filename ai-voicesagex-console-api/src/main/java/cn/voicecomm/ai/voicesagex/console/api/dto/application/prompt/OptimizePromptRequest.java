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
public class OptimizePromptRequest implements Serializable {

  /**
   * 提示词（必填）
   */
  private String prompt;

  /**
   * 用户指令（选填）
   */
  private String instruction;

  /**
   * 是否流式（选填）
   */
  private Boolean stream = false;

  /**
   * 模型供应商（必填）
   */
  private String model_instance_provider = "ollama";

  /**
   * 模型配置信息（必填）
   */
  private JSONObject model_instance_config;

}
