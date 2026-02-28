package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphVisualManageDto;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphVisualManagePo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 图谱可视化管理Converter
 *
 * @author ryc
 * @date 2025-09-16 14:52:27
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeGraphVisualManageConverter {

  KnowledgeGraphVisualManagePo dtoToPo(KnowledgeGraphVisualManageDto knowledgeGraphVisualManageDto);

  KnowledgeGraphVisualManageDto poToDto(KnowledgeGraphVisualManagePo knowledgeGraphVisualManagePo);

  List<KnowledgeGraphVisualManagePo> dtoListToPoList(List<KnowledgeGraphVisualManageDto> knowledgeGraphVisualManageDtoList);

  List<KnowledgeGraphVisualManageDto> poListToDtoList(List<KnowledgeGraphVisualManagePo> knowledgeGraphVisualManagePoList);

}
