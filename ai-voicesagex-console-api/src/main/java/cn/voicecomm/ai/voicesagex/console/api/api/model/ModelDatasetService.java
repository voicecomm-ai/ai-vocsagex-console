package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetPageReq;

/**
 * 数据集Service
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
public interface ModelDatasetService {

  /**
   * 获取数据集列表
   *
   * @return 数据
   */
  CommonRespDto<PagingRespDto<ModelDatasetDto>> getPageList(ModelDatasetPageReq pageReq);

  /**
   * 获取数据集详情数据
   *
   * @param id 数据集id
   * @return 数据
   */
  CommonRespDto<ModelDatasetDto> getInfo(Integer id);

  /**
   * 新增数据集数据
   *
   * @param modelDatasetDto 数据集Dto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(ModelDatasetDto modelDatasetDto);

  /**
   * 修改数据集数据
   *
   * @param modelDatasetDto 数据集Dto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(ModelDatasetDto modelDatasetDto);

  /**
   * 删除数据集数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

  Long getModelDataSetNo(Integer userId);

}

