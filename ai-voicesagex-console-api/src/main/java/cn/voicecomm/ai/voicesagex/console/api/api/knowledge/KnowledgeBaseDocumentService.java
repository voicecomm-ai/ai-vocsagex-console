package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.DocumentProcessDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDocumentDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.PreviewChunksDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.RetrievalTestRecordDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.UpdateDocumentStatusDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.UploadDocDto;

import java.util.List;
import java.util.Map;

public interface KnowledgeBaseDocumentService {

  /**
   * 上传文档
   *
   * @param dto 上传文档DTO
   * @return 文档ID
   */
  Integer uploadDoc(UploadDocDto dto);

  /**
   * 根据知识库ID获取文档列表
   *
   * @param knowledgeBaseId 知识库ID
   * @return 文档列表
   */
  CommonRespDto<List<KnowledgeBaseDocumentDto>> getKnowledgeBaseDocuments(Integer knowledgeBaseId,
    String name, String status);

  /**
   * 根据知识库ID获取所有文档ID
   *
   * @param knowledgeBaseId 知识库ID
   * @return 文档ID列表
   */
  CommonRespDto<List<Integer>> getKnowledgeBaseDocumentIds(Integer knowledgeBaseId);

  /**
   * 批量更新文档状态
   *
   * @param dto 文档状态更新DTO
   * @return 操作结果
   */
  CommonRespDto<Void> updateDocumentStatus(UpdateDocumentStatusDto dto);

  /**
   * 根据文档ID获取文档详情
   *
   * @param documentId 文档ID
   * @return 文档详情
   */
  CommonRespDto<KnowledgeBaseDocumentDto> getDocumentById(Integer documentId);

  /**
   * 获取文档处理状态
   *
   * @param documentId 文档ID
   * @return 文档处理状态
   */
  String getDocumentProcessStatus(Integer documentId);

  /**
   * 获取知识库下所有文档的处理状态
   *
   * @param knowledgeBaseId 知识库ID
   * @return 文档状态映射 (文档ID -> 处理状态)
   */
  Map<Integer, DocumentProcessDto> getKnowledgeBaseDocumentsStatus(Integer knowledgeBaseId);

  /**
   * 根据ID删除文档
   *
   * @param ids 文档ID
   * @return 删除结果
   */
  CommonRespDto<Void> deleteDocuments(List<Integer> ids);

  /**
   * 批量启用文档分段
   * @param documentId 文档ID
   * @param chunkIds 分段ID列表（knowledge_base_doc_vector的主键）
   * @return 启用结果
   */
  CommonRespDto<Void> enableChunks(Integer documentId, List<Integer> chunkIds);

  /**
   * 批量禁用文档分段
   * @param documentId 文档ID
   * @param chunkIds 分段ID列表（knowledge_base_doc_vector的主键）
   * @return 禁用结果
   */
  CommonRespDto<Void> disableChunks(Integer documentId, List<Integer> chunkIds);

  /**
   * 批量删除文档分段
   * @param documentId 文档ID
   * @param chunkIds 分段ID列表（knowledge_base_doc_vector的主键）
   * @return 删除结果
   */
  CommonRespDto<Void> deleteChunks(Integer documentId, List<Integer> chunkIds);

  CommonRespDto<Void> saveAndProcess(PreviewChunksDto dto);


}
