package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.graph.KnowledgeGraphTagEdgePropertyDto;
import java.util.List;

/**
 * 图知识库本体关系属性Service
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
public interface KnowledgeGraphTagEdgePropertyService {

  /**
   * 获取图知识库本体关系属性列表
   *
   * @return 数据
   */
  CommonRespDto<List<KnowledgeGraphTagEdgePropertyDto>> getList();

  /**
   * 获取图知识库本体关系属性详情数据
   *
   * @param id 图知识库本体关系属性id
   * @return 数据
   */
  CommonRespDto<KnowledgeGraphTagEdgePropertyDto> getInfo(Integer id);

  /**
   * 新增图知识库本体关系属性数据
   *
   * @param knowledgeGraphTagEdgePropertyDto 图知识库本体关系属性Dto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto);

  /**
   * 修改图知识库本体关系属性数据
   *
   * @param knowledgeGraphTagEdgePropertyDto 图知识库本体关系属性Dto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(KnowledgeGraphTagEdgePropertyDto knowledgeGraphTagEdgePropertyDto);

  /**
   * 删除图知识库本体关系属性数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

}

