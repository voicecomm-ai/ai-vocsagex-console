package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.agent.AgentVariableDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.AgentVariablePo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * AgentVariableConverter
 *
 * @author wangfan
 * @date 2025/5/21 下午 4:43
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AgentVariableConverter {

  AgentVariableDto poToDto(AgentVariablePo po);

  AgentVariablePo dtoToPo(AgentVariableDto po);

  List<AgentVariablePo> dtoToPoList(List<AgentVariableDto> obj);

  List<AgentVariableDto> poToDtoList(List<AgentVariablePo> obj);

}
