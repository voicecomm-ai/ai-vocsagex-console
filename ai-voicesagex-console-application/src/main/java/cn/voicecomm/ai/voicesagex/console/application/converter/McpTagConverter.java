package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagDto;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * McpTagConverter
 *
 * @author wangf
 * @date 2025/7/8 下午 2:08
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface McpTagConverter {

  McpTagPo dtoToPo(McpTagDto McpTagDto);

  McpTagDto poToDto(McpTagPo McpTagPo);

  List<McpTagPo> dtoListToPoList(List<McpTagDto> McpTagDtoList);

  List<McpTagDto> poListToDtoList(List<McpTagPo> McpTagPoList);
}
