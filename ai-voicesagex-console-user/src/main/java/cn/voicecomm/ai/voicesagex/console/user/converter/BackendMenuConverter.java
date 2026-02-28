package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendMenuDto;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendMenuVo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.MenuPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendMenuConverter {

  List<BackendMenuDto> poListToDtoList(List<MenuPo> poList);

  List<BackendMenuVo> dtoListToVoList(List<BackendMenuDto> list);

}
