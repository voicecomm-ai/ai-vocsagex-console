package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ApiDocumentService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeBaseService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeExtractionManageService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphEntityManageService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDetailDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ChunkCountReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ConfigInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.DropStatusVerification;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.EntityInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractPreviewDTO;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ExtractResultViewData;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDoExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDropExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeSaveExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeUpdateExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ParseResponseDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.Triple;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationEdgeInfo;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationTypeSelectDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.FileParseReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.DocumentVerificationEnums;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.graph.DocumentEnum;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.BatchVerificationInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.BatchVerificationRelation;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.verification.VerificationInfo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ChunkInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListDetailVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DropVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ExtractPreviewVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.InsertVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.JobLineUpVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.KnowledgeEntryMapVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ParseLineUpVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.PropertyInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.StatusVerificationClear;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.UpdateVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationEdgeTypePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationKnowledge;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationKnowledgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationTotalVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeChunkInfomationConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeDocumentConfigConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeGraphExtractionConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.DocumentConfigMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeChunkInformationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeDocumentInformationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeDocumentVerificationMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeExtractionMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgePropertyMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphVertexMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.PropertyValidator;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.SnowflakeIdUtils;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.UniqueIDGenerator;
import cn.voicecomm.ai.voicesagex.console.util.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.DocumentConfigPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeChunkInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentInformationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentVerificationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeExtractionPo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName KnowledgeExtractionManageServiceImpl
 * @Author wangyang
 * @Date 2025/9/15 14:30
 */

