package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.VariableDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.VariablePo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * VariableConverter
 *
 * @author wangfan
 * @date 2025/5/21 下午 4:43
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface VariableConverter {

  VariableDto poToDto(VariablePo po);

  VariablePo dtoToPo(VariableDto po);

  List<VariablePo> dtoToPoList(List<VariableDto> obj);

  List<VariableDto> poToDtoList(List<VariablePo> obj);

}
