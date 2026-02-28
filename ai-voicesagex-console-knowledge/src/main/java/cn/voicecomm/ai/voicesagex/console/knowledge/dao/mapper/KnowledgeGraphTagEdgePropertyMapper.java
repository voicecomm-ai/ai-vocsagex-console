package cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper;

import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeGraphTagEdgePropertyPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 图知识库本体关系属性Mapper
 *
 * @author ryc
 * @date 2025-08-19 16:46:25
 */
@Mapper
public interface KnowledgeGraphTagEdgePropertyMapper extends
    BaseMapper<KnowledgeGraphTagEdgePropertyPo> {

  @Select("SELECT COUNT(*) FROM knowledge_graph_tag_edge_property WHERE tag_edge_id = #{tagEdgeId} AND property_name = #{propertyName}")
  Long selectCountByTagEdgeIdAndNameWithoutDelete(@Param("tagEdgeId") Integer tagEdgeId,
      @Param("propertyName") String propertyName);

}
