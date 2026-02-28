package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.ExcelExportService;
import cn.voicecomm.ai.voicesagex.console.api.constant.graph.ErrorConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import cn.voicecomm.ai.voicesagex.console.api.nebula.enums.ExportTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.nebula.po.excel.BaseExport;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportAllDataVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.ExportDataVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.TemplateEntityVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.TemplateRelationVO;
import cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper.KnowledgeGraphTagEdgeMapper;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.CustomCellWriteHandler;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.CustomColumnWidthHandler;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.DropEntitydownHandler;
import cn.voicecomm.ai.voicesagex.console.knowledge.handle.DropdownHandler;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 实体关系导入导出模块
 *
 * @author ryc
 * @date 2025/9/16
 */
@RestController
@Tag(name = "实体关系导入导出")
@RequestMapping("/excelManage")
@Slf4j
@Validated
@RequiredArgsConstructor
public class KnowledgeGraphExcelManageController {

  private final ExcelExportService excelExportService;

  private final KnowledgeGraphTagEdgeMapper knowledgeGraphTagEdgeMapper;

  @PostMapping("/importEntity")
  @Operation(summary = "导入实体数据", description = "导入实体数据")
  public Result<Boolean> importEntity(@RequestParam("file") MultipartFile file,
      @RequestParam("spaceId") String spaceId) {
    log.info("【实体 {} 开始导入】", file.getName());
    CommonRespDto<Boolean> respDto = excelExportService.importEntity(file, spaceId,
        SpaceConstant.INDEX);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), "文件导入完成，结果稍后通知...");
  }


  @PostMapping("/importRelation")
  @Operation(summary = "导入关系数据", description = "导入关系数据")
  public Result<Boolean> importRelation(@RequestParam("file") MultipartFile file,
      @RequestParam("spaceId") String spaceId) {
    log.info("【实体 {} 开始导入】", file.getName());
    CommonRespDto<Boolean> respDto = excelExportService.importEntity(file, spaceId,
        SpaceConstant.REPLICA_FACTOR);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData(), "文件导入完成，结果稍后通知...");
  }

  @PostMapping("/excelData")
  @Operation(summary = "导出实体关系数据", description = "导出实体关系数据")
  public Result<Void> excel(HttpServletResponse response,
      @RequestBody @Validated ExportAllDataVO exportDataVO) {
    //查询所有数据
    log.info("【get all entity relation  data for space: {}】", exportDataVO.getSpaceId());
    try {
      Class<? extends BaseExport> clazz = (Class<? extends BaseExport>) Class.forName(
          ExportTypeEnum.ENTITY_DATA.getPath(exportDataVO.getType()));
      if (BaseExport.class.isAssignableFrom(clazz)) {
        List<BaseExport> list = excelExportService.getData(exportDataVO);
        List<String> dynamicKeys = exportDataVO.getDynamicKeys();
        //设置头部
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(String.valueOf(exportDataVO.getSpaceId()),
            "UTF-8");
        response.setHeader("Content-disposition",
            "attachment;filename=" + encodedFileName + ".xlsx");

        // 动态生成表头
        List<List<String>> headers = excelExportService.generateDynamicHeaders(dynamicKeys,
            exportDataVO.getType());

        // 转换数据为 Excel 格式
        List<Map<Integer, String>> exportData = excelExportService.convertToExcelData(list,
            dynamicKeys, exportDataVO.getType());

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
          WriteSheet writeSheet = EasyExcel.writerSheet(
              (StrUtil.isBlank(exportDataVO.getTagEdgeName()) ? SpaceConstant.ALL
                  : exportDataVO.getTagEdgeName())).head(headers).build();
          excelWriter.write(exportData, writeSheet);
        }
      }
    } catch (Exception e) {
      log.error("【export data error space : {} for type ： {}】", exportDataVO.getSpaceId(),
          exportDataVO.getType());
      return Result.error(ErrorConstants.EXPORT_ERROP.getMessage());
    }
    return Result.success();
  }


  @PostMapping("/excelDataPart")
  @Operation(summary = "勾选导出实体关系数据", description = "勾选导出实体关系数据")
  public Result<Void> excelDataPart(HttpServletResponse response,
      @RequestBody @Validated ExportDataVO exportDataVO) {
    //查询所有数据
    log.info("【get select  entity relation  data for space: {}】", exportDataVO.getSpaceId());
    try {
      Class<? extends BaseExport> clazz = (Class<? extends BaseExport>) Class.forName(
          ExportTypeEnum.ENTITY_DATA.getPath(exportDataVO.getType()));
      if (BaseExport.class.isAssignableFrom(clazz)) {
        List<BaseExport> list = excelExportService.getSelectData(exportDataVO);
        List<String> dynamicKeys = exportDataVO.getDynamicKeys();
        //设置头部
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(
            exportDataVO.getSpaceId() + "-" + (StrUtil.isBlank(exportDataVO.getTagEdgeName())
                ? SpaceConstant.ALL : exportDataVO.getTagEdgeName()), "UTF-8");
        response.setHeader("Content-disposition",
            "attachment;filename=" + encodedFileName + ".xlsx");

        // 动态生成表头
        List<List<String>> headers = excelExportService.generateDynamicHeaders(dynamicKeys,
            exportDataVO.getType());

        // 转换数据为 Excel 格式
        List<Map<Integer, String>> exportData = excelExportService.convertToExcelData(list,
            dynamicKeys, exportDataVO.getType());

        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build()) {
          WriteSheet writeSheet = EasyExcel.writerSheet(
              (StrUtil.isBlank(exportDataVO.getTagEdgeName()) ? SpaceConstant.ALL
                  : exportDataVO.getTagEdgeName())).head(headers).build();
          excelWriter.write(exportData, writeSheet);
        }
      }
    } catch (Exception e) {
      log.error("【export data error space : {} for type ： {}】", exportDataVO.getSpaceId(),
          exportDataVO.getType());
      return Result.error(ErrorConstants.EXPORT_ERROP.getMessage());
    }
    return Result.success();
  }


  @PostMapping("/entityTemplate")
  @Operation(summary = "实体模版下载", description = "实体模版下载")
  public Result<Void> entityTemplate(HttpServletResponse response,
      @RequestBody @Validated TemplateEntityVO templateEntityVO)
      throws UnsupportedEncodingException {
    log.info("【get all Tag  list  data for space: {}】", templateEntityVO.getSpaceId());
    String fixedHeaderName = SpaceConstant.ENTITY_NAME;
    // 设置导出的文件名
    String fileName = URLEncoder.encode(templateEntityVO.getSpaceId() + "-实体导入", "UTF-8");
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    String headerValue = "attachment; filename=" + fileName + ".xlsx";
    response.setHeader("Content-Disposition", headerValue);

    try (ExcelWriter writerBuilder = EasyExcel.write(response.getOutputStream(), null).build()) {
      // 获取 所有tag
      templateEntityVO.getTemplateList().stream().forEach(tagTemplate -> {
        // 查询 数据并填充到List<String>中
        List<String> dynamicHeaders = excelExportService.getTagData(templateEntityVO.getSpaceId(),
            tagTemplate.getTagName(), SpaceConstant.INDEX);// 动态表头
        // 写入第一个sheet
        WriteSheet sheet1 = EasyExcel.writerSheet(tagTemplate.getTagName())
            .registerWriteHandler(new DropEntitydownHandler())
            .registerWriteHandler(new CustomCellWriteHandler())
            .head(buildHeadersEntity(dynamicHeaders)).build();
        writerBuilder.write(new ArrayList<>(), sheet1);
      });
      WriteSheet explanationSheet = EasyExcel.writerSheet("说明")
          .registerWriteHandler(new CustomColumnWidthHandler()).build();
      // 创建说明数据
      // 写入说明数据
      writerBuilder.write(getExplanationData(), explanationSheet);
      // 刷新输出流，确保数据被发送到客户端
      writerBuilder.finish();
      return Result.success();
    } catch (Exception e) {
      log.error("【export  entity tmplate error space : {} for type ： {}】",
          templateEntityVO.getSpaceId(), e.getMessage());
      return Result.error(ErrorConstants.GET_FILE.getMessage());
    }
  }

  private Collection<?> getExplanationData() {
    List<List<String>> explanationData = new ArrayList<>();
    List<String> explanationRow = new ArrayList<>();
    List<String> explanationRow1 = new ArrayList<>();
    List<String> explanationRow2 = new ArrayList<>();
    explanationRow.add(SpaceConstant.ENTITY_EXPLAIN_ONE);
    explanationRow1.add(SpaceConstant.ENTITY_EXPLAIN_TWO);
    explanationRow2.add(SpaceConstant.ENTITY_EXPLAIN_THREE);
    explanationData.add(explanationRow);
    explanationData.add(explanationRow1);
    explanationData.add(explanationRow2);
    return explanationData;
  }


  @PostMapping("/relateionTemplate")
  @Operation(summary = "关系模版下载", description = "关系模版下载")
  public Result<Void> template(HttpServletResponse response,
      @RequestBody @Validated TemplateRelationVO templateRelationVO)
      throws UnsupportedEncodingException {
    log.info("【get all edge  list  data for space: {}】", templateRelationVO.getSpaceId());
    // 设置导出的文件名
    String fileName = URLEncoder.encode(templateRelationVO.getSpaceId() + "-关系导入", "UTF-8");
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    String headerValue = "attachment; filename=''" + fileName + ".xlsx";
    response.setHeader("Content-Disposition", headerValue);
    try (ExcelWriter writerBuilder = EasyExcel.write(response.getOutputStream(), null).build()) {
      // 获取 所有tag
      templateRelationVO.getTemplateList().stream().forEach(tagTemplate -> {
        // 查询 数据并填充到List<String>中
        List<String> dynamicHeaders = excelExportService.getTagData(templateRelationVO.getSpaceId(),
            tagTemplate.getEdgeName(), SpaceConstant.REPLICA_FACTOR);// 动态表头
        // 写入第一个sheet
        WriteSheet sheet1 = EasyExcel.writerSheet(tagTemplate.getEdgeName()).registerWriteHandler(
                new DropdownHandler(templateRelationVO.getSpaceId(), knowledgeGraphTagEdgeMapper))
            .registerWriteHandler(new DropEntitydownHandler()).head(buildHeaders(dynamicHeaders))
            .build();
        writerBuilder.write(new ArrayList<>(), sheet1);
      });

      WriteSheet explanationSheet = EasyExcel.writerSheet("说明")
          .registerWriteHandler(new CustomColumnWidthHandler()).build();
      // 创建说明数据
      // 写入说明数据
      writerBuilder.write(getRelationExplanationData(), explanationSheet);
      // 刷新输出流，确保数据被发送到客户端
      writerBuilder.finish();
      return Result.success();
    } catch (Exception e) {
      log.error("【export  entity tmplate error space : {} for type ： {}】",
          templateRelationVO.getSpaceId());
      return Result.error(ErrorConstants.EXPORT_ERROP.getMessage());
    }
  }

  private Collection<?> getRelationExplanationData() {
    List<List<String>> explanationData = new ArrayList<>();
    List<String> explanationRow = new ArrayList<>();
    List<String> explanationRow1 = new ArrayList<>();
    List<String> explanationRow2 = new ArrayList<>();
    List<String> explanationRow3 = new ArrayList<>();
    List<String> explanationRow4 = new ArrayList<>();
    explanationRow.add(SpaceConstant.RELATION_EXPLAIN_ONE);
    explanationRow1.add(SpaceConstant.RELATION_EXPLAIN_TWO);
    explanationRow2.add(SpaceConstant.RELATION_EXPLAIN_THREE);
    explanationRow3.add(SpaceConstant.RELATION_EXPLAIN_FORE);
    explanationRow4.add(SpaceConstant.RELATION_EXPLAIN_FIVE);
    explanationData.add(explanationRow);
    explanationData.add(explanationRow1);
    explanationData.add(explanationRow2);
    explanationData.add(explanationRow3);
    explanationData.add(explanationRow4);
    return explanationData;

  }


  // 构建表头
  private List<List<String>> buildHeadersEntity(List<String> dynamicHeaders) {
    List<List<String>> headers = new ArrayList<>();
    // 添加动态表头
    for (String dynamicHeader : dynamicHeaders) {
      List<String> dynamicHeaderRow = new ArrayList<>();
      dynamicHeaderRow.add(dynamicHeader);
      headers.add(dynamicHeaderRow);
    }
    return headers;
  }


  private List<List<String>> buildHeaders(List<String> dynamicHeaders) {
    List<List<String>> headers = new ArrayList<>();

    String[] headerNames = {SpaceConstant.SUBJECT_NAME, SpaceConstant.SUBJECT_VALUE,
        SpaceConstant.OBJECT_NAME, SpaceConstant.OBJECT_VALUE};

    for (String headerName : headerNames) {
      List<String> headerRow = new ArrayList<>();
      headerRow.add(headerName);
      headers.add(headerRow);
    }

    for (String dynamicHeader : dynamicHeaders) {
      List<String> dynamicHeaderRow = new ArrayList<>();
      dynamicHeaderRow.add(dynamicHeader);
      headers.add(dynamicHeaderRow);
    }

    return headers;
  }

}
