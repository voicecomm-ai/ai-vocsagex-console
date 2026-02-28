package cn.voicecomm.ai.voicesagex.console.api.dto.application.agent;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 智能体长期记忆list返回体
 */
@Data
@Accessors(chain = true)
public class AgentLongTermMemoryListRespDto implements Serializable {


  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 日期
   */
  private String date;

  /**
   * 内容
   */
  private List<AgentLongTermMemoryDto> memoryList;

}