package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageReq;
import java.util.List;

/**
 * 模型分类Service
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
public interface ModelCategoryService {

  /**
   * 获取模型分类列表
   *
   * @return 数据
   */
  CommonRespDto<List<ModelCategoryPageDto>> getList(ModelCategoryPageReq pageReq);

  /**
   * 获取模型分类详情数据
   *
   * @param id 模型分类id
   * @return 数据
   */
  CommonRespDto<ModelCategoryPageDto> getInfo(Integer id);

  /**
   * 新增模型分类数据
   *
   * @param modelCategoryDto 模型分类Dto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(ModelCategoryDto modelCategoryDto);

  /**
   * 修改模型分类数据
   *
   * @param modelCategoryDto 模型分类Dto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(ModelCategoryDto modelCategoryDto);

  /**
   * 删除模型分类数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

}

