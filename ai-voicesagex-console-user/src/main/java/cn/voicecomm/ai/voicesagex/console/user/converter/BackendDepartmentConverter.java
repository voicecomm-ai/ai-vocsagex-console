package cn.voicecomm.ai.voicesagex.console.user.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendDepartmentDto;
import cn.voicecomm.ai.voicesagex.console.util.po.user.DepartmentPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BackendDepartmentConverter {

  BackendDepartmentDto poToDto(DepartmentPo obj);

  DepartmentPo dtoToPo(BackendDepartmentDto obj);

  List<DepartmentPo> dtoToPoList(List<BackendDepartmentDto> obj);

  List<BackendDepartmentDto> poToDtoList(List<DepartmentPo> obj);

}
