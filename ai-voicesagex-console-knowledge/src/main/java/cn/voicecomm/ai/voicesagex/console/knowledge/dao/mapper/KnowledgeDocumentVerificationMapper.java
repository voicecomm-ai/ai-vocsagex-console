package cn.voicecomm.ai.voicesagex.console.knowledge.dao.mapper;

import cn.voicecomm.ai.voicesagex.console.util.po.knowledgebase.graph.KnowledgeDocumentVerificationPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * @ClassName KnowledgeDocumentVerificationMapper
 * @Author wangyang
 * @Date 2025/9/16 11:37
 */

@Mapper
public interface KnowledgeDocumentVerificationMapper extends
    BaseMapper<KnowledgeDocumentVerificationPo> {


  @Select("""
          <script>
          SELECT dv.verification_id as verificationId, dv.chunk_id as chunkId, dv.document_id as documentId, dv.subject as subject, dv.subject_tag_name as subjectTagName, dv.object as object
               , dv.edge_type as edgeType, dv.object_tag_name as objectTagName, dv.verification_status as verificationStatus, dv.type as type, dv.property_type as propertyType, dv.create_time as createTime
          FROM knowledge_document_verification dv
          JOIN knowledge_chunk_information ci ON dv.chunk_id = ci.chunk_id
          WHERE dv.deleted = false
          AND dv.document_id = #{documentId}
          AND (#{type} != 1 OR (dv.edge_type != '' ))
          ORDER BY dv.chunk_id ASC, ci.chunk_index ASC, dv.create_time DESC, dv.verification_id ASC
          </script>
      """)
  @ResultType(KnowledgeDocumentVerificationPo.class)
  IPage<KnowledgeDocumentVerificationPo> selectGroupedAndSorted(
      IPage<KnowledgeDocumentVerificationPo> page,
      @Param("documentId") Integer documentId,
      @Param("type") Integer type
  );
}
