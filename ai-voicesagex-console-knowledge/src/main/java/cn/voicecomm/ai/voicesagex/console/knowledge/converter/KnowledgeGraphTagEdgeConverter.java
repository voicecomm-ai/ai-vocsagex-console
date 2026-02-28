package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 图知识库本体关系Converter
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeGraphTagEdgeConverter {

  KnowledgeGraphTagEdgePo dtoToPo(KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto);

  KnowledgeGraphTagEdgeDto poToDto(KnowledgeGraphTagEdgePo knowledgeGraphTagEdgePo);

  List<KnowledgeGraphTagEdgePo> dtoListToPoList(
      List<KnowledgeGraphTagEdgeDto> knowledgeGraphTagEdgeDtoList);

  List<KnowledgeGraphTagEdgeDto> poListToDtoList(
      List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePoList);

}
