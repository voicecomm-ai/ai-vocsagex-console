package cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.LLMRequest.ChatHistory;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用于存储模型实例配置和其他相关信息的Java实体类。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LLMResponse extends BasePromptResponse implements Serializable {

  /**
   * 包含实际数据的对象。
   */
  private LLMData data;

  /**
   * 内部类，用于存储具体的响应数据。
   */
  @Data
  public static class LLMData implements Serializable {

    /**
     * 消息内容。
     */
    private String assistant_message;

    /**
     * 聊天历史记录列表。
     */
    private List<ChatHistory> chat_history;
  }
}



