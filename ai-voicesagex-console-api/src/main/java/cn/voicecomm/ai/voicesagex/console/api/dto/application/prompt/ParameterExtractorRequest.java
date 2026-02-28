package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.hutool.json.JSONObject;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 参数提取请求
 *
 * @author wangf
 * @date 2025/9/8 下午 2:36
 */
@Data
@Accessors(chain = true)
public class ParameterExtractorRequest implements Serializable {

  /**
   * 提取参数的JSON schema
   */
  private JSONObject args_schema;

  /**
   * 提示词
   */
  private String instruction;

  /**
   * 用户输入
   */
  private String query;

  /**
   * 用户输入中待嵌入的参数，没有就不填
   */
  private JSONObject query_arguments;

  /**
   * 是否视觉
   */
  private boolean is_vision = false;
  /**
   * 文本生成模型 配置信息
   */
  private JSONObject model_instance_config;
  /**
   * 文本生成模型 加载方式
   */
  private String model_instance_provider;
  /**
   * 文本生成模型 运行参数
   */
  private JSONObject model_parameters;


}



