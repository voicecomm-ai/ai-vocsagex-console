package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelTagDto;

/**
 * 模型标签Service
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
public interface ModelTagService {

  /**
   * 新增模型标签数据
   *
   * @param modelTagDto 模型标签Dto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(ModelTagDto modelTagDto);

  /**
   * 修改模型标签数据
   *
   * @param modelTagDto 模型标签Dto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(ModelTagDto modelTagDto);

  /**
   * 删除模型标签数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

}

