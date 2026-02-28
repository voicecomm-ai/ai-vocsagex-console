package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatTitleUpdateDto implements Serializable {


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


  /**
   * 对话标题(仅更新标题传递)
   */
  private String conversationTitle;



}
