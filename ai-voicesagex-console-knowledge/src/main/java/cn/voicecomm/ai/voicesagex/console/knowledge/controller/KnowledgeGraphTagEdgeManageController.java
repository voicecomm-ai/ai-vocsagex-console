package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphTagEdgeService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.EdgePatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.GraphPatternDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDeleteReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertySaveReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgeReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.TagPatternDto;
import cn.voicecomm.ai.voicesagex.console.api.valid.AddGroup;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
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
 * TagEdge-属性模块
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@RestController
@RequestMapping("/tagEdgeManage")
@Validated
@Slf4j
@RequiredArgsConstructor
public class KnowledgeGraphTagEdgeManageController {

  private final KnowledgeGraphTagEdgeService knowledgeGraphTagEdgeService;


  /**
   * 获取Tag/Edge列表
   *
   * @return 图知识库本体关系数据集
   */
  @PostMapping("/getAllTagEdges")
  public Result<List<KnowledgeGraphTagEdgeDto>> getList(
      @Validated @RequestBody KnowledgeGraphTagEdgePageReq pageReq) {
    CommonRespDto<List<KnowledgeGraphTagEdgeDto>> respDto = knowledgeGraphTagEdgeService.getList(
        pageReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取属性列表
   *
   * @param tagEdgePropertyReq 请求实体
   * @return 图知识库本体关系详情数据
   */
  @PostMapping("/getTagEdgeInfos")
  public Result<PagingRespDto<KnowledgeGraphTagEdgePropertyDto>> getTagEdgeInfo(
      @RequestBody KnowledgeGraphTagEdgePropertyReq tagEdgePropertyReq) {
    CommonRespDto<PagingRespDto<KnowledgeGraphTagEdgePropertyDto>> respDto = knowledgeGraphTagEdgeService.getTagEdgeInfo(
        tagEdgePropertyReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增Tag/Edge
   *
   * @param knowledgeGraphTagEdgeDto 图知识库本体关系对象
   * @return 图知识库本体关系id
   */
  @PostMapping("/createTagEdge")
  public Result<Integer> save(@Validated(value = {
      AddGroup.class}) @RequestBody KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    CommonRespDto<Integer> respDto = knowledgeGraphTagEdgeService.save(knowledgeGraphTagEdgeDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 删除Tag/Edge
   *
   * @param knowledgeGraphTagEdgeDto 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/dropTagEdge")
  public Result<Boolean> delete(@RequestBody KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.delete(knowledgeGraphTagEdgeDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取Tag/Edge过期时间
   *
   * @param tagEdgeReq 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/getTagEdgeTtl")
  public Result<KnowledgeGraphTagEdgeDto> getTtlCol(
      @RequestBody KnowledgeGraphTagEdgeReq tagEdgeReq) {
    CommonRespDto<KnowledgeGraphTagEdgeDto> respDto = knowledgeGraphTagEdgeService.getTtlCol(
        tagEdgeReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增Tag/Edge过期时间
   *
   * @param knowledgeGraphTagEdgeDto 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/createTagEdgeTtl")
  public Result<Boolean> createTtlCol(
      @RequestBody KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.createTtlCol(
        knowledgeGraphTagEdgeDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 清空Tag/EdgeTtl时间
   *
   * @param knowledgeGraphTagEdgeDto 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/dropTagEdgeTtl")
  public Result<Boolean> dropTtlCol(
      @RequestBody KnowledgeGraphTagEdgeDto knowledgeGraphTagEdgeDto) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.dropTtlCol(
        knowledgeGraphTagEdgeDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 获取Tag/Edge下Ttl属性字段列表
   *
   * @param tagEdgeReq 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/getAllTtlField")
  public Result<List<String>> getAllTtlField(@RequestBody KnowledgeGraphTagEdgeReq tagEdgeReq) {
    CommonRespDto<List<String>> respDto = knowledgeGraphTagEdgeService.getAllTtlField(tagEdgeReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 新增属性
   *
   * @param propertySaveReq 属性新增
   * @return 是否删除成功
   */
  @PostMapping("/createTagEdgeProperties")
  public Result<Integer> createTagEdgeProperties(@Validated(value = {
      AddGroup.class}) @RequestBody KnowledgeGraphTagEdgePropertySaveReq propertySaveReq) {
    CommonRespDto<Integer> respDto = knowledgeGraphTagEdgeService.createTagEdgeProperties(
        propertySaveReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新属性
   *
   * @param knowledgeGraphTagEdgePropertyDto 属性更新
   * @return 是否删除成功
   */
  @PostMapping("/updateProperty")
  public Result<Void> updateProperty(
      @RequestBody KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto) {
    CommonRespDto<Void> respDto = knowledgeGraphTagEdgeService.updateProperty(
        knowledgeGraphTagEdgePropertyDto);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 清空属性
   *
   * @param propertyDeleteReq 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/dropTagEdgeProperty")
  public Result<Boolean> dropTagEdgeProperty(
      @RequestBody KnowledgeGraphTagEdgePropertyDeleteReq propertyDeleteReq) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.dropTagEdgeProperty(
        propertyDeleteReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 清空所有属性
   *
   * @param graphTagEdgeDeleteReq 图知识库本体关系对象
   * @return 是否删除成功
   */
  @PostMapping("/dropAllTagEdgeProperty")
  public Result<Boolean> dropAllTagEdgeProperty(
      @RequestBody KnowledgeGraphTagEdgeDeleteReq graphTagEdgeDeleteReq) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.dropAllTagEdgeProperty(
        graphTagEdgeDeleteReq);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 更新模型可视化
   *
   * @param spaceId 知识库id
   * @return 是否更新成功
   */
  @GetMapping("/updateGraphPattern")
  public Result<Boolean> updateGraphPattern(@RequestParam("spaceId") Integer spaceId) {
    CommonRespDto<Boolean> respDto = knowledgeGraphTagEdgeService.updateGraphPattern(spaceId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型可视化获取本体
   *
   * @param spaceId 知识库id
   * @param tagName 本地名称
   * @return
   */
  @GetMapping("/getGraphPatternTag")
  public Result<TagPatternDto> getGraphPatternTag(@RequestParam("spaceId") Integer spaceId,
      @RequestParam("tagName") String tagName) {
    CommonRespDto<TagPatternDto> respDto = knowledgeGraphTagEdgeService.getGraphPatternTag(spaceId,
        tagName);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型可视化获取关系
   *
   * @param spaceId  知识库id
   * @param edgeName 关系名称
   * @return
   */
  @GetMapping("/getGraphPatternEdge")
  public Result<EdgePatternDto> getGraphPatternEdge(@RequestParam("spaceId") Integer spaceId,
      @RequestParam("edgeName") String edgeName) {
    CommonRespDto<EdgePatternDto> respDto = knowledgeGraphTagEdgeService.getGraphPatternEdge(
        spaceId, edgeName);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  /**
   * 模型可视化
   *
   * @param spaceId 知识库id
   * @return
   */
  @GetMapping("/getGraphPattern")
  public Result<GraphPatternDto> getGraphPattern(@RequestParam("spaceId") Integer spaceId) {
    CommonRespDto<GraphPatternDto> respDto = knowledgeGraphTagEdgeService.getGraphPattern(
        spaceId);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


}
