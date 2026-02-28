package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UserPageReqDto;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendUserReqVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.UserPageReqVo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.SysUserPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendUserConverter {


  BackendUserDto poToDto(SysUserPo po);

  SysUserPo dtoToPo(BackendUserDto dto);

  UserPageReqDto voToDto(UserPageReqVo vo);

  BackendUserDto voToDto(BackendUserReqVo vo);

  PagingRespDto<BackendUserDto> pagePoToDto(Page<SysUserPo> page);

  List<BackendUserDto> poToDtoList(List<SysUserPo> poList);

}
