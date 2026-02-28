package cn.voicecomm.ai.voicesagex.console.knowledge.controller;

import cn.voicecomm.ai.voicesagex.console.api.api.knowledge.KnowledgeGraphVisualManageService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.CenterVertexVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.EdgeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExpansionInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExpansionVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.ExtendVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.FullGraphVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GetCenterNodeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GraphVisualVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.GraphVisualnfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.NodeInfoVo;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.QueryFullGraphVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.QueryPathVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.RouteListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SelectLikeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SingleEdgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.SingleVertexVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VertexTagInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual.VisualmanagerVO;
import cn.voicecomm.ai.voicesagex.console.util.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 图谱可视化模块
 *
 * @author ryc
 * @date 2025/9/16
 */
@RestController
@Tag(name = "图谱可视化模块")
@RequestMapping("/VisualManage")
@Slf4j
@Validated
@RequiredArgsConstructor
public class KnowledgeGraphVisualManageController {

  private final KnowledgeGraphVisualManageService knowledgeGraphVisualManageService;


  @PostMapping("/getGraphVisual")
  @Operation(summary = "获取图谱可视化界面数据", description = "获取图谱可视化界面数据")
  public Result<GraphVisualVO> getGraphVisual(
      @RequestBody @Validated GraphVisualnfoVO graphVisualnfoVO) {
    CommonRespDto<GraphVisualVO> respDto = knowledgeGraphVisualManageService.getGraphVisual(
        graphVisualnfoVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/getNodeNumber")
  @Operation(summary = "统计节点数量", description = "统计节点数量")
  public Result<NodeInfoVo> getNodeNumber(
      @RequestBody @Validated GraphVisualnfoVO graphVisualnfoVO) {
    CommonRespDto<NodeInfoVo> respDto = knowledgeGraphVisualManageService.getNodeNumber(
        graphVisualnfoVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/selectVertexInfo")
  @Operation(summary = "模糊搜索节点名称", description = "模糊搜索节点名称")
  public Result<List<VertexInfoVO>> selectVertexInfo(
      @RequestBody @Validated SelectLikeVO selectLikeVO) {
    CommonRespDto<List<VertexInfoVO>> respDto = knowledgeGraphVisualManageService.selectVertexInfo(
        selectLikeVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/setCenterNode")
  @Operation(summary = "设置中心节点", description = "设置中心节点")
  public Result<Boolean> setCenterNode(@RequestBody @Validated CenterVertexVO centerVertexVO) {
    CommonRespDto<Boolean> respDto = knowledgeGraphVisualManageService.setCenterNode(
        centerVertexVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }

  @PostMapping("/getCenterNode")
  @Operation(summary = "获取中心节点", description = "获取中心节点")
  public Result<VisualmanagerVO> getCenterNode(
      @RequestBody @Validated GetCenterNodeVO getCenterNodeVO) {
    CommonRespDto<VisualmanagerVO> respDto = knowledgeGraphVisualManageService.getCenterNode(
        getCenterNodeVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/singleVertexInfo")
  @Operation(summary = "获取节点属性信息", description = "节点属性信息")
  public Result<VertexTagInfoVO> singleVertexInfo(
      @RequestBody @Validated SingleVertexVO singleVertexVO) {
    CommonRespDto<VertexTagInfoVO> respDto = knowledgeGraphVisualManageService.singleVertexInfo(
        singleVertexVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/singleEdgeInfo")
  @Operation(summary = "获取边属性信息", description = "获取边属性信息")
  public Result<EdgeInfoVO> singleEdgeInfo(@RequestBody @Validated SingleEdgeVO singleEdgeVO) {
    CommonRespDto<EdgeInfoVO> respDto = knowledgeGraphVisualManageService.singleEdgeInfo(
        singleEdgeVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/getEdgeList")
  @Operation(summary = "获取图空间下所有边信息", description = "获取图空间下所有边信息")
  public Result<List<String>> getEdgeList(
      @RequestBody @Validated GraphVisualnfoVO graphVisualnfoVO) {
    CommonRespDto<List<String>> respDto = knowledgeGraphVisualManageService.getEdgeList(
        graphVisualnfoVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/expansionNode")
  @Operation(summary = "节点扩展", description = "节点扩展")
  public Result<ExpansionInfoVO> expansionNode(@RequestBody @Validated ExpansionVO expansionVO) {
    CommonRespDto<ExpansionInfoVO> respDto = knowledgeGraphVisualManageService.expansionNode(
        expansionVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/queryPath")
  @Operation(summary = "路径查询", description = "路径查询")
  public Result<RouteListVO> queryPath(@RequestBody @Validated QueryPathVO queryPathVO) {
    CommonRespDto<RouteListVO> respDto = knowledgeGraphVisualManageService.queryPath(queryPathVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/queryFullGraph")
  @Operation(summary = "全图查询", description = "全图查询")
  public Result<QueryFullGraphVO> queryFullGraph(@RequestBody @Validated FullGraphVO fullGraphVO) {
    CommonRespDto<QueryFullGraphVO> respDto = knowledgeGraphVisualManageService.queryFullGraph(
        fullGraphVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/extendOrNot")
  @Operation(summary = "节点是否可扩展", description = "节点是否可扩展")
  public Result<Set<ExtendVO>> extendOrNot(@RequestBody @Validated FullGraphVO fullGraphVO) {
    CommonRespDto<Set<ExtendVO>> respDto = knowledgeGraphVisualManageService.extendOrNot(
        fullGraphVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


  @PostMapping("/statisticalState")
  @Operation(summary = "统计状态", description = "统计状态")
  public Result<Integer> statisticalState(
      @RequestBody @Validated GraphVisualnfoVO graphVisualnfoVO) {
    CommonRespDto<Integer> respDto = knowledgeGraphVisualManageService.statisticalState(
        graphVisualnfoVO);
    if (!respDto.isOk()) {
      return Result.error(respDto.getCode(), respDto.getMsg(), respDto.getData());
    }
    return Result.success(respDto.getData());
  }


}
