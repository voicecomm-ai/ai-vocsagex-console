package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 模型Converter
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelConverter {

  ModelPo dtoToPo(ModelDto modelDto);

  ModelDto poToDto(ModelPo modelPo);

  List<ModelPo> dtoListToPoList(List<ModelDto> modelDtoList);

  List<ModelDto> poListToDtoList(List<ModelPo> modelPoList);

  PagingRespDto<ModelPageDto> pagePoToDto(Page<ModelPo> modelPoPage);

  List<ModelPageDto> poListToPageDtoList(List<ModelPo> modelPoList);

  ZipNodeDto zipNodePoToDto(ZipNodePo zipNodePo);

}
