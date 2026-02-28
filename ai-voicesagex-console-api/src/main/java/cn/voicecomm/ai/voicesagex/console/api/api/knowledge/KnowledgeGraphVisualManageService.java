package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

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
import java.util.List;
import java.util.Set;

public interface KnowledgeGraphVisualManageService {

  /**
   * 获取节点数量
   *
   * @param graphVisualnfoVO
   * @return
   */
  CommonRespDto<NodeInfoVo> getNodeNumber(GraphVisualnfoVO graphVisualnfoVO);


  /**
   * 模糊搜索节点信息
   *
   * @param selectLikeVO
   * @return
   */
  CommonRespDto<List<VertexInfoVO>> selectVertexInfo(SelectLikeVO selectLikeVO);

  /**
   * 图谱可视化
   *
   * @param graphVisualnfoVO
   * @return
   */
  CommonRespDto<GraphVisualVO> getGraphVisual(GraphVisualnfoVO graphVisualnfoVO);


  /**
   * 设置中心节点
   *
   * @param centerVertexVO
   * @return
   */
  CommonRespDto<Boolean> setCenterNode(CenterVertexVO centerVertexVO);

  /**
   * 获取所有边信息
   *
   * @param graphVisualnfoVO
   * @return
   */
  CommonRespDto<List<String>> getEdgeList(GraphVisualnfoVO graphVisualnfoVO);


  /**
   * 获取节点信息
   *
   * @param singleVertexVO
   * @return
   */
  CommonRespDto<VertexTagInfoVO> singleVertexInfo(SingleVertexVO singleVertexVO);


  /**
   * 获取边的信息
   *
   * @param singleEdgeVO
   * @return
   */
  CommonRespDto<EdgeInfoVO> singleEdgeInfo(SingleEdgeVO singleEdgeVO);


  /**
   * 查询子图
   *
   * @param fullGraphVO
   * @return
   */
  CommonRespDto<QueryFullGraphVO> queryFullGraph(FullGraphVO fullGraphVO);


  /**
   * 节点扩展
   *
   * @param expansionVO
   * @return
   */
  CommonRespDto<ExpansionInfoVO> expansionNode(ExpansionVO expansionVO);

  /**
   * 路径查询
   *
   * @param queryPathVO
   * @return
   */
  CommonRespDto<RouteListVO> queryPath(QueryPathVO queryPathVO);

  /**
   * 统计图谱状态
   *
   * @param graphVisualnfoVO
   * @return
   */
  CommonRespDto<Integer> statisticalState(GraphVisualnfoVO graphVisualnfoVO);

  CommonRespDto<VisualmanagerVO> getCenterNode(GetCenterNodeVO getCenterNodeVO);

  CommonRespDto<Set<ExtendVO>> extendOrNot(FullGraphVO fullGraphVO);
}
