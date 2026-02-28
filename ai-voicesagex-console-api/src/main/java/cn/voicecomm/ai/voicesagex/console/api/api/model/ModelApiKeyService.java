package cn.voicecomm.ai.voicesagex.console.api.api.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyPageReq;
import java.util.List;

/**
 * 模型密钥Service
 *
 * @author ryc
 * @date 2025-07-09 09:57:34
 */
public interface ModelApiKeyService {

  /**
   * 获取模型密钥列表
   *
   * @return 数据
   */
  CommonRespDto<List<ModelApiKeyDto>> getList(ModelApiKeyPageReq modelApiKeyPageReq);

  /**
   * 获取模型密钥详情数据
   *
   * @param id 模型密钥id
   * @return 数据
   */
  CommonRespDto<ModelApiKeyDto> getInfo(Integer id);

  /**
   * 新增模型密钥数据
   *
   * @param modelApiKeyDto 模型密钥Dto
   * @return 成功的id
   */
  CommonRespDto<String> save(ModelApiKeyDto modelApiKeyDto);


  /**
   * 删除模型密钥数据
   *
   * @param id 主键id
   * @return 是否成功
   */
  CommonRespDto<Boolean> delete(Integer id);

  /**
   * 是否检验通过
   *
   * @param secret
   * @return
   */
  boolean isValid(String secret);

}

