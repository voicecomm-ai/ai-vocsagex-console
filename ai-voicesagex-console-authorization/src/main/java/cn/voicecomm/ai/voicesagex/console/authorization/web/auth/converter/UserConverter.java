package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserConverter {

  SysUserDetails userDtoToSysUserDetails(BackendUserDto dto);
}
