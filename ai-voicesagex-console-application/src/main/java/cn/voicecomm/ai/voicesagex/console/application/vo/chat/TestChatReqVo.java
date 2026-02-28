package cn.voicecomm.ai.voicesagex.console.application.vo.chat;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestChatReqVo implements Serializable {

  /**
   * 应用ID
   */
  @NotNull(message = "应用ID不能为空")
  private Integer applicationId;


  /**
   * 应用运行类型    草稿draft,已发布published,上架体验experience
   *
   * @see cn.voicecomm.ai.voicesagex.console.api.enums.application.ApplicationStatusEnum
   */
  private String runType;

  /**
   * 对话内容
   */
  @NotEmpty(message = "对话内容不能为空")
  private String query;

  /**
   * 聊天记录
   */
  private List<JSONObject> chatHistory;

  /**
   * 变量
   */
  private JSONObject inputs;

  /**
   * 对话ID
   */
  private String conversationId;

  /**
   * urlKey
   */
  private String urlKey;

  /**
   * 对话token
   */
  private String token;


  /**
   * url对话id
   */
  private Integer urlChatId;


  /**
   * 是否为同步(前端使用不传 或者 传false)
   */
  private Boolean isSync;
}
