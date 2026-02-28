package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelTagDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelCategoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 模型标签Converter
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelTagConverter {

  ModelTagPo dtoToPo(ModelTagDto modelTagDto);

  ModelTagDto poToDto(ModelTagPo modelTagPo);

  List<ModelTagPo> dtoListToPoList(List<ModelTagDto> modelTagDtoList);

  List<ModelTagDto> poListToDtoList(List<ModelTagPo> modelTagPoList);

  ModelCategoryPageDto poToPageDto(ModelCategoryPo modelCategoryPo);
}
