package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelInvokeParamDto extends ModelInvokeBaseDto{

  @Serial
  private static final long serialVersionUID = 3677285458215162507L;

  /**
   * 模型类型
   */
  @JsonProperty("model_instance_type")
  private String modelInstanceType;

  /**
   * 模型加载方式
   */
  @JsonProperty("model_instance_provider")
  private String modelInstanceProvider;

  /**
   * 模型配置信息
   */
  @JsonProperty("model_instance_config")
  private ModelInstanceConfigDto modelInstanceConfig;

}
