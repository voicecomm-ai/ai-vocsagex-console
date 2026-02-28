package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelDatasetPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 数据集Converter
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelDatasetConverter {

  ModelDatasetPo dtoToPo(ModelDatasetDto modelDatasetDto);

  ModelDatasetDto poToDto(ModelDatasetPo modelDatasetPo);

  List<ModelDatasetPo> dtoListToPoList(List<ModelDatasetDto> modelDatasetDtoList);

  List<ModelDatasetDto> poListToDtoList(List<ModelDatasetPo> modelDatasetPoList);

  PagingRespDto<ModelDatasetDto> pagePoToDto(Page<ModelDatasetPo> modelPoPage);
}
