package cn.voicecomm.ai.voicesagex.console.api.api.knowledge;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ryc
 */
public interface KnowledgeGraphCommentManageService {
  CommonRespDto<String> upload(String fileDir, MultipartFile file);

}
