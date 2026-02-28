package cn.voicecomm.ai.voicesagex.console.api.api.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceListReqDto;
import java.util.List;

/**
 * 应用发现页
 *
 * @author wangf
 * @date 2025/5/19 下午 1:50
 */
public interface ApplicationExperienceService {


  /**
   * 获取应用发现页列表
   *
   * @param dto 查询参数
   * @return 应用列表
   */
  CommonRespDto<List<ApplicationExperienceDto>> applicationExperienceList(
      ApplicationExperienceListReqDto dto);


}
