package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFileDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelDatasetFilePo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 数据集文件Converter
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelDatasetFileConverter {

  ModelDatasetFilePo dtoToPo(ModelDatasetFileDto modelDatasetFileDto);

  ModelDatasetFileDto poToDto(ModelDatasetFilePo modelDatasetFilePo);

  List<ModelDatasetFilePo> dtoListToPoList(List<ModelDatasetFileDto> modelDatasetFileDtoList);

  List<ModelDatasetFileDto> poListToDtoList(List<ModelDatasetFilePo> modelDatasetFilePoList);
  PagingRespDto<ModelDatasetFileDto> pagePoToDto(Page<ModelDatasetFilePo> modelPoPage);
}