@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class KnowledgeExtractionManageServiceImpl implements KnowledgeExtractionManageService {

  private final KnowledgeExtractionMapper knowledgeExtractionMapper;

  private final KnowledgeDocumentInformationMapper knowledgeDocumentInformationMapper;

  private final KnowledgeGraphExtractionConverter knowledgeGraphExtractionConverter;

  private final KnowledgeDocumentVerificationMapper knowledgeDocumentVerificationMapper;

  private final KnowledgeChunkInformationMapper knowledgeChunkInformationMapper;

  private final KnowledgeChunkInfomationConverter knowledgeChunkInfomationConverter;

  private final ApiDocumentService apiDocumentService;

  private final KnowledgeBaseService knowledgeBaseService;

  private final BackendUserService backendUserService;

  private final DocumentConfigMapper documentConfigMapper;

  private final KnowledgeDocumentConfigConverter knowledgeDocumentConfigConverter;

  private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
  private final KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper;

  private final KnowledgeGraphTagEdgePropertyMapper knowledgeGraphTagEdgePropertyMapper;

  private final KnowledgeGraphEntityManageService knowledgeGraphEntityManageService;

  private final GraphEdgeService graphEdgeService;

  private final GraphVertexMapper graphVertexMapper;

  private final GraphEdgeMapper graphEdgeMapper;

  private final GraphVertexService graphVertexService;


  @Value("${knowledge.file.url}")
  private String url;

  @Value("${knowledge.name}")
  private String name;

  @Value("${extract.callbackUrl}")
  private String callbackUrl;

  @Value("${extract.parseReq}")
  private String parseReq;

  @Value("${extract.parseReqDel}")
  private String parseReqDel;


  // 定义文件写入专用线程池
  private static final ExecutorService fileWriteExecutor = new ThreadPoolExecutor(
      10,
      20,
      60L,
      TimeUnit.SECONDS,
      new LinkedBlockingQueue<>(50),
      new ThreadFactoryBuilder().setNameFormat("file-write-pool-%d").build(),
      new ThreadPoolExecutor.CallerRunsPolicy()
  );

  private static final ExecutorService uploadExecutor = new ThreadPoolExecutor(
      10,                       // 核心线程数
      20,                     // 最大线程数
      60L,                    // 空闲线程存活时间
      TimeUnit.SECONDS,       // 时间单位
      new LinkedBlockingQueue<>(100),  // 任务队列
      new ThreadFactoryBuilder().setNameFormat("file-upload-pool-%d").build(), // 线程命名
      new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
  );

  @Override
  public CommonRespDto<Boolean> insertExtractionJob(
      KnowledgeSaveExtractionDto knowledgeSaveExtractionDto) {

    log.info("【新增知识提取参数 ： {} 】", knowledgeSaveExtractionDto);

    KnowledgeExtractionPo knowledgeExtractionPo = KnowledgeExtractionPo.builder()
        .spaceId(knowledgeSaveExtractionDto.getSpaceId())
        .spaceName(knowledgeSaveExtractionDto.getSpaceName())
        .edgeNames(knowledgeSaveExtractionDto.getEdges().stream()
            .collect(Collectors.joining(SpaceConstant.TAG_SPLIT)))
        .tagNames(knowledgeSaveExtractionDto.getTags().stream()
            .collect(Collectors.joining(SpaceConstant.TAG_SPLIT)))
        .jobName(knowledgeSaveExtractionDto.getJobName())
        .createBy(UserAuthUtil.getUserId())
        .updateBy(UserAuthUtil.getUserId())
        .build();

    try {
      // 判断该任务名称是否重复

      List<KnowledgeExtractionPo> knowledgeExtractionPos = knowledgeExtractionMapper.selectList(
          Wrappers.<KnowledgeExtractionPo>lambdaQuery()
              .eq(KnowledgeExtractionPo::getJobName, knowledgeSaveExtractionDto.getJobName()));

      if (CollectionUtils.isEmpty(knowledgeExtractionPos) || !knowledgeExtractionPos.stream()
          .map(KnowledgeExtractionPo::getJobName).collect(Collectors.toList())
          .contains(knowledgeSaveExtractionDto.getJobName())) {

        Integer extractionId = null;

        int extractionRes = knowledgeExtractionMapper.insert(knowledgeExtractionPo);
        if (extractionRes > 0) {
          extractionId = knowledgeExtractionPo.getExtractionId();
        }

        if (!StringUtil.isBlank(knowledgeSaveExtractionDto.getFilePath())) {
          // 查询文件,并将文件移动到目标路径下
          String targetPath = movePdfFile(knowledgeSaveExtractionDto.getFilePath(),
              knowledgeSaveExtractionDto.getSpaceId());
          // 记录文档信息
          String res = null;
          String fileName = knowledgeSaveExtractionDto.getFilePath();
          if (!StringUtil.isBlank(fileName)) {
            String fullFileName = fileName.substring(
                fileName.lastIndexOf(SpaceConstant.DIAGONAL) + SpaceConstant.REPLICA_FACTOR);
            res = fullFileName.replaceFirst(SpaceConstant.CUT_OUT, "");
          }

          KnowledgeDocumentInformationPo knowledgeDocumentInformationPo = KnowledgeDocumentInformationPo.builder()
              .extractionId(extractionId)
              .documentStatus(DocumentEnum.IN_ANALYSIS.getStatus())
              .documentName(res).build();
          int documentRes = knowledgeDocumentInformationMapper.insert(
              knowledgeDocumentInformationPo);
          Integer documentId = null;
          if (documentRes > 0) {
            documentId = knowledgeDocumentInformationPo.getDocumentId();
          }

          // 异步请求  携带唯一标识
          FileParseReq req = new FileParseReq(targetPath, SpaceConstant.PDF_INDEX, callbackUrl);
          JSONObject metadata = new JSONObject();
          metadata.put(SpaceConstant.DOCUMENT_METADATA,
              String.valueOf(documentId));  // 在 metadata 中放入键值对
          req.setMetadata(metadata);
          req.setSpaceId(knowledgeSaveExtractionDto.getSpaceId().toString());
          apiDocumentService.makeAsyncRequest(parseReq, req, documentId);
        }
      } else {
        log.error("【Duplicate task name】");
        throw ServiceExceptionUtil.exception(ErrorConstants.INSERT_EXTRACTION_JOB_SAME);
      }
    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【adding knowledge extraction tasks error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.INSERT_EXTRACTION_JOB, e);
    }

    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> updateExtractionJob(
      KnowledgeUpdateExtractionDto knowledgeUpdateExtractionDto) {
    log.info("【Start updating knowledge extraction tasks ： {} 】", knowledgeUpdateExtractionDto);
    try {
      // 查询该任务
      KnowledgeExtractionPo knowledgeExtractionPo = knowledgeExtractionMapper.selectOne(
          Wrappers.<KnowledgeExtractionPo>lambdaQuery()
              .eq(KnowledgeExtractionPo::getExtractionId, knowledgeUpdateExtractionDto.getJobId()));

      if (Objects.nonNull(knowledgeExtractionPo)) {
        List<KnowledgeExtractionPo> knowledgeExtractionPos = knowledgeExtractionMapper.selectList(
            Wrappers.<KnowledgeExtractionPo>lambdaQuery());
        if (!CollectionUtils.isEmpty(knowledgeExtractionPos)) {
          List<String> jobName = knowledgeExtractionPos.stream()
              .map(KnowledgeExtractionPo::getJobName)
              .filter(name -> !name.equals(knowledgeExtractionPo.getJobName()))
              .collect(Collectors.toList());
          if (!CollectionUtils.isEmpty(jobName) && jobName.contains(
              knowledgeUpdateExtractionDto.getJobName())) {
            log.error("【Duplicate task name】");
            throw ServiceExceptionUtil.exception(ErrorConstants.INSERT_EXTRACTION_JOB_SAME);
          }
        }
      }
      log.info("【Mysql update job info :{} info  in db 】", knowledgeUpdateExtractionDto.getJobId());

      // 执行更新操作
      int result = knowledgeExtractionMapper.updateById(KnowledgeExtractionPo.builder()
          .extractionId(knowledgeUpdateExtractionDto.getJobId())
          .jobName(knowledgeUpdateExtractionDto.getJobName())
          .spaceId(knowledgeUpdateExtractionDto.getSpaceId())
          .spaceName(knowledgeUpdateExtractionDto.getSpaceName())
          .tagNames(knowledgeUpdateExtractionDto.getTags().stream()
              .collect(Collectors.joining(SpaceConstant.TAG_SPLIT)))
          .edgeNames(knowledgeUpdateExtractionDto.getEdges().stream()
              .collect(Collectors.joining(SpaceConstant.TAG_SPLIT)))
          .updateBy(UserAuthUtil.getUserId())
          .build());
      return CommonRespDto.success(result > SpaceConstant.INDEX);

    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【updating knowledge extraction tasks error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_EXTRACTION_JOB, e);
    }

  }

  @Override
  public CommonRespDto<Boolean> deleteExtractionJob(
      KnowledgeDropExtractionDto knowledgeDropExtractionDto) {
    log.info("【Start delete knowledge extraction tasks ： {} 】",
        knowledgeDropExtractionDto.getJobId());

    try {
      // 获取所有文档
      List<KnowledgeDocumentInformationPo> documentInformations = knowledgeDocumentInformationMapper.selectList(
          Wrappers.<KnowledgeDocumentInformationPo>lambdaQuery()
              .eq(KnowledgeDocumentInformationPo::getExtractionId,
                  knowledgeDropExtractionDto.getJobId()));

      if (!CollectionUtils.isEmpty(documentInformations)) {
        documentInformations.stream().forEach(documentInformation -> {
          deleteDocument(documentInformation.getDocumentId());
        });
      }

      // 执行更新操作
      int result = knowledgeExtractionMapper.delete(Wrappers.<KnowledgeExtractionPo>lambdaQuery()
          .eq(KnowledgeExtractionPo::getExtractionId, knowledgeDropExtractionDto.getJobId()));

      return CommonRespDto.success(result > SpaceConstant.INDEX);
    } catch (Exception e) {
      log.error("【delete knowledge extraction tasks error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.DELETE_EXTRACTION_JOB, e);
    }

  }

  @Transactional
  @Override
  public CommonRespDto<Boolean> deleteDocument(Integer documentId) {
    log.info("【Start deleting knowledge document  info   ： {} 】", documentId);
    try {
      // 删除三元组信息
      log.info("【Mysql clear verification status  info :{} info  in db 】", documentId);
      // 执行更新操作
      knowledgeDocumentVerificationMapper.delete(
          Wrappers.<KnowledgeDocumentVerificationPo>lambdaQuery()
              .eq(KnowledgeDocumentVerificationPo::getDocumentId, documentId));

      //异步执行取消任务
      List<KnowledgeChunkInformationPo> knowledgeChunkInformationPos = knowledgeChunkInformationMapper.selectList(
          Wrappers.<KnowledgeChunkInformationPo>lambdaQuery()
              .eq(KnowledgeChunkInformationPo::getDocumentId, documentId));

      if (!CollectionUtils.isEmpty(knowledgeChunkInformationPos) && StringUtils.isNotEmpty(
          knowledgeChunkInformationPos.get(SpaceConstant.INDEX).getJobId())) {

        apiDocumentService.cancelTask(
            knowledgeChunkInfomationConverter.poListToDtoList(knowledgeChunkInformationPos));

      } else {
        log.info("【Start batch knowledge verification   info :{} info  in db 】", documentId);
        // 执行更新操作
        knowledgeChunkInformationMapper.delete(Wrappers.<KnowledgeChunkInformationPo>lambdaQuery()
            .eq(KnowledgeChunkInformationPo::getDocumentId, documentId));


      }

      // 更新任务时间
      KnowledgeDocumentInformationPo knowledgeDocumentInformationPo = knowledgeDocumentInformationMapper.selectOne(
          Wrappers.<KnowledgeDocumentInformationPo>lambdaQuery()
              .eq(KnowledgeDocumentInformationPo::getDocumentId, documentId));

      if (null != knowledgeDocumentInformationPo) {

        // 执行更新操作
        knowledgeExtractionMapper.updateById(KnowledgeExtractionPo
            .builder()
            .extractionId(knowledgeDocumentInformationPo.getExtractionId())
            .build()
        );
      }
      //调用任务解析接口
      apiDocumentService.delJobAsyncRequest(parseReqDel, knowledgeDocumentInformationPo.getJobId());

      int result = knowledgeDocumentInformationMapper.delete(
          Wrappers.<KnowledgeDocumentInformationPo>lambdaQuery()
              .eq(KnowledgeDocumentInformationPo::getDocumentId, documentId));
      return CommonRespDto.success(result > SpaceConstant.INDEX);
    } catch (Exception e) {
      log.error("【Start deleting knowledge document  info error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.DELETE_DOCUMENT_ERROR, e);
    }
  }

  @Override
  public CommonRespDto<PagingRespDto<KnowledgeExtractionListDto>> extractionJobList(
      KnowledgeExtractionReq knowledgeExtractionReq) {
    log.info("获取提取任务分页列表请求参数：{}",
        JSON.toJSONString(knowledgeExtractionReq, WriteMapNullValue));
    Page<KnowledgeExtractionPo> page = Page.of(knowledgeExtractionReq.getCurrent(),
        knowledgeExtractionReq.getSize());

    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();

    LambdaQueryWrapper<KnowledgeExtractionPo> lambdaQueryWrapper = Wrappers.<KnowledgeExtractionPo>lambdaQuery()
        .apply(StrUtil.isNotBlank(knowledgeExtractionReq.getJobName()), "job_name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(knowledgeExtractionReq.getJobName()) + "%")
        .in(KnowledgeExtractionPo::getCreateBy, userIdList)
        .orderByDesc(BasePo::getCreateTime);

    PagingRespDto<KnowledgeExtractionDto> pagingRespDto = knowledgeGraphExtractionConverter.pagePoToDto(
        knowledgeExtractionMapper.selectPage(page, lambdaQueryWrapper));

    List<KnowledgeExtractionListDto> knowledgeExtractionListDtos = new ArrayList<>();

    if (!StringUtil.isBlank(knowledgeExtractionReq.getJobName())
        && knowledgeExtractionReq.getJobName().contains(SpaceConstant.PERCENT)) {
      PagingRespDto<KnowledgeExtractionListDto> targetPage = new PagingRespDto<>();
      targetPage.setCurrent(pagingRespDto.getCurrent());
      targetPage.setSize(pagingRespDto.getSize());
      return CommonRespDto.success(targetPage);
    }

    List<KnowledgeExtractionDto> knowledgeExtractionDtoList = pagingRespDto.getRecords();

    if (!CollectionUtils.isEmpty(knowledgeExtractionDtoList)) {
      knowledgeExtractionListDtos = knowledgeExtractionDtoList.stream()
          .map(knowledgeExtractionDto -> {

            KnowledgeExtractionListDto knowledgeExtractionListDto = new KnowledgeExtractionListDto();

            CommonRespDto<KnowledgeBaseDetailDto> knowledgeBaseDetail = knowledgeBaseService.getKnowledgeBaseDetail(
                knowledgeExtractionDto.getSpaceId());
            KnowledgeBaseDetailDto knowledgeBaseDetailDto = knowledgeBaseDetail.getData();

            if (Objects.nonNull(knowledgeBaseDetailDto)) {
              knowledgeExtractionListDto.setSpaceId(knowledgeExtractionDto.getSpaceId());
              knowledgeExtractionListDto.setSpaceName(knowledgeBaseDetailDto.getName());
            }
            //查询 任务下包含文档数量
            knowledgeExtractionListDto.setType(knowledgeExtractionDto.getType());
            knowledgeExtractionListDto.setTags(
                !StringUtil.isBlank(knowledgeExtractionDto.getTagNames()) ? Arrays.stream(
                    knowledgeExtractionDto.getTagNames().split(SpaceConstant.TAG_SPLIT)).toList()
                    : new ArrayList<>());
            knowledgeExtractionListDto.setEdges(
                !StringUtil.isBlank(knowledgeExtractionDto.getEdgeNames()) ? Arrays.stream(
                    knowledgeExtractionDto.getEdgeNames().split(SpaceConstant.TAG_SPLIT)).toList()
                    : new ArrayList<>());

            knowledgeExtractionListDto.setJobId(knowledgeExtractionDto.getExtractionId());
            knowledgeExtractionListDto.setJobName(knowledgeExtractionDto.getJobName());
            //查询 任务下包含文档数量
            knowledgeExtractionListDto.setIncludeDocument(
                countDocument(knowledgeExtractionListDto.getJobId()).intValue());
            knowledgeExtractionListDto.setCreateTime(knowledgeExtractionDto.getUpdateTime() == null
                ? knowledgeExtractionDto.getCreateTime() : knowledgeExtractionDto.getUpdateTime());
            knowledgeExtractionListDto.setCreateBy(
                (knowledgeExtractionDto.getUpdateBy() == null ? knowledgeExtractionDto.getCreateBy()
                    : knowledgeExtractionDto.getUpdateBy()));

            //查询创建人名称
            Integer userId = knowledgeExtractionDto.getUpdateBy();
            CommonRespDto<BackendUserDto> userInfo = backendUserService.getUserInfo(userId);
            knowledgeExtractionListDto.setCreateUser(userInfo.getData().getAccount());

            return knowledgeExtractionListDto;

          }).collect(Collectors.toList());
    }

    return CommonRespDto.success(convertToIPageVO(pagingRespDto, knowledgeExtractionListDtos));
  }

  @Override
  public CommonRespDto<String> upload(MultipartFile file, Integer jobId) {
    log.info("【知识抽取上传文件原始名字：{}】", file.getOriginalFilename());

    // 通过jobId 获取空间
    KnowledgeExtractionPo knowledgeExtraction = null;

    if (Objects.nonNull(jobId)) {
      knowledgeExtraction = knowledgeExtractionMapper.selectOne(
          Wrappers.<KnowledgeExtractionPo>lambdaQuery()
              .eq(KnowledgeExtractionPo::getExtractionId, jobId));

      if (null == knowledgeExtraction) {
        log.error("【JobId does not exist:{}】", jobId);
        throw ServiceExceptionUtil.exception(ErrorConstants.JOB_EXISTS);
      }
    }
    // 设置目标路径
    String returnUrl = "";
    // 检查文件名是否包含无效的路径序列
    if (file.getOriginalFilename().contains("..")) {
      throw new IllegalArgumentException(
          "Filename contains invalid path sequence: " + file.getOriginalFilename());
    }

    // 检查文件是否为 PDF 格式
    if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
      throw new IllegalArgumentException("The file is not a PDF: " + file.getOriginalFilename());
    }

    try {

      // 判断是否有 jobId
      String targetPath = (null != knowledgeExtraction ? (url + name + SpaceConstant.TARGET
          + SpaceConstant.SPACE_NAME_FIX + knowledgeExtraction.getSpaceId()
          + SpaceConstant.DIAGONAL) :
          (url + name + SpaceConstant.TMP));

      // 创建临时上传目录
      File dir = new File(targetPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }

      String fileName = SnowflakeIdUtils.getDefaultSnowFlakeId() + SpaceConstant.SPILE
          + file.getOriginalFilename();
      // 生成文件路径
      returnUrl = targetPath + fileName;

      if (Objects.nonNull(jobId)) {
        Integer id = saveDocumentInfo(jobId, fileName);

        // 更新操作时间
        knowledgeExtractionMapper.updateById(KnowledgeExtractionPo.builder()
            .extractionId(jobId).build());
        // 异步请求  携带唯一标识
        FileParseReq req = new FileParseReq(returnUrl, SpaceConstant.PDF_INDEX, callbackUrl);
        JSONObject metadata = new JSONObject();
        metadata.put(SpaceConstant.DOCUMENT_METADATA, String.valueOf(id));  // 在 metadata 中放入键值对
        req.setMetadata(metadata);
        req.setSpaceId(knowledgeExtraction.getSpaceId().toString());
        apiDocumentService.makeAsyncRequest(parseReq, req, id);
      }

      // 创建一个新的文件
      File serverFile = new File(returnUrl);
      // 保存文件
      byte[] bytes = file.getBytes();
      Files.write(serverFile.toPath(), bytes);

      log.info("File uploaded successfully: {}", returnUrl);
      return CommonRespDto.success(returnUrl);


    } catch (ServiceException e) {
      throw e;
    } catch (Exception e) {
      log.error("【kg-webserver-web updload file error】", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPLOWD_FILE);
    }

  }

  @Override
  public CommonRespDto<Boolean> uploadFiles(MultipartFile[] files, Integer jobId) {

    KnowledgeExtractionPo knowledgeExtractionPo = getAndValidateKnowledgeExtraction(jobId);
    log.info("=============获取知识抽取信息");

    // 获取已经存在文档列表
    List<KnowledgeDocumentInformationPo> knowledgeDocumentInformationPos = knowledgeDocumentInformationMapper.selectList(
        Wrappers.<KnowledgeDocumentInformationPo>lambdaQuery()
            .eq(KnowledgeDocumentInformationPo::getExtractionId, jobId));

    //  参数校验
    try {
      validateSingleFile(files, knowledgeDocumentInformationPos);
    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    }

    log.info("=============参数校验");

    // 3. 提交到线程池处理文件上传
    log.info("=============开始上传");
    for (MultipartFile file : files) {
      uploadFile(file, jobId, knowledgeGraphExtractionConverter.poToDto(knowledgeExtractionPo));
    }

    //更新操作时间
    knowledgeExtractionMapper.updateById(KnowledgeExtractionPo.builder()
        .extractionId(jobId).build());

    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public void uploadFile(MultipartFile file, Integer jobId,
      KnowledgeExtractionDto knowledgeExtractionDto) {

    String fileName = file.getOriginalFilename();
    log.info("开始上传文件: {}", fileName);

    try {
      CommonRespDto<KnowledgeBaseDetailDto> knowledgeBaseDetail = knowledgeBaseService.getKnowledgeBaseDetail(
          knowledgeExtractionDto.getSpaceId());
      KnowledgeBaseDetailDto knowledgeBaseDetailDto = knowledgeBaseDetail.getData();

      if (null == knowledgeBaseDetailDto) {
        log.error("抽取任务绑定图空间为空");
        throw new ServiceException(ErrorConstants.UPLOWD_FILE);
      }

      // 1. 构建目标路径
      String targetPath = buildTargetPath(knowledgeExtractionDto);
      createDirectoryIfNotExists(targetPath);

      // 2. 生成文件名和完整路径
      String fullFileName =
          SnowflakeIdUtils.getDefaultSnowFlakeId() + SpaceConstant.SPILE + fileName;
      String returnUrl = targetPath + fullFileName;

      // 4. 异步写入文件
      CompletableFuture<Void> writeFileFuture = CompletableFuture.runAsync(() -> {
        try {
          writeFileInChunks(file, Paths.get(returnUrl));
        } catch (IOException e) {
          throw new CompletionException(e);
        }
      }, fileWriteExecutor);

      writeFileFuture.get(60, TimeUnit.SECONDS); // 添加超时控制
      log.info("文件上传成功: {}", returnUrl);

      // 拷贝一份到智图，测试使用 服务器地址为172.20.37.50  密码为voicecomm@123 用户为root 路径和该目录保持一直，使用scp方式拷贝
      copyFileToDemoEnvironment(returnUrl, targetPath);

      // 3. 异步保存文件元信息
      CompletableFuture.runAsync(() -> {
        if (Objects.nonNull(jobId)) {
          Integer id = saveDocumentInfo(jobId, fileName);
          // 异步请求  携带唯一标识
          FileParseReq req = new FileParseReq(returnUrl, getFileType(fileName), callbackUrl,
              SpaceConstant.SPACE_NAME_FIX + knowledgeBaseDetailDto.getId());
          JSONObject metadata = new JSONObject();
          metadata.put(SpaceConstant.DOCUMENT_METADATA, String.valueOf(id));  // 在 metadata 中放入键值对
          req.setMetadata(metadata);
          apiDocumentService.makeAsyncRequest(parseReq, req, id);
        }
      }, uploadExecutor);
      // 等待所有异步操作完成

    } catch (Exception e) {
      log.error("文件上传失败: {}", fileName, e);
      throw new ServiceException(ErrorConstants.UPLOWD_FILE);
    }

  }

  private void copyFileToDemoEnvironment(String filePath, String url) {
    String demoHost = "172.17.20.38";
    String user = "root";
    String password = "voicecomm@123";
    String demoPath = url; // 目标路径

    try {
      JSch jsch = new JSch();
      Session session = jsch.getSession(user, demoHost, 22);
      session.setPassword(password);
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();

      // 创建目录（如果不存在）
      ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
      channelExec.setCommand("mkdir -p " + demoPath);
      channelExec.connect();
      channelExec.disconnect();

      // 上传文件
      ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
      sftpChannel.connect();

      File file = new File(filePath);
      sftpChannel.put(new FileInputStream(file), demoPath + "/" + file.getName());

      sftpChannel.disconnect();
      session.disconnect();

      log.info("文件成功拷贝到演示环境: {}", demoHost);
    } catch (Exception e) {
      log.error("拷贝文件到演示环境失败: {}", e.getMessage());
    }
  }

  @Override
  public CommonRespDto<Boolean> extractDocument(KnowledgeDoExtractionDto knowledgeDoExtractionDto) {
    String entityPrompt = SpaceConstant.ENTITY_PROMPT;
    String relationPrompt = SpaceConstant.RELATION_PROMPT;
    String entityModel = SpaceConstant.DEFAULT_MODEL;
    String relationModel = SpaceConstant.DEFAULT_MODEL;
    log.info("【Start the document extraction  ： {} 】", knowledgeDoExtractionDto.getDocumentId());
    try {
      KnowledgeDocumentInformationPo knowledgeDocumentInformationPo = knowledgeDocumentInformationMapper.selectById(
          knowledgeDoExtractionDto.getDocumentId());

      if (Objects.nonNull(knowledgeDocumentInformationPo)) {
        log.info("【Start the document extraction success   ： {} 】",
            knowledgeDoExtractionDto.getDocumentId());
        KnowledgeExtractionPo knowledgeExtractionPo = knowledgeExtractionMapper.selectById(
            knowledgeDocumentInformationPo.getExtractionId());
        // 清空之前三元组信息
        log.info("【Clear the previous triplet information】");

        knowledgeDocumentVerificationMapper.delete(
            Wrappers.<KnowledgeDocumentVerificationPo>lambdaQuery()
                .eq(KnowledgeDocumentVerificationPo::getDocumentId,
                    knowledgeDocumentInformationPo.getDocumentId()));

        // 更新chunk jobid 和 chunk_status
        knowledgeChunkInformationMapper.update(Wrappers.<KnowledgeChunkInformationPo>lambdaUpdate()
            .set(KnowledgeChunkInformationPo::getJobId, 0)
            .set(KnowledgeChunkInformationPo::getChunkStatus, SpaceConstant.INDEX)
            .eq(KnowledgeChunkInformationPo::getDocumentId,
                knowledgeDoExtractionDto.getDocumentId()));

        // 更新document状态为抽取中
        knowledgeDocumentInformationMapper.update(
            Wrappers.<KnowledgeDocumentInformationPo>lambdaUpdate()
                .eq(KnowledgeDocumentInformationPo::getDocumentId,
                    knowledgeDoExtractionDto.getDocumentId())
                .set(KnowledgeDocumentInformationPo::getDocumentStatus,
                    DocumentEnum.EXTRACT.getStatus())
                .set(KnowledgeDocumentInformationPo::getAnalysis, SpaceConstant.REPLICA_FACTOR));

        if (Objects.nonNull(knowledgeExtractionPo)) {
          // 更新操作时间
          knowledgeExtractionMapper.updateById(KnowledgeExtractionPo.builder()
              .extractionId(knowledgeDocumentInformationPo.getExtractionId()).build());
          // 查询文档下所有chunk
          List<KnowledgeChunkInformationPo> knowledgeChunkInformationPos = knowledgeChunkInformationMapper.selectList(
              Wrappers.<KnowledgeChunkInformationPo>lambdaQuery()
                  .eq(KnowledgeChunkInformationPo::getDocumentId,
                      knowledgeDoExtractionDto.getDocumentId()));

          if (CollectionUtil.isNotEmpty(knowledgeChunkInformationPos)) {
            //查询是否有抽取配置

            apiDocumentService.makeAsyncExtractionRequest(
                knowledgeChunkInfomationConverter.poListToDtoList(knowledgeChunkInformationPos),
                knowledgeGraphExtractionConverter.poToDto(knowledgeExtractionPo), relationPrompt,
                entityPrompt, entityModel, relationModel);
          }
          // 更新文档状态
          knowledgeDocumentInformationMapper.updateById(KnowledgeDocumentInformationPo.builder()
              .documentId(knowledgeDoExtractionDto.getDocumentId())
              .documentStatus(DocumentEnum.EXTRACT.getStatus())
              .analysis(SpaceConstant.REPLICA_FACTOR)
              .build());

        }


      }
      return CommonRespDto.success(Boolean.TRUE);

    } catch (Exception e) {
      log.error("【Document extraction failure error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.EXTRACT_DOCUMENT, e);
    }

  }

  @Override
  public List<KnowledgeExtractionDto> getKnowledgeExtractionBySpaceId(Integer spaceId) {
    log.info("【Mysql all KnowledgeExtraction  info from  mysql 】");
    // 使用QueryWrapper构造查询条件，并指定排序规则
    LambdaQueryWrapper<KnowledgeExtractionPo> queryWrapper = Wrappers.<KnowledgeExtractionPo>lambdaQuery()
        .eq(KnowledgeExtractionPo::getSpaceId, spaceId);
    return knowledgeGraphExtractionConverter.poListToDtoList(
        knowledgeExtractionMapper.selectList(queryWrapper));
  }

  @Override
  public CommonRespDto<PagingRespDto<DocumentListDetailVO>> documentList(
      DocumentListVO documentListVO) {
    log.info("【Start get  knowledge document list  info  】");
    try {
      return getDocumentList(documentListVO);
    } catch (Exception e) {
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_DOCUMENT_INFO, e);
    }

  }

  @Override
  public Map<Integer, Integer> getJobINfoBydocument(Map<String, String> jobs,
      List<String> documentIds) {

    log.info("获取job任务信息并绑定文档id");
    Map<Integer, Integer> map = new HashMap<>();
    if (!CollectionUtils.isEmpty(documentIds)) {

      List<Integer> integerIds = documentIds.stream()
          .filter(str -> {
            try {
              Integer.parseInt(str);
              return true;
            } catch (NumberFormatException e) {
              return false;
            }
          })
          .map(Integer::parseInt)
          .collect(Collectors.toList());

      List<KnowledgeDocumentInformationPo> documentInformations = new ArrayList<KnowledgeDocumentInformationPo>();
      if (!CollectionUtils.isEmpty(integerIds)) {
        documentInformations = knowledgeDocumentInformationMapper.selectList(
            new QueryWrapper<KnowledgeDocumentInformationPo>()
                .in("document_id", integerIds)
        );
      }

      if (!CollectionUtils.isEmpty(documentInformations)) {
        for (KnowledgeDocumentInformationPo documentInformation : documentInformations) {
          map.put(documentInformation.getDocumentId(),
              documentInformation.getExtractionId());
        }
      }
    }
    return map;
  }

  @Override
  public Long totalChunkNumber(Integer documentId) {
    log.info("【Mysql get All  chunk info from  mysql : {}】", documentId);
    // 使用QueryWrapper构造查询条件，并指定排序规则
    QueryWrapper<KnowledgeChunkInformationPo> queryWrapper = Wrappers.query();
    queryWrapper.eq("document_id", documentId);
    return knowledgeChunkInformationMapper.selectCount(queryWrapper);
  }

  @Override
  public Long totalChunkStatusNumber(Integer documentId) {
    log.info("【Mysql get All  chunk info from  mysql : {}】", documentId);
    // 使用QueryWrapper构造查询条件，并指定排序规则
    QueryWrapper<KnowledgeChunkInformationPo> queryWrapper = Wrappers.query();
    queryWrapper.eq("document_id", documentId);
    queryWrapper.eq("chunk_status", SpaceConstant.REPLICA_FACTOR);
    return knowledgeChunkInformationMapper.selectCount(queryWrapper);
  }

  @Override
  public void updateDocumentStatusNoTime(Integer id, Integer status) {
    log.info("【Mysql update document info :{} info  in db 】", id);
    UpdateWrapper<KnowledgeDocumentInformationPo> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("document_id", id);
    updateWrapper.set("document_status", status);
    updateWrapper.set("analysis", SpaceConstant.REPLICA_FACTOR);
    // 执行更新操作
    knowledgeDocumentInformationMapper.update(null, updateWrapper);
  }

  @Override
  public Integer documentChunkCount(ChunkCountReq chunkCountReq) {
    log.info("【Gets the number of valid fragments for document parsing:{}】",
        chunkCountReq.getDocumentId());
    try {
      log.info("【Mysql  Gets the number of valid fragments for document parsing:{}】",
          chunkCountReq.getDocumentId());
      // 使用QueryWrapper构造查询条件，并指定排序规则
      QueryWrapper<KnowledgeChunkInformationPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("chunk_status", SpaceConstant.INDEX);
      queryWrapper.eq("document_id", chunkCountReq.getDocumentId());
      return knowledgeChunkInformationMapper.selectCount(queryWrapper).intValue();
    } catch (Exception e) {
      log.error("【 Gets the number of valid fragments for document parsing  error: {}】",
          e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFY_DATA_TOTAL_CHUNK, e);
    }


  }

  @Override
  public CommonRespDto<VerificationTotalVO> verificationTotal(
      DropStatusVerification dropStatusVerification) {
    log.info("【get total knowledge verification information:{}】",
        dropStatusVerification.getDocumentId());
    try {
      VerificationTotalVO vo = new VerificationTotalVO();
      Long total = knowledgeDocumentVerificationMapper.selectCount(
          new QueryWrapper<KnowledgeDocumentVerificationPo>().eq("document_id",
              dropStatusVerification.getDocumentId()));
      vo.setTotal(total);

      Long verification = knowledgeDocumentVerificationMapper.selectCount(
          new QueryWrapper<KnowledgeDocumentVerificationPo>().eq("document_id",
              dropStatusVerification.getDocumentId()).eq("verification_status",
              SpaceConstant.REPLICA_FACTOR));
      vo.setVerification(verification);

      Long loadedMap = knowledgeDocumentVerificationMapper.selectCount(
          new QueryWrapper<KnowledgeDocumentVerificationPo>().eq("document_id",
              dropStatusVerification.getDocumentId()).eq("verification_status",
              SpaceConstant.TWO));
      vo.setLoadedMap(loadedMap);

      return CommonRespDto.success(vo);
    } catch (Exception e) {
      log.error("【 get total knowledge verification information extraction  error: {}】",
          e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFY_DATA_TOTAL, e);
    }
  }

  @Override
  public CommonRespDto<List<String>> getTagInfo(
      VerificationTypeSelectDto verificationTypeSelectDto) {
    try {
      log.info("【Gets the subject and object types in the graph space:{}】",
          verificationTypeSelectDto.getSpaceId());
      List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePos = knowledgeGraphTagEdgeMapper.selectList(
          new QueryWrapper<KnowledgeGraphTagEdgePo>().eq("space_id",
                  verificationTypeSelectDto.getSpaceId())
              .eq("type", SpaceConstant.INDEX));
      if (!CollectionUtils.isEmpty(knowledgeGraphTagEdgePos)) {
        List<String> list = knowledgeGraphTagEdgePos.stream()
            .map(KnowledgeGraphTagEdgePo::getTagName)
            .collect(Collectors.toList());
        return CommonRespDto.success(list);
      }
    } catch (Exception e) {
      log.error("【 Gets the subject and object types in the graph space extraction  error: {}】",
          e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_SUBJECT_OBJECT_TYPE, e);
    }
    return CommonRespDto.success(new ArrayList<>());
  }

  @Override
  public CommonRespDto<DocumentConfigInfoVO> getConfig(ConfigInfoDto configInfoDto) {
    log.info("获取文档抽取配置信息{}", configInfoDto.getDocumentId());
    QueryWrapper<DocumentConfigPo> queryWrapper = Wrappers.query();
    queryWrapper.eq("document_id", configInfoDto.getDocumentId());
    DocumentConfigPo documentConfigPo = documentConfigMapper.selectOne(queryWrapper);
    DocumentConfigInfoVO documentConfigInfoVO = knowledgeDocumentConfigConverter.poToVo(
        documentConfigPo);
    return CommonRespDto.success(documentConfigInfoVO);
  }

  @Override
  public CommonRespDto<PagingRespDto<VerificationListVO>> knowledgeVerificationList(
      VerificationInfoDto verificationInfoDto) {
    log.info("【Start get  knowledge Verification  info :{} 】", verificationInfoDto.getDocumentId());
    try {

      Page<KnowledgeDocumentVerificationPo> page = new Page<>(verificationInfoDto.getCurrent(),
          verificationInfoDto.getPageSize());

      IPage<KnowledgeDocumentVerificationPo> resultPage = knowledgeDocumentVerificationMapper.selectGroupedAndSorted(
          page,
          verificationInfoDto.getDocumentId(),
          verificationInfoDto.isType() ? SpaceConstant.REPLICA_FACTOR : SpaceConstant.INDEX);

      List<VerificationListVO> verificationListVOS = new ArrayList<>();
      if (resultPage.getSize() > SpaceConstant.INDEX) {
        verificationListVOS = resultPage.getRecords().stream()
            .map(knowledgeExtraction -> {
              // 统计chunk解析数量
              VerificationListVO vo = new VerificationListVO();
              vo.setVerificationId(knowledgeExtraction.getVerificationId());
              vo.setStatus(knowledgeExtraction.getVerificationStatus());
              vo.setEdgeProperty(knowledgeExtraction.getEdgeType());
              vo.setSubjectTag(knowledgeExtraction.getSubjectTagName());
              vo.setSubjectName(knowledgeExtraction.getSubject());
              vo.setObjectTag(knowledgeExtraction.getObjectTagName());
              vo.setType(knowledgeExtraction.getType());
              vo.setObjectNameValue(knowledgeExtraction.getObject());
              //查询 任务下包含文档数量
              return vo;
            })
            .collect(Collectors.toList());
      }

      PagingRespDto pagingRespDto = new PagingRespDto();
      pagingRespDto.setCurrent(verificationInfoDto.getCurrent());
      pagingRespDto.setSize(verificationInfoDto.getPageSize());
      pagingRespDto.setRecords(verificationListVOS);
      pagingRespDto.setTotal(resultPage.getTotal());

      return CommonRespDto.success(pagingRespDto);
    } catch (Exception e) {
      log.error("【get  knowledge Verification  info extraction  error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFICATION_DOCUMENT_INFO_ERROR, e);
    }

  }

  @Override
  public CommonRespDto<EdgePropertyResVO> getEdgeTypeProperty(
      VerificationEdgeTypePropertyVO verificationEdgeTypePropertyVO) {
    log.info("【Gets relational attribute information:{}】",
        verificationEdgeTypePropertyVO.getSpaceId());
    try {
      EdgePropertyResVO edgePropertyResVO = new EdgePropertyResVO();
      List<String> edges = new ArrayList<>();
      List<PropertyInfoVO> propertyInfos = new ArrayList<>();
      QueryWrapper<KnowledgeGraphTagEdgePo> queryWrapper = Wrappers.query();
      queryWrapper.eq("space_id", verificationEdgeTypePropertyVO.getSpaceId());
      queryWrapper.eq("type", SpaceConstant.REPLICA_FACTOR);

      List<KnowledgeGraphTagEdgePo> knowledgeGraphTagEdgePos = knowledgeGraphTagEdgeMapper.selectList(
          null, queryWrapper);

      if (!CollectionUtils.isEmpty(knowledgeGraphTagEdgePos)) {
        edges = knowledgeGraphTagEdgePos.stream().map(KnowledgeGraphTagEdgePo::getTagName)
            .collect(Collectors.toList());
      }
      if (!StringUtil.isBlank(verificationEdgeTypePropertyVO.getSubjectType())) {
        propertyInfos = getEdgeTypePropertyInfo(verificationEdgeTypePropertyVO);
      }
      edgePropertyResVO.setEdges(edges);
      edgePropertyResVO.setPropertyInfos(propertyInfos);

      return CommonRespDto.success(edgePropertyResVO);
    } catch (Exception e) {
      log.error("【 Gets relational attribute information extraction  error: {}】", e.getMessage(),
          e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_EDGE_PROPERTY_INFO, e);
    }
  }

  @Override
  public List<PropertyInfoVO> getEdgeTypePropertyInfo(
      VerificationEdgeTypePropertyVO verificationTypeSelectVO) {
    log.info("【Obtain the attribute id from the ontology】",
        verificationTypeSelectVO.getSubjectType());
    List<PropertyInfoVO> propertyInfoVOS = new ArrayList<>();
    QueryWrapper<KnowledgeGraphTagEdgePo> queryWrapper = Wrappers.query();
    queryWrapper.eq("space_id", verificationTypeSelectVO.getSpaceId());
    queryWrapper.eq("type", SpaceConstant.INDEX);
    queryWrapper.eq("tag_name", verificationTypeSelectVO.getSubjectType());
    KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectOne(queryWrapper);
    if (null != mapTagEdge) {
      // 查询属性
      QueryWrapper<KnowledgeGraphTagEdgePropertyPo> tagEdgePropertyQueryWrapper = Wrappers.query();
      tagEdgePropertyQueryWrapper.eq("tag_edge_id", mapTagEdge.getTagEdgeId());
      tagEdgePropertyQueryWrapper.eq("type", mapTagEdge.getType());
      List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = knowledgeGraphTagEdgePropertyMapper.selectList(
          tagEdgePropertyQueryWrapper);
      if (!CollectionUtils.isEmpty(tagEdgeProperties)) {
        tagEdgeProperties.stream().forEach(tagEdgeProperty -> {
          PropertyInfoVO propertyInfoVO = new PropertyInfoVO();
          propertyInfoVO.setPropertyName(tagEdgeProperty.getPropertyName());
          propertyInfoVO.setPropertyType(tagEdgeProperty.getPropertyType());
          propertyInfoVO.setExtra(tagEdgeProperty.getExtra());
          propertyInfoVO.setDefaultValueAsString(tagEdgeProperty.getDefaultValue());
          propertyInfoVOS.add(propertyInfoVO);
        });
      }
    }
    return propertyInfoVOS;

  }

  @Override
  public CommonRespDto<OriginalInformationVO> originalInformation(
      OriginalInformationInfoVO originalInformationInfoVO) {
    log.info("【Start get  knowledge originalInformation   info :{} 】",
        originalInformationInfoVO.getVerificationId());
    try {
      OriginalInformationVO originalInformationVO = new OriginalInformationVO();
      // 使用QueryWrapper构造查询条件，并指定排序规则
      List<Integer> data = new ArrayList<>();
      QueryWrapper<KnowledgeDocumentVerificationPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("verification_id", originalInformationInfoVO.getVerificationId());
      KnowledgeDocumentVerificationPo documentVerification = knowledgeDocumentVerificationMapper.selectOne(
          queryWrapper);

      if (documentVerification != null) {
        QueryWrapper<KnowledgeDocumentInformationPo> documentInformationQueryWrapper = Wrappers.query();
        documentInformationQueryWrapper.eq("document_id", documentVerification.getDocumentId());
        KnowledgeDocumentInformationPo documentInformation = knowledgeDocumentInformationMapper.selectOne(
            documentInformationQueryWrapper);
        if (null != documentInformation) {
          originalInformationVO.setDocumentName(documentInformation.getDocumentName());
        }
        // 获取chunk信息

        QueryWrapper<KnowledgeChunkInformationPo> chunkQueryWrapper = Wrappers.query();
        chunkQueryWrapper.eq("chunk_id", documentVerification.getChunkId());
        KnowledgeChunkInformationPo chunkInformation = knowledgeChunkInformationMapper.selectOne(
            chunkQueryWrapper);

        if (null != chunkInformation) {
          originalInformationVO.setOriginalInfo(
              new ChunkInfoVO(chunkInformation.getChunkContent(), chunkInformation.getChunkId()));
          QueryWrapper<KnowledgeDocumentVerificationPo> query = Wrappers.query();
          query.eq("chunk_id", chunkInformation.getChunkId());
          data = knowledgeDocumentVerificationMapper.selectList(query).stream()
              .map(KnowledgeDocumentVerificationPo::getVerificationId).collect(Collectors.toList());
          originalInformationVO.setVerificationIds(data);
          // 获取上下文信息
          KnowledgeChunkInformationPo aboveChunk = getChunkInfoDetail(
              chunkInformation.getChunkIndex() - SpaceConstant.REPLICA_FACTOR,
              chunkInformation.getDocumentId());
          if (null != aboveChunk) {
            ChunkInfoVO above = new ChunkInfoVO(aboveChunk.getChunkContent(),
                aboveChunk.getChunkId());
            originalInformationVO.setAboveInfo(above);
          }
          KnowledgeChunkInformationPo belowChunkInformation = getChunkInfoDetail(
              chunkInformation.getChunkIndex() + SpaceConstant.REPLICA_FACTOR,
              chunkInformation.getDocumentId());
          if (null != belowChunkInformation) {
            ChunkInfoVO belowInfo = new ChunkInfoVO(belowChunkInformation.getChunkContent(),
                (belowChunkInformation.getChunkId()));
            originalInformationVO.setBelowInfo(belowInfo);
          }
        }
      }

      return CommonRespDto.success(originalInformationVO);
    } catch (Exception e) {
      log.error("【get  knowledge originalInformation   info extraction  error: {}】", e.getMessage(),
          e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_VERIFICATION_INFO, e);
    }
  }

  @Override
  public CommonRespDto<List<ExtractPreviewVO>> testPreview(DocumentConfigVO documentConfigVO) {
    log.info("获取文档{}抽取内容：", documentConfigVO.getDocumentId());
    List<ExtractPreviewVO> extractPreviewVOS = new ArrayList<>();
    AtomicInteger integer = new AtomicInteger(0);
    try {
      ExtractPreviewDTO extractPreviewDTO = new ExtractPreviewDTO();
      // 通过文档id获取tag edges
      QueryWrapper<KnowledgeDocumentInformationPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("document_id", documentConfigVO.getDocumentId());
      KnowledgeDocumentInformationPo documentInformation = knowledgeDocumentInformationMapper.selectOne(
          queryWrapper);

      if (null != documentInformation) {
        QueryWrapper<KnowledgeExtractionPo> extractionQueryWrapper = Wrappers.query();
        extractionQueryWrapper.eq("extraction_id", documentInformation.getExtractionId());
        KnowledgeExtractionPo knowledgeExtraction = knowledgeExtractionMapper.selectOne(
            extractionQueryWrapper);

        if (null != knowledgeExtraction) {
          //通过文档id获取chunk
          QueryWrapper<KnowledgeChunkInformationPo> chunkQueryWrapper = Wrappers.query();
          chunkQueryWrapper.eq("document_id", documentConfigVO.getDocumentId())
              .last("limit 10");  // 直接拼接 SQL 的 limit 10
          List<KnowledgeChunkInformationPo> chunkInformations = knowledgeChunkInformationMapper.selectList(
              chunkQueryWrapper);

          if (!CollectionUtils.isEmpty(chunkInformations)) {
            extractPreviewDTO.setRelations(
                Arrays.stream(knowledgeExtraction.getEdgeNames().split(",")).map(String::trim)
                    .toArray(String[]::new));
            extractPreviewDTO.setTags(
                Arrays.stream(knowledgeExtraction.getTagNames().split(",")).map(String::trim)
                    .toArray(String[]::new));
            extractPreviewDTO.setNer_prompt(documentConfigVO.getEntityPromptRequire() + "\n"
                + documentConfigVO.getEntityPromptOtherRequire() + "\n"
                + documentConfigVO.getEntityPromptOutput());
            extractPreviewDTO.setRe_prompt(documentConfigVO.getRelationPromptRequire() + "\n"
                + documentConfigVO.getRelationPromptOtherRequire() + "\n"
                + documentConfigVO.getRelationPromptOutput());
            extractPreviewDTO.setNer_model(documentConfigVO.getExtractEntityModel());
            extractPreviewDTO.setRe_model(documentConfigVO.getExtractRelationModel());
            for (KnowledgeChunkInformationPo chunkInformation : chunkInformations) {
              if (integer.get() >= SpaceConstant.SPACE_NUM) {
                break;
              }
              // 调用抽取
              extractPreviewDTO.setChunk(chunkInformation.getChunkContent());
              ExtractResultViewData extractResultViewData = apiDocumentService.getExtractResult(
                  extractPreviewDTO);
              if (null != extractResultViewData && null != extractResultViewData.getTriples()) {
                for (Triple triple : extractResultViewData.getTriples()) {
                  if (integer.get() >= SpaceConstant.SPACE_NUM) {
                    break;
                  }
                  if (null != triple.getSource_node() && null != triple.getTarget_node()) {
                    ExtractPreviewVO extractPreviewVO = new ExtractPreviewVO();
                    extractPreviewVO.setSubjectTag(triple.getSource_node().getTag());
                    extractPreviewVO.setSubjectName(triple.getSource_node().getName());

                    extractPreviewVO.setObjectTag(triple.getTarget_node().getTag());
                    extractPreviewVO.setObjectNameValue(triple.getTarget_node().getName());

                    extractPreviewVO.setChunkContent(chunkInformation.getChunkContent());
                    extractPreviewVO.setEdgeProperty(triple.getEdge_type());
                    extractPreviewVOS.add(extractPreviewVO);
                    integer.addAndGet(SpaceConstant.REPLICA_FACTOR);
                  }
                }
              }
            }

          }
        }
      }

    } catch (Exception e) {
      log.error("获取文档抽取内容失败", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.GET_DOCUMENT_PREVIEW);
    }
    return CommonRespDto.success(extractPreviewVOS);
  }

  @Override
  public CommonRespDto<Boolean> documentConfig(DocumentConfigVO documentConfigVO) {
    log.info("对文档进行抽取配置：{}", documentConfigVO.getDocumentId());
    int result = 0;
    try {

      QueryWrapper<DocumentConfigPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("document_id", documentConfigVO.getDocumentId());
      DocumentConfigPo documentConfigPo = documentConfigMapper.selectOne(queryWrapper);

      if (null == documentConfigPo) {
        DocumentConfigPo config = DocumentConfigPo.builder()
            //时间戳作为id
            .documentId(documentConfigVO.getDocumentId())
            .entityPromptRequire(documentConfigVO.getEntityPromptRequire())
            .extractEntityModel(documentConfigVO.getExtractEntityModel())
            .entityPromptOtherRequire(
                Objects.nonNull(documentConfigVO.getEntityPromptOtherRequire())
                    ? documentConfigVO.getEntityPromptOtherRequire() : "")
            .entityPromptOutput(documentConfigVO.getEntityPromptOutput())
            .relationPromptRequire(documentConfigVO.getRelationPromptRequire())
            .relationPromptOtherRequire(
                Objects.nonNull(documentConfigVO.getRelationPromptOtherRequire())
                    ? documentConfigVO.getRelationPromptOtherRequire() : "")
            .relationPromptOutput(documentConfigVO.getRelationPromptOutput())
            .extractRelationModel(documentConfigVO.getExtractRelationModel())
            .createBy(UserAuthUtil.getUserId())
            .build();
        result = documentConfigMapper.insert(config);
      } else {
        UpdateWrapper<DocumentConfigPo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("document_id", documentConfigVO.getDocumentId());
        updateWrapper.set("entity_prompt_require", documentConfigVO.getEntityPromptRequire());
        updateWrapper.set("entity_prompt_other_require",
            Objects.nonNull(documentConfigVO.getEntityPromptOtherRequire())
                ? documentConfigVO.getEntityPromptOtherRequire() : "");
        updateWrapper.set("entity_prompt_output", documentConfigVO.getEntityPromptOutput());
        updateWrapper.set("relation_prompt_require", documentConfigVO.getRelationPromptRequire());
        updateWrapper.set("relation_prompt_other_require",
            Objects.nonNull(documentConfigVO.getRelationPromptOtherRequire())
                ? documentConfigVO.getRelationPromptOtherRequire() : "");
        updateWrapper.set("relation_prompt_output", documentConfigVO.getRelationPromptOutput());
        updateWrapper.set("extract_relation_model", documentConfigVO.getExtractRelationModel());
        updateWrapper.set("update_time", LocalDateTime.now());
        updateWrapper.set("update_by", UserAuthUtil.getUserId());
        result = documentConfigMapper.update(null, updateWrapper);
      }
      return CommonRespDto.success(result == 1);
    } catch (Exception e) {
      log.error("对文档进行抽取配置失败", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.DOCUMENT_CONFIG);
    }
  }

  @Override
  public CommonRespDto<Boolean> knowledgeEntryMap(KnowledgeEntryMapVO knowledgeEntryMapVO) {
    try {
      // 获取所有校验数据,已经校验通过的数据
      QueryWrapper<KnowledgeDocumentVerificationPo> queryWrapper = Wrappers.query();
      queryWrapper.eq("document_id", knowledgeEntryMapVO.getDocumentId());
      queryWrapper.eq("verification_status", DocumentVerificationEnums.CHECKED.getStatus());
      List<KnowledgeDocumentVerificationPo> documentVerificationList = knowledgeDocumentVerificationMapper.selectList(
          queryWrapper);
      Map<String, Set<EntityInfo>> entityInfoMap = new HashMap<>();
      Map<String, Set<EntityInfo>> objectEntityInfoMap = new HashMap<>();
      Map<String, Set<VerificationEdgeInfo>> edgeInfoMap = new HashMap<>();
      Set<String> edges = new HashSet<>();
      Map<String, String> vertexEdgeMap = new HashMap<>();
      if (!CollectionUtils.isEmpty(documentVerificationList)) {
        for (KnowledgeDocumentVerificationPo documentVerification : documentVerificationList) {
          // 更新状态
          String subjectId = UniqueIDGenerator.generateUniqueID();
          String objectId = UniqueIDGenerator.generateUniqueID();
          Set<EntityInfo> subjectTagSet = entityInfoMap.get(
              documentVerification.getSubjectTagName());

          if (null != subjectTagSet && !CollectionUtils.isEmpty(subjectTagSet)) {
            if (!subjectTagSet.stream().map(EntityInfo::getEntityName).toList()
                .contains(documentVerification.getSubject())) {
              subjectTagSet.add(new EntityInfo(documentVerification.getSubject(), subjectId));
            }
          } else {
            Set<EntityInfo> entitySet = new HashSet<>();
            entitySet.add(new EntityInfo(documentVerification.getSubject(), subjectId));
            entityInfoMap.put(documentVerification.getSubjectTagName(), entitySet);
          }

          if (StringUtil.isBlank(documentVerification.getObjectTagName())) {
            // 新增本体类型实体
            addEntitySubjectType(documentVerification, knowledgeEntryMapVO.getSpaceId(), subjectId);
            entityInfoMap.get(documentVerification.getSubjectTagName())
                .remove(new EntityInfo(documentVerification.getSubject(), subjectId));

          } else {
            Set<EntityInfo> objectTagSet = objectEntityInfoMap.get(
                documentVerification.getObjectTagName());
            if (null != objectTagSet && !CollectionUtils.isEmpty(objectTagSet)) {
              if (!objectTagSet.stream().map(EntityInfo::getEntityName).toList()
                  .contains(documentVerification.getObject())) {
                objectTagSet.add(new EntityInfo(documentVerification.getObject(), objectId));
              }
            } else {
              Set<EntityInfo> entitySet = new HashSet<>();
              entitySet.add(new EntityInfo(documentVerification.getObject(), objectId));
              objectEntityInfoMap.put(documentVerification.getObjectTagName(), entitySet);
            }

            // 保存边信息
            Set<VerificationEdgeInfo> edgeSet = edgeInfoMap.get(documentVerification.getEdgeType());
            if (null != edgeSet && !CollectionUtils.isEmpty(edgeSet)) {
              if (!edges.contains(
                  documentVerification.getSubject() + ":" + documentVerification.getPropertyType()
                      + ":" + documentVerification.getObject())) {
                edgeSet.add(new VerificationEdgeInfo(documentVerification.getSubject(), subjectId,
                    documentVerification.getObject(), objectId));
              }
            } else {
              Set<VerificationEdgeInfo> entitySet = new HashSet<>();
              entitySet.add(new VerificationEdgeInfo(documentVerification.getSubject(), subjectId,
                  documentVerification.getObject(), objectId));
              edgeInfoMap.put(documentVerification.getEdgeType(), entitySet);
              edges.add(
                  documentVerification.getSubject() + ":" + documentVerification.getPropertyType()
                      + ":" + documentVerification.getObject());
            }

          }
        }
        boolean check = knowledgeGraphEntityManageService.checkDataLimitData(
            Long.valueOf(knowledgeEntryMapVO.getSpaceId()),
            (edgeInfoMap.size() + objectEntityInfoMap.size() + edgeInfoMap.size()));
        if (!check) {
          log.error("【空间数据已达上限，如图失败！】");
          throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_ERROR);
        }
        // 更新状态
        for (KnowledgeDocumentVerificationPo documentVerification : documentVerificationList) {
          // 更新状态

          UpdateWrapper<KnowledgeDocumentVerificationPo> updateWrapper = new UpdateWrapper<>();
          updateWrapper.eq("verification_id", documentVerification.getVerificationId());
          updateWrapper.set("verification_status", SpaceConstant.TWO);
          // 执行更新操作
          knowledgeDocumentVerificationMapper.update(null, updateWrapper);

        }

        // 新增实体
        saveVerificationEntity(knowledgeEntryMapVO.getSpaceId(), entityInfoMap, objectEntityInfoMap,
            edgeInfoMap, vertexEdgeMap);

        log.info("【batch save entity success for space: {}】", knowledgeEntryMapVO.getSpaceId());
        // 新增边
        saveVerificationEdge(Long.valueOf(knowledgeEntryMapVO.getSpaceId()), edgeInfoMap,
            vertexEdgeMap);
        log.info("【batch save edge success for space: {}】", knowledgeEntryMapVO.getSpaceId());

        graphVertexService.executeTheTask(
            SpaceConstant.SPACE_NAME_FIX + knowledgeEntryMapVO.getSpaceId());
      }

      return CommonRespDto.success(true);
    } catch (ServiceException e) {
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFY_DATA_GRAPH, e);
    } catch (Exception e) {
      log.error("【 Verify data into the graph extraction  error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.VERIFY_DATA_GRAPH, e);
    }
  }

  @Override
  public CommonRespDto<Boolean> updateVerification(UpdateVerificationVO verificationInfoVO) {
    log.info("【update knowledge verification information:{}】",
        verificationInfoVO.getVerificationId());
    // 校验类型和值是否符合类型
    if (StringUtil.isBlank(verificationInfoVO.getObjectTag())) {
      try {
        if (!PropertyValidator.validateProperty(verificationInfoVO.getPropertyType(),
            verificationInfoVO.getObjectNameValue())) {
          log.error("【 update knowledge verification information extraction  error】");
          throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
        }
      } catch (ServiceException e) {
        return CommonRespDto.error(e.getMessage());
      } catch (Exception e) {
        log.error("【 update knowledge verification information extraction  error】");
        throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
      }
    }
    try {
      // 获取标签和边的信息
      List<String> tags = getTagInfo(
          new VerificationTypeSelectDto(verificationInfoVO.getSpaceId())).getData();

      List<String> edges = getedgesInfo(verificationInfoVO.getSpaceId());

      // 校验 SubjectTag 是否有效
      if (CollectionUtils.isEmpty(tags) || !tags.contains(verificationInfoVO.getSubjectTag())) {
        log.error(
            "【Subject type/object type and graph space do not match the current relationship error】");
        throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_KNOWLEDGE_ERROR);
      }

      // 如果 ObjectTag 不为空，进行更多校验
      if (!StringUtil.isBlank(verificationInfoVO.getObjectTag())) {
        // 校验边是否存在
        if (CollectionUtils.isEmpty(edges) || !edges.contains(
            verificationInfoVO.getEdgeProperty())) {
          log.error("【Edge property is invalid or edges list is empty】");
          throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_EDGE_ERROR);
        }

        // 校验 ObjectTag 是否有效
        if (!tags.contains(verificationInfoVO.getObjectTag())) {
          log.error("【Object tag is invalid or not found in tags】");
          throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_KNOWLEDGE_ERROR);
        }
      } else if (CollectionUtils.isEmpty(edges) && StringUtil.isBlank(
          verificationInfoVO.getObjectTag())) {
        // 校验边的属性
        List<PropertyInfoVO> properties = getEdgeTypePropertyInfo(
            new VerificationEdgeTypePropertyVO(verificationInfoVO.getSpaceId(),
                verificationInfoVO.getSubjectTag()));

        if (CollectionUtils.isEmpty(properties) ||
            properties.stream()
                .noneMatch(p -> p.getPropertyName().equals(verificationInfoVO.getPropertyType()))) {
          log.error("【Edge type property is invalid or not found】");
          throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_EDGE_ERROR);
        }
      }

      // 判断主体名称和客体名称不能相同
      if (!StringUtil.isBlank(verificationInfoVO.getObjectTag()) && !StringUtil.isBlank(
          verificationInfoVO.getSubjectTag())) {
        if (verificationInfoVO.getObjectTag().equals(verificationInfoVO.getSubjectTag())
            && verificationInfoVO.getSubjectName()
            .equals(verificationInfoVO.getObjectNameValue())) {
          log.error("【主体名称和客体名称不能相同】");
          throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_SAVE_NAME);
        }
      }

      // 更新验证信息
      UpdateWrapper<KnowledgeDocumentVerificationPo> updateWrapper = new UpdateWrapper<>();
      updateWrapper.eq("verification_id", verificationInfoVO.getVerificationId());
