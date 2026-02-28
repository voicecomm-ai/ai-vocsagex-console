package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.VariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.VariableOptionRespDto;
import java.util.List;

/**
 * VariableService
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface VariableService {

  CommonRespDto<VariableDto> add(VariableDto dto);

  CommonRespDto<Void> update(VariableDto dto);

  CommonRespDto<VariableDto> getInfo(Integer id);

  CommonRespDto<Void> delete(Integer id);

  CommonRespDto<List<VariableOptionRespDto>> variableOptions(Integer id);


}
