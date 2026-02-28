package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelBatchReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDownloadParamDto.ArchDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import java.util.List;

/**
 * 模型Service
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
public interface ModelService {

  /**
   * 获取模型列表
   *
   * @return 数据
   */
  CommonRespDto<PagingRespDto<ModelPageDto>> getPageList(ModelPageReq pageReq);

  /**
   * 获取列表
   *
   * @param modelReq
   * @return
   */
  CommonRespDto<List<ModelPageDto>> getList(ModelReq modelReq);

  /**
   * 获取模型详情数据
   *
   * @param id 模型id
   * @return 数据
   */
  CommonRespDto<ModelDto> getInfo(Integer id);

  /**
   * 模型是否可用
   *
   * @param id 模型id
   * @return 是否可用 true；可用；false：不可用
   */
  CommonRespDto<Boolean> isAvailable(Integer id);

  /**
   * 新增模型数据
   *
   * @param modelDto 模型Dto
   * @return 成功的id
   */
  CommonRespDto<Integer> save(ModelDto modelDto);

  /**
   * 修改模型数据
   *
   * @param modelDto 模型Dto
   * @return 是否成功
   */
  CommonRespDto<Boolean> update(ModelDto modelDto);

  /**
   * 删除模型数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

  /**
   * 批量删除模型
   *
   * @param ids 主键id集合
   * @return 是否成功
   */
  CommonRespDto<Boolean> deleteBatch(List<Integer> ids);

  /**
   * 批量上下架模型
   *
   * @param modelBatchReq modelBatchReq
   * @return 是否成功
   */
  CommonRespDto<Boolean> shelfBatch(ModelBatchReq modelBatchReq);

  /**
   * 批量更新分类
   *
   * @param modelBatchReq modelBatchReq
   * @return 是否成功
   */
  CommonRespDto<Boolean> updateCategoryBatch(ModelBatchReq modelBatchReq);

  /**
   * 标准调用
   *
   * @param secret
   * @param modelInvokeBaseDto
   * @return
   */
  CommonRespDto<Object> invokeStandard(String secret, ModelInvokeBaseDto modelInvokeBaseDto);

  /**
   * 预训练模型下载
   *
   * @param id
   * @param archDto
   * @return
   */
  CommonRespDto<Boolean> preTrainedDownload(Integer id, ArchDto archDto);

  /**
   * 下载回调接口
   *
   * @param modelParamDto
   * @return
   */
  CommonRespDto<Void> downloadCallback(ModelParamDto modelParamDto);

  /**
   * 返回文件夹结构
   *
   * @param id
   * @return
   */
  CommonRespDto<ZipNodeDto> buildTree(Integer id);


  /**
   * 获取算法模型配置文件
   *
   * @param id
   * @param type
   * @param modelSource
   * @return
   */
  CommonRespDto<String> getAlgorithmConfig(Integer id, Integer type, Integer modelSource);



  CommonRespDto<ModelDto> getMemoryModel();

}

