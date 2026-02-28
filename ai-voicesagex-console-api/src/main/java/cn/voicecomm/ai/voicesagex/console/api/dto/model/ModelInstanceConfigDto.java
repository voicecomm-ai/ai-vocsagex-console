package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
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
public class ModelInstanceConfigDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -4175228615408381723L;
  /**
   * 模型名称
   */
  @JsonProperty("model_name")
  private String modelName;
  /**
   * 调用地址
   */
  @JsonProperty("base_url")
  private String baseUrl;
  /**
   * apikey
   */
  @JsonProperty("apikey")
  private String apiKey;
  /**
   * ollama类型
   */
  @JsonProperty("llm_type")
  private String llmType;
  /**
   * 上下文长度
   */
  @JsonProperty("context_length")
  private Integer contextLength;
  /**
   * 最长token
   */
  @JsonProperty("max_token_length")
  private Integer maxTokenLength;
  /**
   * 是否支持视觉
   */
  @JsonProperty("is_support_vision")
  private Boolean isSupportVision;
  /**
   * 是否支持函数
   */
  @JsonProperty("is_support_function")
  private Boolean isSupportFunction;

}
