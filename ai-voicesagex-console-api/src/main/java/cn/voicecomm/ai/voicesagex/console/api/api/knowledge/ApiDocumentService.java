package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractPreviewDTO;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultViewData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseResultCallbackDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeChunkInformationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ParseResponseDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.FileParseReq;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName ApiDocumentService
 * @Description TODO
 * @Date 2025/9/15 16:34
 */
public interface ApiDocumentService {

  CompletableFuture<Void> makeAsyncRequest(String url, FileParseReq req, Integer id);

  CompletableFuture<Void> cancelTask(List<KnowledgeChunkInformationDto> chunkInformations);


  CompletableFuture<Void> delJobAsyncRequest(String url, String jobId);

  CompletableFuture<Void> processChunkInfoAsync(
      FileParseResultCallbackDto fileParseResultCallbackDto);

  CompletableFuture<Void> makeAsyncExtractionRequest(
      List<KnowledgeChunkInformationDto> chunkInformations,
      KnowledgeExtractionDto knowledgeExtractionDto, String rePrompt, String nerPrompt,
      String nerModel, String reModel);

  ParseResponseDataDto parseLinUp();

  ExtractResultViewData getExtractResult(ExtractPreviewDTO extractPreviewDTO);
}
