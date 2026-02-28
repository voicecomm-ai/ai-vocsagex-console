package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeChunkInformationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeSaveExtractionDto;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeChunkInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeExtractionPo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @ClassName KnowledgeChunkInfomationConverter
 * @Author wangyang
 * @Date 2025/9/16 14:01
 */

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeChunkInfomationConverter {

    List<KnowledgeChunkInformationDto> poListToDtoList(List<KnowledgeChunkInformationPo> knowledgeChunkInformationPos);


}
