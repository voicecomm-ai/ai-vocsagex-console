package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleMenuRelationDto;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RoleMenuRelationPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendRoleMenuRelationConverter {

  List<BackendRoleMenuRelationDto> poListToDtoList(List<RoleMenuRelationPo> poList);
}
