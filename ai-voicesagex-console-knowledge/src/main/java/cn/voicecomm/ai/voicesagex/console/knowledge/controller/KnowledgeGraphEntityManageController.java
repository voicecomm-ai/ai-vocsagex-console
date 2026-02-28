package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphEntityManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDetailsVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityInfosVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EntityLikeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveEntityVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagEdgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TagPropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalTagInfosVO;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import java.util.List;
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
 * 实体模块
 *
 * @Author ryc
 * @Date 2025/9/8 14:15
 */

@RestController
@Tag(name = "实体模块")
@RequestMapping("/entityManage")
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphEntityManageController {

  private final KnowledgeGraphEntityManageService graphEntityManageService;

  @PostMapping("/getAllTags")
  @Operation(summary = "获取图空间下本体列表", description = "获取图空间下本体列表")
  public Result<TotalTagInfosVO> getAllTagEdges(@RequestBody @Validated TagEdgeVO tagEdgeVO) {
    log.info("【Process Kg-webserver-web controller get all tag list for space :  {}】",
        tagEdgeVO.getSpaceId());
    CommonRespDto<TotalTagInfosVO> respDto = graphEntityManageService.getTagInfoList(
        tagEdgeVO.getSpaceId(), tagEdgeVO.getTagName());
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @GetMapping("/getTagProperties")
  @Operation(summary = "获取图空间下本体属性列表", description = "获取图空间下本体属性列表")
  public Result<List<TagPropertyResultVO>> getTagProperties(
      @RequestParam("tagIds") List<Long> tagIds) {
    CommonRespDto<List<TagPropertyResultVO>> respDto = graphEntityManageService.getTagProperties(
        tagIds);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getEntities")
  @Operation(summary = "获取实体列表详细信息", description = "获取实体列表详细信息")
  public Result<PagingRespDto<EntityInfosVO>> getEntities(
      @RequestBody @Validated TagPropertyVO tagPropertyVO) {
    CommonRespDto<PagingRespDto<EntityInfosVO>> respDto = graphEntityManageService.getEntities(
        tagPropertyVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/deleteEntities")
  @Operation(summary = "删除实体记录", description = "删除实体记录")
  public Result<Boolean> deleteEntities(@RequestBody @Validated TagDeleteVO tagDeleteVO) {
    CommonRespDto<Boolean> respDto = graphEntityManageService.deleteVertex(tagDeleteVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/deleteAllEnties")
  @Operation(summary = "删除实体记录(全部勾选)", description = "删除实体记录(全部勾选)")
  public Result<Boolean> deleteAllEnties(@RequestBody @Validated EntityDropAllVO entityDropAllVO) {
    CommonRespDto<Boolean> respDto = graphEntityManageService.deleteAllVertex(entityDropAllVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/saveEntity")
  @Operation(summary = "新增实体", description = "新增实体")
  public Result<List<SaveEntityVO>> saveEntity(
      @RequestBody @Validated List<SaveEntityVO> saveEntityVOs) {
    CommonRespDto<List<SaveEntityVO>> respDto = graphEntityManageService.saveEntity(saveEntityVOs);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @GetMapping("/checkDataUp")
  @Operation(summary = "检查数据总量上限", description = "检查数据总量上限")
  public Result<Boolean> checkDataUp(@RequestParam("spaceId") Long spaceId) {
    log.info("【新增数据检查数据总量上限】");
    CommonRespDto<Boolean> respDto = graphEntityManageService.checkDataUp(spaceId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/getEntity")
  @Operation(summary = "获取实体信息", description = "获取实体信息")
  public Result<EntityDetailsVO> getEntity(@RequestBody @Validated SaveEntityVO saveEntityVO) {
    CommonRespDto<EntityDetailsVO> respDto = graphEntityManageService.getEntity(saveEntityVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/updateEntity")
  @Operation(summary = "编辑实体信息", description = "编辑实体信息")
  public Result<Boolean> updateEntity(@RequestBody @Validated EntityDetailsVO entityDetailsVO) {
    CommonRespDto<Boolean> respDto = graphEntityManageService.updateEntity(entityDetailsVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/selectLikeEntity")
  @Operation(summary = "模糊搜索实体", description = "模糊搜索实体")
  public Result<List<EntityLikeInfoVO>> selectLikeEntity(
      @RequestBody @Validated EntityLikeVO entityLikeVO) throws UnsupportedEncodingException {
    CommonRespDto<List<EntityLikeInfoVO>> respDto = graphEntityManageService.selectLikeEntity(
        entityLikeVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

}
