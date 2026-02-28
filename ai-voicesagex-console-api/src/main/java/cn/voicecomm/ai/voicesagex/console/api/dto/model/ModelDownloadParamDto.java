package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
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
public class ModelDownloadParamDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 7370849167888946898L;

  /**
   * 模型名称
   */
  @JsonProperty("model_name")
  private String modelName;

  /**
   * 模型名称
   */
  @JsonProperty("model_type")
  private String modelType;

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

  /**
   * 代码路径
   */
  @JsonProperty("source_dir")
  private String sourceDir;

  /**
   * 权重路径
   */
  @JsonProperty("weight_dir")
  private String weightDir;

  /**
   * 量化模型路径
   */
  @JsonProperty("model_dir")
  private String modelDir;

  /**
   * 启动配置信息
   */
  @JsonProperty("config_content")
  private String configContent;

  /**
   * cpu架构，x86_64 或 arm64
   */
  @JsonProperty("cpu_arch")
  @NotEmpty(message = "cpu架构不能为空")
  private String cpuArch;

  /**
   * gpu架构，nvidia 或 amd 或 ascend
   */
  @JsonProperty("gpu_arch")
  private String gpuArch;

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

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @SuperBuilder
  public static class ArchDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 4943499745869598438L;

    /**
     * cpu架构，x86_64 或 arm64
     */
    @JsonProperty("cpu_arch")
    @NotEmpty(message = "cpu架构不能为空")
    private String cpuArch;

    /**
     * 是否启用gpu，若未启用，下述字段可不填，可为null
     */
    @JsonProperty("use_gpu")
    private Boolean useGpu;

    /**
     * gpu架构，nvidia 或 amd 或 ascend
     */
    @JsonProperty("gpu_arch")
    private String gpuArch;
  }

}
