package cn.voicecomm.ai.voicesagex.console.knowledge.service.knowledge.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ExcelExportService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphRelationDataService;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceException;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ServiceExceptionUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto.Type;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEdgeDropDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphEntityRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO.GraphRelationEntityDO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Ralation;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.EdgeDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.ImportHeadDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.RelationVectorDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveEntityDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveRelationDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.dto.vector.SaveTagInfoDTO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.BaseExport;
import cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.EntityExport;
import cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.RelationExport;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportAllDataVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportDataVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityPropertiesVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityTagVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalEdgeVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.converter.KnowledgeGraphTagEdgeConverter;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgePropertyMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.EntityImportVoked;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.MessageHandler;
import cn.voicecomm.ai.voicesagex.console.knowledge.mapper.GraphEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphEdgeService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphTagEdgeManageService;
import cn.voicecomm.ai.voicesagex.console.knowledge.service.nebula.GraphVertexService;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.DateGraphUtil;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.MD5Util;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.PropertyValidator;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.SnowflakeIdUtils;
import cn.voicecomm.ai.voicesagex.console.knowledge.util.UniqueIDGenerator;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.Entity;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePo;
import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.analysis.ExcelReadExecutor;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.nebula.contrib.ngbatis.models.data.NgEdge;
import org.nebula.contrib.ngbatis.models.data.NgVertex;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelExportServiceImpl implements ExcelExportService {

  private final KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper;

  private final KnowledgeGraphTagEdgeConverter knowledgeGraphTagEdgeConverter;

  private final KnowledgeGraphTagEdgePropertyMapper knowledgeGraphTagEdgePropertyMapper;

  private final KnowledgeGraphEntityManageServiceImpl knowledgeGraphEntityManageService;

  private final MessageHandler messageHandler;

  private final GraphVertexService graphVertexService;

  private final GraphEdgeService graphEdgeService;

  private final KnowledgeGraphTagEdgeService knowledgeGraphTagEdgeService;

  private final GraphEdgeMapper graphEdgeMapper;

//  private final ApiVectorQuantityService apiVectorQuantityService;

  private final KnowledgeGraphRelationDataService knowledgeGraphRelationDataService;

  private final GraphTagEdgeManageService graphTagEdgeManageService;

  @Value("${file.export}")
  private String fileExport;

  @Value("${entity.save.number}")
  private Integer number;

  static List<String> list = new ArrayList<>(Arrays.asList("image", "audio", "video", "otherFile"));


  @Override
  public List<BaseExport> getData(ExportAllDataVO exportDataVO) {
    log.info("【Process Kg-webserver-web export data space :  {} tag : {}】",
        exportDataVO.getSpaceId(), exportDataVO.getTagEdgeName());
    return exportDataVO.getType() == SpaceConstant.INDEX ? getEntityData(exportDataVO)
        : getRelationData(exportDataVO);
  }

  @Override
  public List<BaseExport> getSelectData(ExportDataVO exportDataVO) {
    return exportDataVO.getType() == SpaceConstant.INDEX ? getSelectEntityData(exportDataVO)
        : getSelectRelationData(exportDataVO);
  }


  private List<BaseExport> getSelectRelationData(ExportDataVO exportDataVO) {
    log.info("【Select Export relation data: {} 】", exportDataVO.getSpaceId());
    List<BaseExport> baseExports = new ArrayList<>();
    Map<String, EntityTagVO> entityMap = new HashMap<>();
    Set<String> entityList = new HashSet<>();
    List<EdgeListVO> edgeListVOS = new ArrayList<>();
    Set<String> dynamicKeys = new HashSet<>();

    exportDataVO.getEntityRelationExportList().forEach(entityRelationExport -> {
      GraphRelationDO graphRelationDO = new GraphRelationDO();
      graphRelationDO.setEdgeName(entityRelationExport.getEdgeName());
      graphRelationDO.setSpaceId(SpaceConstant.SPACE_NAME_FIX + exportDataVO.getSpaceId());
      graphRelationDO.setSubjectId(entityRelationExport.getSubjectId());
      graphRelationDO.setObjectId(entityRelationExport.getObjectId());
      graphRelationDO.setRank(SpaceConstant.RANK + entityRelationExport.getRank());
      NgEdge<String> ngEdge = graphEdgeMapper.getEdgeInfo(graphRelationDO);
      if (ngEdge != null) {
        EdgeListVO edgeListVO = new EdgeListVO();
        edgeListVO.setSubjectId(ngEdge.getSrcID());
        edgeListVO.setObjectId(ngEdge.getDstID());
        edgeListVO.setEdgeName(ngEdge.getEdgeName());
        // 查询本体id
        entityList.add(String.valueOf(edgeListVO.getSubjectId()));
        entityList.add(String.valueOf(edgeListVO.getObjectId()));

        if (CollUtil.isNotEmpty(ngEdge.getProperties())) {
          Map<String, Object> dynamicProperties = new LinkedHashMap<>();
          Map<String, Object> properties = ngEdge.getProperties();
          for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (!entry.getKey().equals(SpaceConstant.NAME)) {
              dynamicProperties.put(entry.getKey(), entry.getValue());
              dynamicKeys.add(entry.getKey());
            }

          }

          edgeListVO.setDynamicProperties(dynamicProperties);
        }
        exportDataVO.setDynamicKeys(new ArrayList<>(dynamicKeys));
        edgeListVOS.add(edgeListVO);
      }
    });
    if (CollUtil.isNotEmpty(edgeListVOS)) {
      // 解析查询数据
      processEntityData(entityList, SpaceConstant.SPACE_NAME_FIX + exportDataVO.getSpaceId(),
          entityMap);
      // 设置 entityName 和tagName
      edgeListVOS.stream().forEach(v -> {
        List<Long> tagId = new ArrayList<>();
        List<Long> objectTagId = new ArrayList<>();
        EntityTagVO subjuctEntity = entityMap.get(v.getSubjectId());
        v.setSubjectTagName(subjuctEntity.getTagName());
        v.setSubjectName(subjuctEntity.getEntityName());
        if (!StringUtil.isBlank(v.getSubjectTagName())) {
          for (String s : v.getSubjectTagName().split(SpaceConstant.TAG_SPLIT_INDEX)) {
            tagId.add(Long.valueOf(
                knowledgeGraphTagEdgeService.getTagInfo(exportDataVO.getSpaceId(), s)
                    .getTagEdgeId()));
          }
          v.setSubjectTagId(tagId);
        }
        EntityTagVO objectEntity = entityMap.get(v.getObjectId());
        v.setObjectTagName(objectEntity.getTagName());
        v.setObjectName(objectEntity.getEntityName());
        if (!StringUtil.isBlank(v.getObjectTagName())) {
          for (String s : v.getObjectTagName().split(SpaceConstant.TAG_SPLIT_INDEX)) {
            objectTagId.add(Long.valueOf(
                knowledgeGraphTagEdgeService.getTagInfo(exportDataVO.getSpaceId(), s)
                    .getTagEdgeId()));
          }
          v.setObjectTagId(objectTagId);
        }
        RelationExport relationExport = new RelationExport(subjuctEntity.getTagName(),
            subjuctEntity.getEntityName(), v.getEdgeName(), objectEntity.getTagName(),
            objectEntity.getEntityName(), v.getDynamicProperties());
        baseExports.add(relationExport);
      });
    }

    return baseExports;
  }


  private List<BaseExport> getSelectEntityData(ExportDataVO exportDataVO) {
    log.info("【Select Export entity data: {} 】", exportDataVO.getSpaceId());
    List<String> collect = exportDataVO.getEntityRelationExportList().stream()
        .map(entityRelationExport -> String.valueOf(entityRelationExport.getEntityId()))
        .collect(Collectors.toList());
    Set<String> dynamicKeys = new HashSet<>();
    List<BaseExport> baseExports = new ArrayList<>();
    List<NgVertex<String>> ngvertexs = graphVertexService.getSelectNgvertexs(
        SpaceConstant.SPACE_FIX_NAME + "_" + exportDataVO.getSpaceId(), collect);
    ngvertexs.stream().forEach(v -> {
      EntityExport entityExport = new EntityExport();
      entityExport.setVid(v.getVid());
      Map<String, Object> properties = v.getProperties();
      Map<String, Object> dynamicProperties = new LinkedHashMap<>();
      for (Map.Entry<String, Object> entry : properties.entrySet()) {
        entityExport.setTagName(entry.getKey());
        LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
        entityExport.setEntityName(entryValue.get(SpaceConstant.NAME));

        // 收集动态属性
        for (Map.Entry<String, String> map : entryValue.entrySet()) {
          if (!map.getKey().equals(SpaceConstant.NAME)) {
            dynamicProperties.put(map.getKey(), map.getValue());
            dynamicKeys.add(map.getKey());
          }
        }
      }
      entityExport.setDynamicProperties(dynamicProperties);
      baseExports.add(entityExport);
    });

    exportDataVO.setDynamicKeys(new ArrayList<>(dynamicKeys));
    return baseExports;
  }

  @Override
  public List<String> getTagData(Long spaceId, String tagName, int type) {
    List<String> result = new ArrayList<>();
    log.info("【Mysql get all relateion for Tag: {} 】", tagName);
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, tagName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, type)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    if (CollUtil.isNotEmpty(mapTagEdges)) {
      List<KnowledgeGraphTagEdgePropertyPo> tagEdges = knowledgeGraphTagEdgePropertyMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId,
                  mapTagEdges.getFirst().getTagEdgeId())
              .eq(KnowledgeGraphTagEdgePropertyPo::getType, type)
              .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
      if (SpaceConstant.INDEX == type) {
        result.add(SpaceConstant.ENTITY_NAME);
      }
      tagEdges.forEach(entity -> {
        if (!list.contains(entity.getExtra())) {
          StringBuilder builder = new StringBuilder();
          builder.append(entity.getPropertyName()).append(SpaceConstant.TAG_SPACE)
              .append(SpaceConstant.FIX_TAG_NAME_START);
          if (entity.getTagRequired() == SpaceConstant.INDEX) {
            builder.append(SpaceConstant.YES).append(SpaceConstant.SEPARATOR);
          }
          builder.append(entity.getPropertyType()).append(SpaceConstant.FIX_TAG_NAME_SUX);
          result.add(builder.toString().trim());
        }
      });
    }
    return result;
  }

  @Override
  public CommonRespDto<Boolean> importEntity(MultipartFile file, String spaceId, int type) {
    log.info("【start import excel for entity】");
    AtomicInteger success = new AtomicInteger(SpaceConstant.INDEX);
    AtomicInteger error = new AtomicInteger(SpaceConstant.INDEX);
    AtomicInteger length = new AtomicInteger(SpaceConstant.INDEX);

    Map<Map<String, List<String>>, List<List<String>>> errorMap = new HashMap<>();
    try {
      // 首先校验传入文件是否为空
      if (file == null) {
        log.error("【文件导入为空，请检查！】");
        return CommonRespDto.error(ErrorConstants.IMPORT_ERROP_TEMPTE.getMessage());
      }
      InputStream fileInputStream = file.getInputStream();
      ExcelReaderBuilder readerBuilder = EasyExcel.read(fileInputStream);
      ExcelReader excelReader = readerBuilder.build();
      ExcelReadExecutor excelReadExecutor = excelReader.excelExecutor();
      List<ReadSheet> sheets = excelReadExecutor.sheetList();

      try {
        if (sheets.get(sheets.size() - SpaceConstant.REPLICA_FACTOR).getSheetName()
            .equals(SpaceConstant.EXPLAIN)) {
          length.set(sheets.size() - SpaceConstant.REPLICA_FACTOR);
        } else {
          length.set(sheets.size());
        }
      } catch (Exception e) {
        log.error("【文件导入模板错误】");
        return CommonRespDto.error(ErrorConstants.IMPORT_ERROP_TEMPTE.getMessage());
      }
      
      // 预先处理校验表头和sheet页
      int totalData = SpaceConstant.INDEX;
      for (int m = SpaceConstant.INDEX; m < length.get(); m++) {
        EntityImportVoked readListener = new EntityImportVoked();
        EasyExcelFactory.read(file.getInputStream(), readListener).sheet(m).doRead();
        List<Map<Integer, String>> headList = readListener.getHeadList();
        if (CollUtil.isEmpty(headList)) {
          log.error("【Excel表头不能为空！】");
          return CommonRespDto.error(ErrorConstants.IMPORT_ERROP_HEADER.getMessage());
        } else {

          List<Map<Integer, String>> dataList = readListener.getDataList();
          if (CollUtil.isEmpty(dataList) && dataList.size() == SpaceConstant.REPLICA_FACTOR) {
            log.error("【Excel表数据不能为空！】");
            return CommonRespDto.error(ErrorConstants.IMPORT_ERROP_DATA.getMessage());
          } else {
            if (dataList.size() > SpaceConstant.MAX) {
              log.error("【超过最大条数限制！】");
              return CommonRespDto.error(ErrorConstants.IMPORT_ERROP_DATA_MAX.getMessage());
            }
            totalData += dataList.size();
          }
        }
      }
      boolean check = knowledgeGraphEntityManageService.checkDataLimitData(Long.valueOf(spaceId),
          totalData);
      if (!check) {
        log.error("【空间数据已达上限，导入失败！】");
        return CommonRespDto.error(ErrorConstants.RESOURCE_DATA_IMPORT_ERROR.getMessage());
      }
      File tempFile = File.createTempFile("upload_", ".xlsx");
      file.transferTo(tempFile);
      AtomicReference<String> errorUrl = new AtomicReference<>("");
      Integer userId = UserAuthUtil.getUserId();
      Locale locale = LocaleContextHolder.getLocale();
      CompletableFuture.runAsync(() -> {
        LocaleContextHolder.setLocale(locale);
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        AtomicInteger resourceData = new AtomicInteger(
            knowledgeGraphEntityManageService.getResourceLimitData(Long.valueOf(spaceId)));
        AtomicInteger spaceData = null;
        try {
          spaceData = new AtomicInteger(
              knowledgeGraphEntityManageService.getTotalDataUpBySpace(Long.valueOf(spaceId)));
        } catch (UnsupportedEncodingException e) {
          log.error("获取向量库数据异常", e);
          throw new RuntimeException(e);
        }
        Set<String> entityIds = new HashSet<>();
        // 存储边信息
        Set<EdgeDTO> edges = new HashSet<>();
        for (int i = SpaceConstant.INDEX; i < length.get(); i++) {
          ImportHeadDTO importHeadDTO = new ImportHeadDTO();
          int successSheet = SpaceConstant.INDEX;
          Set<String> relationSet = new HashSet<>();
          EntityImportVoked readListener = new EntityImportVoked();

          StringBuilder errorInfo = new StringBuilder();
          Map<String, String> entities = new HashMap<>();
          List<List<String>> errorAllData = new ArrayList<>();
          List<String> errorHead = new ArrayList<>();
          Map<Integer, Map<String, String>> typeMap = new HashMap<>();
          Map<String, String> relationData = new HashMap<>();
          Set<String> md5All = new HashSet<>();
          // 开始处理excel
          try (InputStream inputStream = new FileInputStream(tempFile)) {
            EasyExcelFactory.read(inputStream, readListener).sheet(i).doRead();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          // 获取表头（验空）
          List<Map<Integer, String>> headList = readListener.getHeadList();
          // 校验表头是否和下载模板一致
          List<String> dynamicHeaders =
              SpaceConstant.INDEX == type ? getTagData(Long.valueOf(spaceId),
                  sheets.get(i).getSheetName(), SpaceConstant.INDEX)
                  : getEdgeData(sheets.get(i).getSheetName(), SpaceConstant.REPLICA_FACTOR,
                      Long.valueOf(spaceId));

          Map<Integer, String> head = headList.get(SpaceConstant.INDEX);
          for (Entry<Integer, String> p : head.entrySet()) {
            errorHead.add(p.getValue());
          }

          if (CollUtil.isNotEmpty(dynamicHeaders)) {

            if (SpaceConstant.INDEX == type) {
              checkDynamicHeadMatch(head, dynamicHeaders, errorInfo);
            } else {
              checkDynamicHeadMatchRelation(head, dynamicHeaders, errorInfo);
            }


          } else {
            if (SpaceConstant.INDEX == type) {
              errorInfo.append(SpaceConstant.Tag_ERROR);
            } else {
              errorInfo.append(SpaceConstant.EDGE_ERROR);
            }
          }
          // 获取表数据(验空)
          List<Map<Integer, String>> dataList = readListener.getDataList();
          log.info("【开始获取数据： {}】", dataList);
          // 校验数据是否符合规则
          //获取头部,取最后一次解析的列头数据
          Map<Integer, String> excelHeadIdxNameMap = headList.get(headList.size() - 1);
          //封装数据体
          StringJoiner properties = new StringJoiner(",");
          Map<String, String> vectorMap = new HashMap<>();
          Set<RelationVectorDTO> relationVectorDTOS = new HashSet<>();
          StringJoiner relationProperties = new StringJoiner(",");
          GraphEntityDO entityDO = new GraphEntityDO();
          GraphRelationDO graphRelationDO = new GraphRelationDO();
          if (SpaceConstant.INDEX == type) {
            entityDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
            entityDO.setTagName(sheets.get(i).getSheetName());
            log.info("【导入实体类属性信息】 {}", entityDO);
          } else {
            graphRelationDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
            graphRelationDO.setEdgeName(sheets.get(i).getSheetName());
            log.info("【导入关系类属性信息】 {}", graphRelationDO);
          }
          boolean flag = true;
          int total = SpaceConstant.INDEX;
          int rank = SpaceConstant.INDEX;

          for (int t = SpaceConstant.INDEX; t < dataList.size(); t++) {
            List<String> errorData = new ArrayList<>();
            StringBuilder errorDataInfo = new StringBuilder();

            List<String> excelDataList = new ArrayList();
            List<String> errorDataList = new ArrayList();
            List<String> successDataList = new ArrayList();
            List<String> md5List = new ArrayList<>();
            AtomicInteger index = new AtomicInteger();
            boolean flagImport = true;

            if (successSheet > number && (successSheet % number == SpaceConstant.INDEX)
                && successSheet > total) {
              if (SpaceConstant.INDEX == type && !StringUtil.isBlank(properties.toString())) {
                if (flag && successSheet > SpaceConstant.INDEX) {
                  log.info("【开始导入实体数据： {}】", spaceId, type);
                  entityDO.setEntityPropertiesBath(properties.toString());
                  graphVertexService.saveEntityBath(entityDO);
                  // 向量存储
                  saveVectorBach(entityDO, vectorMap);
                  vectorMap.clear();
                }

              } else {
                if (flag && successSheet > SpaceConstant.INDEX && !StringUtil.isBlank(
                    relationProperties.toString())) {
                  log.info("【开始导入关系数据： {}】", spaceId, type);
                  graphRelationDO.setEntityPropertiesBath(relationProperties.toString());
                  graphEdgeService.saveRelationExcel(graphRelationDO);
                  // 向量数据库存储
                  saveVectorBachRelation(graphRelationDO, relationVectorDTOS);
                  relationVectorDTOS.clear();
                }
              }
              total = successSheet;
              errorData.clear();
              errorDataList.clear();
              successDataList.clear();
              excelDataList.clear();
              if (SpaceConstant.INDEX == type) {
                entityDO = new GraphEntityDO();
                entityDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
                entityDO.setTagName(sheets.get(i).getSheetName());
                properties = new StringJoiner(",");
                log.info("【导入实体类属性信息】 {}", entityDO);
              } else {
                graphRelationDO = new GraphRelationDO();
                graphRelationDO.setSpaceId(SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
                graphRelationDO.setEdgeName(sheets.get(i).getSheetName());
                log.info("【导入关系类属性信息】 {}", graphRelationDO);
                relationProperties = new StringJoiner(",");
              }

            }
            // 获取
            KnowledgeGraphTagEdgePo mapTagEdge = getMapTagEdgeInfo(type, spaceId,
                sheets.get(i).getSheetName());

            for (Entry<Integer, String> columnHead : excelHeadIdxNameMap.entrySet()) {
              // 校验每一行数据
              String value = "";
              if (StringUtil.isBlank(errorInfo.toString())) {

                value = checkData(columnHead.getValue(), dataList.get(t).get(columnHead.getKey()),
                    errorDataInfo, typeMap, index.get(), type, sheets.get(i).getSheetName(),
                    Long.valueOf(spaceId), mapTagEdge, md5List);
              }
              if (!StringUtil.isBlank(value)) {
                excelDataList.add(value);

              } else {
                excelDataList.add(dataList.get(t).get(columnHead.getKey()));
              }
              index.getAndIncrement();
            }
            if (!StringUtil.isBlank(errorDataInfo.toString()) || !StringUtil.isBlank(
                errorInfo.toString())) {
              errorDataList.addAll(excelDataList);
              errorData.addAll(excelDataList);
              if (!StringUtil.isBlank(errorInfo.toString())) {
                errorData.add(errorInfo.toString());
              }
              if (!StringUtil.isBlank(errorDataInfo.toString())) {
                errorData.add(errorDataInfo.toString());
              }
              errorAllData.add(errorData);
              error.addAndGet(SpaceConstant.REPLICA_FACTOR);
              flag = false;


            } else {
              if (CollUtil.isNotEmpty(md5List)) {
                StringBuilder md5Builder = new StringBuilder();
                for (String md5 : md5List) {
                  md5Builder.append(md5);
                }
                if (!StringUtil.isBlank(md5Builder.toString())) {
                  if (!md5All.contains(MD5Util.generateMD5(md5Builder.toString()))) {
                    md5All.add(MD5Util.generateMD5(md5Builder.toString()));
                  } else {
                    errorDataList.addAll(excelDataList);
                    errorData.addAll(excelDataList);
                    errorData.add(SpaceConstant.DATA_DUPLICATION);
                    errorAllData.add(errorData);
                    error.addAndGet(SpaceConstant.REPLICA_FACTOR);
                    flagImport = false;
                  }
                }
              }
              if (flagImport) {
                // 校验通过的生成实体，存入图数据库
                if (SpaceConstant.INDEX == type) {
                  saveGraph(excelDataList, typeMap, entityDO, properties, t, excelHeadIdxNameMap,
                      importHeadDTO, vectorMap, entityIds, resourceData, spaceData,
                      SpaceConstant.SPACE_FIX_NAME + "_" + spaceId);
                  successDataList.addAll(excelDataList);
                  successSheet++;
                  flag = true;
                } else {
                  for (int j = SpaceConstant.INDEX; j < SpaceConstant.FOUR; j++) {
                    switch (j) {
                      case 0:
                        relationData.put(SpaceConstant.RELATION_NAME, excelDataList.get(j));
                        break;
                      case 1:
                        relationData.put(SpaceConstant.SUBJECT_VALUE, excelDataList.get(j));
                        break;
                      case 2:
                        relationData.put(SpaceConstant.OBJECT_NAME, excelDataList.get(j));
                        break;
                      case 3:
                        relationData.put(SpaceConstant.OBJECT_VALUE, excelDataList.get(j));
                        break;
                      default:
                        break;
                    }
                  }
                  StringBuilder builder = null;
                  try {
                    builder = saveRelationGraph(excelDataList, typeMap, spaceId, relationData,
                        sheets.get(i).getSheetName(), errorDataInfo, graphRelationDO,
                        relationProperties, excelHeadIdxNameMap, relationSet, entities,
                        importHeadDTO, relationVectorDTOS, rank, edges, resourceData, spaceData,
                        entityIds);
                  } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                  }

                  if (StringUtil.isBlank(builder.toString())) {
                    successDataList.addAll(excelDataList);
                    successSheet++;
                    flag = true;
                  } else {
                    errorDataList.addAll(excelDataList);
                    errorData.addAll(excelDataList);
                    errorData.add(builder.toString());
                    errorAllData.add(errorData);
                    error.addAndGet(SpaceConstant.REPLICA_FACTOR);
                  }
                }
              }
            }
          }
          if (successSheet > SpaceConstant.INDEX && successSheet > total) {
            if (SpaceConstant.INDEX == type && !StringUtil.isBlank(properties.toString())) {
              log.info("【开始导入实体数据： {}】", spaceId, type);
              entityDO.setEntityPropertiesBath(properties.toString());
              graphVertexService.saveEntityBath(entityDO);

              //向量存储
              // 向量数据库存储
              saveVectorBach(entityDO, vectorMap);
              vectorMap.clear();

            } else if (!StringUtil.isBlank(relationProperties.toString())) {
              log.info("【开始导入关系数据： {}】", spaceId, type);
              graphRelationDO.setEntityPropertiesBath(relationProperties.toString());
              graphEdgeService.saveRelationExcel(graphRelationDO);
              // 向量数据库存储
              saveVectorBachRelation(graphRelationDO, relationVectorDTOS);
              relationVectorDTOS.clear();
            }

          }
//                INSERT VERTEX `疾病`  (NAME,`描述`,`预防措施`,`得病概率`,`易得人群`,`关联科室`,`治疗手段`,`治愈时间`,`治愈概率`,`原因`) VALUES "227685974508323840":("格林-巴利综合征","格林-巴利综合征(GBS)是神经系统常见的一种严重疾病，主要病变在脊神经根和脊神经，可累及颅神经。与病毒感染或自身免疫反应有关。临床表现为急性，对称性、弛缓性肢体瘫痪。","格林-巴利综合征(GBS)是神经系统常见的一种严重疾病，主要病变在脊神经根和脊神经，可累及颅神经。与病毒感染或自身免疫反应有关。临床表现为急性，对称性、弛缓性肢体瘫痪。","发病率约为0.0001%--0.0003%","无特定人群","['内科', '神经内科']","['对症治疗', '药物治疗', '康复治疗']","3-6个月","3-6个月","到目前为止GBS的病因仍不十分清楚，最早认为感染与中毒是病因基础，40%病人有前驱感染，其前驱因素有以下几种。\n1.病毒与GBS：1983年Hurroiz报告1034例GBS，70%有前驱因素，2/3为病毒感染，主要表现为上感或腹泻，普遍认为巨细胞病毒，EB病毒，流感病毒等与GBS有很大关系，也有报告与肝炎病毒有密切关系，刘秀梅1988年报告4例急性肝炎合并GBS，另外徐贤豪报告乙肝病人中GBS发病6/500，明显高於对照组，乙肝ABSAg阳性100例中，GBS发病17例，对照组45例中GBS发病1例。\n2.空肠弯曲菌与GBS：1982年Rhodes报告1例GBS，经便培养及抗体检测证实为空肠弯曲菌继发GBS，1984年又一学者报告56例GBS有空肠弯曲菌感染21例，国内唐健等1993年首先报告空肠弯曲菌感染是我国GBS主要前驱因素，报告17例GBS中71%有胃肠道感染症状，且血清空肠弯曲菌Igm抗体阳性率53%，均高於国外文献，目前空肠弯曲菌与GBS的关系已成为GBS研究的焦点。\n3.疫苗接种与GBS：一组资料1034例GBS中有4.5%疫苗接种后发病，多见於流感疫苗，肝炎疫苗，麻疹疫苗接种后发病，我科於1995年收治一例甲肝疫苗接种后发病的GBS。\n4.遗传与GBS：有人报告GBS病人A3和B8基因频率明显增高，认为GBS与遗传有一定关系。\n5.微量元素与GBS：张祥建等报告GBS病人存在微量元素Zn，Cu，Fe代谢异常，认为微量元素异常，可能在GBS发病中起一定作用。"),"227685974508848128":("肺出血-肾炎综合征","肺出血-肾炎综合征，又称抗基膜性肾小球肾炎，Goodpasture综合征或Goodpasture病，可能系病毒感染和/或吸入某些化学性物质引起。它是由抗基膜抗体导致的肾小球和肺泡壁基膜的严重损伤，临床表现为肺出血，急进性肾小球肾炎和血清抗肾小球基膜(GBM)抗体阳性三联征。多数患者病情进展迅速，预后凶险。","肺出血-肾炎综合征，又称抗基膜性肾小球肾炎，Goodpasture综合征或Goodpasture病，可能系病毒感染和/或吸入某些化学性物质引起。它是由抗基膜抗体导致的肾小球和肺泡壁基膜的严重损伤，临床表现为肺出血，急进性肾小球肾炎和血清抗肾小球基膜(GBM)抗体阳性三联征。多数患者病情进展迅速，预后凶险。","0.00%","无特殊人群","['内科', '呼吸内科']","['药物治疗', '康复治疗', '支持治疗']","10-30天","10-30天","呼吸道感染特别与流感病毒感染是本病最常见的诱因，最近研究发现获得性免疫缺陷病患者感染卡氏肺囊虫肺炎(Pneumocystis Carinii Pneumonia)后，机体易产生抗GBM抗体，Calderon等报道4例HIV感染者中3例抗IV型胶原a3链抗体(抗GBM抗体)阳性，提示卡氏肺囊虫肺炎时肺泡损害可以诱发肺出血-肾炎综合征。\n硬皮病是以局限性或弥漫性皮肤及内脏器官结缔组织纤维化、硬化及萎缩为特点的结缔组织病，其主要特点为皮肤、滑膜、骨骼肌、血管和食道出现纤维化或硬化硬皮病(scleroderma)，是以局限性或弥漫性皮肤及内脏器官结缔组织纤维化、硬化及萎缩为特点的结缔组织病，其主要特点为皮肤、滑膜、骨骼肌、血管和食道出现纤维化或硬化。\n吸入可卡因 Perez等报道1例长期吸烟的患者在吸用可卡因3周以后发生了肺出血-肾炎综合征。接触汽油蒸汽羟化物，松节油及吸入各种碳氢化合物。\n发病机制\n由于某些病因使机体同时产生了抗肺泡，肾小球基底膜抗体，并由此攻击了肾小球与肺，发生II型变态反应，至于同时向肺泡和肾小球发生免疫复合物沉积并激活补体(III型变态反应)的发病机理，尚无确切的解释。\n1962年Steblay等人证实，肺出血-肾炎综合征的肾小球基底膜(GBM)损害是由抗GBM抗体介导，遂后大量的研究工作集中于分离和研究GBM组分，寻找抗体针对的相应抗原及表明抗原的分子结构与特征，近年来随着分子生物学及生物化学的飞速发展，人们在新发现的胶原IV的a3(IV)链中，证实a3(IV)链的NC1结构域是Goodpasture自身抗原，又称Goodpasture抗原，继而克隆了该抗原基因Co14A3，定位于第二条染色体q35～37区域。\n应用间接免疫荧光和免疫电镜技术证实，Goodpasture抗原不仅见于GBM，也分布于肾小管基膜(TBM)，肺泡毛细血管基膜(ABM)及其他组织基膜(如脉络膜，角膜，晶体，视网膜血管基底膜等处)，但具有致病作用的Goodpasture抗原主要分布于GBM，TBM和ABM，抗原的隐匿性造成其暴露过程的可逆性，体外可通过6mol盐酸胍或pH 3的强酸条件暴露a3NC1结构域，但体内抗原是如何暴露并产生免疫应答损伤GBM尚未完全明了，目前推测，在生理条件下Goodpasture抗原隐匿在胶原IVa3NC1结构域中，各种诱发因素(毒素，病毒感染，细菌感染，肿瘤，免疫遗传因素)及内毒素等均可激活上皮，内皮及系膜细胞增殖，并释放炎性介质(IL-1，RDS，前列腺素，中性蛋白酶等)，GBM等在细胞酶作用下，胶原IV高级结构解离，暴露Goodpasture抗原决定簇，刺激机体产生抗体，导致免疫损伤，由于在全身毛细血管内皮层中唯有肾小球毛细血管的内皮层有窗孔，使得抗体可以与GBM抗原直接接触而致病，而ABM只有当受到某些外界因素(如感染，吸烟，吸入汽油或有机溶剂)影响后，破坏其完整性使基底膜抗原暴露后肺部方出现病症，此即为何肾脏最易受累且受累程度与抗体滴度相一致，而肺部受累程度与抗体滴度不一致的缘故。\n本病患者HLA-DR2等抗原频率明显增高(达89%，正常对照仅32%)，应用基因DNA限制性片段长度多态性分析还显示本病与HLA-DR4，HLA-DQB链基因DQWLb和DQW3相关，表明HLA二类抗原相关的淋巴细胞在本病起一定作用，有实验发现，如果仅给受试动物抗GBM抗体虽可产生GBM线条状沉着，但不发病，只有同时输入患病动物T细胞后受试动物才发病，如此证实T细胞在本病发病机制中起重要作用，近年的研究也发现，某些细胞因子如肿瘤坏死因子，IL-1可以加重本病的发展。\n肺部病变表现为 肺丰满胀大，表面有较多出血斑，光镜下可见肺泡腔内有大量红细胞及很多含有含铁血黄素的巨噬细胞，肺泡壁呈局灶性增厚，纤维化，肺泡细胞肥大，电镜下可见肺泡基底膜增厚及断裂，内皮下有电子致密物呈斑点样沉积，而内皮细胞正常，免疫荧光检查可见毛细血管壁有IgG，C3呈连续或不连续线样沉积。\n肾脏病变可见到双肾柔软呈灰白色，表面有多数小出血斑点，光镜下多数呈新月体性肾炎的病变特征，但内皮及系膜细胞增生一般不重，可见毛细血管纤维素样坏死，晚期肾小球纤维化，肾间质可见炎症细胞浸润及间质小动脉炎，肾小管变性，萎缩和坏死，电镜下可见球囊下皮细胞增生，形成新月体，系膜基质增生，基底膜断裂，肾小球毛细血管壁一般无致密物沉积，偶见内皮下有电子致密物呈斑点样沉积，免疫荧光检查可见IgG(100%)，C3(60%～70%)沿肾小球毛细血管壁呈线状沉积，部分患者远曲小管基底膜上抗体IgG阳性。\n既往认为本病征主要是由基底膜(GBM)抗体解导引起，免疫荧光检查示IgG沿肾小球基底膜呈线条状沉积，此症仅一部分可确诊为肺出血-肾炎综合征，另一部分患者临床酷似肺出血-肾炎综合征，但其免疫荧光则示IgG沿GMB呈颗粒状沉积，血中抗GBM抗体阴性，实际此部分病例系免疫复合物性肾炎(ICGN)，自身免疫机理在本病起重要作用，表现为ICGN者，是由于免疫复合物沉积于肾小球及肺泡的相应部位而引起，临床上肺部病变出现于肾病变之前，肾功能多急速恶化，可于数周至数月内死亡。"),"227685974508848129":("小儿糖原贮积病IX型","糖原贮积病(glycogenstoragedisease,GSD)是一类先天性酶缺陷所造成的糖原代谢障碍疾病。糖原贮积病IX型(glycogenstoragediseasetypeIX，GSD-IX)是因缺乏磷酸化酶激酶所致的一组不同的疾病，属遗传性疾病。包括X连锁遗传性肝磷酸化酶激酶缺乏症、常染色体遗传性肝和肌磷酸化酶激酶缺乏症、特定性肌磷酸化酶激酶缺乏症和心脏磷酸化酶激酶缺乏。","糖原贮积病(glycogenstoragedisease,GSD)是一类先天性酶缺陷所造成的糖原代谢障碍疾病。糖原贮积病IX型(glycogenstoragediseasetypeIX，GSD-IX)是因缺乏磷酸化酶激酶所致的一组不同的疾病，属遗传性疾病。包括X连锁遗传性肝磷酸化酶激酶缺乏症、常染色体遗传性肝和肌磷酸化酶激酶缺乏症、特定性肌磷酸化酶激酶缺乏症和心脏磷酸化酶激酶缺乏。","0.0001%--0.0007%","儿童","['儿科', '儿科综合']","['对症治疗', '药物治疗', '支持性治疗']","终身治疗","终身治疗","发病原因：\n本型糖原贮积病是由于缺乏磷酸化酶激酶所致。糖原在体内主要以肝糖原、肌糖原的形式存在，是由许多葡萄糖经由过程a-1，4-糖苷键(直链)及a-1，6-糖苷键(分枝)相连而成的带有分枝的多糖存在于细胞质中。肝糖原的合成与分化主如果为了维持血糖浓度的相对于永恒固定;肌糖原是肌肉糖酵解的主要来源。糖原的分解和合成由不同的酶催化。糖原磷酸化酶以a、b两种情势存在。糖原磷酸化酶b无活性，反应时需转化为a。在肌肉剧烈运动时，糖原磷酸化酶的活性是受到肾上腺素的调节。肾上腺素经由过程旌旗灯号转导系统使cAMP的浓度提高，激活A激酶使无活性的糖原磷酸化酶激酶b磷酸化成为有活性的糖原磷酸化酶激酶a，糖原磷酸化酶激酶a进一步使无活性的糖原磷酸化酶b成为有活性的糖原磷酸化酶a，促进糖原分化，产生能量。在肝脏中，糖原磷酸化酶的活性调节主要受胰高血糖素调节，当血糖浓度减低到肯定是水平，经由过程胰高血糖素形成cAMP，激活A激酶使磷酸化酶激酶b成为磷酸化酶激酶a，催化无活性的磷酸化酶b改变为有活性的磷酸化酶a，促使肝糖原分化成蒲萄糖开释到血液中，达到升血糖目的。\n磷酸化酶激酶是由4个亚单位(a、B、y、δ)组成的一个蛋白激酶，来自神经中枢的冲动或激素的调控可通过它激活磷酸化酶，从而促进糖原的分解过程。磷酸化酶激酶本身的激活是经由Ca2 、腺苷酸环化酶、环腺苷酸(cAMP)依赖性蛋白激素等一系列的作用进行的，这一过程主要由胰高糖素调控。组成磷酸化酶激酶的4个亚单位都各自有位于不同染色体上的编码基因，在各种组织中的表达亦各不相同。从理论上讲，上述过程中任一酶的缺陷都可以造成糖原分解受阻而累积，而实际上仅磷酸化酶激酶缺乏是最主要的病因，依据其病变累及的器官和遗传特征加以区分：\n1.X连锁遗传性肝磷酸化酶激酶缺乏症：是因位于Xp22的a亚单位编码基因突变所致。\n2.常染色体遗传性肝和肌磷酸化酶激酶缺乏症：这是由于位于常染色体上编码a、B亚单位的基因突变所造成的(目前仅B亚单位已定位于16q12-q13)。\n3.特定性肌磷酸化酶激酶缺乏症：这是由于在肌组织中编码a亚单位的结构基因(位于Xql2)突变所造成的。\n4.心脏磷酸化酶激酶缺乏：迄今仅有少数报道，酶缺陷仅限于心肌内。\n上列各型磷酸化酶激酶缺乏症的确诊都必须依赖病变器官组织的酶活力检测。由于磷酸化酶激酶在各种组织中有多种同工酶，因此，外周血红、白细胞中酶活力的检测有可能发生误诊。\n发病机制：\n是由磷酸化酶激酶的先天性缺乏引起的。目前根据遗传型及受累组织分为3个亚型。IXa为常染色体隐性遗传;IXb属性联隐性遗传，男性发病。这2个亚型皆累及肝脏，骨骼肌不受影响，生物化学及形态学均正常。IXc型属常染色体隐性遗传，肝脏和肌肉磷酸化酶激酶均缺乏活性。");

          // 校验如果不通过下载错误文件，写入到错误文件中
          if (CollUtil.isNotEmpty(errorAllData)) {
            // 生成excel，存入服务器
            Map<String, List<String>> mapInfo = new HashMap<>();
            mapInfo.put(sheets.get(i).getSheetName(), errorHead);
            errorMap.put(mapInfo, errorAllData);
          }

          success.addAndGet(successSheet);
          if (error.get() > SpaceConstant.INDEX) {
            // 生成错误excel
            errorUrl.set(createErrorExcel(errorMap, spaceId, type));
          }

        }

        Map<String, TotalEdgeVO> edgeCache = knowledgeGraphRelationDataService.edgeCache;
        edgeCache.clear();
        graphVertexService.executeTheTask(SpaceConstant.SPACE_NAME_FIX + spaceId);


      }).thenRun(() -> {
        String message;
        String resourcePath = "";
        Type resultType = Type.SUCCESS;
        log.info("发送消息");
        // 发送完成消息
        if (error.get() == SpaceConstant.INDEX) {
//          message =
//              file.getOriginalFilename() + " " + "成功 " + success.get() + " 失败 " + error.get();
          message = StrUtil.format("导入完成：成功{}条，失败{}条", success.get(), error.get());
        } else {
//          message =
////              file.getOriginalFilename() + " " + "成功 " + success.get() + " 失败 " + error.get();
          message = StrUtil.format("导入完成：成功{}条，失败{}条", success.get(), error.get());
          resourcePath = errorUrl.get();
          resultType = Type.FAILURE;
        }
        //发送下载成功的消息
        messageHandler.sendMessage(MessageTypeEnum.KNOWLEDGE_ENTITY_RELATION_IMPORT, message,
            userId, resultType, resourcePath, true);
        tempFile.delete();
      }).exceptionally(ex -> {
        log.error("异步任务发生异常: {}", ex.getMessage(), ex);
        log.info("导入失败");
        tempFile.delete();
        return null;
      });
    } catch (ServiceException e) {
      log.error("【import excel error space : {} for type ： {}】", spaceId, type);
      return CommonRespDto.error(e.getMessage());
    } catch (Exception e) {
      log.error("导入出错", e);
      return CommonRespDto.error(e.getMessage());
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public List<List<String>> generateDynamicHeaders(List<String> dynamicKeys, int type) {
    List<List<String>> headers = new ArrayList<>();

    if (SpaceConstant.INDEX == type) {
      // 固定表头
      headers.add(Collections.singletonList("实体名称"));
      headers.add(Collections.singletonList("VID"));
      headers.add(Collections.singletonList("本体名称"));

    } else {

      headers.add(Collections.singletonList("主体类型"));
      headers.add(Collections.singletonList("主体名称"));
      headers.add(Collections.singletonList("关系名称"));
      headers.add(Collections.singletonList("客体类型"));
      headers.add(Collections.singletonList("客体名称"));

    }
    // 动态表头
    dynamicKeys.forEach(key -> headers.add(Collections.singletonList(key)));

    return headers;
  }

  @Override
  public List<Map<Integer, String>> convertToExcelData(List<BaseExport> entityExports,
      List<String> dynamicKeys, int type) {
    List<Map<Integer, String>> dataList = new ArrayList<>();
    for (BaseExport export : entityExports) {
      if (export instanceof EntityExport) {
        EntityExport entityExport = (EntityExport) export;
        Map<Integer, String> row = new LinkedHashMap<>();

        // 固定字段
        row.put(SpaceConstant.INDEX, entityExport.getEntityName());
        row.put(SpaceConstant.REPLICA_FACTOR, entityExport.getVid());
        row.put(SpaceConstant.TWO, entityExport.getTagName());

        // 动态字段数据
        Map<String, Object> dynamicProperties = entityExport.getDynamicProperties();
        for (int i = SpaceConstant.INDEX; i < dynamicKeys.size(); i++) {
          String key = dynamicKeys.get(i);
          Object propertyObj = dynamicProperties.get(key);
          String value = propertyChange(key, propertyObj);
          row.put(SpaceConstant.THREE + i, value);
        }
        dataList.add(row);
      } else {
        RelationExport relationExport = (RelationExport) export;
        Map<Integer, String> row = new LinkedHashMap<>();

        // 固定字段
        row.put(SpaceConstant.INDEX, relationExport.getSubjectTagName());
        row.put(SpaceConstant.REPLICA_FACTOR, relationExport.getSubjectName());
        row.put(SpaceConstant.TWO, relationExport.getEdgeName());
        row.put(SpaceConstant.THREE, relationExport.getObjectTagName());
        row.put(SpaceConstant.FOUR, relationExport.getObjectName());

        // 动态字段数据
        Map<String, Object> dynamicProperties = relationExport.getDynamicProperties();
        for (int i = SpaceConstant.INDEX; i < dynamicKeys.size(); i++) {
          String key = dynamicKeys.get(i);
          Object propertyObj = dynamicProperties.get(key);
          String value = propertyChange(key, propertyObj);
          row.put(SpaceConstant.FIVE + i, value);
        }

        dataList.add(row);
      }
    }

    return dataList;
  }


  private String propertyChange(String key, Object value) {
    if (value == null) {
      return "";
    }
    return switch (key) {
      case "datetime" -> DateGraphUtil.dateProcessTime(value.toString());
      case "日期时间" -> DateGraphUtil.dateProcessTime(value.toString());
      case "timestamp" ->
        // 获取边属性，拿取边的类型
          DateGraphUtil.dateProcessTimeStamp(Long.valueOf(value.toString()));
      case "time" -> DateGraphUtil.TimeZoneConversion(value.toString());
      case "时间戳" -> DateGraphUtil.dateProcessTimeStamp(Long.valueOf(value.toString()));
      case null, default -> value.toString();
    };
  }

  private KnowledgeGraphTagEdgePo getMapTagEdgeInfo(int type, String spaceId, String sheetName) {
    log.info("【Mysql get all relateion for Tag: {} 】", spaceId);
    KnowledgeGraphTagEdgePo mapTagEdge = null;
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, sheetName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, Integer.valueOf(spaceId))
            .eq(KnowledgeGraphTagEdgePo::getType, type)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    if (CollUtil.isNotEmpty(mapTagEdges)) {
      mapTagEdge = mapTagEdges.getFirst();
    }
    return mapTagEdge;
  }

  private void saveVectorBachRelation(GraphRelationDO graphRelationDO,
      Set<RelationVectorDTO> relationVectorDTOS) {
    for (RelationVectorDTO relationVectorDTO : relationVectorDTOS) {
      SaveRelationDTO saveRelationDTO = new SaveRelationDTO();
      saveRelationDTO.setSpaceId(graphRelationDO.getSpaceId());
      saveRelationDTO.setSubjectId(relationVectorDTO.getSubjectId());
      saveRelationDTO.setSubjectName(relationVectorDTO.getSubjectName());
      saveRelationDTO.setObjectId(relationVectorDTO.getObjectId());
      saveRelationDTO.setObjectName(relationVectorDTO.getObjectName());
      saveRelationDTO.setRank(relationVectorDTO.getRank());
      saveRelationDTO.setEdgeName(graphRelationDO.getEdgeName());
      saveRelationDTO.setEntityProperties(relationVectorDTO.getPropertiesVOList());
      //TODO 添加向量库
//      apiVectorQuantityService.makeAsyncCreateRelation(saveRelationDTO);
    }
  }

  private void saveVectorBach(GraphEntityDO entityDO, Map<String, String> vectorMap) {
    //        INSERT VERTEX `${tex.tagName}`  (${tex.entityProperties}) VALUES ${tex.entityPropertiesBath};
    for (Map.Entry<String, String> entry : vectorMap.entrySet()) {
      SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
      saveEntityDTO.setSpaceId(entityDO.getSpaceId());
      saveEntityDTO.setEntityId(entry.getKey());
      List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
      SaveTagInfoDTO tag = new SaveTagInfoDTO();
      tag.setTagName(entityDO.getTagName());
      String[] propertyNames = entityDO.getEntityProperties().split(SpaceConstant.TAG_SPLIT);
      List<EntityPropertiesVO> entityProperties = new ArrayList<>();
      String[] propertyValues = entry.getValue().split(SpaceConstant.TAG_SPLIT);
      for (int i = SpaceConstant.REPLICA_FACTOR; i < propertyNames.length; i++) {
        EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
        entityPropertiesVO.setPropertyName(propertyNames[i].replace("`", ""));
        entityPropertiesVO.setPropertyValue(propertyValues[i].replace("\"", ""));
        entityProperties.add(entityPropertiesVO);
      }
      saveEntityDTO.setEntityName(propertyValues[SpaceConstant.INDEX]);
      tag.setEntityProperties(entityProperties);
      saveTagInfoDTOS.add(tag);
      saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
      //TODO 添加向量库
//      apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
    }


  }

  private void checkDynamicHeadMatchRelation(Map<Integer, String> head, List<String> dynamicHeaders,
      StringBuilder errorInfo) {
    Map<Integer, String> relationMap = Map.ofEntries(
        Map.entry(SpaceConstant.INDEX, SpaceConstant.SUBJECT_NAME),
        Map.entry(SpaceConstant.REPLICA_FACTOR, SpaceConstant.SUBJECT_VALUE),
        Map.entry(SpaceConstant.TWO, SpaceConstant.OBJECT_NAME),
        Map.entry(SpaceConstant.THREE, SpaceConstant.OBJECT_VALUE));
    for (int i = SpaceConstant.INDEX; i < SpaceConstant.FOUR; i++) {
      if (!head.get(i).equals(relationMap.get(i))) {
        if (!errorInfo.toString().contains(SpaceConstant.ENTITY_RELATION_CHECK)) {
          errorInfo.append(SpaceConstant.ENTITY_RELATION_CHECK);
        }
      }
    }
    List<Integer> keys = new ArrayList<>(head.keySet());
    if (keys.size() == dynamicHeaders.size()) {
      for (int i = SpaceConstant.FOUR; i < keys.size(); i++) {
        Integer key = keys.get(i);
        if (!head.get(key).equals(dynamicHeaders.get(i))) {
          if (!errorInfo.toString().contains(SpaceConstant.ENTITY_RELATION_CHECK)) {
            errorInfo.append(SpaceConstant.ENTITY_RELATION_CHECK);
          }
        }
      }
    } else {
      if (!errorInfo.toString().contains(SpaceConstant.ENTITY_RELATION_CHECK)) {
        errorInfo.append(SpaceConstant.ENTITY_RELATION_CHECK);
      }
    }
  }

  private StringBuilder saveRelationGraph(List<String> successDataList,
      Map<Integer, Map<String, String>> typeMap, String spaceId, Map<String, String> relationData,
      String relationName, StringBuilder errorInfo, GraphRelationDO graphRelationDO,
      StringJoiner relationProperties, Map<Integer, String> excelHeadIdxNameMap,
      Set<String> relationSet, Map<String, String> entities, ImportHeadDTO importHeadDTO,
      Set<RelationVectorDTO> relationVectorDTOS, int rank, Set<EdgeDTO> edges,
      AtomicInteger resourceData, AtomicInteger spaceData, Set<String> entityIds)
      throws UnsupportedEncodingException {
    if (CollUtil.isNotEmpty(successDataList)) {
      log.info("【开始解析关系数据信息： {}】", graphRelationDO.getEdgeName());
      if (relationData.get(SpaceConstant.SUBJECT_VALUE)
          .equals(relationData.get(SpaceConstant.OBJECT_VALUE))) {
        errorInfo.append(SpaceConstant.EDGE_ERROR_SAME);
      } else {
//                if (relationSet.contains(relationData.get(SpaceConstant.SUBJECT_NAME) + SpaceConstant.SET_RELATION + relationData.get(SpaceConstant.SUBJECT_VALUE)
//                        + SpaceConstant.SET_RELATION + relationData.get(SpaceConstant.OBJECT_NAME) + SpaceConstant.SET_RELATION + relationData.get(SpaceConstant.OBJECT_VALUE))) {
//                    errorInfo.append(SpaceConstant.SAME_RELATION);
//                } else {
        List<Entity> subjects = getSubjectInfo(graphRelationDO.getSpaceId(),
            relationData.get(SpaceConstant.SUBJECT_NAME),
            relationData.get(SpaceConstant.SUBJECT_VALUE));
        List<Entity> objects = getObjectInfo(graphRelationDO.getSpaceId(),
            relationData.get(SpaceConstant.OBJECT_NAME),
            relationData.get(SpaceConstant.OBJECT_VALUE));
        // 检查主体 和客体是否已经创建
        String subjectId = checkSubject(spaceId, graphRelationDO.getSpaceId(),
            relationData.get(SpaceConstant.SUBJECT_NAME),
            relationData.get(SpaceConstant.SUBJECT_VALUE), graphRelationDO, subjects, errorInfo,
            entities, entityIds, resourceData, spaceData, edges);
        String objectId = checkObject(spaceId, graphRelationDO.getSpaceId(),
            relationData.get(SpaceConstant.OBJECT_NAME),
            relationData.get(SpaceConstant.OBJECT_VALUE), graphRelationDO, objects, errorInfo,
            entities, entityIds, resourceData, spaceData, edges);
        if (StringUtil.isBlank(errorInfo.toString())) {
          graphRelationDO.setSubjectId(subjectId);
          graphRelationDO.setObjectId(objectId);
          List<EntityPropertiesVO> entityProperties = new ArrayList<>();
          for (int i = SpaceConstant.INDEX; i < successDataList.size(); i++) {
            if (!StringUtil.isBlank(successDataList.get(i))) {
              EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
              entityPropertiesVO.setPropertyValue(successDataList.get(i));
              Map<String, String> currentMap = typeMap.get(i - SpaceConstant.INDEX);
              if (currentMap != null) {
                for (Map.Entry<String, String> entry : currentMap.entrySet()) {
                  entityPropertiesVO.setPropertyName(entry.getKey());
                  entityPropertiesVO.setPropertyType(
                      entry.getKey() != null ? entry.getValue() : "");
                }
              }
              entityProperties.add(entityPropertiesVO);
            }
          }
          processRelation(graphRelationDO, entityProperties, relationProperties,
              excelHeadIdxNameMap, errorInfo, importHeadDTO, relationData, relationVectorDTOS, rank,
              edges, resourceData, spaceData, entityIds);
        }
        relationSet.add(relationData.get(SpaceConstant.SUBJECT_NAME) + SpaceConstant.SET_RELATION
            + relationData.get(SpaceConstant.SUBJECT_VALUE) + SpaceConstant.SET_RELATION
            + relationData.get(SpaceConstant.OBJECT_NAME) + SpaceConstant.SET_RELATION
            + relationData.get(SpaceConstant.OBJECT_VALUE));


      }

    }
    return errorInfo;
  }


  private String checkSubject(String space, String spaceId, String subjectTagName,
      String subjectName, GraphRelationDO graphRelationDO, List<Entity> subjects,
      StringBuilder errorInfo, Map<String, String> entities, Set<String> entityIds,
      AtomicInteger resourceData, AtomicInteger spaceData, Set<EdgeDTO> edges) {
    String id = UniqueIDGenerator.generateUniqueID();
    // 获取主体tagid
    KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeConverter.dtoToPo(
        knowledgeGraphTagEdgeService.getTagInfo(Long.valueOf(space), subjectTagName));
    // 判断subject是否有值，如果没有新建实体 ，如果有多条 也同样走新建,如果只用一条则使用
    if (!org.springframework.util.CollectionUtils.isEmpty(subjects)) {
      if (subjects.size() > 1) {
//                新建
        saveEntityForSubject(spaceId, subjectTagName, subjectName, id, errorInfo, mapTagEdge,
            entityIds, resourceData, spaceData, edges);
        // mysql记录
//                relationDataService.saveSubject(saveRelationVO,id);
        if (StringUtil.isBlank(errorInfo.toString())) {
          graphRelationDO.setSubjectId(id);
        }
      } else {
        graphRelationDO.setSubjectId(subjects.get(SpaceConstant.INDEX).getEntityId());
        id = subjects.get(SpaceConstant.INDEX).getEntityId();
      }
    } else {
      // 新建
      if (!entities.containsKey(subjectName)) {
        saveEntityForSubject(spaceId, subjectTagName, subjectName, id, errorInfo, mapTagEdge,
            entityIds, resourceData, spaceData, edges);

        if (StringUtil.isBlank(errorInfo.toString())) {
          graphRelationDO.setSubjectId(id);
          entities.put(subjectName, id);
        }
      } else {
        id = entities.get(subjectName);
      }

    }

    return id;
  }


  private void saveEntityForSubject(String spaceId, String subjectTagName, String subjectName,
      String id, StringBuilder errorInfo, KnowledgeGraphTagEdgePo mapTag, Set<String> entityIds,
      AtomicInteger resourceData, AtomicInteger spaceData, Set<EdgeDTO> edges) {
    log.info("【Process Kg-webserver-web save  Entity : {} for space :  {} to Relation : {} 】",
        subjectTagName, subjectName, spaceId);
    GraphRelationEntityDO graphRelationEntityDO = new GraphRelationEntityDO();
    graphRelationEntityDO.setVid(id);
    graphRelationEntityDO.setTagName(subjectTagName);
    graphRelationEntityDO.setSpaceId(spaceId);

    if (!subjectName.matches(SpaceConstant.PATTERN_STRING)) {
      log.info("【entity name not matches :{}】", subjectName);
      errorInfo.append(SpaceConstant.ENTITY_NAME_CHECK);

    } else {
      if (null != mapTag) {
        KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeMapper.selectById(
            mapTag.getTagEdgeId());
        List<KnowledgeGraphTagEdgePropertyPo> tagEdgeProperties = knowledgeGraphTagEdgePropertyMapper.selectList(
            Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
                .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, mapTag.getTagEdgeId())
                .eq(KnowledgeGraphTagEdgePropertyPo::getType, SpaceConstant.INDEX)
                .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
        if (null != mapTagEdge && CollUtil.isNotEmpty(tagEdgeProperties)) {
          if (!StringUtil.isBlank(mapTagEdge.getTtlCol())
              && SpaceConstant.INDEX != mapTagEdge.getTtlDuration()) {
            for (KnowledgeGraphTagEdgePropertyPo property : tagEdgeProperties) {
              if (property.getPropertyName().equals(mapTagEdge.getTtlCol()) && !StringUtil.isBlank(
                  property.getDefaultValue()) && property.getTagRequired() == SpaceConstant.INDEX) {
                if (!isNotExpired(property.getDefaultValue(), mapTagEdge.getTtlDuration())) {
                  // 已经过期
                  log.error("【属性值已经过期，新增失败 :{}】", mapTagEdge.getTtlCol());
                  errorInfo.append(SpaceConstant.ENTITY_NAME_CHECK_ERROR);
                }
              }
            }
          }
        }
      }
      if (resourceData.get() * SpaceConstant.UP < spaceData.get() + SpaceConstant.REPLICA_FACTOR) {
        // 删除
        deleteVertexIds(entityIds, spaceId);
        deleteEdges(edges, spaceId);
        log.error("【文件数据超过空间数据上限，导入失败!】");
        throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_IMPORT_ERROR);
      }

      graphRelationEntityDO.setName("\'" + subjectName.replace(SpaceConstant.SINGLE_QUOTES,
          SpaceConstant.SINGLE_QUOTES_CHANGE) + "\'");
      graphEdgeService.saveSubject(graphRelationEntityDO);
      // 存储向量数据库
      saveVector(graphRelationEntityDO, id, subjectName);
      entityIds.add(id);
      spaceData.addAndGet(SpaceConstant.REPLICA_FACTOR);
    }
  }


  private void saveVector(GraphRelationEntityDO graphRelationEntityDO, String id,
      String subjectName) {

    //向量数据库存储
    SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
    saveEntityDTO.setSpaceId(graphRelationEntityDO.getSpaceId());
    saveEntityDTO.setEntityId(id);
    saveEntityDTO.setEntityName(subjectName);
    List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
    SaveTagInfoDTO tag = new SaveTagInfoDTO();
    tag.setTagName(graphRelationEntityDO.getTagName());
    saveTagInfoDTOS.add(tag);
    saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
    // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
  }


  private String checkObject(String space, String spaceId, String objectTagName, String objectName,
      GraphRelationDO graphRelationDO, List<Entity> objects, StringBuilder errorInfo,
      Map<String, String> entities, Set<String> entityIds, AtomicInteger resourceData,
      AtomicInteger spaceData, Set<EdgeDTO> edges) {
    String id = UniqueIDGenerator.generateUniqueID();
    KnowledgeGraphTagEdgePo mapTagEdge = knowledgeGraphTagEdgeConverter.dtoToPo(
        knowledgeGraphTagEdgeService.getTagInfo(Long.valueOf(space), objectName));
    if (!org.springframework.util.CollectionUtils.isEmpty(objects)) {
      if (objects.size() > 1) {
//                新建
        saveEntityForSubject(spaceId, objectTagName, objectName, id, errorInfo, mapTagEdge,
            entityIds, resourceData, spaceData, edges);
        // mysql记录
//                relationDataService.saveObject(saveRelationVO,id);
        if (StringUtil.isBlank(errorInfo.toString())) {
          graphRelationDO.setObjectId(id);
        }
      } else {
        graphRelationDO.setObjectId(objects.get(SpaceConstant.INDEX).getEntityId());
        id = objects.get(SpaceConstant.INDEX).getEntityId();
      }
    } else {
      // 新建
      if (!entities.containsKey(objectName)) {
        saveEntityForSubject(spaceId, objectTagName, objectName, id, errorInfo, mapTagEdge,
            entityIds, resourceData, spaceData, edges);
        if (StringUtil.isBlank(errorInfo.toString())) {
          graphRelationDO.setObjectId(id);
          entities.put(objectName, id);
        }
      } else {
        id = entities.get(objectName);
      }
    }

    return id;
  }


  private List<Entity> getSubjectInfo(String spaceId, String subjectTagName, String subjectName) {
    log.info("【Process Kg-webserver-web check have entity :  {} 】", subjectName, spaceId);
    List<Entity> entities = new ArrayList<>();
    GraphEntityRelationDO graphEntityRelationDO = new GraphEntityRelationDO();
    graphEntityRelationDO.setSpaceId(spaceId);
    graphEntityRelationDO.setSubjectName(
        subjectName.replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
    graphEntityRelationDO.setSubjectTagName(subjectTagName);
    List<NgVertex<String>> objectInfos = graphEdgeService.getSubjectInfo(graphEntityRelationDO);
    objectInfos.stream().forEach(o -> {
      Entity entity = new Entity();
      entity.setEntityId(o.getVid());
      entity.setTagName(o.getTags().get(SpaceConstant.INDEX));
      LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) o.getProperties()
          .get(entity.getTagName());
      if (null != properties && null != properties.get(SpaceConstant.NAME)) {
        entity.setEntityName((String) properties.get(SpaceConstant.NAME));
      }
      entities.add(entity);
    });
    return entities;
  }

  private List<Entity> getObjectInfo(String spaceId, String objectTagName, String objectName) {
    log.info("【Process Kg-webserver-web check have entity :  {} 】", objectName, spaceId);
    List<Entity> entities = new ArrayList<>();
    GraphEntityRelationDO graphEntityRelationDO = new GraphEntityRelationDO();
    graphEntityRelationDO.setSpaceId(spaceId);

    graphEntityRelationDO.setObjectName(
        objectName.replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
    graphEntityRelationDO.setObjectTagName(objectTagName);
    List<NgVertex<String>> subjectInfo = graphEdgeService.getObjectInfo(graphEntityRelationDO);
    subjectInfo.stream().forEach(o -> {
      Entity entity = new Entity();
      entity.setEntityId(o.getVid());
      entity.setTagName(o.getTags().get(SpaceConstant.INDEX));
      LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) o.getProperties()
          .get(entity.getTagName());
      if (null != properties && null != properties.get(SpaceConstant.NAME)) {
        entity.setEntityName((String) properties.get(SpaceConstant.NAME));
      }
      entities.add(entity);
    });
    return entities;

  }

  private List<String> getEdgeData(String sheetName, int type, Long spaceId) {
    List<String> result = new ArrayList<>();
    log.info("【Mysql get all relateion for Tag: {} 】", sheetName);
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, sheetName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, type)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    if (!CollectionUtils.isEmpty(mapTagEdges)) {
      Integer tagEdgeId = mapTagEdges.getFirst().getTagEdgeId();
      List<KnowledgeGraphTagEdgePropertyPo> tagEdges = knowledgeGraphTagEdgePropertyMapper.selectList(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgeId)
              .eq(KnowledgeGraphTagEdgePropertyPo::getType, type)
              .orderByAsc(KnowledgeGraphTagEdgePropertyPo::getPropertyId));
      Collections.addAll(result, SpaceConstant.SUBJECT_NAME, SpaceConstant.SUBJECT_VALUE,
          SpaceConstant.OBJECT_NAME, SpaceConstant.OBJECT_VALUE);
      if (!CollectionUtils.isEmpty(tagEdges)) {
        tagEdges.stream().forEach(entity -> {
          if (!list.contains(entity.getExtra())) {
            StringBuilder builder = new StringBuilder();
            builder.append(entity.getPropertyName()).append(SpaceConstant.TAG_SPACE)
                .append(SpaceConstant.FIX_TAG_NAME_START);
            if (entity.getTagRequired() == SpaceConstant.INDEX) {
              builder.append(SpaceConstant.YES).append(SpaceConstant.SEPARATOR);
            }
            builder.append(entity.getPropertyType()).append(SpaceConstant.FIX_TAG_NAME_SUX);
            result.add(builder.toString().trim());
          }
        });
      }
    }
    return result;

  }


  private Map<String, String> getEdgeDataMast(String sheetName, int type, Long spaceId,
      String propertyName) {
    Map<String, String> map = new HashMap<>();
    log.info("【Mysql get all relateion for Tag: {} 】", sheetName);
    List<KnowledgeGraphTagEdgePo> mapTagEdges = knowledgeGraphTagEdgeMapper.selectList(
        Wrappers.<KnowledgeGraphTagEdgePo>lambdaQuery()
            .eq(KnowledgeGraphTagEdgePo::getTagName, sheetName)
            .eq(KnowledgeGraphTagEdgePo::getSpaceId, spaceId)
            .eq(KnowledgeGraphTagEdgePo::getType, type)
            .orderByAsc(KnowledgeGraphTagEdgePo::getTagEdgeId));
    if (!CollectionUtils.isEmpty(mapTagEdges)) {
      Integer tagEdgeId = mapTagEdges.getFirst().getTagEdgeId();
      KnowledgeGraphTagEdgePropertyPo tagEdges = knowledgeGraphTagEdgePropertyMapper.selectOne(
          Wrappers.<KnowledgeGraphTagEdgePropertyPo>lambdaQuery()
              .eq(KnowledgeGraphTagEdgePropertyPo::getTagEdgeId, tagEdgeId)
              .eq(KnowledgeGraphTagEdgePropertyPo::getPropertyName, propertyName)
              .eq(KnowledgeGraphTagEdgePropertyPo::getType, type), false);
      if (ObjectUtil.isNotNull(tagEdges)) {
        StringBuilder param = new StringBuilder();
        if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.FIX_STRING)
            || tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.STRING)) {
          param.append(JSON.toJSONString(tagEdges.getDefaultValue().toString()))
              .append(SpaceConstant.TAG_SPACE);
        } else if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.DOUBLE)
            || tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.FLOAT)) {
          double doubleValue = Double.parseDouble(tagEdges.getDefaultValue());

          boolean isInteger = (doubleValue == Math.floor(doubleValue));
          // 如果是整数，则显式添加"00"作为小数部分
          String formatValue = isInteger ? String.format("%.2f", doubleValue)
              : String.format("%.15f", doubleValue);// 尝试解析为double
          param.append(formatValue).append(SpaceConstant.TAG_SPACE);
        } else if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.DATE)) {
          // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD"格式的字符串
          String dateValue = "date(\"" + tagEdges.getDefaultValue() + "\")";
          param.append(dateValue).append(SpaceConstant.TAG_SPACE);
        } else if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.TIME)) {
          // 注意：这里假设time只包含小时、分钟和秒，不包括毫秒
          // 你可能需要调整以适应你的具体需求
          String timeValue = "time(\"" + tagEdges.getDefaultValue() + "\")";
          param.append(timeValue).append(SpaceConstant.TAG_SPACE);
        } else if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.DATETIME)) {
          // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD HH:mm:ss"格式的字符串
          // 我们需要添加毫秒（假设为0）以符合某些数据库或系统的datetime格式
          String datetimeValue = "datetime(\"" + tagEdges.getDefaultValue() + ".000000\")";
          param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
        } else if (tagEdges.getPropertyType().equalsIgnoreCase(SpaceConstant.TIMESTAMP)) {
          String datetimeValue = "timestamp(\"" + tagEdges.getDefaultValue() + ".000000\")";
          param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
// 输出最终构建的字符串（如果需要）

        } else {
          param.append(tagEdges.getDefaultValue()).append(SpaceConstant.TAG_SPACE);
        }
        map.put(param.toString().trim(), tagEdges.getDefaultValue());
        return map;
      }

    }

    return map;

  }

  /**
   * 存数据到图数据库
   *
   * @param successDataList
   * @param
   * @param
   * @param
   */
  private void saveGraph(List<String> successDataList, Map<Integer, Map<String, String>> map,
      GraphEntityDO entityDO, StringJoiner properties, int t,
      Map<Integer, String> excelHeadIdxNameMap, ImportHeadDTO importHeadDTO,
      Map<String, String> vectorMap, Set<String> entityIds, AtomicInteger resourceData,
      AtomicInteger spaceData, String space) {
    if (CollUtil.isNotEmpty(successDataList)) {
      String id = UniqueIDGenerator.generateUniqueID();
      entityDO.setVid(id);
      List<EntityPropertiesVO> entityProperties = new ArrayList<>();
      for (int i = SpaceConstant.INDEX; i < successDataList.size(); i++) {
        if (!StringUtil.isBlank(successDataList.get(i))) {
          EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
          entityPropertiesVO.setPropertyValue(successDataList.get(i));
          Map<String, String> currentMap = map.get(i - SpaceConstant.INDEX);
          if (currentMap != null) {
            for (Map.Entry<String, String> entry : currentMap.entrySet()) {
              entityPropertiesVO.setPropertyName(entry.getKey());
              entityPropertiesVO.setPropertyType(entry.getValue());
            }

          }
          entityProperties.add(entityPropertiesVO);
        }
      }
      processEntity(entityDO, entityProperties, properties, t, id, excelHeadIdxNameMap, entityDO,
          importHeadDTO, vectorMap, entityIds, resourceData, spaceData, space);

      // 新增到图数据库
//            graphVertexService.saveEntity(entityDO);
//            新增到集合中

    }
  }

  private void processEntity(GraphEntityDO graphEntityDO,
      List<EntityPropertiesVO> entityPropertiesVOS, StringJoiner properties, int t, String id,
      Map<Integer, String> excelHeadIdxNameMap, GraphEntityDO entityDO, ImportHeadDTO importHeadDTO,
      Map<String, String> vectorMap, Set<String> entityIds, AtomicInteger resourceData,
      AtomicInteger spaceData, String space) {
    StringJoiner key = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner vector = new StringJoiner(SpaceConstant.TAG_SPLIT);
    StringJoiner value = new StringJoiner(SpaceConstant.TAG_SPLIT);
    int number = SpaceConstant.REPLICA_FACTOR;
    // 设置名称
    key.add(SpaceConstant.NAME);
    value.add(JSON.toJSONString(entityPropertiesVOS.get(SpaceConstant.INDEX).getPropertyValue()));
    vector.add(entityPropertiesVOS.get(SpaceConstant.INDEX).getPropertyValue());
    if (CollUtil.isNotEmpty(entityPropertiesVOS)) {
      for (int i = SpaceConstant.REPLICA_FACTOR; i < entityPropertiesVOS.size(); i++) {
        key.add("`" + entityPropertiesVOS.get(i).getPropertyName() + "`");
        String buildString = processData(entityPropertiesVOS.get(i).getPropertyType(),
            entityPropertiesVOS.get(i).getPropertyValue());
        value.add(buildString);
        vector.add(buildString);
        number++;
      }
    }
    graphEntityDO.setEntityProperties(key.toString());

    if (resourceData.get() * SpaceConstant.UP < spaceData.get() + SpaceConstant.REPLICA_FACTOR) {
      // 删除
      deleteVertexIds(entityIds, space);
      log.error("【文件数据超过空间数据上限，导入失败!】");
      throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_IMPORT_ERROR);
    }
    if (number < excelHeadIdxNameMap.size()) {
      GraphEntityDO tmp = new GraphEntityDO();
      GraphEntityDO tmpVector = new GraphEntityDO();
      BeanUtils.copyProperties(entityDO, tmp);
      BeanUtils.copyProperties(entityDO, tmpVector);
      tmp.setVid(id);
      tmpVector.setVid(id);

      tmp.setEntityValue(value.toString());
      tmpVector.setEntityValue(vector.toString());
      graphVertexService.saveEntityImport(tmp);
//<nql id = "saveEntityImport" space="${ tex.spaceId }" spaceFromParam="true">
//        INSERT VERTEX `${tex.tagName}`  (${tex.entityProperties}) VALUES "${tex.vid}": (${tex.entityValue});
//    </nql>
//            // 向量数据库存储
      saveVector(tmpVector);

      if (!StringUtil.isBlank(importHeadDTO.getKey())) {
        graphEntityDO.setEntityProperties(importHeadDTO.getKey());
      }

    } else {
      importHeadDTO.setKey(key.toString());
      properties.add("\"" + id + "\"" + ":" + SpaceConstant.FIX_TAG_NAME_START + value.toString()
          + SpaceConstant.FIX_TAG_NAME_SUX);

      vectorMap.put(id, vector.toString());
    }
    spaceData.addAndGet(SpaceConstant.REPLICA_FACTOR);
    entityIds.add(id);


  }

  private void deleteVertexIds(Set<String> entityIds, String space) {
    log.info("【超过数据上限，删除已经进入的节点】");
    if (!CollectionUtils.isEmpty(entityIds)) {
      List<String> ids = new ArrayList<>();
      ids.addAll(entityIds);
      String result = entityIds.stream()
          .map(s -> SpaceConstant.DOUBLE_QUOTATION_MARKS + s + SpaceConstant.DOUBLE_QUOTATION_MARKS)
          .collect(Collectors.joining(", "));
      graphVertexService.deleteVertexInfo(space, result);

      // TODO 向量同步
//      apiVectorQuantityService.makeAsyncDelete(new DeleteVectorDTO(space, ids));
    }
  }


  private void saveVector(GraphEntityDO tmp) {
    //向量数据库存储
    SaveEntityDTO saveEntityDTO = new SaveEntityDTO();
    saveEntityDTO.setSpaceId(tmp.getSpaceId());
    saveEntityDTO.setEntityId(tmp.getVid());
    List<SaveTagInfoDTO> saveTagInfoDTOS = new ArrayList<>();
    SaveTagInfoDTO tag = new SaveTagInfoDTO();
    tag.setTagName(tmp.getTagName());
    List<EntityPropertiesVO> entityProperties = new ArrayList<>();
    String[] propertyNames = tmp.getEntityProperties().split(SpaceConstant.TAG_SPLIT);
    String[] propertyValues = tmp.getEntityValue().split(SpaceConstant.TAG_SPLIT);
    saveEntityDTO.setEntityName(propertyValues[SpaceConstant.INDEX]);
    for (int i = SpaceConstant.REPLICA_FACTOR; i < propertyNames.length; i++) {
      EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
      entityPropertiesVO.setPropertyName(propertyNames[i].replace("`", ""));
      entityPropertiesVO.setPropertyValue(propertyValues[i].replace("\"", ""));
      entityProperties.add(entityPropertiesVO);
    }
    tag.setEntityProperties(entityProperties);
    saveTagInfoDTOS.add(tag);
    saveEntityDTO.setSaveTagInfoDTO(saveTagInfoDTOS);
    // TODO 向量库
//    apiVectorQuantityService.makeAsyncCreateEntity(saveEntityDTO);
  }

  private void processRelation(GraphRelationDO graphRelationDO,
      List<EntityPropertiesVO> entityPropertiesVOS, StringJoiner relationProperties,
      Map<Integer, String> excelHeadIdxNameMap, StringBuilder errorInfo,
      ImportHeadDTO importHeadDTO, Map<String, String> relationData,
      Set<RelationVectorDTO> relationVectorDTOS, int rank, Set<EdgeDTO> edges,
      AtomicInteger resourceData, AtomicInteger spaceData, Set<String> entityIds)
      throws UnsupportedEncodingException {
    StringJoiner key = new StringJoiner(",");
    StringJoiner value = new StringJoiner(",");
    StringJoiner match = new StringJoiner(" AND ");
    List<EntityPropertiesVO> propertiesVOList = new ArrayList<>();
    // 设置名称
    int number = SpaceConstant.FOUR;

    for (int i = SpaceConstant.FOUR; i < entityPropertiesVOS.size(); i++) {
      EntityPropertiesVO entityPropertiesVO = new EntityPropertiesVO();
      key.add(SpaceConstant.QUOTATIONMARK + entityPropertiesVOS.get(i).getPropertyName()
          + SpaceConstant.QUOTATIONMARK);
      String buildString = processData(entityPropertiesVOS.get(i).getPropertyType(),
          entityPropertiesVOS.get(i).getPropertyValue());
      value.add(buildString);
      number++;
      match.add("e." + SpaceConstant.QUOTATIONMARK + entityPropertiesVOS.get(i).getPropertyName()
          + SpaceConstant.QUOTATIONMARK + (buildString.equals("NULL") ? " is " : "==")
          + buildString);

      entityPropertiesVO.setPropertyName(entityPropertiesVOS.get(i).getPropertyName());
      entityPropertiesVO.setPropertyValue(entityPropertiesVOS.get(i).getPropertyValue());
      propertiesVOList.add(entityPropertiesVO);
    }
    graphRelationDO.setEntityProperties(key.toString());
    graphRelationDO.setAttributeMatch(match.toString());
    // 查询本条关系是否存在
    ResultSet resultSet = graphEdgeMapper.getEdgeInfoExcel(graphRelationDO);

    if (!resultSet.getRows().isEmpty()) {
      if (StringUtil.isBlank(graphRelationDO.getEntityProperties())) {
        // 确保所需的列都存在
        List<String> columnNames = resultSet.getColumnNames();
        if (columnNames.contains(SpaceConstant.E)) {
          // 获取各列的值
          List<ValueWrapper> es = resultSet.colValues(SpaceConstant.E);
          // 假设所有列表的长度相同（这是处理这种情况的关键假设）
          for (int i = SpaceConstant.INDEX; i < es.size(); i++) {
            boolean flag = true;
            // 假设 ValueWrapper 有一个 asString() 方法来获取字符串表示
            HashMap<String, ValueWrapper> name = es.get(i).asMap();
            for (Map.Entry<String, ValueWrapper> entry : name.entrySet()) {
              if (!entry.getValue().isNull()) {
                flag = false;
                break;
              }
            }
            if (flag) {
              log.info("【边已经创建,请勿重复插入： {}】", graphRelationDO.getEdgeName());
              errorInfo.append(SpaceConstant.SAME);

            }
          }
        }
      } else {
        log.info("【边已经创建,请勿重复插入： {}】", graphRelationDO.getEdgeName());
        errorInfo.append(SpaceConstant.SAME);
      }
    }

    if (resourceData.get() * SpaceConstant.UP < spaceData.get() + SpaceConstant.REPLICA_FACTOR) {
      // 删除
      deleteVertexIds(entityIds, graphRelationDO.getSpaceId());
      deleteEdges(edges, graphRelationDO.getSpaceId());
      log.error("【文件数据超过空间数据上限，导入失败!】");
      throw ServiceExceptionUtil.exception(ErrorConstants.RESOURCE_DATA_IMPORT_ERROR);
    }

    if (StringUtil.isBlank(errorInfo.toString())) {
      Long rankId = System.currentTimeMillis();
      edges.add(new EdgeDTO(graphRelationDO.getSubjectId(), graphRelationDO.getEdgeName(),
          graphRelationDO.getObjectId(), rankId));
      spaceData.addAndGet(SpaceConstant.REPLICA_FACTOR);
      if (number < excelHeadIdxNameMap.size()) {
        GraphRelationDO relation = new GraphRelationDO();
        BeanUtils.copyProperties(graphRelationDO, relation);
        relation.setEntityValue(value.toString());
        relation.setRank(SpaceConstant.RANK + rankId);
        graphEdgeService.saveRelation(relation, SpaceConstant.INDEX);

        //向量存储
        saveRelationVector(relation, relationData.get(SpaceConstant.SUBJECT_VALUE),
            relationData.get(SpaceConstant.OBJECT_VALUE), propertiesVOList, rankId);

        if (!StringUtil.isBlank(importHeadDTO.getKey())) {
          graphRelationDO.setEntityProperties(importHeadDTO.getKey());
        }
      } else {
        RelationVectorDTO relationVectorDTO = new RelationVectorDTO(graphRelationDO.getSubjectId(),
            graphRelationDO.getObjectId(), relationData.get(SpaceConstant.SUBJECT_VALUE),
            relationData.get(SpaceConstant.OBJECT_VALUE), String.valueOf(rankId), propertiesVOList);
//                if (!relationVectorDTOS.contains(relationVectorDTO)) {
        relationVectorDTOS.add(relationVectorDTO);
//                    importHeadDTO.setKey(key.toString());
//                    relationProperties.add("\"" + graphRelationDO.getSubjectId() + "\"" + SpaceConstant.DIRECTION + "\"" + graphRelationDO.getObjectId() + "\"" + ":" +
//                            SpaceConstant.FIX_TAG_NAME_START + value.toString() + SpaceConstant.FIX_TAG_NAME_SUX);
//                }else {
        importHeadDTO.setKey(key.toString());
        relationProperties.add(
            "\"" + graphRelationDO.getSubjectId() + "\"" + SpaceConstant.DIRECTION + "\""
                + graphRelationDO.getObjectId() + "\"" + SpaceConstant.RANK
                + System.currentTimeMillis() + ":" + SpaceConstant.FIX_TAG_NAME_START
                + value.toString() + SpaceConstant.FIX_TAG_NAME_SUX);
//                }
      }
    }
  }

  private void deleteEdges(Set<EdgeDTO> edges, String spaceId) {
    log.info("【文件数据超过空间数据上限,删除已导入数据】");
    GraphEdgeDropDO graphEdgeDropDO = new GraphEdgeDropDO();
    graphEdgeDropDO.setSpaceId(spaceId);
    List<String> vids = new ArrayList<>();
    List<Ralation> ralations = new ArrayList<>();
    if (CollUtil.isNotEmpty(edges)) {
      edges.stream().forEach(ngEdge -> {
        Ralation ralation = new Ralation();
        ralation.setSourceId(ngEdge.getSourceId());
        ralation.setRank(ngEdge.getRank());
        ralation.setObjectId(ngEdge.getTargetId());
        ralation.setEdgeName(ngEdge.getEdge());
        ralations.add(ralation);
        vids.add(MD5Util.generateMD5(
            ralation.getSourceId() + ralation.getEdgeName() + ralation.getObjectId()));
      });

    }
    // 删除图数据库
    graphEdgeDropDO.setRalations(ralations);
    graphEdgeService.deleteRelation(graphEdgeDropDO);
    // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncDeleteRelationEntity(
//        new DeleteVectorDTO(graphEdgeDropDO.getSpaceId(), vids));
  }


  /**
   * @param graphRelationDO
   * @param
   */
  private void saveRelationVector(GraphRelationDO graphRelationDO, String subjectName,
      String objectName, List<EntityPropertiesVO> propertiesVOList, Long rankId) {
    SaveRelationDTO saveRelationDTO = new SaveRelationDTO();
    saveRelationDTO.setSpaceId(graphRelationDO.getSpaceId());
    saveRelationDTO.setSubjectId(graphRelationDO.getSubjectId());
    saveRelationDTO.setSubjectName(subjectName);
    saveRelationDTO.setRank(String.valueOf(rankId));
    saveRelationDTO.setObjectId(graphRelationDO.getObjectId());
    saveRelationDTO.setObjectName(objectName);
    saveRelationDTO.setEdgeName(graphRelationDO.getEdgeName());
    saveRelationDTO.setEntityProperties(propertiesVOList);
    // TODO 向量数据库
//    apiVectorQuantityService.makeAsyncCreateRelation(saveRelationDTO);
  }


  private String createErrorExcel(Map<Map<String, List<String>>, List<List<String>>> errorMap,
      String spaceId, int type) {
    // 判断是否有 jobId
    String targetPath =
        fileExport + "knowledge/entity/" + SpaceConstant.ERROR_FILE + SpaceConstant.SPACE_NAME_FIX
            + spaceId + SpaceConstant.DIAGONAL;
    // 创建临时上传目录
    File dir = new File(targetPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String fileName = SnowflakeIdUtils.getDefaultSnowFlakeId() + ".xlsx";
    // 生成文件路径
    String returnUrl = targetPath + fileName;
    log.info("【write error data into excel】", spaceId);
    try (ExcelWriter excelWriter = EasyExcel.write(returnUrl).build()) {
      for (Map<String, List<String>> key : errorMap.keySet()) {
        for (String value : key.keySet()) {
          // 写入第一个Sheet
          WriteSheet sheet1 = EasyExcel.writerSheet(value).head(buildHeaders(key.get(value), type))
              .build();
          excelWriter.write(errorMap.get(key), sheet1);
        }
      }
      // 刷新输出流，确保数据被发送到客户端
      excelWriter.finish();
    } catch (Exception e) {
      e.printStackTrace(); // 或者使用更合适的日志记录方式
      log.error("写入 Excel 文件时出错: " + returnUrl, e);
    }
    return StrUtil.replaceFirst(returnUrl, "/data1", "/file");
  }


  // 构建表头
  private List<List<String>> buildHeaders(List<String> dynamicHeaders, int type) {
    List<List<String>> headers = new ArrayList<>();
    // 添加动态表头
    for (String dynamicHeader : dynamicHeaders) {
      List<String> dynamicHeaderRow = new ArrayList<>();
      dynamicHeaderRow.add(dynamicHeader);
      headers.add(dynamicHeaderRow);
    }

    List<String> lastRow = new ArrayList<>();
    lastRow.add(SpaceConstant.EXCEL_ERROR);
    headers.add(lastRow);
    return headers;
  }

  private String checkData(String value, String s, StringBuilder errorInfo,
      Map<Integer, Map<String, String>> map, int index, int type, String tagEdgeName, Long spaceId,
      KnowledgeGraphTagEdgePo mapTagEdge, List<String> md5List) {
    md5List.add(s);
    if (SpaceConstant.INDEX == type) {
      if (SpaceConstant.ENTITY_NAME.equals(value)) {
        if (StringUtil.isBlank(s)) {
          errorInfo.append(SpaceConstant.ENTITY_NAME_YES);
        } else if (!isValidString(s)) {
          errorInfo.append(SpaceConstant.ENTITY_NAME_CHECK);
        }
      } else {
        // 校验字段类型/
        String[] str = value.trim().split(" ");
        return splitType(str[SpaceConstant.REPLICA_FACTOR], s, errorInfo, str[SpaceConstant.INDEX],
            map, index, str[SpaceConstant.INDEX], type, tagEdgeName, spaceId, mapTagEdge);
      }

    } else {
      if (SpaceConstant.RELATION_NAME.equals(value) || SpaceConstant.SUBJECT_VALUE.equals(value)
          || SpaceConstant.OBJECT_NAME.equals(value) || SpaceConstant.OBJECT_VALUE.equals(value)) {
        if (StringUtil.isBlank(s)) {
          if (SpaceConstant.SUBJECT_VALUE.equals(value) || SpaceConstant.OBJECT_VALUE.equals(
              value)) {
            errorInfo.append(SpaceConstant.ENTITY_RELATION_YES);
          } else {
            errorInfo.append(SpaceConstant.TYPE_RELATION_YES);
          }
        } else if (!isValidString(s)) {
          if (SpaceConstant.SUBJECT_VALUE.equals(value)) {
            errorInfo.append(SpaceConstant.ENTITY_SUBJECT_CHECK);
          } else if (SpaceConstant.OBJECT_VALUE.equals(value)) {
            errorInfo.append(SpaceConstant.ENTITY_OBJECT_CHECK);
          }
        }
      } else {
        // 校验字段类型/
        String[] str = value.trim().split(" ");
        return splitType(str[SpaceConstant.REPLICA_FACTOR], s, errorInfo, str[SpaceConstant.INDEX],
            map, index, str[SpaceConstant.INDEX], type, tagEdgeName, spaceId, mapTagEdge);
      }
    }

    return new String();
  }

  public boolean isValidString(String input) {
    if (!LocaleContextHolder.getLocale().toString().equals("ar_EG")) {

      return input != null && input.matches(SpaceConstant.PATTERN_STRING);
    } else {
      return input != null;
    }
  }

  // 校验类型是否填写正确和是否必填
  public String splitType(String input, String value, StringBuilder errorInfo, String key,
      Map<Integer, Map<String, String>> map, int index, String name, int type, String tagEdgeName,
      Long spaceId, KnowledgeGraphTagEdgePo mapTagEdge) {
    String propertyCheck = "";
    String withoutBrackets = input.replaceAll("\\(|\\)", "");
    if (withoutBrackets.contains(SpaceConstant.CHECK)) {
      // 使用"必填/"进行拆分，并取剩余部分作为类型
      String[] parts = withoutBrackets.split(SpaceConstant.SEPARATOR);
      if (SpaceConstant.YES.equals(parts[SpaceConstant.INDEX]) && StringUtil.isBlank(value)) {
        Map<String, String> result = getEdgeDataMast(tagEdgeName, type, spaceId, key);
        Map.Entry<String, String> entry = result.entrySet().iterator().next();
        Map<String, String> property = new HashMap<>();
        property.put(name, entry.getKey());
        propertyCheck = entry.getKey();
        map.put(index, property);
        if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
            && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX && key.contains(
            mapTagEdge.getTtlCol())) {
          if (!StringUtil.isBlank(entry.getValue()) && result.size() > SpaceConstant.INDEX) {
            if (!isNotExpired(entry.getValue(), mapTagEdge.getTtlDuration())) {
              // 已经过期
              log.error("【属性值已经过期，更新失败 :{}】", mapTagEdge.getTtlCol());
              errorInfo.append(SpaceConstant.PROPERRY_CHACK + key + SpaceConstant.TTL_TYPE_ERROR);
            }
          }
        }
        return entry.getKey();
      } else {
        // 校验类型
        if (parts[SpaceConstant.REPLICA_FACTOR].contains(SpaceConstant.FIX_STRING)) {
          parts[SpaceConstant.REPLICA_FACTOR] = SpaceConstant.FIX_STRING;
        }
        Map<String, String> property = new HashMap<>();
        property.put(name, parts[SpaceConstant.REPLICA_FACTOR]);
        propertyCheck = value;
        map.put(index, property);
        try {
          if (!PropertyValidator.validatePropertyType(parts[SpaceConstant.REPLICA_FACTOR], value)) {
            errorInfo.append(SpaceConstant.PROPERRY_CHACK + key + SpaceConstant.TYPE_ERROR);
          }
        } catch (Exception e) {
          log.error("【 update knowledge verification information extraction  error】", e);
          throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
        }
      }
    } else {
      if (!StringUtil.isBlank(value)) {
        Map<String, String> property = new HashMap<>();
        property.put(name, withoutBrackets);
        propertyCheck = value;
        map.put(index, property);
        try {
          if (!PropertyValidator.validatePropertyType(withoutBrackets, value)) {
            errorInfo.append(SpaceConstant.PROPERRY_CHACK + key + SpaceConstant.TYPE_ERROR);
          }
        } catch (Exception e) {
          log.error("【 update knowledge verification information extraction  error】", e);
          throw ServiceExceptionUtil.exception(ErrorConstants.INPUT_ERROR_RANGE);
        }
      }
    }
    if (mapTagEdge != null && !StringUtil.isBlank(mapTagEdge.getTtlCol())
        && mapTagEdge.getTtlDuration() != SpaceConstant.INDEX && key.contains(
        mapTagEdge.getTtlCol())) {
      if (!StringUtil.isBlank(propertyCheck)) {
        if (!isNotExpired(propertyCheck, mapTagEdge.getTtlDuration())) {
          // 已经过期
          log.error("【属性值已经过期，更新失败 :{}】", mapTagEdge.getTtlCol());
          errorInfo.append(SpaceConstant.PROPERRY_CHACK + key + SpaceConstant.TTL_TYPE_ERROR);
        }
      }
    }
    return new String();

  }


  private boolean isNotExpired(String result, int ttlDuration) {
    LocalDateTime now = LocalDateTime.now();

    // 定义多个可能的日期格式
    DateTimeFormatter[] formatters = {DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss")};

    LocalDateTime resultDateTime = null;

    // 尝试解析输入时间字符串
    for (DateTimeFormatter formatter : formatters) {
      try {
        resultDateTime = LocalDateTime.parse(result, formatter);
        break; // 成功解析后退出循环
      } catch (DateTimeParseException e) {
        // 继续尝试下一个格式
      }
    }

    // 如果无法解析，抛出异常或返回 false
    if (resultDateTime == null) {
      throw new IllegalArgumentException("Invalid date format: " + result);
    }

    // 计算 resultDateTime 加上 TTL 持续时间后的时间
    LocalDateTime expiryDateTime = resultDateTime.plus(ttlDuration, ChronoUnit.HOURS);

    // 判断是否不过期
    return resultDateTime.isAfter(now) || expiryDateTime.isAfter(now);

  }

  @Override
  public String processData(String propertyType, String propertyValue) {
    StringBuilder param = new StringBuilder();
    if (propertyType.equalsIgnoreCase(SpaceConstant.FIX_STRING) || propertyType.equalsIgnoreCase(
        SpaceConstant.STRING)) {
      param.append(JSON.toJSONString(propertyValue.toString())).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DOUBLE) || propertyType.equalsIgnoreCase(
        SpaceConstant.FLOAT)) {

      double doubleValue = Double.parseDouble(propertyValue);

      boolean isInteger = (doubleValue == Math.floor(doubleValue));
      // 如果是整数，则显式添加"00"作为小数部分
      String formatValue = isInteger ? String.format("%.2f", doubleValue)
          : String.format("%.15f", doubleValue);// 尝试解析为double
      param.append(formatValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATE)) {
      // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD"格式的字符串
      String dateValue = "date(\"" + propertyValue + "\")";
      param.append(dateValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIME)) {
      // 注意：这里假设time只包含小时、分钟和秒，不包括毫秒
      // 你可能需要调整以适应你的具体需求
      String timeValue = "time(\"" + propertyValue + "\")";
      param.append(timeValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.DATETIME)) {
      // 假设tagTypes.getDefaultValue()返回的是"YYYY-MM-DD HH:mm:ss"格式的字符串
      // 我们需要添加毫秒（假设为0）以符合某些数据库或系统的datetime格式
      String datetimeValue = "datetime(\"" + propertyValue + ".000000\")";
      param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
    } else if (propertyType.equalsIgnoreCase(SpaceConstant.TIMESTAMP)) {
      String datetimeValue = "timestamp(\"" + propertyValue + ".000000\")";
      param.append(datetimeValue).append(SpaceConstant.TAG_SPACE);
// 输出最终构建的字符串（如果需要）
    } else {
      param.append(propertyValue).append(SpaceConstant.TAG_SPACE);
    }
    return param.toString().trim();
  }


  private void checkDynamicHeadMatch(Map<Integer, String> head, List<String> dynamicHeaders,
      StringBuilder errorInfo) {
    String firstValue = head.values().iterator().next();
    if (!SpaceConstant.ENTITY_NAME.equals(firstValue)) {
      if (!errorInfo.toString().contains(SpaceConstant.ENTITY_EXCEL_CHECK)) {
        errorInfo.append(SpaceConstant.ENTITY_EXCEL_CHECK);
      }
    }
    List<Integer> keys = new ArrayList<>(head.keySet());
    if (keys.size() == dynamicHeaders.size()) {
      for (int i = SpaceConstant.REPLICA_FACTOR; i < keys.size(); i++) {
        Integer key = keys.get(i);
        if (!head.get(key).equals(dynamicHeaders.get(i))) {
          if (!errorInfo.toString().contains(SpaceConstant.ENTITY_EXCEL_CHECK)) {
            errorInfo.append(SpaceConstant.ENTITY_EXCEL_CHECK);
          }
        }
      }
    } else {
      if (!errorInfo.toString().contains(SpaceConstant.ENTITY_EXCEL_CHECK)) {
        errorInfo.append(SpaceConstant.ENTITY_EXCEL_CHECK);
      }
    }

  }

  private List<BaseExport> getRelationData(ExportAllDataVO exportDataVO) {
    exportDataVO.setEntityName(exportDataVO.getSubjectName());
    log.info("【Mysql get all relateion for Tag: {} 】", exportDataVO.getTagEdgeId());
    List<BaseExport> baseExports = new ArrayList<>();
    Set<String> dynamicKeys = new HashSet<>();
    if (!StringUtil.isBlank(exportDataVO.getEntityName())) {
      exportDataVO.setEntityName(exportDataVO.getEntityName()
          .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
    }
    List<NgEdge<String>> vertexes = graphEdgeService.getExportAllData(
        SpaceConstant.SPACE_NAME_FIX + exportDataVO.getSpaceId(), exportDataVO.getTagEdgeName(),
        exportDataVO.getEntityName(), exportDataVO.getSubjectTagName(),
        exportDataVO.getObjectTagName());
    List<EdgeListVO> edgeListVOS = new ArrayList<>();
    Map<String, EntityTagVO> entityMap = new HashMap<>();
    Set<String> entityList = new HashSet<>();
    vertexes.stream().forEach(v -> {
      EdgeListVO edgeListVO = new EdgeListVO();
      edgeListVO.setSubjectId(v.getSrcID());
      edgeListVO.setObjectId(v.getDstID());
      edgeListVO.setEdgeName(v.getEdgeName());
      // 查询本体id
      entityList.add(String.valueOf(edgeListVO.getSubjectId()));
      entityList.add(String.valueOf(edgeListVO.getObjectId()));
      edgeListVOS.add(edgeListVO);

      if (CollUtil.isNotEmpty(v.getProperties())) {
        Map<String, Object> dynamicProperties = new LinkedHashMap<>();
        Map<String, Object> properties = v.getProperties();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
          if (!entry.getKey().equals(SpaceConstant.NAME)) {
            dynamicProperties.put(entry.getKey(), entry.getValue());
            dynamicKeys.add(entry.getKey());
          }

        }

        edgeListVO.setDynamicProperties(dynamicProperties);
      }
      exportDataVO.setDynamicKeys(new ArrayList<>(dynamicKeys));
    });

    if (!org.springframework.util.CollectionUtils.isEmpty(vertexes)) {

      // 解析查询数据
      processEntityData(entityList, SpaceConstant.SPACE_NAME_FIX + exportDataVO.getSpaceId(),
          entityMap);
      // 设置 entityName 和tagName
      edgeListVOS.stream().forEach(v -> {
        List<Long> tagId = new ArrayList<>();
        List<Long> objectTagId = new ArrayList<>();
        EntityTagVO subjuctEntity = entityMap.get(v.getSubjectId());
        v.setSubjectTagName(subjuctEntity.getTagName());
        v.setSubjectName(subjuctEntity.getEntityName());
        if (!StringUtil.isBlank(v.getSubjectTagName())) {
          for (String s : v.getSubjectTagName().split(SpaceConstant.TAG_SPLIT_INDEX)) {
            tagId.add(Long.valueOf(
                knowledgeGraphTagEdgeService.getTagInfo(exportDataVO.getSpaceId(), s)
                    .getTagEdgeId()));
          }
          v.setSubjectTagId(tagId);
        }
        EntityTagVO objectEntity = entityMap.get(v.getObjectId());
        v.setObjectTagName(objectEntity.getTagName());
        v.setObjectName(objectEntity.getEntityName());
        if (!StringUtil.isBlank(v.getObjectTagName())) {
          for (String s : v.getObjectTagName().split(SpaceConstant.TAG_SPLIT_INDEX)) {
            objectTagId.add(Long.valueOf(
                knowledgeGraphTagEdgeService.getTagInfo(exportDataVO.getSpaceId(), s)
                    .getTagEdgeId()));
          }
          v.setObjectTagId(objectTagId);
        }

        RelationExport relationExport = new RelationExport(subjuctEntity.getTagName(),
            subjuctEntity.getEntityName(), v.getEdgeName(), objectEntity.getTagName(),
            objectEntity.getEntityName(), v.getDynamicProperties());
        baseExports.add(relationExport);
      });
    }

    return baseExports;


  }


  /**
   * 解析实体数据
   *
   * @param entityList
   * @param spaceId
   * @param entityMap
   */
  private void processEntityData(Set<String> entityList, String spaceId,
      Map<String, EntityTagVO> entityMap) {
    List<NgVertex<String>> ngVertex = graphVertexService.getEntitySet(entityList, spaceId);
    if (!org.springframework.util.CollectionUtils.isEmpty(ngVertex)) {
      for (NgVertex<String> vertex : ngVertex) {
        EntityTagVO entityTagVO = new EntityTagVO();
        entityTagVO.setTagName(
            vertex.getTags().stream().collect(Collectors.joining(SpaceConstant.TAG_SPLIT_INDEX)));
        Map<String, Object> properties = vertex.getProperties();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
          LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
          entityTagVO.setEntityName(entryValue.get(SpaceConstant.NAME));
        }
        entityMap.put(vertex.getVid(), entityTagVO);
      }
    }

  }

  private List<BaseExport> getEntityData(ExportAllDataVO exportDataVO) {
    log.info("【Mysql get all entity for Tag: {} 】", exportDataVO.getTagEdgeId());
    List<BaseExport> baseExports = new ArrayList<>();
    List<NgVertex<String>> ngvertexs = new ArrayList<>();
    Set<String> dynamicKeys = new HashSet<>();
    if (!StringUtil.isBlank(exportDataVO.getEntityName())) {
      exportDataVO.setEntityName(exportDataVO.getEntityName()
          .replace(SpaceConstant.SINGLE_QUOTES, SpaceConstant.SINGLE_QUOTES_CHANGE));
    }
    if (StringUtil.isBlank(exportDataVO.getTagEdgeName())) {
      ngvertexs = graphVertexService.getNgvertexsExport(
          SpaceConstant.SPACE_FIX_NAME + "_" + exportDataVO.getSpaceId(),
          exportDataVO.getEntityName());

    } else {
      ngvertexs = graphVertexService.getVertexesByTagNameExport(
          SpaceConstant.SPACE_FIX_NAME + "_" + exportDataVO.getSpaceId(),
          exportDataVO.getTagEdgeName(), exportDataVO.getEntityName());

    }
    ngvertexs.stream().forEach(v -> {
      Map<String, Object> dynamicProperties = new LinkedHashMap<>();
      EntityExport entityExport = new EntityExport();
      entityExport.setVid(v.getVid());
      Map<String, Object> properties = v.getProperties();
      for (Map.Entry<String, Object> entry : properties.entrySet()) {
        entityExport.setTagName(entry.getKey());
        LinkedHashMap<String, String> entryValue = (LinkedHashMap<String, String>) entry.getValue();
        entityExport.setEntityName(entryValue.get(SpaceConstant.NAME));

        for (Map.Entry<String, String> map : entryValue.entrySet()) {
          if (!map.getKey().equals(SpaceConstant.NAME)) {
            dynamicProperties.put(map.getKey(), map.getValue());
            dynamicKeys.add(map.getKey());
          }
        }
      }
      entityExport.setDynamicProperties(dynamicProperties);
      baseExports.add(entityExport);
    });
    exportDataVO.setDynamicKeys(new ArrayList<>(dynamicKeys));
    return baseExports;
  }
}

