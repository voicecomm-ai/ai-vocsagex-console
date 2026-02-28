package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * agent url聊天列表返回参数
 *
 * @author wangfan
 * @date 2026/1/6 下午 4:55
 */
@Data
@Accessors(chain = true)
public class AgentUrlChatListRespDto implements Serializable {


  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 日期
   */
  private String date;

  /**
   * 当天的agent对话列表
   */
  private List<AgentChatListRespDto> agentChatList;

}