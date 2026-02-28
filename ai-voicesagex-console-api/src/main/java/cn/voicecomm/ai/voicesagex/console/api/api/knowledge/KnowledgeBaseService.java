package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.CommonChunkPreviewRespDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDetailDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.KnowledgeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.ParentChildChunkPreviewRespDataDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.PreviewChunksDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.RetrievalTestRecordDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.SaveAndProcessBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.SaveAndProcessExistBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.UpdateKnowledgeBaseSettingDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.knowledgebase.KnowledgeBaseType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface KnowledgeBaseService {

  CommonRespDto<Void> createEmptyKnowledgeBase(@NotNull String name, String description,
      KnowledgeBaseType type);

  /**
   * 删除知识库
   * <p>
   * 1. 应用中的绑定关系删除
   * <p>
   * 2. 知识库中的文档删除，包含数据库和本地文件
   *
   * @param id 知识库ID
   */
  CommonRespDto<String> deleteKnowledgeBase(int id);

  CommonRespDto<Void> editKnowledgeBaseNameAndDesc(int id, String name, String description);

  CommonRespDto<Void> updateBaseSetting(UpdateKnowledgeBaseSettingDto dto);

  CommonRespDto<Integer> saveAndProcess(SaveAndProcessBaseDto dto);

  CommonRespDto<Void> saveAndProcessExistKnowledgeBase(SaveAndProcessExistBaseDto dto);

  CommonRespDto<List<KnowledgeBaseDto>> list(List<Integer> tagIds, String name, String type);

  CommonRespDto<CommonChunkPreviewRespDataDto> previewCommonChunks(PreviewChunksDto dto);

  CommonRespDto<ParentChildChunkPreviewRespDataDto> previewParentChildChunks(PreviewChunksDto dto);

  /**
   * 同步应用知识库关联关系 将应用的知识库关联关系同步为目标状态： - 添加knowledgeBaseIds中未绑定的知识库 - 移除当前绑定但不在knowledgeBaseIds中的知识库 -
   * 如果knowledgeBaseIds为空或null，则移除所有关联关系
   *
   * @param knowledgeBaseIds 目标知识库ID列表，可以为空或null
   * @param applicationId    应用ID
   * @return 操作结果
   */
  CommonRespDto<Void> addKnowledgeBasesToApplication(List<Integer> knowledgeBaseIds,
      Integer applicationId);

  CommonRespDto<Void> removeKnowledgeBaseFromApplication(Integer knowledgeBaseId,
      Integer applicationId);

  CommonRespDto<KnowledgeBaseDetailDto> getKnowledgeBaseDetail(Integer id);

  CommonRespDto<List<KnowledgeBaseDto>> getApplicationKnowledgeBases(Integer applicationId);

  CommonRespDto<List<KnowledgeBaseDto>> getKnowledgeBasesByIds(List<Integer> ids);

  CommonRespDto<Long> getKnowledgeBaseDocumentCount(Integer knowledgeBaseId);

  /**
   * 保存检索测试记录
   *
   * @param knowledgeBaseId 知识库ID
   * @param query           检索查询
   * @return 操作结果
   */
  CommonRespDto<Void> saveRetrievalTestRecord(Integer knowledgeBaseId, String query);

  /**
   * 根据知识库ID获取所有检索测试记录，按时间倒序排列
   *
   * @param knowledgeBaseId 知识库ID
   * @return 检索测试记录列表
   */
  CommonRespDto<List<RetrievalTestRecordDto>> getRetrievalTestRecordsByKnowledgeBaseId(
      Integer knowledgeBaseId);

  Long getKnowledgeBaseNo(Integer userId);
}
