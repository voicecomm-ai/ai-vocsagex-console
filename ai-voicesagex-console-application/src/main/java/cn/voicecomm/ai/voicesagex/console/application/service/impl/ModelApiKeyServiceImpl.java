package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelApiKeyService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelApiKeyPageReq;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelApiKeyConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelApiKeyMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelApiKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.util.TokenHelp;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * 模型密钥接口实现类
 *
 * @author ryc
 * @date 2025-07-09 09:57:34
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelApiKeyServiceImpl extends ServiceImpl<ModelApiKeyMapper, ModelApiKeyPo> implements
    ModelApiKeyService {

  private final ModelApiKeyConverter modelApiKeyConverter;
  @DubboReference
  private BackendUserService backendUserService;

  @Override
  public CommonRespDto<List<ModelApiKeyDto>> getList(ModelApiKeyPageReq modelApiKeyPageReq) {
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();
    List<ModelApiKeyPo> modelApiKeyPoList = baseMapper.selectList(
        Wrappers.<ModelApiKeyPo>lambdaQuery()
            .eq(ModelApiKeyPo::getModelId, modelApiKeyPageReq.getModelId())
            .in(BaseAuditPo::getCreateBy, userIdList).orderByDesc(BasePo::getCreateTime)
            .orderByDesc(ModelApiKeyPo::getId));
    return CommonRespDto.success(modelApiKeyConverter.poListToDtoList(modelApiKeyPoList));
  }

  @Override
  public CommonRespDto<ModelApiKeyDto> getInfo(Integer id) {
    ModelApiKeyPo modelApiKeyPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelApiKeyPo)) {
      return CommonRespDto.error("数据不存在");
    }
    return CommonRespDto.success(modelApiKeyConverter.poToDto(modelApiKeyPo));
  }

  @Override
  public CommonRespDto<String> save(ModelApiKeyDto modelApiKeyDto) {
    ModelApiKeyPo modelApiKeyPo = modelApiKeyConverter.dtoToPo(modelApiKeyDto);
    // 生成密钥
    String generateToken = TokenHelp.generateToken();
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelApiKeyPo>lambdaQuery().eq(ModelApiKeyPo::getSecret, generateToken));
    if (selectCount > 0) {
      return CommonRespDto.error("密钥生成重复，请重新生成");
    }
    modelApiKeyPo.setSecret(generateToken);
    baseMapper.insert(modelApiKeyPo);
    return CommonRespDto.success(generateToken);
  }

  @Override
  public CommonRespDto<Boolean> delete(Integer id) {
    baseMapper.deleteById(id);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public boolean isValid(String secret) {
    if (CharSequenceUtil.isBlank(secret)) {
      return false;
    }
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelApiKeyPo>lambdaQuery().eq(ModelApiKeyPo::getSecret, secret));
    return selectCount > 0;
  }

}