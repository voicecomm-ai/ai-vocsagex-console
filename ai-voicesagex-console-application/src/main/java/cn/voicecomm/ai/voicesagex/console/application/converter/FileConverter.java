package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
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
public interface FileConverter {
  List<ZipNodeDto> zipNodePoListToDtoList(List<ZipNodePo> zipNodePoList);

}
