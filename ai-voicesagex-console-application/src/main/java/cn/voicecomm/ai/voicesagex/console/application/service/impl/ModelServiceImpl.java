package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import static cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil.replaceFirstDataToEmptyStr;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.constant.model.ModelConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto.Type;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.ModelAttachmentDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelBaseResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelBatchReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDownloadParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDownloadParamDto.ArchDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInstanceConfigDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeBaseDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelInvokeResponse;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelPageReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelParamDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelReq;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ZipNodeDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.WhetherEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ConfigTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.ClassificationEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.GenerateStatusEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.ReasoningModeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.model.ModelEnum.TypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelConverter;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelApiKeyMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.application.handler.MessageHandler;
import cn.voicecomm.ai.voicesagex.console.application.handler.ModelTrainDeployHandler;
import cn.voicecomm.ai.voicesagex.console.util.enums.ResultCodeEnum;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelApiKeyPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelTagRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
import cn.voicecomm.ai.voicesagex.console.util.util.FileReadUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 模型接口实现类
 *
 * @author ryc
 * @date 2025-06-03 17:31:51
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
@RefreshScope
public class ModelServiceImpl extends ServiceImpl<ModelMapper, ModelPo> implements ModelService {

  private final ModelTagRelationMapper modelTagRelationMapper;

  private final ModelApiKeyMapper modelApiKeyMapper;

  private final ModelTagMapper modelTagMapper;

  private final ModelConverter modelConverter;

  private final ModelTagConverter modelTagConverter;

  private final MessageHandler messageHandler;

  private final ModelTrainDeployHandler modelTrainDeployHandler;

  @DubboReference
  private BackendUserService backendUserService;

  /**
   * 预训练模型调用地址
   */
  @Value("${algoUrlPrefix}${preTrainedModel.invoke}")
  private String preTrainedModelInvokeUrl;

  /**
   * 预训练模型下载地址
   */
  @Value("${algoUrlPrefix}${preTrainedModel.download}")
  private String preTrainedModelDownloadUrl;

  /**
   * 模型回调地址
   */
  @Value("${invoke.base-url}")
  private String invokeBaseUrl;

  /**
   * 文件上传路径前缀
   */
  @Value("${file.upload}")
  private String uploadDir;

