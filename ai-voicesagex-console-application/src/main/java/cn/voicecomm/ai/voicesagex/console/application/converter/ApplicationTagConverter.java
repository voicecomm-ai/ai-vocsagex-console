package cn.voicecomm.ai.voicesagex.console.application.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagRelationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpDto;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * ApplicationTagConverter
 *
 * @author wangfan
 * @date 2025/4/1 下午 3:39
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ApplicationTagConverter {

  ApplicationTagDto poToDto(ApplicationTagPo po);

  ApplicationTagPo dtoToPo(ApplicationTagDto po);

  List<ApplicationTagPo> dtoToPoList(List<ApplicationTagDto> obj);

  List<ApplicationTagDto> poToDtoList(List<ApplicationTagPo> obj);

  McpDto poToMcpDto(McpPo po);



  ApplicationTagRelationDto poToDto(ApplicationTagRelationPo po);

  ApplicationTagRelationPo dtoToPo(ApplicationTagRelationDto po);

  List<ApplicationTagRelationPo> relationDtoToPoList(List<ApplicationTagRelationDto> obj);

  List<ApplicationTagRelationDto> relationPoToDtoList(List<ApplicationTagRelationPo> obj);

  ApplicationExperienceTagDto experiencePoToDto(ApplicationExperienceTagPo po);

  ApplicationExperienceTagPo experienceDtoToPo(ApplicationExperienceTagDto po);

  List<ApplicationExperienceTagDto> experiencePoToDtoList(List<ApplicationExperienceTagPo> obj);
}
