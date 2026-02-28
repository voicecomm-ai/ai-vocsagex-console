package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphRelationDataService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.RelationDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveRelationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ScreenTagVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SubjectInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalEdgeVO;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关系数据模块
 *
 * @author ryc
 * @date 2025/9/15
 */
@RestController
@Tag(name = "关系数据模块")
@RequestMapping("/relationDataManage")
@Slf4j
@Validated
@RequiredArgsConstructor
public class KnowledgeGraphRelationDataManageController {

  private final KnowledgeGraphRelationDataService graphRelationDataService;

  @PostMapping("/getAllTags")
  @Operation(summary = "获取图空间下关系列表", description = "获取图空间下关系列表")
  public Result<TotalEdgeVO> getAllTagEdges(@RequestBody @Validated EdgeVO edgeVO) {
    log.info("【Process Kg-webserver-web controller get all edge list for space :  {}】",
        edgeVO.getSpaceId());
    CommonRespDto<TotalEdgeVO> respDto = graphRelationDataService.getTagInfoList(
        edgeVO.getSpaceId(), edgeVO.getEdgeName());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @GetMapping("/getEdgeProperties")
  @Operation(summary = "下拉获取关系属性", description = "下拉获取关系属性")
  public Result<List<EdgePropertyResultVO>> getEdgeProperties(@RequestParam("edgeId") Long edgeId) {
    CommonRespDto<List<EdgePropertyResultVO>> respDto = graphRelationDataService.getEdgeProperties(
        edgeId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getRelations")
  @Operation(summary = "获取关系详细列表", description = "获取关系详细列表")
  public Result<PagingRespDto<EdgeListVO>> getRelations(
      @RequestBody @Validated EdgePropertyVO edgePropertyVO) {
    CommonRespDto<PagingRespDto<EdgeListVO>> respDto = graphRelationDataService.getEntities(
        edgePropertyVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/screenTag")
  @Operation(summary = "关系筛选tag类型", description = "关系筛选tag类型")
  public Result<Set<String>> screenTag(@RequestBody @Validated ScreenTagVO screenTagVO) {
    CommonRespDto<Set<String>> respDto = graphRelationDataService.screenTag(screenTagVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/deleteRelations")
  @Operation(summary = "删除关系记录", description = "删除关系记录")
  public Result<Boolean> deleteEntities(@RequestBody @Validated EdgeDeleteVO edgeDeleteVO) {
    CommonRespDto<Boolean> respDto = graphRelationDataService.deleteRelateions(edgeDeleteVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/deleteAllRelations")
  @Operation(summary = "删除关系记录(全部勾选)", description = "删除关系记录(全部勾选)")
  public Result<Boolean> deleteAllRelations(
      @RequestBody @Validated RelationDropAllVO relationDropAllVO) {
    CommonRespDto<Boolean> respDto = graphRelationDataService.deleteAllRelations(relationDropAllVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/saveRelation")
  @Operation(summary = "新增关系", description = "新增关系")
  public Result<List<SaveRelationVO>> saveRelation(
      @RequestBody @Validated List<SaveRelationVO> saveRelationVO) {
    CommonRespDto<List<SaveRelationVO>> respDto = graphRelationDataService.saveRelation(
        saveRelationVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @GetMapping("/getEntity")
  @Operation(summary = "新增关系下拉获取主体客体列表", description = "新增关系下拉获取主体客体列表")
  public Result<List<SubjectInfoVO>> getEntity(@RequestParam("spaceId") String spaceId) {
    CommonRespDto<List<SubjectInfoVO>> respDto = graphRelationDataService.getEntity(spaceId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @GetMapping("/getEdges")
  @Operation(summary = "新增关系下拉获取关系列表", description = "新增关系下拉获取关系列表")
  public Result<List<EdgeInfoVO>> getEdges(@RequestParam("spaceId") String spaceId) {
    CommonRespDto<List<EdgeInfoVO>> respDto = graphRelationDataService.getEdges(spaceId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getRelation")
  @Operation(summary = "获取关系信息", description = "获取关系信息")
  public Result<SaveRelationVO> getRelation(@RequestBody SaveRelationVO saveRelationVO) {
    CommonRespDto<SaveRelationVO> respDto = graphRelationDataService.getRelation(saveRelationVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/updateRelation")
  @Operation(summary = "编辑关系信息", description = "编辑关系信息")
  public Result<Boolean> updateRelation(@RequestBody @Validated SaveRelationVO saveRelationVO) {
    CommonRespDto<Boolean> respDto = graphRelationDataService.updateRelation(saveRelationVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


}
