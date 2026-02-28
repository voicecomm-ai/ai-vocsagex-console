package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFileDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFilePageReq;

/**
 * 数据集文件Service
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
public interface ModelDatasetFileService {

  /**
   * 获取数据集文件列表
   *
   * @return 数据
   */
  CommonRespDto<PagingRespDto<ModelDatasetFileDto>> getPageList(ModelDatasetFilePageReq pageReq);

}