//        updateWrapper.set("verification_status", DocumentVerificationEnums.ORIGINAL_STATE.getStatus());
      updateWrapper.set("subject", verificationInfoVO.getSubjectName());
      updateWrapper.set("subject_tag_name", verificationInfoVO.getSubjectTag());
      updateWrapper.set("type", verificationInfoVO.getType());
      updateWrapper.set("edge_type", verificationInfoVO.getEdgeProperty());
      updateWrapper.set("property_type", verificationInfoVO.getPropertyType());
      updateWrapper.set("object", verificationInfoVO.getObjectNameValue());
      updateWrapper.set("object_tag_name", verificationInfoVO.getObjectTag());
      knowledgeDocumentVerificationMapper.update(null, updateWrapper);
    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【 The data type is not in range  error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_KNOWLEDGE_INFO, e);
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> deleteVerification(DropVerificationVO updateVerificationVO) {
    log.info("【Start batch knowledge delete  】");
    if (CollectionUtil.isNotEmpty(updateVerificationVO.getVerificationId())) {
      try {
        int result = knowledgeDocumentVerificationMapper.delete(
            Wrappers.<KnowledgeDocumentVerificationPo>lambdaQuery()
                .in(KnowledgeDocumentVerificationPo::getVerificationId,
                    updateVerificationVO.getVerificationId()));
        return CommonRespDto.success(result > SpaceConstant.INDEX);
      } catch (Exception e) {
        log.error("【  batch knowledge delete extraction  error: {}】", e.getMessage(), e);
        throw ServiceExceptionUtil.exception(ErrorConstants.BATCH_VERIFICATION_DELETE_INFO, e);
      }
    }
    return CommonRespDto.success(Boolean.TRUE);

  }

  @Override
  public CommonRespDto<Boolean> insertVerification(InsertVerificationVO insertVerificationVO) {
    log.info("【Added knowledge verification information:{}】", insertVerificationVO.getDocumentId());
    try {
      if (!PropertyValidator.validateProperty(insertVerificationVO.getPropertyType(),
          insertVerificationVO.getObjectNameValue())) {
        log.error("【 update knowledge verification information extraction  error】");
        throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
      }

    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【 update knowledge verification information extraction  error】");
      throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
    }

    try {
      // 获取标签和边的信息
      List<String> tags = getTagInfo(
          new VerificationTypeSelectDto(insertVerificationVO.getSpaceId())).getData();
      List<String> edges = getedgesInfo(insertVerificationVO.getSpaceId());

      // 校验 SubjectTag 是否有效
      if (CollectionUtils.isEmpty(tags) || !tags.contains(insertVerificationVO.getSubjectTag())) {
        log.error(
            "【Subject type/object type and graph space do not match the current relationship error】");
        throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_KNOWLEDGE_ERROR);
      }

      // 如果 ObjectTag 不为空，进行更多校验
      if (!StringUtil.isBlank(insertVerificationVO.getObjectTag())) {
        // 校验边是否存在
        if (CollectionUtils.isEmpty(edges) || !edges.contains(
            insertVerificationVO.getEdgeProperty())) {
          log.error("【Edge property is invalid or edges list is empty】");
          throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_EDGE_ERROR);
        }

        // 校验 ObjectTag 是否有效
        if (!tags.contains(insertVerificationVO.getObjectTag())) {
          log.error("【Object tag is invalid or not found in tags】");
          throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_KNOWLEDGE_ERROR);
        } else if (CollectionUtils.isEmpty(edges) && StringUtil.isBlank(
            insertVerificationVO.getObjectTag())) {
          // 校验边的属性
          List<PropertyInfoVO> properties = getEdgeTypePropertyInfo(
              new VerificationEdgeTypePropertyVO(insertVerificationVO.getSpaceId(),
                  insertVerificationVO.getSubjectTag()));

          if (CollectionUtils.isEmpty(properties) ||
              properties.stream().noneMatch(
                  p -> p.getPropertyName().equals(insertVerificationVO.getPropertyType()))) {
            log.error("【Edge type property is invalid or not found】");
            throw ServiceExceptionUtil.exception(ErrorConstants.UPDATE_EDGE_ERROR);
          }
        }

        // 判断主体名称和客体名称不能相同
        if (!StringUtil.isBlank(insertVerificationVO.getObjectTag()) && !StringUtil.isBlank(
            insertVerificationVO.getSubjectTag())) {
          if (insertVerificationVO.getObjectTag().equals(insertVerificationVO.getSubjectTag())
              && insertVerificationVO.getSubjectName()
              .equals(insertVerificationVO.getObjectNameValue())) {
            log.error("【主体名称和客体名称不能相同】");
            throw ServiceExceptionUtil.exception(ErrorConstants.ENTITY_SAVE_NAME);
          }
        }

      }
      KnowledgeDocumentVerificationPo documentVerification = KnowledgeDocumentVerificationPo.builder()
          .chunkId(insertVerificationVO.getChunkId())
          .documentId(insertVerificationVO.getDocumentId())
          .verificationStatus(DocumentVerificationEnums.ORIGINAL_STATE.getStatus())
          .subject(insertVerificationVO.getSubjectName())
          .subjectTagName(insertVerificationVO.getSubjectTag())
          .edgeType(insertVerificationVO.getEdgeProperty())
          .type(insertVerificationVO.getType())
          .object(insertVerificationVO.getObjectNameValue())
          .propertyType(insertVerificationVO.getPropertyType())
          .objectTagName(insertVerificationVO.getObjectTag())
          .createBy(UserAuthUtil.getUserId())
          .build();
      // 新增
      knowledgeDocumentVerificationMapper.insert(documentVerification);

    } catch (ServiceException e) {
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("【 Added knowledge verification information extraction  error: {}】", e.getMessage(),
          e);
      throw ServiceExceptionUtil.exception(ErrorConstants.ADD_KNOWLEDGE_INFO, e);
    }

    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Boolean> verificationKnowledge(
      VerificationKnowledgeVO verificationKnowledgeVO) {

    log.info("【Start batch knowledge verification  】");
    try {
// 通过图空间获取到本体和关系信息
      List<String> tags = getTagInfo(
          new VerificationTypeSelectDto(verificationKnowledgeVO.getSpaceId())).getData();
      List<String> edges = getedgesInfo(verificationKnowledgeVO.getSpaceId());

      if (!CollectionUtils.isEmpty(tags) && !CollectionUtils.isEmpty(edges)) {
        log.info("【Start batch knowledge verification】");
        // 获取所有校验数据
        List<VerificationKnowledge> verificationKnowledges = verificationKnowledgeVO.getVerificationKnowledges();
        if (!CollectionUtils.isEmpty(verificationKnowledges)) {
          verificationKnowledges.forEach(verificationKnowledge -> {

            QueryWrapper<KnowledgeDocumentVerificationPo> verificationQueryWrapper = Wrappers.query();
            verificationQueryWrapper.eq("verification_id",
                verificationKnowledge.getVerificationId());

            KnowledgeDocumentVerificationPo documentVerification = knowledgeDocumentVerificationMapper.selectOne(
                verificationQueryWrapper);

            boolean isValid = Optional.ofNullable(documentVerification)
                .map(dv -> isTagValid(dv, tags, edges, verificationKnowledgeVO.getSpaceId()))
                .orElse(false);

            UpdateWrapper<KnowledgeDocumentVerificationPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("verification_id", verificationKnowledge.getVerificationId());
            updateWrapper.set("verification_status",
                isValid ? DocumentVerificationEnums.CHECKED.getStatus()
                    : DocumentVerificationEnums.ORIGINAL_STATE.getStatus());

            // 执行更新操作
            knowledgeDocumentVerificationMapper.update(null, updateWrapper);
          });
        } else {
          verificationKnowledges.forEach(this::updateVerificationFailStatus);
        }
      } else {
        verificationKnowledgeVO.getVerificationKnowledges()
            .forEach(this::updateVerificationFailStatus);
      }

      return CommonRespDto.success(Boolean.TRUE);
    } catch (Exception e) {
      log.error("【Start batch knowledge verification extraction  error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.BATCH_VERIFICATION_INFO, e);
    }
  }

  @Override
  public CommonRespDto<Boolean> dropStatus(
      StatusVerificationClear statusVerificationClear) {
    log.info("【Start drop  knowledge Verification status  info  】");
    try {
      if (!CollectionUtils.isEmpty(statusVerificationClear.getVerificationIds())) {
        statusVerificationClear.getVerificationIds().stream().forEach(verificationId -> {
          log.info("【Mysql update verification status  info :{} info  in db 】", verificationId);
          UpdateWrapper<KnowledgeDocumentVerificationPo> updateWrapper = new UpdateWrapper<>();
          updateWrapper.eq("verification_id", verificationId);
          updateWrapper.set("verification_status",
              DocumentVerificationEnums.ORIGINAL_STATE.getStatus());
          // 执行更新操作
          knowledgeDocumentVerificationMapper.update(null, updateWrapper);
        });

      }
      return CommonRespDto.success(Boolean.TRUE);
    } catch (Exception e) {
      log.error("【drop  knowledge Verification  status info extraction  error: {}】", e.getMessage(),
          e);
      throw ServiceExceptionUtil.exception(ErrorConstants.DROP_VERIFICATION_STATUS, e);
    }
  }

  private void updateVerificationFailStatus(VerificationKnowledge verificationKnowledge) {
    log.info("【Start batch knowledge verification   info :{} info  in db 】",
        verificationKnowledge.getVerificationId());
    UpdateWrapper<KnowledgeDocumentVerificationPo> updateWrapper = new UpdateWrapper<>();
    updateWrapper.eq("verification_id", verificationKnowledge.getVerificationId());
    updateWrapper.set("verification_status", DocumentVerificationEnums.ORIGINAL_STATE.getStatus());
    // 执行更新操作
    knowledgeDocumentVerificationMapper.update(null, updateWrapper);
  }

  public List<String> getedgesInfo(Integer spaceId) {
    log.info("【Gets the subject and object types in the graph space:{}】", spaceId);
    QueryWrapper<KnowledgeGraphTagEdgePo> queryWrapper = Wrappers.query();
    queryWrapper.eq("space_id", spaceId);
    queryWrapper.eq("type", SpaceConstant.REPLICA_FACTOR);
    // 执行更新操作
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(null,
        queryWrapper);
    if (!CollectionUtils.isEmpty(mapTagEdges)) {
      return mapTagEdges.stream().map(KnowledgeGraphTagEdgePo::getTagName)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * 验证标签和边是否有效
   */
  private boolean isTagValid(KnowledgeDocumentVerificationPo documentVerification,
      List<String> tags, List<String> edges, Integer spaceId) {
    if (tags.contains(documentVerification.getSubjectTagName())) {
      if (!StringUtil.isBlank(documentVerification.getObjectTagName())) {
        return tags.contains(documentVerification.getObjectTagName()) &&
            edges.contains(documentVerification.getEdgeType());
      } else {
        List<PropertyInfoVO> properties = getEdgeTypePropertyInfo(
            new VerificationEdgeTypePropertyVO(spaceId, documentVerification.getSubjectTagName()));

        return !CollectionUtils.isEmpty(properties) &&
            properties.stream().map(PropertyInfoVO::getPropertyName).collect(Collectors.toList())
                .contains(documentVerification.getEdgeType());
      }
    }
    return false;
  }

  /**
   * 批量新增边
   *
   * @param spaceId
   * @param edgeInfoMap
   */
  private void saveVerificationEdge(Long spaceId,
      Map<String, Set<VerificationEdgeInfo>> edgeInfoMap, Map<String, String> vertexEdgeMap) {
    log.info("【start batch save relation success for space: {}】", spaceId);
    for (Map.Entry<String, Set<VerificationEdgeInfo>> entry : edgeInfoMap.entrySet()) {
      StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
      entry.getValue().stream().forEach(e -> {
        e.setSubjectId(vertexEdgeMap.get(e.getSubjectName()));
        e.setObjectId(vertexEdgeMap.get(e.getObjectName()));
        // 校验边是否存在
        GraphRelationDO graphRelationDO = new GraphRelationDO();
        graphRelationDO.setSubjectId(e.getSubjectId());
        graphRelationDO.setObjectId(e.getObjectId());
        graphRelationDO.setEdgeName(entry.getKey());
        graphRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + spaceId);
        ResultSet resultSet = graphEdgeMapper.getEdgeInfoExcel(graphRelationDO);
        boolean relation = false;
        Long rank = System.currentTimeMillis();
        if (!resultSet.getRows().isEmpty()) {

          // 确保所需的列都存在
          List<String> columnNames = resultSet.getColumnNames();
          if (columnNames.contains(SpaceConstant.E)) {
            // 获取各列的值
            List<ValueWrapper> es = resultSet.colValues(SpaceConstant.E);
            // 假设所有列表的长度相同（这是处理这种情况的关键假设）
            for (int i = SpaceConstant.INDEX; i < es.size(); i++) {
              boolean flag = true;
              // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
              HashMap<String, ValueWrapper> name = null;
              try {
                name = es.get(i).asMap();
              } catch (UnsupportedEncodingException ex) {
                log.error("【解析失败】", e);
                throw ServiceExceptionUtil.exception(ErrorConstants.VERIFY_DATA_GRAPH, e);
              }
              for (Map.Entry<String, ValueWrapper> entry1 : name.entrySet()) {
                if (!entry1.getValue().isNull()) {
                  flag = false;
                  break;
                }
              }
              if (flag) {
                relation = true;
              }
            }
          }
          if (!relation) {
            vectorRelationSave(spaceId, value, e, graphRelationDO, rank, entry.getKey());
          }
        } else {
          vectorRelationSave(spaceId, value, e, graphRelationDO, rank, entry.getKey());
        }
      });

    }

  }

  private void vectorRelationSave(Long spaceId, StringJoiner value, VerificationEdgeInfo e,
      GraphRelationDO graphRelationDO, Long rank, String key) {
    value.add(SpaceConstant.DOUBLE_QUOTATION_MARKS + e.getSubjectId()
        + SpaceConstant.DOUBLE_QUOTATION_MARKS
        + SpaceConstant.DIRECTION + SpaceConstant.DOUBLE_QUOTATION_MARKS + e.getObjectId()
        + SpaceConstant.DOUBLE_QUOTATION_MARKS + SpaceConstant.RANK + rank + ":"
        + SpaceConstant.BRACKET);
    graphRelationDO.setRank(String.valueOf(rank));
//    saveRelationVector(graphRelationDO, SpaceConstant.SPACE_NAME_FIX + spaceId, e.getSubjectName(),
//        e.getObjectName());
    if (!StringUtil.isBlank(value.toString())) {
      BatchVerificationRelation batchVVerificationRelation = new BatchVerificationRelation(
          SpaceConstant.SPACE_NAME_FIX + spaceId, key, value.toString());
      graphEdgeMapper.batchSaveRelation(batchVVerificationRelation);
    }
  }

  /**
   * 批量新增实体
   *
   * @param spaceId
   * @param entityInfoMap
   * @param objectEntityInfoMap
   */
  private void saveVerificationEntity(Integer spaceId, Map<String, Set<EntityInfo>> entityInfoMap,
      Map<String, Set<EntityInfo>> objectEntityInfoMap,
      Map<String, Set<VerificationEdgeInfo>> edgeInfoMap, Map<String, String> vertexEdgeMap) {
    log.info("【start batch save entity success for space: {}】", spaceId);
    saveVerificationEdgeUtil(entityInfoMap, Long.valueOf(spaceId), edgeInfoMap, vertexEdgeMap);
    saveVerificationEdgeUtil(objectEntityInfoMap, Long.valueOf(spaceId), edgeInfoMap,
        vertexEdgeMap);
  }

  private void saveVerificationEdgeUtil(Map<String, Set<EntityInfo>> entityInfoMap, Long spaceId,
      Map<String, Set<VerificationEdgeInfo>> edgeInfoMap, Map<String, String> vertexEdgeMap) {
    BatchVerificationInfo batchVerificationInfo = null;
    for (Map.Entry<String, Set<EntityInfo>> entry : entityInfoMap.entrySet()) {
      // 通过id查询
      StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
      if (!CollectionUtils.isEmpty(entry.getValue())) {
        entry.getValue().forEach(e -> {
          List<NgVertex<String>> entityInfos = graphEdgeService.getEntityInfo(
              JSON.toJSONString(e.getEntityName()), SpaceConstant.SPACE_NAME_FIX + spaceId,
              SpaceConstant.QUOTATIONMARK + entry.getKey() + SpaceConstant.QUOTATIONMARK);
          if (!CollectionUtils.isEmpty(entityInfos)
              && entityInfos.size() == SpaceConstant.REPLICA_FACTOR) {
            vertexEdgeMap.put(e.getEntityName(), entityInfos.get(SpaceConstant.INDEX).getVid());
          } else {
            value.add(
                SpaceConstant.DOUBLE_QUOTATION_MARKS +
                    e.getId() +
                    SpaceConstant.DOUBLE_QUOTATION_MARKS +
                    ":" +
                    SpaceConstant.FIX_TAG_NAME_FUSION +
                    JSON.toJSONString(e.getEntityName()) +
                    SpaceConstant.FIX_TAG_NAME_SUX_FUSION
            );
            vertexEdgeMap.put(e.getEntityName(), e.getId());
            // 向量数据库存储
//            saveVector(entry.getKey(), e, spaceId);
          }
        });
        if (!StringUtil.isBlank(value.toString())) {
          batchVerificationInfo = new BatchVerificationInfo(SpaceConstant.SPACE_NAME_FIX + spaceId,
              SpaceConstant.QUOTATIONMARK + entry.getKey()
                  + SpaceConstant.QUOTATIONMARK, value.toString());
          log.info("新增实体信息：【{}】", batchVerificationInfo);
          graphVertexMapper.batchSaveEntityVerification(batchVerificationInfo);
        }

      }
    }
  }


  /**
   * 新增带属性实体
   *
   * @param documentVerification
   * @param spaceId
   */
  private void addEntitySubjectType(KnowledgeDocumentVerificationPo documentVerification,
      Integer spaceId, String subjectId) {
    log.info("【Added entities with attribute values   ： {} 】", documentVerification.getSubject());
    StringJoiner tag = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    tag.add(SpaceConstant.NAME + SpaceConstant.TAG_SPACE);
    value.add(JSON.toJSONString(documentVerification.getSubject()));
    if (!StringUtil.isBlank(documentVerification.getEdgeType()) && !StringUtil.isBlank(
        documentVerification.getPropertyType())) {
      tag.add(SpaceConstant.QUOTATIONMARK + documentVerification.getEdgeType()
          + SpaceConstant.QUOTATIONMARK);
      value.add(
          knowledgeGraphEntityManageService.processData(documentVerification.getPropertyType(),
              documentVerification.getObject()));
    }

    List<NgVertex<String>> entityInfos = graphEdgeService.getEntityInfo(
        JSON.toJSONString(documentVerification.getSubject()),
        SpaceConstant.SPACE_NAME_FIX + spaceId,
        SpaceConstant.QUOTATIONMARK + documentVerification.getSubjectTagName()
            + SpaceConstant.QUOTATIONMARK);
    if (!CollectionUtils.isEmpty(entityInfos) && entityInfos.size() == 1) {
      subjectId = entityInfos.get(SpaceConstant.INDEX).getVid();
    }
    VerificationInfo verificationInfo =
        new VerificationInfo(SpaceConstant.SPACE_NAME_FIX + spaceId,
            documentVerification.getSubjectTagName(), tag.toString(), subjectId, value.toString());
    // 新增实体
    graphVertexMapper.saveEntityVerification(verificationInfo);

//    saveVector(documentVerification, spaceId, subjectId);

  }

  private KnowledgeChunkInformationPo getChunkInfoDetail(int i, Integer documentId) {
    log.info("【Mysql get All  ChunkInformation info from  mysql : {}】", documentId);
    // 使用QueryWrapper构造查询条件，并指定排序规则
    QueryWrapper<KnowledgeChunkInformationPo> queryWrapper = Wrappers.query();
    queryWrapper.eq("document_id", documentId);
    queryWrapper.eq("chunk_index", i);
    List<KnowledgeChunkInformationPo> chunkInformations = knowledgeChunkInformationMapper.selectList(
        queryWrapper);
    if (!CollectionUtils.isEmpty(chunkInformations)) {
      return chunkInformations.get(SpaceConstant.INDEX);
    }
    return null;
  }


  private CommonRespDto<PagingRespDto<DocumentListDetailVO>> getDocumentList(
      DocumentListVO documentListVO) {
    log.info("【Mysql get All  document info from  mysql : {}】", documentListVO.getJobId());
    // 使用QueryWrapper构造查询条件，并指定排序规则
    QueryWrapper<KnowledgeDocumentInformationPo> queryWrapper = Wrappers.query();
    queryWrapper.orderByAsc("update_time");

    QueryWrapper<KnowledgeDocumentInformationPo> queryWrapperTmp = Wrappers.query();
    queryWrapperTmp.eq("extraction_id", documentListVO.getJobId());
//        queryWrapper.orderByDesc("created_time");
    queryWrapperTmp.orderByAsc("update_time");

    Page<KnowledgeDocumentInformationPo> pageTmp = Page.of(documentListVO.getCurrent(),
        documentListVO.getPageSize());

    List<KnowledgeDocumentInformationPo> documentInformationPages = knowledgeDocumentInformationMapper.selectList(
        queryWrapper);

    Page<KnowledgeDocumentInformationPo> documentInformationPagesTmp = knowledgeDocumentInformationMapper.selectPage(
        pageTmp, queryWrapperTmp);

    List<DocumentListDetailVO> documentListDetailVOS = new ArrayList<>();
    List<DocumentListDetailVO> result = new ArrayList<>();

    QueryWrapper<KnowledgeDocumentInformationPo> queryWrapperLineUp = Wrappers.query();
    queryWrapperLineUp.eq("document_status", DocumentEnum.EXTRACT.getStatus());
    queryWrapperLineUp.orderByAsc("update_time");
    List<KnowledgeDocumentInformationPo> documentInformationLineUp = knowledgeDocumentInformationMapper.selectList(
        queryWrapperLineUp);

    JobLineUpVO jobLineUpVO = parseLineUp(documentListVO.getJobId());

    if (!documentInformationPages.isEmpty()) {
      documentListDetailVOS = documentInformationPages.stream()
          .map(knowledgeExtraction -> {
            // 统计chunk解析数量
            DocumentListDetailVO documentListDetailVO = new DocumentListDetailVO();
            documentListDetailVO.setDocumentId(String.valueOf(knowledgeExtraction.getDocumentId()));
            documentListDetailVO.setDocumentName(knowledgeExtraction.getDocumentName());
            documentListDetailVO.setUpdateTime(DateUtil.date(knowledgeExtraction.getUpdateTime()));
            documentListDetailVO.setDocumentStatus(knowledgeExtraction.getDocumentStatus());
            documentListDetailVO.setCreateTime(DateUtil.date(knowledgeExtraction.getCreateTime()));
            documentListDetailVO.setDocumentTotal(knowledgeExtraction.getChunkSize());
            documentListDetailVO.setExtractionId(knowledgeExtraction.getExtractionId());
            if (documentListDetailVO.getDocumentStatus() == SpaceConstant.INDEX) {
              if (null != jobLineUpVO) {
                documentListDetailVO.setParseLineUpNumber(
                    jobLineUpVO.getParseLineUpVOList().getLineUpNumber()
                        .get(knowledgeExtraction.getDocumentId()));
              }

            }

            if (documentListDetailVO.getDocumentStatus() == SpaceConstant.THREE
                && knowledgeExtraction.getAnalysis() != SpaceConstant.INDEX) {
              Long totalChunkNumber = totalChunkNumber(knowledgeExtraction.getDocumentId());
              if (null != totalChunkNumber
                  && totalChunkNumber.intValue() == knowledgeExtraction.getChunkSize()) {
                Long analyChunkNumber = totalChunkStatusNumber(knowledgeExtraction.getDocumentId());
                if (null != analyChunkNumber && analyChunkNumber.intValue()
                    == knowledgeExtraction.getChunkSize()) {
                  documentListDetailVO.setDocumentStatus(DocumentEnum.EXTRACT_SUCCESS.getStatus());
                  // 更新数据库Documnet状态
                  updateDocumentStatusNoTime(knowledgeExtraction.getDocumentId(),
                      DocumentEnum.EXTRACT_SUCCESS.getStatus());
                } else if ((null != analyChunkNumber && analyChunkNumber.intValue()
                    < knowledgeExtraction.getChunkSize())) {
                  documentListDetailVO.setDocumentStatus(DocumentEnum.EXTRACT.getStatus());
                  // 更新数据库Documnet状态
                  documentListDetailVO.setAnalysisNumber(analyChunkNumber.intValue());
                  updateDocumentStatusNoTime(knowledgeExtraction.getDocumentId(),
                      DocumentEnum.EXTRACT.getStatus());
                  if (documentListDetailVO.getAnalysisNumber() == SpaceConstant.INDEX) {
                    List<KnowledgeDocumentInformationPo> filteredDocuments = documentInformationLineUp.stream()
                        .filter(
                            doc -> doc.getUpdateTime() != null && DateUtil.date(doc.getUpdateTime())
                                .before(DateUtil.date(knowledgeExtraction.getUpdateTime())))
                        .collect(Collectors.toList());
                    getQueueNumber(filteredDocuments, documentListDetailVO, knowledgeExtraction);
                  } else {
                    documentListDetailVO.setStatus(false);
                    documentListDetailVO.setLineUpNumber(SpaceConstant.INDEX);
                  }

                } else {
                  documentListDetailVO.setAnalysisNumber(SpaceConstant.INDEX);
                }

              }
            }
            //查询 任务下包含文档数量
            return documentListDetailVO;
          })
          .collect(Collectors.toList());
    }

    Collections.sort(documentListDetailVOS,
        Comparator.comparing(DocumentListDetailVO::getCreateTime).reversed());
    for (DocumentListDetailVO detailVO : documentListDetailVOS) {
      if (detailVO.getExtractionId().equals(documentListVO.getJobId())) {
        result.add(detailVO);
      }

    }
    return convertToIPageVODocumnet(documentInformationPagesTmp, result);
  }

  private CommonRespDto<PagingRespDto<DocumentListDetailVO>> convertToIPageVODocumnet(
      Page<KnowledgeDocumentInformationPo> documentInformationPages,
      List<DocumentListDetailVO> documentListDetailVOS) {

    PagingRespDto<DocumentListDetailVO> targetPage = new PagingRespDto<>();
    targetPage.setCurrent(documentInformationPages.getCurrent());
    targetPage.setSize(documentInformationPages.getSize());
    targetPage.setTotal(documentInformationPages.getTotal());

    targetPage.setRecords(documentListDetailVOS);
    return CommonRespDto.success(targetPage);


  }


  private void getQueueNumber(List<KnowledgeDocumentInformationPo> documentInformations,
      DocumentListDetailVO documentListDetailVO,
      KnowledgeDocumentInformationPo documentInformation) {
    try {
      // 获取所有正在抽取的文档
      AtomicInteger total = new AtomicInteger();

      if (!CollectionUtils.isEmpty(documentInformations)) {
        if (!documentInformations.get(SpaceConstant.INDEX).getDocumentId()
            .equals(documentListDetailVO.getDocumentId())) {
          documentListDetailVO.setStatus(true);
          for (int num = SpaceConstant.INDEX; num < documentInformations.size(); num++) {
            if (documentInformations.get(num).equals(documentListDetailVO.getDocumentId())) {
              break;
            }
            Long totalChunkNumber = totalChunkNumber(documentInformations.get(num).getDocumentId());
            Long analyChunkNumber = totalChunkStatusNumber(
                documentInformations.get(num).getDocumentId());
            if (totalChunkNumber.intValue() != SpaceConstant.INDEX) {
              total.addAndGet(SpaceConstant.REPLICA_FACTOR);
            }
          }
          documentListDetailVO.setLineUpNumber(total.get());
        }
      }
      documentInformations.add(documentInformation);
    } catch (Exception e) {
      log.error(
          "【 Gets whether the current task is being extracted or the number of tasks queued error: {}】",
          e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.EXTRACT_LINEUP, e);
    }
  }

  public JobLineUpVO parseLineUp(Integer jobId) {
    log.info("获取解析排队状态");
    try {
      ParseResponseDataDto parseLinUp = apiDocumentService.parseLinUp();
      if (parseLinUp == null || parseLinUp.getDocid_num().isEmpty()) {
        return null;
      }

      List<String> documentIds = new ArrayList<>(parseLinUp.getDocid_num().keySet());

      // 获取 job 任务
      Map<Integer, Integer> jobMap = getJobINfoBydocument(new HashMap<>(), documentIds);

      // 准备一个 Map，存符合条件的 documentId -> lineUpNumber
      Map<Integer, Integer> lineUpNumberMap = new HashMap<>();

      for (Map.Entry<Integer, Integer> entry : jobMap.entrySet()) {
        Integer documentId = entry.getKey();
        Integer jobIdStr = entry.getValue();

        if (jobIdStr == null) {
          log.warn("文档ID:{} 找不到对应的JobID", documentId);
          continue;
        }

        // 判断是不是当前传入的 jobId
        if (!jobId.equals(jobIdStr)) {
          continue;
        }

        Integer lineUpNumber = parseLinUp.getDocid_num().get(String.valueOf(documentId));
        if (lineUpNumber == null) {
          log.warn("文档ID:{} 找不到对应的排队数", documentId);
          continue;
        }

        lineUpNumberMap.put(documentId, lineUpNumber);
      }

      if (lineUpNumberMap.isEmpty()) {
        return null;
      }

      // 封装
      ParseLineUpVO parseLineUpVO = ParseLineUpVO.builder()
          .lineUpNumber(lineUpNumberMap)
          .build();

      JobLineUpVO jobLineUpVO = JobLineUpVO.builder()
          .parseLineUpVOList(parseLineUpVO)
          .build();

      log.info("获取到排队信息，jobId:{}，文档数量:{}", jobId, lineUpNumberMap.size());
      return jobLineUpVO;

    } catch (Exception e) {
      log.error("获取解析排队状态失败", e);
      throw ServiceExceptionUtil.exception(ErrorConstants.PARSE_LINEUP_ERROR);
    }
  }

  private String getFileType(String fileName) {
    if (fileName == null) {
      return null;
    }

    if (fileName.endsWith(".doc")) {
      return "doc";
    } else if (fileName.endsWith(".docx")) {
      return "docx";
    } else if (fileName.endsWith(".xls")) {
      return "xls";
    } else if (fileName.endsWith(".xlsx")) {
      return "xlsx";
    } else if (fileName.endsWith(".ppt")) {
      return "ppt";
    } else if (fileName.endsWith(".pptx")) {
      return "pptx";
    } else if (fileName.endsWith(".pdf")) {
      return "pdf";
    } else {
      return null; // Invalid file type
    }
  }

  private void writeFileInChunks(MultipartFile file, Path targetPath) throws IOException {
    int bufferSize = 8 * 1024 * 1024; // 8MB 缓冲区
    byte[] buffer = new byte[bufferSize];
    long totalBytes = file.getSize();
    long bytesWritten = 0;
    int lastProgress = 0;

    try (InputStream input = file.getInputStream();
        BufferedOutputStream output = new BufferedOutputStream(
            new FileOutputStream(targetPath.toFile()), bufferSize)) {

      int bytesRead;
      while ((bytesRead = input.read(buffer)) != -1) {
        output.write(buffer, 0, bytesRead);
        bytesWritten += bytesRead;

        // 进度报告
        int progress = (int) ((bytesWritten * 100) / totalBytes);
        if (progress >= lastProgress + 10) { // 每完成10%记录一次
          log.info("文件写入进度: {}%, 已写入: {}MB",
              progress, bytesWritten / (1024 * 1024));
          lastProgress = progress;
        }
      }
      output.flush();
    }
  }

  private String buildTargetPath(KnowledgeExtractionDto knowledgeExtraction) {
    if (knowledgeExtraction == null) {
      return url + name + SpaceConstant.TARGET + SpaceConstant.DIAGONAL;
    }
    return url + name + SpaceConstant.TARGET +
        SpaceConstant.SPACE_NAME_FIX +
        knowledgeExtraction.getSpaceId() +
        SpaceConstant.DIAGONAL;
  }


  // 优化目录创建方法
  private void createDirectoryIfNotExists(String targetPath) {
    Path dir = Paths.get(targetPath);
    try {
      Files.createDirectories(dir);
    } catch (IOException e) {
      log.error("创建目录失败: {}", targetPath, e);
      throw new ServiceException(ErrorConstants.CREATE_DIR_ERROR);
    }
  }

  private void validateSingleFile(MultipartFile[] files,
      List<KnowledgeDocumentInformationPo> knowledgeDocumentInformationPos) {
    for (MultipartFile file : files) {
      String fileName = file.getOriginalFilename();
      if (fileName == null) {
        throw ServiceExceptionUtil.exception(ErrorConstants.FILE_NAME_EMPTY);
      }

      log.info("文件名校验{}", file.getOriginalFilename());
      String fullFileName = fileName.substring(
          fileName.lastIndexOf(SpaceConstant.DIAGONAL) + SpaceConstant.REPLICA_FACTOR);
      String result = fullFileName.replaceFirst(SpaceConstant.CUT_OUT, "");

      if (knowledgeDocumentInformationPos.stream()
          .anyMatch(documentInformation -> documentInformation.getDocumentName().equals(result))) {
        throw ServiceExceptionUtil.exception(ErrorConstants.FILE_NAME_EXISTS);
      }

      String lowerFileName = fileName.toLowerCase();
      if (!isValidFileExtension(lowerFileName)) {
        throw ServiceExceptionUtil.exception(ErrorConstants.FILE_NAME_FORMAT);
      }

      if (file.getSize() > MAX_FILE_SIZE) {
        throw ServiceExceptionUtil.exception(ErrorConstants.FILE_ONE_SIZE);
      }

    }

  }

  private boolean isValidFileExtension(String fileName) {
    return fileName.endsWith(".doc") || fileName.endsWith(".docx")
        || fileName.endsWith(".xlsx") || fileName.endsWith(".xls")
        || fileName.endsWith(".ppt") || fileName.endsWith(".pptx")
        || fileName.endsWith(".pdf");
  }

  private KnowledgeExtractionPo getAndValidateKnowledgeExtraction(Integer jobId) {
    if (Objects.isNull(jobId)) {
      return null;
    }

    KnowledgeExtractionPo knowledgeExtractionPo = knowledgeExtractionMapper.selectById(jobId);

    if (knowledgeExtractionPo == null) {
      throw ServiceExceptionUtil.exception(ErrorConstants.JOB_EXISTS);
    }

    return knowledgeExtractionPo;
  }


  public Integer saveDocumentInfo(Integer jobId, String fileName) {
    log.info("【Start adding knowledge document  info   ： {} 】", fileName);
    try {
      String result = null;
      if (!StringUtil.isBlank(fileName)) {
        String fullFileName = fileName.substring(
            fileName.lastIndexOf(SpaceConstant.DIAGONAL) + SpaceConstant.REPLICA_FACTOR);
        result = fullFileName.replaceFirst(SpaceConstant.CUT_OUT, "");
      }

      KnowledgeDocumentInformationPo documentInformationPo = KnowledgeDocumentInformationPo.builder()
          .extractionId(Integer.valueOf(jobId))
          // 默认解析中
          .documentStatus(DocumentEnum.IN_ANALYSIS.getStatus())
          .documentName(result)
          .createBy(UserAuthUtil.getUserId())
          .build();
      knowledgeDocumentInformationMapper.insert(documentInformationPo);

      return documentInformationPo.getDocumentId();
    } catch (Exception e) {
      log.error("【Start adding knowledge document  info error: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.INSERT_DOCUMENT_ERROR, e);
    }
  }


  private PagingRespDto<KnowledgeExtractionListDto> convertToIPageVO(
      PagingRespDto<KnowledgeExtractionDto> sourcePage,
      List<KnowledgeExtractionListDto> targetList) {
    PagingRespDto<KnowledgeExtractionListDto> targetPage = new PagingRespDto<>();
    targetPage.setCurrent(sourcePage.getCurrent());
    targetPage.setSize(sourcePage.getSize());
    targetPage.setTotal(sourcePage.getTotal());
    targetPage.setRecords(targetList);
    return targetPage;
  }


  /**
   * 查询包含文档统计值
   *
   * @param jobId
   * @return
   */
  private Long countDocument(Integer jobId) {
    log.info("【Mysql get All  document info from  mysql : {}】", jobId);
    return knowledgeDocumentInformationMapper.selectCount(
        Wrappers.<KnowledgeDocumentInformationPo>lambdaQuery()
            .eq(KnowledgeDocumentInformationPo::getExtractionId, jobId));

  }


  /**
   * @param filePath
   */
  private String movePdfFile(String filePath, Integer spaceId) {
    try {
      // 检查文件是否存在
      File file = new File(filePath);
      if (!file.exists() || !file.isFile()) {
        log.error("【File does not exist: {}】", filePath);
        throw new IOException("File does not exist: " + filePath);
      }
      // 检查文件是否为PDF格式
      if (!filePath.toLowerCase().endsWith(".pdf")) {
        log.error("【The file is not a PDF】", filePath);
        throw new IOException("The file is not a PDF: " + filePath);
      }
      // 创建目标目录（如果不存在）
      File targetDirectory = new File(
          url + name + SpaceConstant.TARGET + SpaceConstant.SPACE_NAME_FIX + spaceId
              + SpaceConstant.DIAGONAL);
      if (!targetDirectory.exists()) {
        targetDirectory.mkdirs();
      }
      // 生成目标文件路径
      Path targetPath = new File(
          url + name + SpaceConstant.TARGET + SpaceConstant.SPACE_NAME_FIX + spaceId
              + SpaceConstant.DIAGONAL, file.getName()).toPath();
      // 移动文件
      Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
      return targetPath.toString();
    } catch (IOException e) {
      log.error("【Move temporary failure: {}】", e.getMessage(), e);
      throw ServiceExceptionUtil.exception(ErrorConstants.MOVE_FILE_ERROR, e);
    }


  }


}