  @Override
  public CommonRespDto<PagingRespDto<ModelPageDto>> getPageList(ModelPageReq pageReq) {
    PagingRespDto<ModelPageDto> pagingRespDto = new PagingRespDto<>();
    Page<ModelPo> page = Page.of(pageReq.getCurrent(), pageReq.getSize());
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();
    LambdaQueryWrapper<ModelPo> lambdaQuery = Wrappers.<ModelPo>lambdaQuery()
        .apply(StrUtil.isNotBlank(pageReq.getName()), "name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(pageReq.getName()) + "%")
        .eq(ObjectUtil.isNotNull(pageReq.getType()), ModelPo::getType, pageReq.getType())
        .eq(ObjectUtil.isNotNull(pageReq.getIsShelf()), ModelPo::getIsShelf, pageReq.getIsShelf())
        .in(BooleanUtil.isTrue(pageReq.getIsAuth()), BaseAuditPo::getCreateBy, userIdList);
    if (CollUtil.isNotEmpty(pageReq.getClassificationIdList()) || CollUtil.isNotEmpty(
        pageReq.getTagIdList())) {
      List<Integer> orModelIds = new ArrayList<>();
      if (CollUtil.isNotEmpty(pageReq.getClassificationIdList())) {
        orModelIds = baseMapper.selectList(Wrappers.<ModelPo>lambdaQuery()
                .in(ModelPo::getClassification, pageReq.getClassificationIdList())).stream()
            .map(ModelPo::getId).distinct().collect(Collectors.toList());
        if (CollUtil.isEmpty(orModelIds)) {
          pagingRespDto.setCurrent(pageReq.getCurrent());
          pagingRespDto.setSize(pageReq.getSize());
          pagingRespDto.setRecords(new ArrayList<>());
          return CommonRespDto.success(pagingRespDto);
        }
      }
      List<Integer> andModelIds = new ArrayList<>();
      if (CollUtil.isNotEmpty(pageReq.getTagIdList())) {
        LambdaQueryWrapper<ModelTagRelationPo> tagRelationQueryWrapper = new LambdaQueryWrapper<>();
        tagRelationQueryWrapper.in(ModelTagRelationPo::getTagId, pageReq.getTagIdList())
            .select(ModelTagRelationPo::getModelId).groupBy(ModelTagRelationPo::getModelId)
            .having("COUNT(DISTINCT tag_id) = {0}", pageReq.getTagIdList().size());
        andModelIds = modelTagRelationMapper.selectList(tagRelationQueryWrapper).stream()
            .map(ModelTagRelationPo::getModelId).collect(Collectors.toList());
        if (CollUtil.isEmpty(andModelIds)) {
          pagingRespDto.setCurrent(pageReq.getCurrent());
          pagingRespDto.setSize(pageReq.getSize());
          pagingRespDto.setRecords(new ArrayList<>());
          return CommonRespDto.success(pagingRespDto);
        }
      }
      // 取交集
      List<Integer> finalModelIds = orModelIds;
      if (CollUtil.isNotEmpty(orModelIds) && CollUtil.isNotEmpty(andModelIds)) {
        finalModelIds = orModelIds.stream().filter(andModelIds::contains)
            .collect(Collectors.toList());
      } else if (CollUtil.isNotEmpty(andModelIds)) {
        finalModelIds = andModelIds;
      }
      if (CollUtil.isEmpty(finalModelIds)) {
        pagingRespDto.setCurrent(pageReq.getCurrent());
        pagingRespDto.setSize(pageReq.getSize());
        pagingRespDto.setRecords(new ArrayList<>());
        return CommonRespDto.success(pagingRespDto);
      }
      lambdaQuery.in(CollUtil.isNotEmpty(finalModelIds), ModelPo::getId, finalModelIds);
    }
    lambdaQuery.orderByDesc(BasePo::getUpdateTime).orderByDesc(ModelPo::getId);
    Page<ModelPo> modelPoPage = baseMapper.selectPage(page, lambdaQuery);
    pagingRespDto = modelConverter.pagePoToDto(modelPoPage);
    List<ModelPageDto> modelPageDtoList = pagingRespDto.getRecords();
    // 处理具体数据
    dealModelList(modelPageDtoList);
    return CommonRespDto.success(pagingRespDto);
  }

  @Override
  public CommonRespDto<List<ModelPageDto>> getList(ModelReq modelReq) {
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();
    LambdaQueryWrapper<ModelPo> lambdaQuery = Wrappers.<ModelPo>lambdaQuery()
        .like(StrUtil.isNotBlank(modelReq.getName()), ModelPo::getName,
            SpecialCharUtil.transfer(modelReq.getName()))
        .eq(ObjectUtil.isNotNull(modelReq.getType()), ModelPo::getType, modelReq.getType())
        .in(CollUtil.isNotEmpty(modelReq.getTagIdList()), ModelPo::getClassification,
            modelReq.getTagIdList())
        .eq(ObjectUtil.isNotNull(modelReq.getIsShelf()), ModelPo::getIsShelf, modelReq.getIsShelf())
        .eq(ObjectUtil.isNotNull(modelReq.getIsSupportVisual()), ModelPo::getIsSupportVisual,
            modelReq.getIsSupportVisual())
        .eq(ObjectUtil.isNotNull(modelReq.getIsSupportAdjust()), ModelPo::getIsSupportAdjust,
            modelReq.getIsSupportAdjust())
        .eq(ObjectUtil.isNotNull(modelReq.getGenerateStatus()), ModelPo::getGenerateStatus,
            modelReq.getGenerateStatus())
        .eq(StrUtil.isNotBlank(modelReq.getLoadingMode()), ModelPo::getLoadingMode,
            modelReq.getLoadingMode())
        .in(BooleanUtil.isTrue(modelReq.getIsAuth()), BaseAuditPo::getCreateBy, userIdList)
        .orderByDesc(BasePo::getUpdateTime).orderByDesc(ModelPo::getId);
    List<ModelPo> modelPoList = baseMapper.selectList(lambdaQuery);
    List<ModelPageDto> modelPageDtoList = modelConverter.poListToPageDtoList(modelPoList);
    // 处理具体数据
    dealModelList(modelPageDtoList);
    return CommonRespDto.success(modelPageDtoList);
  }

  @Override
  public CommonRespDto<ModelDto> getInfo(Integer id) {
    ModelPo modelPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelPo)) {
      return CommonRespDto.error("数据不存在");
    }
    ModelDto modelDto = modelConverter.poToDto(modelPo);
    List<ModelTagRelationPo> modelTagRelationPoList = modelTagRelationMapper.selectList(
        Wrappers.<ModelTagRelationPo>lambdaQuery()
            .in(ModelTagRelationPo::getModelId, modelDto.getId()));
    List<Integer> tagIdList = modelTagRelationPoList.stream().map(ModelTagRelationPo::getTagId)
        .collect(Collectors.toList());
    modelDto.setTagIdList(tagIdList);
    return CommonRespDto.success(modelDto);
  }

  @Override
  public CommonRespDto<Boolean> isAvailable(Integer id) {
    ModelPo modelPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelPo)) {
      return CommonRespDto.error("模型不存在", Boolean.FALSE);
    }
    if (BooleanUtil.isFalse(modelPo.getIsShelf())) {
      return CommonRespDto.error("模型已下架", Boolean.FALSE);
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Integer> save(ModelDto modelDto) {
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getName, modelDto.getName())
            .eq(ModelPo::getType, modelDto.getType())
            .eq(ModelPo::getClassification, modelDto.getClassification()));
    if (selectCount > 0) {
      return CommonRespDto.error("模型名称重复");
    }
    Long count = baseMapper.selectCount(
        Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getInternalName, modelDto.getInternalName())
            .eq(ModelPo::getType, modelDto.getType()));
    if (count > 0) {
      return CommonRespDto.error("模型内部名称重复");
    }
    modelDto.setGenerateStatus(GenerateStatusEnum.SUCCESS.getKey());
    ModelPo modelPo = modelConverter.dtoToPo(modelDto);
    baseMapper.insert(modelPo);
    if (CollUtil.isNotEmpty(modelDto.getTagIdList())) {
      List<ModelTagPo> modelTagPoList = modelTagMapper.selectList(
          Wrappers.<ModelTagPo>lambdaQuery().in(ModelTagPo::getId, modelDto.getTagIdList()));
      List<ModelTagRelationPo> modelTagRelationPoList = new ArrayList<>();
      modelTagPoList.forEach(modelTagPo -> modelTagRelationPoList.add(
          ModelTagRelationPo.builder().categoryId(modelTagPo.getCategoryId())
              .tagId(modelTagPo.getId()).modelId(modelPo.getId()).build()));
      MybatisPlusUtil<ModelTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          modelTagRelationMapper, ModelTagRelationPo.class);
      mybatisPlusUtil.saveBatch(modelTagRelationPoList, modelTagRelationPoList.size());
    }
    if (TypeEnum.PRE_TRAINING.getKey().equals(modelDto.getType())) {
      moveModelFile(modelDto, modelPo, modelPo, true);
    }
    return CommonRespDto.success(modelPo.getId());
  }


  /**
   * 迁移文件
   *
   * @param modelDto
   * @param modelPo
   * @param isInsert
   * @return
   */
  public CommonRespDto<Void> moveModelFile(ModelDto modelDto, ModelPo modelPo, ModelPo modelInfo,
      Boolean isInsert) {
    // 迁移文件位置
    if ((StrUtil.isNotBlank(modelDto.getCodeUrl()) && isInsert) || (!isInsert
        && !modelDto.getCodeUrl().equals(modelInfo.getCodeUrl()))) {
      // 将临时文件改写到模型下
      String codeUrl = modelDto.getCodeUrl()
          .replaceFirst("/temp/", StrUtil.format("/{}/", modelInfo.getId()));
      // 改写的目录
      String codeDir = codeUrl.substring(0, codeUrl.lastIndexOf("/"));
      CommonRespDto<String> respDto = modelTrainDeployHandler.moveFolder(modelDto.getCodeUrl(),
          codeDir);
      if (respDto.isOk()) {
        modelPo.setCodeUrl(codeUrl);
      }
    }
    if (WhetherEnum.TURE.getKey().equals(modelDto.getWeightStorageType()) && (
        (StrUtil.isNotBlank(modelDto.getWeightStorageUrl()) && isInsert) || (!isInsert
            && !modelDto.getWeightStorageUrl().equals(modelInfo.getWeightStorageUrl())))) {
      String weightStorageUrl = modelDto.getWeightStorageUrl()
          .replaceFirst("/temp/", StrUtil.format("/{}/", modelInfo.getId()));
      String weightStorageDir = weightStorageUrl.substring(0, weightStorageUrl.lastIndexOf("/"));
      CommonRespDto<String> respDto = modelTrainDeployHandler.moveFolder(
          modelDto.getWeightStorageUrl(), weightStorageDir);
      if (respDto.isOk()) {
        modelPo.setWeightStorageUrl(weightStorageUrl);
      }
    }
    if (WhetherEnum.TURE.getKey().equals(modelDto.getQuantifiedStorageType()) && (
        (StrUtil.isNotBlank(modelDto.getQuantifiedStorageUrl()) && isInsert) || (!isInsert
            && !modelDto.getQuantifiedStorageUrl().equals(modelInfo.getQuantifiedStorageUrl())))) {
      String quantifiedStorageUrl = modelDto.getQuantifiedStorageUrl()
          .replaceFirst("/temp/", StrUtil.format("/{}/", modelInfo.getId()));
      String quantifiedStorageDir = quantifiedStorageUrl.substring(0,
          quantifiedStorageUrl.lastIndexOf("/"));
      CommonRespDto<String> respDto = modelTrainDeployHandler.moveFolder(
          modelDto.getQuantifiedStorageUrl(), quantifiedStorageDir);
      if (respDto.isOk()) {
        modelPo.setQuantifiedStorageUrl(quantifiedStorageUrl);
      }
    }
    baseMapper.updateById(modelPo);
    return CommonRespDto.success();
  }


  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> update(ModelDto modelDto) {
    // 最原始的数据
    ModelPo modelInfo = baseMapper.selectById(modelDto.getId());
    if (ObjectUtil.isNull(modelInfo)) {
      return CommonRespDto.error("模型不存在");
    }
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getName, modelDto.getName())
            .eq(ModelPo::getType, modelDto.getType())
            .eq(ModelPo::getClassification, modelDto.getClassification())
            .ne(ModelPo::getId, modelDto.getId()));
    if (selectCount > 0) {
      return CommonRespDto.error("模型名称重复");
    }
    Long count = baseMapper.selectCount(
        Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getInternalName, modelDto.getInternalName())
            .eq(ModelPo::getType, modelDto.getType()).ne(ModelPo::getId, modelDto.getId()));
    if (count > 0) {
      return CommonRespDto.error("模型内部名称重复");
    }
    ModelPo modelPo = modelConverter.dtoToPo(modelDto);
    Integer userId = UserAuthUtil.getUserId();
    baseMapper.updateById(modelPo);
    ModelPo model = baseMapper.selectById(modelPo.getId());
    modelTagRelationMapper.delete(Wrappers.<ModelTagRelationPo>lambdaQuery()
        .eq(ModelTagRelationPo::getModelId, modelPo.getId()));
    if (CollUtil.isNotEmpty(modelDto.getTagIdList())) {
      List<ModelTagPo> modelTagPoList = modelTagMapper.selectList(
          Wrappers.<ModelTagPo>lambdaQuery().in(ModelTagPo::getId, modelDto.getTagIdList()));
      List<ModelTagRelationPo> modelTagRelationPoList = new ArrayList<>();
      modelTagPoList.forEach(modelTagPo -> modelTagRelationPoList.add(
          ModelTagRelationPo.builder().categoryId(modelTagPo.getCategoryId())
              .tagId(modelTagPo.getId()).modelId(modelPo.getId()).build()));
      MybatisPlusUtil<ModelTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
          modelTagRelationMapper, ModelTagRelationPo.class);
      mybatisPlusUtil.saveBatch(modelTagRelationPoList, modelTagRelationPoList.size());
    }
    // 如果是预训练模型，需要调用接口生成
    if (TypeEnum.PRE_TRAINING.getKey().equals(modelPo.getType()) && BooleanUtil.isFalse(
        modelInfo.getIsSpecial())) {
      moveModelFile(modelDto, model, modelInfo, false);
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(Integer id) {
    baseMapper.deleteById(id);
    modelTagRelationMapper.delete(
        Wrappers.<ModelTagRelationPo>lambdaQuery().eq(ModelTagRelationPo::getModelId, id));
    modelApiKeyMapper.delete(
        Wrappers.<ModelApiKeyPo>lambdaQuery().eq(ModelApiKeyPo::getModelId, id));
    deleteModeFile(CollUtil.newArrayList(id));
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> deleteBatch(List<Integer> ids) {
    log.info("传入的模型id：{}", ids);
    if (CollUtil.isNotEmpty(ids)) {
      deleteModeFile(ids);
      baseMapper.deleteBatchIds(ids);
      modelTagRelationMapper.delete(
          Wrappers.<ModelTagRelationPo>lambdaQuery().in(ModelTagRelationPo::getModelId, ids));
      modelApiKeyMapper.delete(
          Wrappers.<ModelApiKeyPo>lambdaQuery().in(ModelApiKeyPo::getModelId, ids));
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  /**
   * 删除模型文件
   *
   * @param ids
   */
  private void deleteModeFile(List<Integer> ids) {
    if (CollUtil.isEmpty(ids)) {
      return;
    }
    List<ModelPo> modelPoList = baseMapper.selectList(
        Wrappers.<ModelPo>lambdaQuery().in(ModelPo::getId, ids));
    if (CollUtil.isNotEmpty(modelPoList)) {
      modelPoList.forEach(modelPo -> {
        if (TypeEnum.PRE_TRAINING.getKey().equals(modelPo.getType())) {
          String folderPath = uploadDir + ModelConstants.MODEL_PRE_TRAIN_PATH + modelPo.getId();
          modelTrainDeployHandler.deleteFolderOrFile(folderPath);
        }
        List<String> filePathList = new ArrayList<>();
        if (CharSequenceUtil.isNotBlank(modelPo.getPath())) {
          filePathList.add(modelPo.getPath());
        }
        if (CharSequenceUtil.isNotBlank(modelPo.getApiDocument())) {
          filePathList.add(modelPo.getApiDocument());
        }
        FileReadUtil.deleteBatchFile(filePathList);
      });
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> shelfBatch(ModelBatchReq modelBatchReq) {
    if (ObjectUtil.isNull(modelBatchReq.getIsShelf())) {
      return CommonRespDto.error("是否上下架不能为空");
    }
    if (Boolean.TRUE.equals(modelBatchReq.getIsShelf())) {
      List<Integer> modelIds = modelBatchReq.getIds();
      Long selectCount = baseMapper.selectCount(
          Wrappers.<ModelPo>lambdaQuery().in(ModelPo::getId, modelIds)
              .eq(ModelPo::getType, TypeEnum.PRE_TRAINING.getKey())
              .ne(ModelPo::getGenerateStatus, GenerateStatusEnum.SUCCESS.getKey()));
      if (selectCount > 0) {
        return CommonRespDto.error("上架失败，存在未添加成功模型");
      }
    }
    List<ModelPo> modelPoList = new ArrayList<>();
    modelBatchReq.getIds().forEach(id -> modelPoList.add(
        ModelPo.builder().id(id).isShelf(modelBatchReq.getIsShelf()).build()));
    return CommonRespDto.success(this.updateBatchById(modelPoList));
  }

  @Override
  public CommonRespDto<Boolean> updateCategoryBatch(ModelBatchReq modelBatchReq) {
    List<Integer> tagIdList = modelBatchReq.getTagIdList();
    if (CollUtil.isEmpty(tagIdList)) {
      return CommonRespDto.error("标签不能为空");
    }
    List<ModelTagPo> modelTagPoList = modelTagMapper.selectList(
        Wrappers.<ModelTagPo>lambdaQuery().in(ModelTagPo::getId, tagIdList));
    if (CollUtil.isEmpty(modelTagPoList)) {
      return CommonRespDto.error("标签不存在");
    }
    List<Integer> modelIds = modelBatchReq.getIds();
    Long selectCount = baseMapper.selectCount(
        Wrappers.<ModelPo>lambdaQuery().in(ModelPo::getId, modelIds)
            .eq(ModelPo::getType, TypeEnum.PRE_TRAINING.getKey())
            .ne(ModelPo::getGenerateStatus, GenerateStatusEnum.SUCCESS.getKey()));
    if (selectCount > 0) {
      return CommonRespDto.error("修改失败，存在未添加成功模型");
    }
    // 先删除模型现有的标签
    modelTagRelationMapper.delete(
        Wrappers.<ModelTagRelationPo>lambdaQuery().in(ModelTagRelationPo::getModelId, modelIds));
    List<ModelTagRelationPo> modelTagRelationPoList = new ArrayList<>();
    modelIds.forEach(modelId -> modelTagPoList.forEach(modelTagPo -> modelTagRelationPoList.add(
        ModelTagRelationPo.builder().categoryId(modelTagPo.getCategoryId())
            .tagId(modelTagPo.getId()).modelId(modelId).build())));
    MybatisPlusUtil<ModelTagRelationPo> mybatisPlusUtil = new MybatisPlusUtil<>(
        modelTagRelationMapper, ModelTagRelationPo.class);
    mybatisPlusUtil.saveBatch(modelTagRelationPoList, modelTagRelationPoList.size());
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public CommonRespDto<Object> invokeStandard(String secret,
      ModelInvokeBaseDto modelInvokeBaseDto) {
    // 根据秘钥查询模型信息
    ModelApiKeyPo modelApiKeyPo = modelApiKeyMapper.selectOne(
        Wrappers.<ModelApiKeyPo>lambdaQuery().eq(ModelApiKeyPo::getSecret, secret), false);
    ModelPo modelPo = baseMapper.selectById(modelApiKeyPo.getModelId());
    if (ObjectUtil.isNull(modelPo) || BooleanUtil.isFalse(modelPo.getIsShelf())) {
      return CommonRespDto.error("模型已下架");
    }
    // 更新时间
    modelApiKeyPo.setLastUsedTime(LocalDateTime.now());
    modelApiKeyMapper.updateById(modelApiKeyPo);
    Integer modelCategoryType = modelPo.getClassification();
    ModelInstanceConfigDto modelInstanceConfigDto = ModelInstanceConfigDto.builder()
        .modelName(modelPo.getInternalName()).baseUrl(modelPo.getUrl()).apiKey(modelPo.getApiKey())
        .llmType(ClassificationEnum.TEXTGENERATION.getKey().equals(modelCategoryType) ? "chat" : "")
        .contextLength(modelPo.getContextLength()).maxTokenLength(modelPo.getContextLength())
        .isSupportVision(modelPo.getIsSupportVisual())
        .isSupportFunction(modelPo.getIsSupportFunction()).build();
    ModelInvokeParamDto modelInvokeParamDto = ModelInvokeParamDto.builder()
        .modelInstanceType(ClassificationEnum.getValueByKey(modelCategoryType))
        .modelInstanceProvider(modelPo.getLoadingMode()).modelInstanceConfig(modelInstanceConfigDto)
        .modelInputs(modelInvokeBaseDto.getModelInputs())
        .modelParameters(modelInvokeBaseDto.getModelParameters()).build();
    try {
      String requestBody = new ObjectMapper().writeValueAsString(modelInvokeParamDto);
      log.info("准备调用预训练模型接口，URL: {}，请求体: {}", preTrainedModelInvokeUrl, requestBody);
      HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(preTrainedModelInvokeUrl))
          .header("Content-Type", "application/json").POST(BodyPublishers.ofString(requestBody))
          .build();
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
      if (response.statusCode() != HttpStatus.HTTP_OK || CharSequenceUtil.isBlank(
          response.body())) {
        return CommonRespDto.error("调用预训练模型失败");
      }
      ModelInvokeResponse modelInvokeResponse = JSONUtil.toBean(response.body(),
          ModelInvokeResponse.class);
      if (ResultCodeEnum.SUCCESS.getCode() == modelInvokeResponse.getCode()) {
        return CommonRespDto.success(modelInvokeResponse.getData());
      } else {
        return CommonRespDto.error(modelInvokeResponse.getMsg());
      }
    } catch (Exception e) {
      log.error("调用预训练模型接口失败", e);
      return CommonRespDto.error("调用失败");
    }
  }

  @Override
  public CommonRespDto<Boolean> preTrainedDownload(Integer id, ArchDto archDto) {
    ModelPo modelPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelPo)) {
      return CommonRespDto.error("模型不存在");
    }
    Integer modelCategoryType = modelPo.getClassification();
    ModelInstanceConfigDto modelInstanceConfigDto = ModelInstanceConfigDto.builder()
        .modelName(modelPo.getInternalName()).baseUrl(modelPo.getUrl())
        .llmType(ClassificationEnum.TEXTGENERATION.getKey().equals(modelCategoryType) ? "chat" : "")
        .contextLength(modelPo.getContextLength()).maxTokenLength(modelPo.getContextLength())
        .isSupportVision(modelPo.getIsSupportVisual())
        .isSupportFunction(modelPo.getIsSupportFunction()).build();
    ModelParamDto modelParamDto = ModelParamDto.builder().userId(UserAuthUtil.getUserId())
        .model(modelConverter.poToDto(modelPo)).build();
    ModelDownloadParamDto modelDownloadParamDto = ModelDownloadParamDto.builder()
        .modelName(modelPo.getInternalName())
        .modelType(ClassificationEnum.getValueByKey(modelCategoryType))
        .modelInstanceConfig(modelInstanceConfigDto).modelInstanceProvider(modelPo.getLoadingMode())
        .sourceDir(replaceFirstDataToEmptyStr(modelPo.getCodeDir()))
        .weightDir(replaceFirstDataToEmptyStr(modelPo.getWeightStorageDir()))
        .modelDir(replaceFirstDataToEmptyStr(modelPo.getQuantifiedStorageDir()))
        .cpuArch(archDto.getCpuArch())
        .gpuArch(BooleanUtil.isTrue(archDto.getUseGpu()) ? archDto.getGpuArch() : null)
        .callbackUrl(invokeBaseUrl + "voicesagex-console/application-web/model/download/callback")
        .callbackBody(modelParamDto).build();
    try {
      String requestBody = new ObjectMapper().writeValueAsString(modelDownloadParamDto);
      log.info("准备调用预训练模型下载接口，URL: {}，请求体: {}", preTrainedModelDownloadUrl,
          requestBody);
      HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(preTrainedModelDownloadUrl))
          .header("Content-Type", "application/json").POST(BodyPublishers.ofString(requestBody))
          .build();
      HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
      log.info("预训练模型下载接口返回: {}", response);
      if (response.statusCode() != HttpStatus.HTTP_OK || CharSequenceUtil.isBlank(
          response.body())) {
        return CommonRespDto.error("调用预训练模型下载失败");
      }
      log.info("预训练模型下载接口返回body: {}", response.body());
      ModelBaseResponse modelBaseResponse = JSONUtil.toBean(response.body(),
          ModelBaseResponse.class);
      if (ResultCodeEnum.SUCCESS.getCode() == modelBaseResponse.getCode()) {
        return CommonRespDto.success("正在生成，结果稍后通知", Boolean.TRUE);
      } else {
        return CommonRespDto.error(SpecialCharUtil.sub(modelBaseResponse.getMsg(), 255));
      }
    } catch (Exception e) {
      log.error("调用预训练模型接口失败", e);
      return CommonRespDto.error("调用下载失败");
    }
  }

  @Override
  public CommonRespDto<Void> downloadCallback(ModelParamDto modelParamDto) {
    log.info("模型下载回调参数：{}", JSONUtil.parseObj(modelParamDto));
    ModelBaseResponse modelBaseResponse = modelParamDto.getResponse();
    ModelDto modelDto = modelParamDto.getModel();
    ModelAttachmentDto modelAttachmentDto = ModelAttachmentDto.builder().id(modelDto.getId())
        .typeName(TypeEnum.getDescByKey(modelDto.getType())).build();
    if (ResultCodeEnum.SUCCESS.getCode() == modelBaseResponse.getCode()) {
      JSONObject dataObject = JSONUtil.parseObj(modelBaseResponse.getData());
      String downloadPath = StrUtil.format("{}{}", "/file/", dataObject.getStr("model_image_path"));
      //发送下载成功的消息
      messageHandler.sendMessage(MessageTypeEnum.MODEL_DOWNLOAD_RESULT_NOTICE, modelDto.getName(),
          modelParamDto.getUserId(), Type.SUCCESS, JSONUtil.toJsonStr(modelAttachmentDto),
          downloadPath, true);
    } else {
      modelAttachmentDto.setReason(SpecialCharUtil.sub(modelBaseResponse.getMsg(), 255));
      //发送下载失败的消息
      messageHandler.sendMessage(MessageTypeEnum.MODEL_DOWNLOAD_RESULT_NOTICE, modelDto.getName(),
          modelParamDto.getUserId(), Type.FAILURE, JSONUtil.toJsonStr(modelAttachmentDto),
          StrUtil.EMPTY, true);
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<ZipNodeDto> buildTree(Integer id) {
    ModelPo modelPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelPo)) {
      return CommonRespDto.error("数据不存在");
    }
    ModelDto modelDto = modelConverter.poToDto(modelPo);
    // 获取上传文件的目录
    if (TypeEnum.ALGORITHM.getKey().equals(modelDto.getType())) {
      ZipNodePo zipNodePo = FileReadUtil.buildZipTree(modelDto.getPath());
      return CommonRespDto.success(modelConverter.zipNodePoToDto(zipNodePo));
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<String> getAlgorithmConfig(Integer id, Integer type, Integer modelSource) {
    String configName = ConfigTypeEnum.getValueByKey(type);
    log.info("类型：{}，获取的配置文件：{}", type, configName);
    String path = StrUtil.EMPTY;
    if (ConfigTypeEnum.ALGORITHM_TRAIN.getKey().equals(type)) {
      // 算法模型训练用的算法模型
      ModelPo modelPo = baseMapper.selectById(id);
      if (ObjectUtil.isNull(modelPo)) {
        return CommonRespDto.error("训练的算法模型不存在");
      }
      path = modelPo.getPath();
    } else if (ConfigTypeEnum.PRE_TRAIN_FINETUNE.getKey().equals(type)) {
      // 预训练模型微调用的预训练模型
      ModelPo modelPo = baseMapper.selectById(id);
      if (ObjectUtil.isNull(modelPo)) {
        return CommonRespDto.error("微调的预训练模型不存在");
      }
      path = modelPo.getCodeUrl();
    }
    String configYaml = FileReadUtil.extractConfigYaml(path, configName);
    if (configYaml == null) {
      return CommonRespDto.error("配置文件不存在");
    }
    return CommonRespDto.success(configYaml);
  }

  /**
   * 处理模型列表数据
   *
   * @param modelPageDtoList 模型数据列表
   */
  private void dealModelList(List<ModelPageDto> modelPageDtoList) {
    if (CollUtil.isNotEmpty(modelPageDtoList)) {
      List<Integer> modelIds = modelPageDtoList.stream().map(ModelPageDto::getId)
          .collect(Collectors.toList());
      // 查询所有的标签
      List<ModelTagRelationPo> modelTagRelationPoList = modelTagRelationMapper.selectList(
          Wrappers.<ModelTagRelationPo>lambdaQuery().in(ModelTagRelationPo::getModelId, modelIds));
      Set<Integer> tagIds = modelTagRelationPoList.stream().map(ModelTagRelationPo::getTagId)
          .collect(Collectors.toSet());
      // 标签名称
      Map<Integer, List<ModelTagPo>> nonBuiltNameMap = new HashMap<>();
      if (CollUtil.isNotEmpty(tagIds)) {
        Map<Integer, ModelTagPo> tagNameMap = modelTagMapper.selectList(
                Wrappers.<ModelTagPo>lambdaQuery().in(ModelTagPo::getId, tagIds)).stream()
            .collect(Collectors.toMap(ModelTagPo::getId, Function.identity()));
        // 非内建标签分组
        Map<Integer, List<Integer>> nonBuiltIdMap = modelTagRelationPoList.stream().collect(
            Collectors.groupingBy(ModelTagRelationPo::getModelId,
                Collectors.mapping(ModelTagRelationPo::getTagId, Collectors.toList())));
        nonBuiltNameMap = nonBuiltIdMap.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
            e -> e.getValue().stream().map(tagNameMap::get).filter(Objects::nonNull)
                .collect(Collectors.toList())));
      }
      Map<Integer, List<ModelTagPo>> finalNonBuiltNameMap = nonBuiltNameMap;
      modelPageDtoList.forEach(modelPageDto -> {
        Integer classification = modelPageDto.getClassification();
        Map<String, String> reasoningModeMap = new HashMap<>();
        reasoningModeMap.put(ReasoningModeEnum.REACT.getKey(), ReasoningModeEnum.REACT.getDesc());
        if ((ClassificationEnum.TEXTGENERATION.getKey().equals(classification)
            || ClassificationEnum.MULTIMODAL.getKey().equals(classification)) && BooleanUtil.isTrue(
            modelPageDto.getIsSupportFunction())) {
          reasoningModeMap.put(ReasoningModeEnum.FUNCTION_CALL.getKey(),
              ReasoningModeEnum.FUNCTION_CALL.getDesc());
        }
        modelPageDto.setReasoningMode(reasoningModeMap);
        modelPageDto.setTypeName(TypeEnum.getDescByKey(modelPageDto.getType()));
        modelPageDto.setClassificationName(ClassificationEnum.getDescByKey(classification));
        List<ModelTagPo> tagList = finalNonBuiltNameMap.getOrDefault(modelPageDto.getId(),
            new ArrayList<>());
        modelPageDto.setTagList(modelTagConverter.poListToDtoList(tagList));
      });
    }
  }

  @Override
  public CommonRespDto<ModelDto> getMemoryModel() {

    ModelPo modelPo = this.baseMapper.selectOne(
        Wrappers.<ModelPo>lambdaQuery().eq(ModelPo::getName, "qllama/bce-embedding-base_v1:f16")
            .eq(ModelPo::getClassification, 6));
    return CommonRespDto.success(modelConverter.poToDto(modelPo));
  }

}