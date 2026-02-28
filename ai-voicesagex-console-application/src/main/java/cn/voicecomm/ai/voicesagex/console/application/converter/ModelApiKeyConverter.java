package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelApiKeyPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 模型密钥Converter
 *
 * @author ryc
 * @date 2025-07-09 09:57:34
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ModelApiKeyConverter {

  ModelApiKeyPo dtoToPo(ModelApiKeyDto modelApiKeyDto);

  ModelApiKeyDto poToDto(ModelApiKeyPo modelApiKeyPo);

  List<ModelApiKeyPo> dtoListToPoList(List<ModelApiKeyDto> modelApiKeyDtoList);

  List<ModelApiKeyDto> poListToDtoList(List<ModelApiKeyPo> modelApiKeyPoList);

}
