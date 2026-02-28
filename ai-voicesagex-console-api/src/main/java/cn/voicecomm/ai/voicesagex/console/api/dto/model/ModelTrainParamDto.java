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
public class ModelTrainParamDto implements Serializable {

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
  @JsonProperty("dataset_dir")
  private String datasetDir;

  /**
   * 模型路径，没有可不填
   */
  @JsonProperty("out_log_dir")
  private String outLogDir;

  /**
   * 模型路径，没有可不填
   */
  @JsonProperty("out_weight_dir")
  private String outWeightDir;

  /**
   * 模型路径，没有可不填
   */
  @JsonProperty("out_model_dir")
  private String outModelDir;

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
   * 是否支持分布式训练
   */
  @JsonProperty("distributed")
  private Boolean distributed;

  /**
   * 分布式训练框架
   */
  @JsonProperty("model_framework")
  private String modelFramework;

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

  /**
   * 上传的权重路径
   */
  @JsonProperty("in_weight_dir")
  private String inWeightDir;

  /**
   * 上传的量化模型路径
   */
  @JsonProperty("in_model_dir")
  private String inModelDir;

}
