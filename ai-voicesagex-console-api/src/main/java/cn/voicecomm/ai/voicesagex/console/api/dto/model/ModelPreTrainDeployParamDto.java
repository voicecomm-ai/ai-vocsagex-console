package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型下载Dto
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelPreTrainDeployParamDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -7664388420666244545L;

  /**
   * 模型名称
   */
  @JsonProperty("model_name")
  private String modelName;

  /**
   * 模型加载方式
   */
  @JsonProperty("model_instance_provider")
  private String modelInstanceProvider;

  /**
   * 代码路径，没有可不填
   */
  @JsonProperty("source_dir")
  private String sourceDir;

  /**
   * 权重路径，没有可不填
   */
  @JsonProperty("weight_dir")
  private String weightDir;

  /**
   * 模型路径，没有可不填
   */
  @JsonProperty("model_dir")
  private String modelDir;

  /**
   * 部署的配置文件内容，没有可不填
   */
  @JsonProperty("config_content")
  private String configContent;

  /**
   * CPU核数，单位: 核
   */
  @JsonProperty("cpu_core")
  private Integer cpuCore;

  /**
   * 内存大小，单位: MB
   */
  @JsonProperty("memory_usage")
  private Integer memoryUsage;

  /**
   * 是否使用GPU，默认false
   */
  @JsonProperty("gpu")
  private Boolean gpu;

  /**
   * GPU数量，单位: 块，不使用gpu可不填
   */
  @JsonProperty("gpu_num")
  private Integer gpuNum;

  /**
   * 回调url
   */
  @JsonProperty("callback_url")
  private String callbackUrl;

  /**
   * 回调消息体
   */
  @JsonProperty("callback_body")
  private ModelParamDto callbackBody;


}
