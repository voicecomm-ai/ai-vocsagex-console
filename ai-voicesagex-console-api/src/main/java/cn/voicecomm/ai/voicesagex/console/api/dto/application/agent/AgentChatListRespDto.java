package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentChatListRespDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * id
   */
  private Integer id;


  /**
   * 对话标题
   */
  private String conversationTitle;

}
