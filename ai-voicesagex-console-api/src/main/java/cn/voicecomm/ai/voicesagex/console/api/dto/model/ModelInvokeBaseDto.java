package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelInvokeBaseDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 3750359883673910816L;

  /**
   * 模型输入
   */
  @JsonProperty("model_inputs")
  private Map<String, Object> modelInputs;

  /**
   * 模型调用参数
   */
  @JsonProperty("model_parameters")
  private Map<String, Object> modelParameters;

  /**
   * 是否使用流式，仅CHAT模型支持
   */
  @JsonProperty("stream")
  private boolean stream;
}
