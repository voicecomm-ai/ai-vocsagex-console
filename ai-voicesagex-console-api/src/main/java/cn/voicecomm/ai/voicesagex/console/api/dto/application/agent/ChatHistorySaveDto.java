package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import cn.hutool.json.JSONObject;
import jakarta.validation.constraints.NotEmpty;
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
public class ChatHistorySaveDto implements Serializable {

  /**
   * 聊天记录
   */
  @NotEmpty(message = "聊天记录不能为空")
  private List<JSONObject> chatHistory;


  /**
   * urlKey
   */
  @NotEmpty(message = "urlKey不能为空")
  private String urlKey;

  /**
   * 对话token
   */
  @NotEmpty(message = "对话token不能为空")
  private String token;


  /**
   * 对话id
   */
  private Integer id;



}
