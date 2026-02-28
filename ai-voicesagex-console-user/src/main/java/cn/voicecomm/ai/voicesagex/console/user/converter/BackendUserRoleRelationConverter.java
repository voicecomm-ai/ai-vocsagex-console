package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserRoleRelationDto;
import cn.voicecomm.ai.voicesagex.console.util.po.user.UserRoleRelationPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendUserRoleRelationConverter {

  List<BackendUserRoleRelationDto> poListToDtoList(List<UserRoleRelationPo> poList);

}
