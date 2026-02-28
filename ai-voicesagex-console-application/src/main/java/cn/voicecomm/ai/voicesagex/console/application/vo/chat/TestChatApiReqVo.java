package cn.voicecomm.ai.voicesagex.console.application.vo.chat;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Api测试对话请求参数
 *
 * @author wangfan
 * @date 2026/1/7 上午 10:53
 */
@Data
public class TestChatApiReqVo implements Serializable {


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


}
