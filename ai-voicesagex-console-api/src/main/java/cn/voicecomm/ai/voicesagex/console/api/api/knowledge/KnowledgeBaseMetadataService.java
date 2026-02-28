package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseMetadataWithValueDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.metadata.MetadataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface KnowledgeBaseMetadataService {

  CommonRespDto<Void> enableBuiltInMetadata(Integer knowledgeBaseId);

  CommonRespDto<Void> disableBuiltInMetadata(Integer knowledgeBaseId);

  CommonRespDto<Integer> addMetadata(Integer knowledgeBaseId, String name, MetadataType type);

  CommonRespDto<Void> editMetadata(Integer id, String name);

  CommonRespDto<Void> deleteMetadata(Integer id);

  CommonRespDto<List<KnowledgeBaseMetadataDto>> getKnowledgeBaseMetadataList(Integer knowledgeBaseId);

  /**
   * 根据文档ID列表获取元数据列表（包含值）
   * @param documentIds 文档ID列表
   * @return 元数据列表（包含值）
   */
  CommonRespDto<List<KnowledgeBaseMetadataWithValueDto>> getMetadataListByDocumentIds(List<Integer> documentIds);

  /**
   * 批量编辑元数据值
   * @param metadataAddRequests 元数据新增请求列表
   * @param metadataEditRequests 元数据编辑请求列表
   * @param documentIds 文档ID列表
   * @param applyToAllDocuments 是否应用于所有选定文档
   * @param deletedMetadataIds 被删除的元数据绑定关系列表
   * @return 操作结果
   */
  CommonRespDto<Void> editMetadataValueBatch(List<MetadataAddRequest> metadataAddRequests, List<MetadataEditRequest> metadataEditRequests, List<Integer> documentIds, Boolean applyToAllDocuments, List<Integer> deletedMetadataIds);

  CommonRespDto<List<KnowledgeBaseMetadataBaseDto>> getSameMetadataByKnowledgeBaseIds(List<Integer> knowledgeBaseIds);

  CommonRespDto<Void> updateLastUpdateDate(Integer knowledgeBaseId, Integer documentId);

  /**
   * 元数据编辑请求
   */
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class MetadataEditRequest {
    private Integer metadataId;
    private String newValue;
  }

  /**
   * 元数据新增请求
   */
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  class MetadataAddRequest {
    private Integer metadataId;
    private String newValue;
  }
}
