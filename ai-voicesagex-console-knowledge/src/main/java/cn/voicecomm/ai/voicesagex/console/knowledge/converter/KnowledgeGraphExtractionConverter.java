package cn.voicecomm.ai.voicesagex.console.knowledge.converter;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeSaveExtractionDto;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeExtractionPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * @ClassName KnowledgeGraphExtractionConverter
 * @Author wangyang
 * @Date 2025/9/15 15:02
 */

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface KnowledgeGraphExtractionConverter {

  KnowledgeExtractionPo dtoToPo(KnowledgeSaveExtractionDto knowledgeExtractionDto);

  PagingRespDto<KnowledgeExtractionDto> pagePoToDto(Page<KnowledgeExtractionPo> page);

  KnowledgeExtractionDto poToDto(KnowledgeExtractionPo knowledgeExtractionPo);

  List<KnowledgeExtractionDto> poListToDtoList(
      List<KnowledgeExtractionPo> knowledgeExtractionPoList);

}
