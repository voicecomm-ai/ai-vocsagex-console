package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentChatHistoryDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentChatHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * AgentChatHistoryConverter
 *
 * @author wangfan
 * @date 2025/5/21 下午 4:43
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AgentChatHistoryConverter {

  default AgentChatHistoryDto poToDto(AgentChatHistoryPo po) {
    if ( po == null ) {
      return null;
    }

    AgentChatHistoryDto agentChatHistoryDto = new AgentChatHistoryDto();

    agentChatHistoryDto.setId( po.getId() );
    agentChatHistoryDto.setAppId( po.getAppId() );
    agentChatHistoryDto.setUrlKey( po.getUrlKey() );
    agentChatHistoryDto.setConversationTitle( po.getConversationTitle() );
    agentChatHistoryDto.setConversationToken( po.getConversationToken() );
    agentChatHistoryDto.setChatHistory(JacksonUtil.toList(po.getChatHistory(), ObjectNode.class));
    agentChatHistoryDto.setCreateTime( po.getCreateTime() );
    agentChatHistoryDto.setUpdateTime( po.getUpdateTime() );
    agentChatHistoryDto.setAgentId( po.getAgentId() );

    return agentChatHistoryDto;
  }

}
