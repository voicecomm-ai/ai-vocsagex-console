package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.TagType;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * 图知识库本体关系属性Converter
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeGraphTagEdgePropertyConverter {

  KnowledgeGraphTagEdgePropertyPo dtoToPo(
      KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto);

  KnowledgeGraphTagEdgePropertyDto poToDto(
      KnowledgeGraphTagEdgePropertyPo knowledgeGraphTagEdgePropertyPo);

  List<KnowledgeGraphTagEdgePropertyPo> dtoListToPoList(
      List<KnowledgeGraphTagEdgePropertyDto> knowledgeGraphTagEdgePropertyDtoList);

  List<KnowledgeGraphTagEdgePropertyDto> poListToDtoList(
      List<KnowledgeGraphTagEdgePropertyPo> knowledgeGraphTagEdgePropertyPoList);

  PagingRespDto<KnowledgeGraphTagEdgePropertyDto> pagePoToDto(
      Page<KnowledgeGraphTagEdgePropertyPo> page);

  KnowledgeGraphTagEdgePropertyPo tagTypeToPo(TagType graphTagEdgePropertyDto);
}
