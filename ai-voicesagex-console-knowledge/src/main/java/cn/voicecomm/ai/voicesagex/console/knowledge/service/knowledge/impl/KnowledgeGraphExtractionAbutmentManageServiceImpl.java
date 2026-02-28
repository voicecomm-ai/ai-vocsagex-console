package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;


import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ApiDocumentService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphExtractionAbutmentManageService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultCallbackData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.FileParseResultCallbackDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.DocumentVerificationEnums;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.DocumentEnum;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeChunkInformationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeDocumentInformationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeDocumentVerificationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.SheetInfomationMapper;
import cn.voicecomm.ai.voicesagex.console.util.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeChunkInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentVerificationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.SheetInfomationPo;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphExtractionAbutmentManageServiceImpl implements
    KnowledgeGraphExtractionAbutmentManageService {


  private final KnowledgeDocumentInformationMapper knowledgeDocumentInformationMapper;

  private final SheetInfomationMapper sheetInfomationMapper;

  private final ThreadPoolTaskExecutor analysisExecutor;

  private final ApiDocumentService apiDocumentService;
  private final KnowledgeChunkInformationMapper knowledgeChunkInformationMapper;
  private final KnowledgeDocumentVerificationMapper knowledgeDocumentVerificationMapper;

  private final ThreadPoolTaskExecutor chunkExecutor;

  @Override
  public CommonRespDto<Boolean> processDocumentExtract(
      FileParseResultCallbackDto fileParseResultCallbackDto) {

    try {
      log.info("【回调接收参数为：{}】", fileParseResultCallbackDto);
      if (null != fileParseResultCallbackDto
          && null == fileParseResultCallbackDto.getDocument_node()
          && null == fileParseResultCallbackDto.getChunk_nodes()) {
        updateDocumentInfoError(Integer.parseInt(
            fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA)
                .toString()), DocumentEnum.ANALYSIS_LOSE.getStatus());
        // 更新解析失败状态
        log.error("【文件解析失败】");

      } else if (null != fileParseResultCallbackDto
          && null != fileParseResultCallbackDto.getDocument_node()
          && (null == fileParseResultCallbackDto.getDocument_node().getChunk_size()
          || SpaceConstant.INDEX == fileParseResultCallbackDto.getDocument_node()
          .getChunk_size())) {
        log.error("【文件解析失败】");
        updateDocumentInfoError(Integer.parseInt(
            fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA)
                .toString()), DocumentEnum.ANALYSIS_LOSE.getStatus());

      } else {
        // 解析 chuck 信息 异步存储
        analysisExecutor.execute(() -> {
          // 更新 document chunk_size
          updateDocumentInfo(fileParseResultCallbackDto, DocumentEnum.IN_ANALYSIS.getStatus());
          apiDocumentService.processChunkInfoAsync(fileParseResultCallbackDto);
        });
      }
    } catch (ServiceException e) {
      // 更新解析失败状态
      log.error("【解析文件回调接口执行失败:】", e);
      updateDocumentInfoError(Integer.parseInt(
              fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA).toString()),
          DocumentEnum.ANALYSIS_LOSE.getStatus());
      throw e;
    } catch (Exception e) {
      updateDocumentInfoError(Integer.parseInt(
              fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA).toString()),
          DocumentEnum.ANALYSIS_LOSE.getStatus());
      log.error("【解析文件回调接口执行失败:】", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.EXTRACT_DOCUMENT_ERROR);
    }

    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> extractTriad(ExtractResultCallbackData extractResultCallbackData) {
    try {
      //抽取三元组 信息 异步存储
      chunkExecutor.execute(() -> {
        // 新增 document verification
        insertDocumentVerification(extractResultCallbackData,
            DocumentVerificationEnums.ORIGINAL_STATE.getStatus());
      });
      log.info("【抽取三元组信息回调接口执行成功：{}】",
          extractResultCallbackData.getMetadata().get(SpaceConstant.CHUNK_METADATA).toString());
      return CommonRespDto.success(Boolean.TRUE);
    } catch (Exception e) {
      log.error("【抽取三元组回调接口执行失败:】", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFICATION_DOCUMENT_ERROR);
    }

  }


  /**
   * 抽取三元组信息
   *
   * @param extractResultCallbackData
   * @param status
   */
  private void insertDocumentVerification(ExtractResultCallbackData extractResultCallbackData,
      Integer status) {
    log.info("【抽取三元组信息： {}】", extractResultCallbackData);
    if (null != extractResultCallbackData.getMetadata()
        && null != extractResultCallbackData.getMetadata().get(SpaceConstant.CHUNK_METADATA)) {

      Integer chunkId = Integer.valueOf(
          extractResultCallbackData.getMetadata().get(SpaceConstant.CHUNK_METADATA).toString());

      QueryWrapper<KnowledgeChunkInformationPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("chunk_id", chunkId);
      KnowledgeChunkInformationPo chunkInformation = knowledgeChunkInformationMapper.selectOne(
          queryWrapper);

      if (null != chunkInformation) {
        UpdateWrapper<KnowledgeChunkInformationPo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("chunk_id", chunkId);
        updateWrapper.set("chunk_status", SpaceConstant.REPLICA_FACTOR);
        // 执行更新操作
        knowledgeChunkInformationMapper.update(null, updateWrapper);
        if (null != extractResultCallbackData.getTriples() && !CollectionUtils.isEmpty(
            extractResultCallbackData.getTriples())) {

          List<KnowledgeDocumentVerificationPo> documentVerificationList = new ArrayList<>();
          extractResultCallbackData.getTriples().stream().forEach(triple -> {
            log.info("输出三元组信息======================{}", triple);
            KnowledgeDocumentVerificationPo documentVerification = new KnowledgeDocumentVerificationPo();
            // 获取文档id
            if (null != chunkInformation) {
              documentVerification.setDocumentId(chunkInformation.getDocumentId());
            }
            documentVerification.setChunkId(Integer.valueOf(
                extractResultCallbackData.getMetadata().get(SpaceConstant.CHUNK_METADATA)
                    .toString()));
            documentVerification.setVerificationStatus(status);
            documentVerification.setEdgeType(triple.getEdge_type());
            if (null != triple.getSource_node()) {
              documentVerification.setSubject(triple.getSource_node().getName());
              documentVerification.setSubjectTagName(triple.getSource_node().getTag());
            }

            if (null != triple.getTarget_node()) {
              documentVerification.setObject(triple.getTarget_node().getName());
              documentVerification.setObjectTagName(triple.getTarget_node().getTag());
            }
            documentVerification.setCreateBy(UserAuthUtil.getUserId());
            documentVerificationList.add(documentVerification);
          });

          if (!CollectionUtils.isEmpty(documentVerificationList)) {
            MybatisPlusUtil<KnowledgeDocumentVerificationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
                knowledgeDocumentVerificationMapper, KnowledgeDocumentVerificationPo.class);
            mybatisPlusUtil.saveBatch(documentVerificationList, documentVerificationList.size());

          }

        }
      }
    }
  }

  private void updateDocumentInfoError(Integer id, Integer status) {
    // 执行更新操作
    knowledgeDocumentInformationMapper.updateById(KnowledgeDocumentInformationPo.builder()
        .documentId(id)
        .documentStatus(status).build());
  }

  private void updateDocumentInfo(FileParseResultCallbackDto fileParseResultCallbackDto,
      Integer status) {
    log.info("【更新document信息： {}】",
        fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA).toString());
    try {
      KnowledgeDocumentInformationPo documentInformation = getDocumentInfo(
          fileParseResultCallbackDto, status);
      log.info("【文档状态为:{}】", documentInformation.getDocumentStatus());
      // 执行更新操作
      knowledgeDocumentInformationMapper.updateById(KnowledgeDocumentInformationPo.builder()
          .documentId(documentInformation.getDocumentId())
          .totalPages(documentInformation.getTotalPages())
          .documentStatus(documentInformation.getDocumentStatus())
          .filePath(documentInformation.getFilePath())
          .fileFormat(documentInformation.getFileFormat())
          .chunkSize(documentInformation.getChunkSize()).build());

    } catch (Exception e) {
      log.error("【updating knowledge Document Info   error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_DOCUMENT_ERROR, e);
    }
  }


  private KnowledgeDocumentInformationPo getDocumentInfo(
      FileParseResultCallbackDto fileParseResultCallbackDto, Integer status) {
    log.info("【文档状态为：{}】", status);

    Integer documentId = Integer.valueOf(
        fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA).toString());

    KnowledgeDocumentInformationPo documentInformation = new KnowledgeDocumentInformationPo();
    documentInformation.setDocumentId(documentId);
    documentInformation.setChunkSize(fileParseResultCallbackDto.getDocument_node().getChunk_size());
    documentInformation.setDocumentStatus(status);
    documentInformation.setFilePath(fileParseResultCallbackDto.getDocument_node().getFile_path());
    documentInformation.setTotalPages(
        fileParseResultCallbackDto.getDocument_node().getTotal_pages());
    documentInformation.setFileFormat(fileParseResultCallbackDto.getDocument_node().getFormat());
    JSONArray sheetListArray = fileParseResultCallbackDto.getMetadata().getJSONArray("sheet_lists");
    if (null != sheetListArray) {
      List<String> sheetList = sheetListArray.toJavaList(String.class);
      if (!CollectionUtils.isEmpty(sheetList)) {
        QueryWrapper<SheetInfomationPo> queryWrapper = Wrappers.query();
        queryWrapper.eq("document_id", Integer.parseInt(
            fileParseResultCallbackDto.getMetadata().get(SpaceConstant.DOCUMENT_METADATA)
                .toString()));
        List<SheetInfomationPo> sheetInfomations = sheetInfomationMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(sheetInfomations)) {
          for (String sheetName : sheetList) {
            SheetInfomationPo sheetInfomation = SheetInfomationPo.builder()
                .sheetName(sheetName)
                .documentId(documentId)
                .build();
            sheetInfomationMapper.insert(sheetInfomation);
          }
        }

      }
    }
    log.info("【文档信息为：{}】", documentInformation);
    return documentInformation;
  }

}
