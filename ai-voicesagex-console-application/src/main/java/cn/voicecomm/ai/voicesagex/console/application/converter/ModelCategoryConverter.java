package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelCategoryPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 模型分类Converter
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelCategoryConverter {

  ModelCategoryPo dtoToPo(ModelCategoryDto modelCategoryDto);

  ModelCategoryDto poToDto(ModelCategoryPo modelCategoryPo);

  List<ModelCategoryPo> dtoListToPoList(List<ModelCategoryDto> modelCategoryDtoList);

  List<ModelCategoryDto> poListToDtoList(List<ModelCategoryPo> modelCategoryPoList);

  List<ModelCategoryPageDto> poListToPageDtoList(List<ModelCategoryPo> modelCategoryPoList);
}
