package cn.voicecomm.ai.voicesagex.console.application.vo.chat;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class UrlChatApiReqVo implements Serializable {


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
   * 对话token
   */
  @NotEmpty(message = "对话token不能为空")
  private String token;

  /**
   * urlKey
   */
  @NotEmpty(message = "urlKey不能为空")
  private String urlKey;



  /**
   * url对话id（每个对话的第一次不传，第一次对话结束后，将返回的urlchatId加到后续轮次对话的这个参数上）
   */
  private Integer urlChatId;





}
