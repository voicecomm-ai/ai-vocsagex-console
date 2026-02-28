package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentLongTermMemoryDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentLongTermMemoryPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AgentLongTermMemoryConverter {

  AgentLongTermMemoryDto poToDto(AgentLongTermMemoryPo po);

  AgentLongTermMemoryPo dtoToPo(AgentLongTermMemoryDto dto);

  List<AgentLongTermMemoryPo> dtoToPoList(List<AgentLongTermMemoryDto> dtoList);

  List<AgentLongTermMemoryDto> poToDtoList(List<AgentLongTermMemoryPo> poList);
}
