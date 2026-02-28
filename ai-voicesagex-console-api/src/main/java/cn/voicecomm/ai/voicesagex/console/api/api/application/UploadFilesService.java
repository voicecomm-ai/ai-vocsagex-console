package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.UploadFilesResp;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFilesService {

  CommonRespDto<UploadFilesResp> upload(MultipartFile file);

  CommonRespDto<UploadFilesResp> remoteFileUpload(String url);

}
