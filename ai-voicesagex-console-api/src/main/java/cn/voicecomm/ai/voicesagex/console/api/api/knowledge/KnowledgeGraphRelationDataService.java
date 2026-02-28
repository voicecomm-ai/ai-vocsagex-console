package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeDeleteVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgeListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResultVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.RelationDropAllVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SaveRelationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ScreenTagVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.SubjectInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.TotalEdgeVO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaoyan 关系数据业务接口层
 */
public interface KnowledgeGraphRelationDataService {

  Map<String, TotalEdgeVO> edgeCache = new ConcurrentHashMap<>();

  /**
   * 获取关系列表
   *
   * @return
   */
  CommonRespDto<TotalEdgeVO> getTagInfoList(String spaceId, String edgeName);


  /**
   * 获取关系属性详细信息
   *
   * @param
   * @return
   */
  CommonRespDto<List<EdgePropertyResultVO>> getEdgeProperties(Long edgeId);

  /**
   * 获取关系列表
   *
   * @param edgePropertyVO
   * @return
   */
  CommonRespDto<PagingRespDto<EdgeListVO>> getEntities(EdgePropertyVO edgePropertyVO);


  /**
   * 删除关系列表
   *
   * @param edgeDeleteVO
   */
  CommonRespDto<Boolean> deleteRelateions(EdgeDeleteVO edgeDeleteVO);

  /**
   * 删除所有关系列表
   *
   * @param
   */
  CommonRespDto<Boolean> deleteAllRelations(RelationDropAllVO relationDropAllVO);

  /**
   * 获取主体列表
   *
   * @param spaceId
   * @return
   */
  CommonRespDto<List<SubjectInfoVO>> getEntity(String spaceId);

  /**
   * 新增关系
   */
  CommonRespDto<List<SaveRelationVO>> saveRelation(List<SaveRelationVO> saveRelationVO);


  /**
   * 获取关系列表
   *
   * @param spaceId
   * @return
   */
  CommonRespDto<List<EdgeInfoVO>> getEdges(String spaceId);


  /**
   * @param saveRelationVO
   */
  CommonRespDto<SaveRelationVO> getRelation(SaveRelationVO saveRelationVO);


  /**
   * 编辑关系
   *
   * @param
   */
  CommonRespDto<Boolean> updateRelation(SaveRelationVO saveRelationVO);


  CommonRespDto<Set<String>> screenTag(ScreenTagVO screenTagVO);

}
