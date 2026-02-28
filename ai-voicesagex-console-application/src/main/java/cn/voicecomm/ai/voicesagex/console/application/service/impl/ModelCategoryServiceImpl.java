package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelCategoryService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelCategoryPageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelTagDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.ClassificationEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelCategoryConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelCategoryMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelCategoryPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 模型分类接口实现类
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelCategoryServiceImpl extends
    ServiceImpl<ModelCategoryMapper, ModelCategoryPo> implements ModelCategoryService {

  private final ModelMapper modelMapper;

  private final ModelTagMapper modelTagMapper;

  private final ModelTagRelationMapper modelTagRelationMapper;

  private final ModelCategoryConverter modelCategoryConverter;

  private final ModelTagConverter modelTagConverter;

  @Override
  public CommonRespDto<List<ModelCategoryPageDto>> getList(ModelCategoryPageReq pageReq) {
    List<ModelCategoryPo> modelCategoryPoList = baseMapper.selectList(
        Wrappers.<ModelCategoryPo>lambdaQuery()
            .eq(ObjectUtil.isNotNull(pageReq.getIsBuilt()), ModelCategoryPo::getIsBuilt,
                pageReq.getIsBuilt()).orderByAsc(ModelCategoryPo::getId));
    List<ModelCategoryPageDto> modelCategoryPageDtoList = modelCategoryConverter.poListToPageDtoList(
        modelCategoryPoList);
    Map<Integer, List<ModelTagPo>> modelTagMap = new HashMap<>();
    Map<Integer, Long> tagModelRelateNumMap = new HashMap<>();
    // 先查询出内置的数据
    if (BooleanUtil.isFalse(pageReq.getIsSquare())) {
      Map<Integer, List<ModelTagPo>> tagList = modelTagMapper.selectList(
              Wrappers.<ModelTagPo>lambdaQuery().orderByDesc(BasePo::getCreateTime)
                  .orderByDesc(ModelTagPo::getId)).stream()
          .collect(Collectors.groupingBy(ModelTagPo::getCategoryId));
      if (CollUtil.isNotEmpty(tagList)) {
        modelTagMap.putAll(tagList);
      }
    } else {
      // 先查询出上架的模型
      List<Integer> modelIds = modelMapper.selectList(
              Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getIsShelf, Boolean.TRUE)).stream()
          .map(ModelPo::getId).collect(Collectors.toList());
      if (CollUtil.isNotEmpty(modelIds)) {
        // 查询出模型关联的标签
        List<ModelTagRelationPo> modelTagRelationPoList = modelTagRelationMapper.selectList(
            Wrappers.<ModelTagRelationPo>lambdaQuery()
                .in(ModelTagRelationPo::getModelId, modelIds));
        tagModelRelateNumMap = modelTagRelationPoList.stream()
            .collect(Collectors.groupingBy(ModelTagRelationPo::getTagId, Collectors.counting()));
        // 查询出标签
        Set<Integer> tagIds = modelTagRelationPoList.stream().map(ModelTagRelationPo::getTagId)
            .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(tagIds)) {
          // 分类下的
          modelTagMap.putAll(modelTagMapper.selectList(
                  Wrappers.<ModelTagPo>lambdaQuery().in(ModelTagPo::getId, tagIds)
                      .orderByDesc(BasePo::getCreateTime).orderByDesc(ModelTagPo::getId)).stream()
              .collect(Collectors.groupingBy(ModelTagPo::getCategoryId)));
        }
      }
    }
    Map<Integer, Long> finalTagModelRelateNumMap = tagModelRelateNumMap;
    modelCategoryPageDtoList.forEach(categoryPageDto -> {
      if (BooleanUtil.isTrue(categoryPageDto.getIsBuilt())) {
        List<ModelTagPo> tagPoList = Arrays.stream(ClassificationEnum.values()).map(
            item -> ModelTagPo.builder().id(item.getKey()).categoryId(categoryPageDto.getId())
                .name(item.getDesc()).build()).collect(Collectors.toList());
        List<ModelTagDto> modelTagDtoList = modelTagConverter.poListToDtoList(tagPoList);
        // 查询模型数量
        if (BooleanUtil.isTrue(pageReq.getIsSquare())) {
          Map<Integer, Long> classificationMap = modelMapper.selectList(
                  Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getIsShelf, Boolean.TRUE)).stream()
              .collect(Collectors.groupingBy(ModelPo::getClassification, Collectors.counting()));
          modelTagDtoList.forEach(modelTagDto -> modelTagDto.setModelRelationNum(
              classificationMap.getOrDefault(modelTagDto.getId(), 0L)));
        }
        categoryPageDto.setModelTagList(modelTagDtoList);
      } else {
        List<ModelTagPo> modelTagPoList = modelTagMap.getOrDefault(categoryPageDto.getId(),
            CollUtil.newArrayList());
        List<ModelTagDto> modelTagDtoList = modelTagConverter.poListToDtoList(modelTagPoList);
        // 广场查询才需要赋值
        if (BooleanUtil.isTrue(pageReq.getIsSquare()) && CollUtil.isNotEmpty(modelTagDtoList)) {
          modelTagDtoList.forEach(modelTagDto -> modelTagDto.setModelRelationNum(
              finalTagModelRelateNumMap.getOrDefault(modelTagDto.getId(), 0L)));
        }
        categoryPageDto.setModelTagList(modelTagDtoList);
      }
    });
    return CommonRespDto.success(modelCategoryPageDtoList);
  }

  @Override
  public CommonRespDto<ModelCategoryPageDto> getInfo(Integer id) {
    ModelCategoryPo modelCategoryPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelCategoryPo)) {
      return CommonRespDto.error("数据不存在");
    }
    ModelCategoryPageDto modelCategoryPageDto = modelTagConverter.poToPageDto(modelCategoryPo);
    if (BooleanUtil.isTrue(modelCategoryPo.getIsBuilt())) {
      List<ModelTagPo> tagPoList = Arrays.stream(ClassificationEnum.values()).map(
          item -> ModelTagPo.builder().id(item.getKey()).categoryId(id).name(item.getDesc())
              .build()).collect(Collectors.toList());
      modelCategoryPageDto.setModelTagList(modelTagConverter.poListToDtoList(tagPoList));
      return CommonRespDto.success(modelCategoryPageDto);
    }
    List<ModelTagPo> modelTagPoList = modelTagMapper.selectList(
        Wrappers.<ModelTagPo>lambdaQuery().eq(ModelTagPo::getCategoryId, id)
            .orderByDesc(BasePo::getCreateTime).orderByDesc(ModelTagPo::getId));
    List<ModelTagDto> modelTagDtoList = modelTagConverter.poListToDtoList(modelTagPoList);
    if (CollUtil.isNotEmpty(modelTagDtoList)) {
      List<Integer> tagIds = modelTagDtoList.stream().map(ModelTagDto::getId)
          .collect(Collectors.toList());
      Map<Integer, Long> tagNumMap = modelTagRelationMapper.selectList(
              Wrappers.<ModelTagRelationPo>lambdaQuery().in(ModelTagRelationPo::getTagId, tagIds))
          .stream()
          .collect(Collectors.groupingBy(ModelTagRelationPo::getTagId, Collectors.counting()));
      modelTagDtoList.forEach(modelTagDto -> modelTagDto.setModelRelationNum(
          tagNumMap.getOrDefault(modelTagDto.getId(), 0L)));
    }
    modelCategoryPageDto.setModelTagList(modelTagDtoList);
    return CommonRespDto.success(modelCategoryPageDto);
  }

  @Override
  public CommonRespDto<Integer> save(ModelCategoryDto modelCategoryDto) {
    Long count = baseMapper.selectCount(
        Wrappers.<ModelCategoryPo>lambdaQuery().eq(ModelCategoryPo::getIsBuilt, Boolean.FALSE));
    if (count > 0) {
      return CommonRespDto.error("分类只能添加一次");
    }
    Long selectCount = baseMapper.selectCount(Wrappers.<ModelCategoryPo>lambdaQuery()
        .eq(ModelCategoryPo::getName, modelCategoryDto.getName()));
    if (selectCount > 0) {
      return CommonRespDto.error("分类已存在");
    }
    ModelCategoryPo modelCategoryPo = modelCategoryConverter.dtoToPo(modelCategoryDto);
    baseMapper.insert(modelCategoryPo);
    return CommonRespDto.success(modelCategoryPo.getId());
  }

  @Override
  public CommonRespDto<Boolean> update(ModelCategoryDto modelCategoryDto) {
    Long selectCount = baseMapper.selectCount(Wrappers.<ModelCategoryPo>lambdaQuery()
        .eq(ModelCategoryPo::getName, modelCategoryDto.getName())
        .ne(ModelCategoryPo::getId, modelCategoryDto.getId()));
    if (selectCount > 0) {
      return CommonRespDto.error("分类已存在");
    }
    ModelCategoryPo modelCategoryPo = modelCategoryConverter.dtoToPo(modelCategoryDto);
    baseMapper.updateById(modelCategoryPo);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(Integer id) {
    ModelCategoryPo modelCategoryPo = baseMapper.selectById(id);
    if (BooleanUtil.isTrue(modelCategoryPo.getIsBuilt())) {
      return CommonRespDto.error("内置分类不能删除");
    }
    baseMapper.deleteById(id);
    modelTagMapper.delete(Wrappers.<ModelTagPo>lambdaQuery().eq(ModelTagPo::getCategoryId, id));
    modelTagRelationMapper.delete(
        Wrappers.<ModelTagRelationPo>lambdaQuery().eq(ModelTagRelationPo::getCategoryId, id));
    return CommonRespDto.success(Boolean.TRUE);
  }

}