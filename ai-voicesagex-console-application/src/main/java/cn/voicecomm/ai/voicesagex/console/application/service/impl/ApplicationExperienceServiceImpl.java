package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.ApplicationExperienceService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceListReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.ApplicationExperienceTagDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.ApplicationConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ApplicationExperienceTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperiencePo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.application.ApplicationExperienceTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
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
public class ApplicationExperienceServiceImpl extends
    ServiceImpl<ApplicationExperienceMapper, ApplicationExperiencePo> implements
    ApplicationExperienceService {

  private final ApplicationConverter applicationConverter;
  private final ApplicationExperienceTagRelationMapper applicationExperienceTagRelationMapper;
  private final ApplicationExperienceTagMapper applicationExperienceTagMapper;

  @DubboReference
  public BackendUserService backendUserService;

  @Override
  public CommonRespDto<List<ApplicationExperienceDto>> applicationExperienceList(
      ApplicationExperienceListReqDto req) {
    String name = req.getName();
    if (StrUtil.isNotBlank(name)) {
      name = SpecialCharUtil.replaceSpecialWord(name);
    }
    LambdaQueryWrapper<ApplicationExperiencePo> queryWrapper = Wrappers.<ApplicationExperiencePo>lambdaQuery()
        .eq(StrUtil.isNotBlank(req.getType()), ApplicationExperiencePo::getType, req.getType())
        .apply(StrUtil.isNotBlank(name), "LOWER(\"name\") LIKE LOWER({0})", "%" + name + "%")
        .orderByDesc(ApplicationExperiencePo::getCreateTime);
    if (CollUtil.isNotEmpty(req.getTagIdList())) {
      // 添加分类查询条件
      List<Integer> appIds = applicationExperienceTagRelationMapper.selectObjs(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .select(ApplicationExperienceTagRelationPo::getExperienceApplicationId)
              .in(ApplicationExperienceTagRelationPo::getTagId, req.getTagIdList()));
      if (CollUtil.isNotEmpty(appIds)) {
        queryWrapper.in(ApplicationExperiencePo::getId, appIds);
      } else {
        return CommonRespDto.success(List.of());
      }
    }
    List<ApplicationExperiencePo> poList = baseMapper.selectList(queryWrapper);
    List<ApplicationExperienceDto> experienceDtoList = applicationConverter.exePoToDtoList(poList);
    if (CollUtil.isNotEmpty(experienceDtoList)) {
      // 设置创建人用户名
      List<Integer> userIds = experienceDtoList.stream().map(ApplicationExperienceDto::getCreateBy)
          .toList();
      List<BackendUserDto> data = backendUserService.getUserByIds(userIds).getData();
      // 转为map，key为id，value为用户名
      Map<Integer, String> userMap = data.stream()
          .collect(Collectors.toMap(BackendUserDto::getId, BackendUserDto::getUsername));
      List<Integer> experienceIds = experienceDtoList.stream().map(ApplicationExperienceDto::getId)
          .toList();

      // 查出应用id对应的所有标签,并根据ApplicationId分组
      List<ApplicationExperienceTagRelationPo> relationList = applicationExperienceTagRelationMapper.selectList(
          Wrappers.<ApplicationExperienceTagRelationPo>lambdaQuery()
              .in(ApplicationExperienceTagRelationPo::getExperienceApplicationId, experienceIds));
      if (CollUtil.isNotEmpty(relationList)) {
        List<Integer> tagIdList = relationList.stream()
            .map(ApplicationExperienceTagRelationPo::getTagId).toList();
        Map<Integer, ApplicationExperienceTagPo> tagMap = applicationExperienceTagMapper.selectList(
                Wrappers.<ApplicationExperienceTagPo>lambdaQuery()
                    .in(ApplicationExperienceTagPo::getId, tagIdList)).stream()
            .collect(Collectors.toMap(ApplicationExperienceTagPo::getId, v -> v));
        // 根据ApplicationId分组成map，key为应用id，value为标签id集合
        Map<Integer, List<Integer>> appTagListMap = relationList.stream().collect(
            Collectors.groupingBy(ApplicationExperienceTagRelationPo::getExperienceApplicationId,
                Collectors.mapping(ApplicationExperienceTagRelationPo::getTagId,
                    Collectors.toList())));
        experienceDtoList.forEach(dto -> {
          List<Integer> appTagList = appTagListMap.getOrDefault(dto.getId(), List.of());
          if (CollUtil.isNotEmpty(appTagList)) {
            List<ApplicationExperienceTagDto> tagDtoList = new ArrayList<>();
            appTagList.forEach(tagId -> {
              ApplicationExperienceTagPo tagPo = tagMap.get(tagId);
              tagDtoList.add(applicationConverter.tagPoToDto(tagPo));
            });
            dto.setTagList(tagDtoList);
          }
          dto.setCreateUsername(userMap.get(dto.getCreateBy()));
        });
      }
    }
    return CommonRespDto.success(experienceDtoList);
  }
}
