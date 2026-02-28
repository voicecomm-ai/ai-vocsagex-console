package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationTagDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationTagRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author wangf
 * @date 2025/5/19 下午 1:51
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class ApplicationTagServiceImpl extends
    ServiceImpl<ApplicationTagMapper, ApplicationTagPo> implements ApplicationTagService {

  private final ApplicationTagConverter applicationTagConverter;

  private final ApplicationTagRelationMapper applicationTagRelationMapper;
  private final ApplicationMapper applicationMapper;

  @Override
  public CommonRespDto<List<ApplicationTagDto>> getList(String tagName) {

    List<ApplicationTagPo> list = baseMapper.selectList(Wrappers.<ApplicationTagPo>lambdaQuery()
        .like(StrUtil.isNotBlank(tagName), ApplicationTagPo::getName, tagName)
        .orderByDesc(BasePo::getCreateTime));

    List<ApplicationTagDto> dtoList = applicationTagConverter.poToDtoList(list);
    if (CollUtil.isNotEmpty(dtoList)) {
      List<Integer> tagIdList = dtoList.stream().map(ApplicationTagDto::getId).toList();
      List<ApplicationTagRelationPo> relationPoList = applicationTagRelationMapper.selectList(
          Wrappers.<ApplicationTagRelationPo>lambdaQuery()
              .in(ApplicationTagRelationPo::getTagId, tagIdList));
      if (CollUtil.isNotEmpty(relationPoList)) {
        // 去除删除的应用id
        List<Integer> appIds = relationPoList.stream()
            .map(ApplicationTagRelationPo::getApplicationId).toList();
        List<Integer> existAppIds = applicationMapper.selectObjs(
            Wrappers.<ApplicationPo>lambdaQuery().select(ApplicationPo::getId)
                .in(ApplicationPo::getId, appIds).ne(ApplicationPo::getStatus, -1));
        List<ApplicationTagRelationPo> tagRelationPos = relationPoList.stream()
            .filter(relationPo -> existAppIds.contains(relationPo.getApplicationId()))
            .toList();
        // 根据标签id分组成map，key为标签id，value为应用id的list的size的map
        Map<Integer, Long> tagIdMap = tagRelationPos.stream().collect(
            Collectors.groupingBy(ApplicationTagRelationPo::getTagId,
                Collectors.counting()));
        dtoList.forEach(
            dto -> dto.setTagUsedNumber(tagIdMap.getOrDefault(dto.getId(), 0L).intValue()));
      }
    }

    return CommonRespDto.success(dtoList);
  }

  @Override
  public CommonRespDto<ApplicationTagDto> getById(Integer id) {
    ApplicationTagPo po = baseMapper.selectById(id);
    if (po != null) {
      ApplicationTagDto dto = applicationTagConverter.poToDto(po);
      return CommonRespDto.success(dto);
    }
    return CommonRespDto.error();
  }

  @Override
  public CommonRespDto<Integer> add(ApplicationTagDto dto) {
    ApplicationTagPo po = applicationTagConverter.dtoToPo(dto);
    // 查询所有数量
    Long l = baseMapper.selectCount(Wrappers.<ApplicationTagPo>lambdaQuery());
    if (l >= 30) {
      return CommonRespDto.error("最多添加30个标签");
    }
    // 重名校验
    if (baseMapper.selectCount(
        Wrappers.<ApplicationTagPo>lambdaQuery().eq(ApplicationTagPo::getName, po.getName())) > 0) {
      return CommonRespDto.error("名称已存在");
    }
    baseMapper.insert(po);
    return CommonRespDto.success(po.getId());
  }

  @Override
  public CommonRespDto<Void> update(ApplicationTagDto dto) {
    ApplicationTagPo po = applicationTagConverter.dtoToPo(dto);
    // 重名校验
    if (baseMapper.selectCount(
        Wrappers.<ApplicationTagPo>lambdaQuery().eq(ApplicationTagPo::getName, po.getName())
            .ne(ApplicationTagPo::getId, po.getId())) > 0) {
      return CommonRespDto.error("名称已存在");
    }
    int i = baseMapper.updateById(po);
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<Void> delete(Integer id) {
    int i = baseMapper.deleteById(id);
    applicationTagRelationMapper.delete(Wrappers.<ApplicationTagRelationPo>lambdaQuery()
        .eq(ApplicationTagRelationPo::getTagId, id));
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<Boolean> deleteCheck(Integer id) {
    List<ApplicationTagRelationPo> relationPoList = applicationTagRelationMapper.selectList(
        Wrappers.<ApplicationTagRelationPo>lambdaQuery()
            .eq(ApplicationTagRelationPo::getTagId, id));
    if (CollUtil.isEmpty(relationPoList)) {
      return CommonRespDto.success(true);
    }
    List<Integer> appIds = relationPoList.stream().map(ApplicationTagRelationPo::getApplicationId)
        .toList();
    // 标签被未删除的应用使用，不能删除
    Long count = applicationMapper.selectCount(
        Wrappers.<ApplicationPo>lambdaQuery().in(ApplicationPo::getId, appIds)
            .ne(ApplicationPo::getStatus, -1));
    if (count > 0) {
      return CommonRespDto.success(false);
    }
    return CommonRespDto.success(true);
  }
}
