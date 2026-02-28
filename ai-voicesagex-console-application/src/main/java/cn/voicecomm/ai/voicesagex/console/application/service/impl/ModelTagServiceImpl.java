package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelTagDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 模型标签接口实现类
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelTagServiceImpl extends ServiceImpl<ModelTagMapper, ModelTagPo> implements
    ModelTagService {

  private final ModelTagRelationMapper modelTagRelationMapper;

  private final ModelTagConverter modelTagConverter;

  @Override
  public CommonRespDto<Integer> save(ModelTagDto modelTagDto) {
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelTagPo>lambdaQuery().eq(ModelTagPo::getName, modelTagDto.getName()));
    if (selectCount > 0) {
      return CommonRespDto.error("标签已存在");
    }
    Long tagCount = baseMapper.selectCount(Wrappers.<ModelTagPo>lambdaQuery()
        .eq(ModelTagPo::getCategoryId, modelTagDto.getCategoryId()));
    if (tagCount == 30) {
      return CommonRespDto.error("添加数量限制30个");
    }
    ModelTagPo modelTagPo = modelTagConverter.dtoToPo(modelTagDto);
    baseMapper.insert(modelTagPo);
    return CommonRespDto.success(modelTagPo.getId());
  }

  @Override
  public CommonRespDto<Boolean> update(ModelTagDto modelTagDto) {
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelTagPo>lambdaQuery().eq(ModelTagPo::getName, modelTagDto.getName())
            .ne(ModelTagPo::getId, modelTagDto.getId()));
    if (selectCount > 0) {
      return CommonRespDto.error("标签已存在");
    }
    ModelTagPo modelTagPo = modelTagConverter.dtoToPo(modelTagDto);
    baseMapper.updateById(modelTagPo);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(Integer id) {
    baseMapper.deleteById(id);
    modelTagRelationMapper.delete(
        Wrappers.<ModelTagRelationPo>lambdaQuery().eq(ModelTagRelationPo::getTagId, id));
    return CommonRespDto.success(Boolean.TRUE);
  }

}