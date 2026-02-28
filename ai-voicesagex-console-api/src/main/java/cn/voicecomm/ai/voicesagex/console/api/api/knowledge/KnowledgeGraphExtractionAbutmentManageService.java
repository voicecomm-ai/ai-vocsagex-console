package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;


import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultCallbackData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseResultCallbackDto;

public interface KnowledgeGraphExtractionAbutmentManageService {

  CommonRespDto<Boolean> processDocumentExtract(
      FileParseResultCallbackDto fileParseResultCallbackDto);

  CommonRespDto<Boolean> extractTriad(ExtractResultCallbackData extractResultCallbackData);
}
