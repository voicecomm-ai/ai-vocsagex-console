package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentInfoResponseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentPublishHistoryDto;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatApiReqVo;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.TestChatReqVo;
import cn.voicecomm.ai.voicesagex.console.application.vo.chat.UrlChatApiReqVo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentInfoPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentPublishHistoryPo;
import cn.voicecomm.ai.voicesagex.console.util.util.JacksonUtil;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * AgentInfoConverter
 *
 * @author wangfan
 * @date 2025/5/21 下午 4:43
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AgentInfoConverter {

  AgentInfoDto poToDto(AgentInfoPo po);

  AgentInfoPo dtoToPo(AgentInfoDto po);

  List<AgentInfoPo> dtoToPoList(List<AgentInfoDto> obj);

  List<AgentInfoDto> poToDtoList(List<AgentInfoPo> obj);

  AgentInfoResponseDto poToInfoDto(AgentInfoPo po);

  AgentInfoPo dtoToInfoPo(AgentInfoResponseDto po);

  List<AgentPublishHistoryDto> publishDataPoToDtoList(List<AgentPublishHistoryPo> obj);

  TestChatReqVo chatApiVoToChatVo(TestChatApiReqVo vo);

  TestChatReqVo chatApiVoToChatVo(UrlChatApiReqVo vo);

  default AgentPublishHistoryDto publishDataPoToDto(AgentPublishHistoryPo agentPublishHistoryPo) {
    if (agentPublishHistoryPo == null) {
      return null;
    }

    AgentPublishHistoryDto agentPublishHistoryDto = new AgentPublishHistoryDto();

    agentPublishHistoryDto.setId(agentPublishHistoryPo.getId());
    agentPublishHistoryDto.setAgentId(agentPublishHistoryPo.getAgentId());
    agentPublishHistoryDto.setApplicationId(agentPublishHistoryPo.getApplicationId());
    agentPublishHistoryDto.setCreateTime(agentPublishHistoryPo.getCreateTime());
    agentPublishHistoryDto.setVersion(agentPublishHistoryPo.getVersion());
    agentPublishHistoryDto.setConfigData(agentPublishHistoryPo.getConfigData());
    agentPublishHistoryDto.setAgentInfoResponseDto(
        JacksonUtil.toBean(agentPublishHistoryPo.getConfigData(), AgentInfoResponseDto.class));
    return agentPublishHistoryDto;
  }


}
