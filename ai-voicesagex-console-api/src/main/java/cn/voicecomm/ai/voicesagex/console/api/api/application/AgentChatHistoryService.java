package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentChatHistoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentUrlChatListRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.ChatHistorySaveDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.InitChatReqDto;
import java.util.List;

/**
 * ApplicationService
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface AgentChatHistoryService {


  CommonRespDto<Integer> initChat(InitChatReqDto dto);


  void updateChatHistory(ChatHistorySaveDto dto);


  CommonRespDto<Void> updateTitle(Integer id, String title);


  CommonRespDto<List<AgentUrlChatListRespDto>> chatList(String token);


  CommonRespDto<AgentChatHistoryDto> chatInfo(Integer id);


  CommonRespDto<Void> deleteChat(Integer id);

}
