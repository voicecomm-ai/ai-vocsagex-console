package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ApiDocumentService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractPreviewDTO;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultViewData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseDelReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseResultCallbackDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KGExtractReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeChunkInformationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ParseResponseDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.FileParseReq;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.DocumentEnum;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeChunkInformationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeDocumentInformationMapper;
import cn.voicecomm.ai.voicesagex.console.util.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeChunkInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * @ClassName ApiDocumentServiceImpl
 * @Author wangyang
 * @Date 2025/9/15 16:35
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiDocumentServiceImpl implements ApiDocumentService {

  private final KnowledgeDocumentInformationMapper knowledgeDocumentInformationMapper;

  private final KnowledgeChunkInformationMapper knowledgeChunkInformationMapper;

  private final ThreadPoolTaskExecutor chunkExecutor;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${vector.batchCancelTask}")
  private Integer batchCancelTask;

  @Value("${extract.deleteTask}")
  private String deleteTask;

  @Value("${extract.extractReqUrl}")
  private String extractReqUrl;

  @Value("${parse.linUp}")
  private String linUp;

  @Value("${extract.extractReq}")
  private String extractReq;

  @Value("${extract.extractReqView}")
  private String extractReqView;


  @Async
  @Override
  @RefreshScope
  public CompletableFuture<Void> makeAsyncRequest(String url, FileParseReq req, Integer id) {
    return CompletableFuture.runAsync(() -> {
      try {
        log.info("【开始异步调用解析远程接口,解析文件地址为：{}】", req.getFilePath());
        req.setDocumentId(String.valueOf(id));

        String requestBody = new ObjectMapper().writeValueAsString(req);
        log.info("【准备调用解析远程接口,URL: {}，请求体: {}】", url, requestBody);

        String response = HttpUtil.post(url, requestBody);
        JSONObject jsonObject = JSONUtil.parseObj(response);

        log.info("【调用解析远程接口返回: {}】", response);

        knowledgeDocumentInformationMapper.updateById(KnowledgeDocumentInformationPo.builder()
            .documentId(id)
            .jobId(jsonObject.get(SpaceConstant.DATA).toString()).build());

        log.info("【异步调用解析远程接口成功,解析文件地址为：{}】", req.getFilePath());
      } catch (Exception e) {
        log.error("【Description Failed to invoke the remote extraction interface】", e);
        throw ServiceExceptionUtil.exception(ErrorConstants.INVOKE_DOCUMENT);
      }
    });


  }

  @Async
  @Override
  public CompletableFuture<Void> cancelTask(List<KnowledgeChunkInformationDto> chunkInformations) {
    final int BATCH_SIZE = batchCancelTask;

    List<CompletableFuture<Void>> allBatches = new ArrayList<>();

    for (int i = SpaceConstant.INDEX; i < chunkInformations.size(); i += BATCH_SIZE) {
      int end = Math.min(chunkInformations.size(), i + BATCH_SIZE);
      List<KnowledgeChunkInformationDto> batch = chunkInformations.subList(i, end);
      List<String> jobIds = batch.stream()
          .map(KnowledgeChunkInformationDto::getJobId)
          .collect(Collectors.toList());
      CompletableFuture<Void> batchFuture = processBatch(jobIds, chunkInformations);
      allBatches.add(batchFuture);
    }

    return CompletableFuture.allOf(allBatches.toArray(new CompletableFuture[0]));
  }

  @Async
  @Override
  public CompletableFuture<Void> delJobAsyncRequest(String url, String jobId) {
    return CompletableFuture.runAsync(() -> {
      try {
        log.info("【开始异步调用删除解析任务接口,任务id为：{},地址为：{}】", jobId, url);

        FileParseDelReq fileParseDelReq = new FileParseDelReq(jobId);
        String requestBody = new ObjectMapper().writeValueAsString(fileParseDelReq);
        log.info("【准备调用删除解析任务接口,URL: {}，请求体: {}】", url, requestBody);

        String response = HttpUtil.post(url, requestBody);
        log.info("【异步调用删除解析任务接口成功，响应{}】", response);

      } catch (Exception e) {
        log.error("【Description Failed to invoke the remote extraction interface】", e);
        throw ServiceExceptionUtil.exception(ErrorConstants.INVOKE_DOCUMENT);
      }
    });

  }

  @Async
  @Override
  public CompletableFuture<Void> processChunkInfoAsync(
      FileParseResultCallbackDto fileParseResultCallbackDto) {
    return CompletableFuture.runAsync(() -> {
      log.info("【异步存储chunk信息,包含chunk数量为：{}】",
          fileParseResultCallbackDto.getChunk_nodes().size());

      List<KnowledgeChunkInformationPo> chunkInformations = new ArrayList<>();

      if (!CollectionUtils.isEmpty(fileParseResultCallbackDto.getChunk_nodes())) {
        fileParseResultCallbackDto.getChunk_nodes().stream().forEach(chunk -> {
          KnowledgeChunkInformationPo chunkInformation = new KnowledgeChunkInformationPo();
          chunkInformation.setChunkContent(chunk.getPage_content());
          chunkInformation.setChunkIndex(chunk.getChunk_index());
          chunkInformation.setSheetName(chunk.getSheet_name());
          chunkInformation.setPageNumber(chunk.getPage());
          chunkInformation.setDocumentId(Integer.valueOf(
              fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA)
                  .toString()));
          chunkInformations.add(chunkInformation);
        });
      }
      log.info("【开始批量存储chunk信息】");

      MybatisPlusUtil<KnowledgeChunkInformationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          knowledgeChunkInformationMapper, KnowledgeChunkInformationPo.class);
      mybatisPlusUtil.saveBatch(chunkInformations, chunkInformations.size());

      KnowledgeDocumentInformationPo documentInfo = getDocumentInfo(fileParseResultCallbackDto,
          DocumentEnum.ANALYSIS_SUCCESS.getStatus());

      log.info("【文档状态为:{}】", documentInfo.getDocumentStatus());

      // 执行更新操作
      knowledgeDocumentInformationMapper.updateById(KnowledgeDocumentInformationPo.builder()
          .documentId(documentInfo.getDocumentId())
          .totalPages(documentInfo.getTotalPages())
          .documentStatus(documentInfo.getDocumentStatus())
          .fileFormat(documentInfo.getFileFormat())
          .filePath(documentInfo.getFilePath())
          .chunkSize(documentInfo.getChunkSize()).build());
    });


  }

  @Override
  public CompletableFuture<Void> makeAsyncExtractionRequest(
      List<KnowledgeChunkInformationDto> chunkInformations,
      KnowledgeExtractionDto knowledgeExtraction, String rePrompt, String nerPrompt,
      String nerModel, String reModel) {
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (KnowledgeChunkInformationDto chunkInformation : chunkInformations) {

      // Prepare the tags and edges
      List<String> tags = Arrays.stream(
          knowledgeExtraction.getTagNames().split(SpaceConstant.TAG_SPLIT)).toList();
      List<String> edges = Arrays.stream(
          knowledgeExtraction.getEdgeNames().split(SpaceConstant.TAG_SPLIT)).toList();
      // Create metadata
      com.alibaba.fastjson.JSONObject metadata = new com.alibaba.fastjson.JSONObject();
      metadata.put(SpaceConstant.CHUNK_METADATA,
          String.valueOf(chunkInformation.getChunkId()));  // Add key-value pair to metadata
      log.info("【chunk id 信息： {}】", chunkInformation.getChunkId());
      log.info("【metadata信息  ： {}】", metadata.get(SpaceConstant.CHUNK_METADATA));

      // Create KGExtractReq object
      KGExtractReq kgExtractReq = new KGExtractReq(edges, tags, chunkInformation.getChunkContent(),
          metadata, extractReqUrl, rePrompt, nerPrompt, nerModel, reModel);
      // Create an asynchronous task for each chunk
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        try {

          String requestParam = new ObjectMapper().writeValueAsString(kgExtractReq);
          log.info("【开始异步调用抽取远程接口,调用地址为：{}, 参数：{}】", extractReq,
              requestParam);
          String response = HttpUtil.post(extractReq, requestParam);

          log.info("【异步调用抽取远程接口成功，响应{}】", response);
          JSONObject jsonObject = JSONUtil.parseObj(response);

          knowledgeChunkInformationMapper.updateById(KnowledgeChunkInformationPo.builder()
              .chunkId(chunkInformation.getChunkId())
              .jobId(jsonObject.get(SpaceConstant.DATA).toString()).build());

        } catch (Exception e) {
          log.error("【Remote call extraction failed interface】", e);
          throw ServiceExceptionUtil.exception(ErrorConstants.EXTRACT_INVOKE_DOCUMENT);
        }
      }, chunkExecutor);
      // Add the future to the list
      futures.add(future);
    }

    // Combine all futures and return a single CompletableFuture
    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
  }

  @Override
  public ParseResponseDataDto parseLinUp() {
    log.info("调用解析排队任务信息，调用地址为：{}", linUp);

    try {
      String json = HttpUtil.get(linUp);
      log.info("【解析排队任务信息成功，响应{}】", json);
      JsonNode root = objectMapper.readTree(json);
      JsonNode dataNode = root.path("data");
      return objectMapper.treeToValue(dataNode, ParseResponseDataDto.class);

    } catch (Exception e) {
      log.error("【调用解析排队任务信息失败: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_DOCUMENT_ERROR, e);
    }

  }

  @Override
  public ExtractResultViewData getExtractResult(ExtractPreviewDTO extractPreviewDTO) {
    log.info("【开始调用抽取远程接口, 调用地址为：{}】", extractReqView);
    ExtractResultViewData extractResultViewData = new ExtractResultViewData();

    try {
      String json = objectMapper.writeValueAsString(extractPreviewDTO);

      String response = HttpUtil.post(extractReqView, json);
      log.info("【抽取远程接口成功，响应{}】", response);

      com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response);

      if ("1000".equals(jsonObject.get("status").toString())) {
        com.alibaba.fastjson.JSONObject dataNode = (com.alibaba.fastjson.JSONObject) jsonObject.get(
            "data");

        extractResultViewData = dataNode.toJavaObject(ExtractResultViewData.class);
      }
    } catch (Exception e) {
      log.error("【Remote call extraction failed interface】", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.CALLBACK_INVOKE_DOCUMENT_FAIL);
    }
    return extractResultViewData;
  }

  private KnowledgeDocumentInformationPo getDocumentInfo(
      FileParseResultCallbackDto fileParseResultCallbackDto, Integer status) {
    log.info("【文档状态为：{}】", status);
    KnowledgeDocumentInformationPo documentInformation = new KnowledgeDocumentInformationPo();
    documentInformation.setDocumentId(Integer.parseInt(
        fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA).toString()));
    documentInformation.setChunkSize(fileParseResultCallbackDto.getDocument_node().getChunk_size());
    documentInformation.setDocumentStatus(status);
    documentInformation.setFilePath(fileParseResultCallbackDto.getDocument_node().getFile_path());
    documentInformation.setTotalPages(
        fileParseResultCallbackDto.getDocument_node().getTotal_pages());
    documentInformation.setFileFormat(fileParseResultCallbackDto.getDocument_node().getFormat());
    log.info("【文档信息为：{}】", documentInformation);
    return documentInformation;
  }


  private CompletableFuture<Void> processBatch(List<String> jobIds,
      List<KnowledgeChunkInformationDto> chunkInformations) {
    JSONObject requestBody = new JSONObject();
    requestBody.put(SpaceConstant.TASK_ID, jobIds);

    return CompletableFuture.runAsync(() -> {
      try {
        log.info("【开始异步调用抽取远程接口, 调用地址为：{},参数：{}】", deleteTask, requestBody);

        String requestParam = new ObjectMapper().writeValueAsString(requestBody);
        String response = HttpUtil.post(deleteTask, requestParam);

        log.info("【异步调用抽取远程接口成功，响应{}】", response);

        for (KnowledgeChunkInformationDto chunk : chunkInformations) {
          // 执行更新操作
          knowledgeChunkInformationMapper.delete(Wrappers.<KnowledgeChunkInformationPo>lambdaQuery()
              .eq(KnowledgeChunkInformationPo::getChunkId, chunk.getChunkId()));
        }
      } catch (Exception e) {
        log.error("【Remote call extraction failed interface】", e);
        throw ServiceExceptionUtil.exception(ErrorConstants.EXTRACT_INVOKE_DOCUMENT);
      }
    }, chunkExecutor);

  }
}
