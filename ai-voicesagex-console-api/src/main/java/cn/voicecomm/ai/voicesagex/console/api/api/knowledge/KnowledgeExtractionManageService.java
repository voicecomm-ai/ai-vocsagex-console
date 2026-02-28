package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ChunkCountReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.ConfigInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.DropStatusVerification;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDoExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeDropExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionListDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeExtractionReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeSaveExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.KnowledgeUpdateExtractionDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationInfoDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction.VerificationTypeSelectDto;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentConfigVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListDetailVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DocumentListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.DropVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.EdgePropertyResVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.ExtractPreviewVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.InsertVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.KnowledgeEntryMapVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.OriginalInformationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.PropertyInfoVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.StatusVerificationClear;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.UpdateVerificationVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationEdgeTypePropertyVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationKnowledgeVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationListVO;
import cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity.VerificationTotalVO;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName knowledgeExtractionManageService
 * @Author wangyang
 * @Date 2025/9/15 14:28
 */
public interface KnowledgeExtractionManageService {

  CommonRespDto<Boolean> insertExtractionJob(KnowledgeSaveExtractionDto knowledgeExtractionDto);

  CommonRespDto<Boolean> updateExtractionJob(
      KnowledgeUpdateExtractionDto knowledgeUpdateExtractionDto);

  CommonRespDto<Boolean> deleteExtractionJob(KnowledgeDropExtractionDto knowledgeDropExtractionDto);

  CommonRespDto<Boolean> deleteDocument(Integer documentId);


  CommonRespDto<PagingRespDto<KnowledgeExtractionListDto>> extractionJobList(
      KnowledgeExtractionReq knowledgeExtractionReq);

  CommonRespDto<String> upload(MultipartFile file, Integer jobId);

  CommonRespDto<Boolean> uploadFiles(MultipartFile[] files, Integer jobId);

  void uploadFile(MultipartFile file, Integer jobId, KnowledgeExtractionDto knowledgeExtractionDto);

  CommonRespDto<Boolean> extractDocument(KnowledgeDoExtractionDto knowledgeDoExtractionDto);

  List<KnowledgeExtractionDto> getKnowledgeExtractionBySpaceId(Integer spaceId);

  CommonRespDto<PagingRespDto<DocumentListDetailVO>> documentList(DocumentListVO documentListVO);

  Map<Integer, Integer> getJobINfoBydocument(Map<String, String> jobs, List<String> documentIds);

  Long totalChunkNumber(Integer documentId);

  Long totalChunkStatusNumber(Integer documentId);

  void updateDocumentStatusNoTime(Integer id, Integer status);

  Integer documentChunkCount(ChunkCountReq chunkCountReq);

  CommonRespDto<VerificationTotalVO> verificationTotal(
      DropStatusVerification dropStatusVerification);

  CommonRespDto<List<String>> getTagInfo(VerificationTypeSelectDto verificationTypeSelectDto);

  CommonRespDto<DocumentConfigInfoVO> getConfig(ConfigInfoDto configInfoDto);

  CommonRespDto<PagingRespDto<VerificationListVO>> knowledgeVerificationList(
      VerificationInfoDto verificationInfoDto);

  CommonRespDto<EdgePropertyResVO> getEdgeTypeProperty(
      VerificationEdgeTypePropertyVO verificationEdgeTypePropertyVO);

  List<PropertyInfoVO> getEdgeTypePropertyInfo(
      VerificationEdgeTypePropertyVO verificationTypeSelectVO);

  CommonRespDto<OriginalInformationVO> originalInformation(
      OriginalInformationInfoVO originalInformationInfoVO);

  CommonRespDto<List<ExtractPreviewVO>> testPreview(DocumentConfigVO documentConfigVO);

  CommonRespDto<Boolean> documentConfig(DocumentConfigVO documentConfigVO);

  CommonRespDto<Boolean> knowledgeEntryMap(KnowledgeEntryMapVO knowledgeEntryMapVO);

  CommonRespDto<Boolean> updateVerification(UpdateVerificationVO updateVerificationVO);

  CommonRespDto<Boolean> deleteVerification(DropVerificationVO updateVerificationVO);

  CommonRespDto<Boolean> insertVerification(InsertVerificationVO insertVerificationVO);

  CommonRespDto<Boolean> verificationKnowledge(VerificationKnowledgeVO verificationKnowledgeVO);

  CommonRespDto<Boolean> dropStatus(StatusVerificationClear statusVerificationClear);
}
