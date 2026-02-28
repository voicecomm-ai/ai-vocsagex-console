package cn.voicecomm.ai.voicesagex.console.api.api.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.RegisteredUserDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.RegisteredUserPageReqDto;

public interface RegisteredUserService {

  CommonRespDto<PagingRespDto<RegisteredUserDto>> page(RegisteredUserPageReqDto dto);

  CommonRespDto<Void> forbidden(Integer userId);

  CommonRespDto<Void> enabled(Integer userId);

  CommonRespDto<Void> deleted(Integer userId);

  CommonRespDto<RegisteredUserDto> getInfo(Integer userId);

}
