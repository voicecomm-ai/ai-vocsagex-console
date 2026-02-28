package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * McpConverter
 *
 * @author wangf
 * @date 2025/7/8 下午 2:08
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface McpConverter {

  McpPo dtoToPo(McpDto McpDto);

  McpDto poToDto(McpPo McpPo);

  List<McpPo> dtoListToPoList(List<McpDto> McpDtoList);

  List<McpDto> poListToDtoList(List<McpPo> McpPoList);

  PagingRespDto<McpDto> pagePoToDto(Page<McpPo> McpPoPage);

}
