package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendRolePageVo;
import cn.voicecomm.ai.voicesagex.console.user.vo.user.BackendRoleReqVo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RolePo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingReqVo;
import cn.voicecomm.ai.voicesagex.console.util.vo.PagingRespVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendRoleConverter {

  BackendRoleDto poToDto(RolePo po);

  List<BackendRoleDto> poListToDtoList(List<RolePo> poList);

  RolePo dtoToPo(BackendRoleDto dto);

  PagingReqDto pagingReqVoToDto(PagingReqVo vo);

  PagingRespDto<BackendRoleDto> pagingPoToDto(Page<RolePo> page);

  PagingRespVo<BackendRolePageVo> pagingRespDtoToVo(PagingRespDto<BackendRoleDto> dto);

  BackendRoleDto reqVoToDto(BackendRoleReqVo vo);

}
