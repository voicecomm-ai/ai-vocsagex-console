package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.hutool.json.JSONObject;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用于存储模型实例配置和其他相关信息的Java实体类。
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMRequest implements Serializable {

  /**
   * 文本生成模型 加载方式
   */
  private String model_instance_provider = "ollama";

  /**
   * 文本生成模型 运行参数
   */
  private JSONObject model_parameters;


  /**
   * 文本生成模型 配置信息
   */
  private JSONObject model_instance_config;

  /**
   * 是否流式传输。
   */
  private Boolean stream = false;

  /**
   * 输入消息中的参数，没有可填null，也可不传。
   */
  private List<PromptMessage> prompt_messages;

  /**
   * 输入参数。
   */
  private JSONObject input_arguments;

  /**
   * 是否包含历史记录。
   */
  private Boolean is_history = true;

  /**
   * 聊天历史，转发接口响应中的内容即可，不开启可不传
   */
  private List<ChatHistory> chat_history;

  /**
   * 记忆窗口，不开启可不传
   */
  private Integer chat_history_depth;

  /**
   * 是否启用视觉
   */
  private Boolean is_vision;

  /**
   * 视觉文件内容列表，不开启可不传
   */
  private List<String> vision_images;

  /**
   * 视觉分辨率，仅可输入“low”、”high“
   */
  private String vision_resolution;

  /**
   * 是否结构化输出
   */
  private Boolean is_structured_output;

  /**
   * 结构化输出的JSON schema，不开启可不传
   */
  private JSONObject structured_output_schema;


  /**
   * 内部类，用于存储提示消息。
   */
  @Data
  @Accessors(chain = true)
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PromptMessage implements Serializable {

    /**
     * 类型。
     */
    private String type;

    /**
     * 内容。
     */
    private String content;
  }


  /**
   * 内部类，用于存储聊天历史记录。
   */
  @Data
  @Accessors(chain = true)
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ChatHistory implements Serializable {

    /**
     * 类型。
     */
    private String type;

    /**
     * 内容。
     */
    private String content;

    /**
     * 名称。
     */
    private String name;
  }

}



