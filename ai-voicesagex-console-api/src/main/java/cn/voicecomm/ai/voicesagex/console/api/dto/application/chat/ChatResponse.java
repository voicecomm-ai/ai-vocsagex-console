package cn.voicecomm.ai.voicesagex.console.api.dto.application.chat;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.prompt.BasePromptResponse.UsageInfo;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse implements Serializable {

  /**
   * 状态码（1000=成功, 2000=失败）
   */
  private int code;

  /**
   * 状态信息（成功时为唯一标识符，失败时为错误描述）
   */
  private String msg;


  /**
   * 是否结束
   */
  private Boolean done;

  /**
   * 对话数据
   */
  private ChatData data;


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ChatData implements Serializable {

    /**
     * 回复
     */
    private String assistant_message;

    /**
     * 对话历史
     */
    private List<JSONObject> chat_history;

    /**
     * Token用量统计（失败时为null）
     */
    private UsageInfo usage;

    /**
     * 额外响应消息(智能体调用过程)
     */
    private AdditionalKwargs additional_kwargs;

  }

  /**
   * Token用量统计详情
   */
  @Data
  @Accessors(chain = true)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AdditionalKwargs implements Serializable {

    /**
     * 子智能体名称
     */
    private String agent_name;

    /**
     * 子智能体消息内容
     */
    private String agent_content;

    /**
     * 子智能体处理状态 枚举值: STREAM 流式消息 FINISHED 结束 FAILED 失败
     */
    private String agent_status;


    /**
     * 思考过程结束
     */
    private Boolean is_subtask_done;


    /**
     * 子任务名称
     */
    private String task_name;
  }

}
