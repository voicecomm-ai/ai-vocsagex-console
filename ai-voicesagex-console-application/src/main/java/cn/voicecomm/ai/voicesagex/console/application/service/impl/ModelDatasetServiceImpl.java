package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelDatasetService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto.Type;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.ModelDatasetAttachmentDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetPageReq;
import cn.voicecomm.ai.voicesagex.console.api.enums.WhetherEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelDatasetConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelDatasetFileMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelDatasetMapper;
import cn.voicecomm.ai.voicesagex.console.application.handler.MessageHandler;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelDatasetFilePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelDatasetPo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ZipNodePo;
import cn.voicecomm.ai.voicesagex.console.util.util.FileReadUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.MybatisPlusUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据集接口实现类
 *
 * @author ryc
 * @date 2025-07-29 10:12:03
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelDatasetServiceImpl extends
    ServiceImpl<ModelDatasetMapper, ModelDatasetPo> implements ModelDatasetService {

  private final ModelDatasetConverter modelDatasetConverter;

  private final ModelDatasetFileMapper modelDatasetFileMapper;

  private final MessageHandler messageHandler;

  private static ExecutorService executor = Executors.newCachedThreadPool();

  @DubboReference
  private BackendUserService backendUserService;

  @Override
  public CommonRespDto<PagingRespDto<ModelDatasetDto>> getPageList(ModelDatasetPageReq pageReq) {
    Page<ModelDatasetPo> page = Page.of(pageReq.getCurrent(), pageReq.getSize());
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();
    LambdaQueryWrapper<ModelDatasetPo> lambdaQuery = Wrappers.<ModelDatasetPo>lambdaQuery()
        .apply(CharSequenceUtil.isNotBlank(pageReq.getName()), "name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(pageReq.getName()) + "%")
        .eq(ObjectUtil.isNotNull(pageReq.getType()), ModelDatasetPo::getType, pageReq.getType())
        .in(CollUtil.isNotEmpty(pageReq.getClassificationIdList()),
            ModelDatasetPo::getClassification, pageReq.getClassificationIdList())
        .in(BaseAuditPo::getCreateBy, userIdList).orderByDesc(BasePo::getCreateTime)
        .orderByDesc(ModelDatasetPo::getId);
    Page<ModelDatasetPo> modelPoPage = baseMapper.selectPage(page, lambdaQuery);
    PagingRespDto<ModelDatasetDto> datasetDtoPagingRespDto = modelDatasetConverter.pagePoToDto(
        modelPoPage);
    List<ModelDatasetDto> modelDatasetDtoList = datasetDtoPagingRespDto.getRecords();
    modelDatasetDtoList.forEach(modelDatasetDto -> modelDatasetDto.setAnalysisDesc(
        WhetherEnum.FALSE.getKey().equals(modelDatasetDto.getIsAnalysis()) ? "解析中"
            : WhetherEnum.TURE.getKey().equals(modelDatasetDto.getIsAnalysis()) ? String.valueOf(
                modelDatasetDto.getFileNum()) : "解析失败"));
    return CommonRespDto.success(datasetDtoPagingRespDto);
  }

  @Override
  public CommonRespDto<ModelDatasetDto> getInfo(Integer id) {
    ModelDatasetPo modelDatasetPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelDatasetPo)) {
      return CommonRespDto.error("数据不存在");
    }
    return CommonRespDto.success(modelDatasetConverter.poToDto(modelDatasetPo));
  }

  @Override
  public CommonRespDto<Integer> save(ModelDatasetDto modelDatasetDto) {
    Long selectCount = baseMapper.selectCount(Wrappers.<ModelDatasetPo>lambdaQuery()
        .eq(ModelDatasetPo::getName, modelDatasetDto.getName()));
    if (selectCount > 0) {
      return CommonRespDto.error("名称重复");
    }
    Integer userId = UserAuthUtil.getUserId();
    ModelDatasetPo modelDatasetPo = modelDatasetConverter.dtoToPo(modelDatasetDto);
    baseMapper.insert(modelDatasetPo);
    ModelDatasetAttachmentDto modelDatasetAttachmentDto = ModelDatasetAttachmentDto.builder()
        .id(modelDatasetPo.getId()).build();
    executor.submit(() -> {
      try {
        Thread.sleep(1500);
        log.info("数据集数据信息：{}", JSONUtil.toJsonStr(modelDatasetPo));
        List<ZipNodePo> zipNodePoList = FileReadUtil.extractEntity(modelDatasetPo.getPath());
        List<ModelDatasetFilePo> modelDatasetFilePoList = new ArrayList<>();
        zipNodePoList.forEach(zipNodePo -> modelDatasetFilePoList.add(
            ModelDatasetFilePo.builder().datasetId(modelDatasetPo.getId()).name(zipNodePo.getName())
                .size(zipNodePo.getSize()).path(zipNodePo.getFullPath()).build()));
        MybatisPlusUtil<ModelDatasetFilePo> mybatisPlusUtil = new MybatisPlusUtil<>(
            modelDatasetFileMapper, ModelDatasetFilePo.class);
        mybatisPlusUtil.saveBatch(modelDatasetFilePoList, 2000);
        modelDatasetPo.setIsAnalysis(WhetherEnum.TURE.getKey());
        modelDatasetPo.setFileNum(zipNodePoList.size());
        baseMapper.updateById(modelDatasetPo);
        //发送解析成功的消息
        modelDatasetAttachmentDto.setFileNum(zipNodePoList.size());
        messageHandler.sendMessage(MessageTypeEnum.MODEL_DATASET_ANALYSIS_NOTICE,
            modelDatasetPo.getName(), userId, Type.SUCCESS,
            JSONUtil.toJsonStr(modelDatasetAttachmentDto), StrUtil.EMPTY, true);
      } catch (Exception e) {
        // 发送失败消息
        // 设置解析失败
        log.error("解析文件失败:{}", e.getMessage(), e);
        modelDatasetPo.setIsAnalysis(2);
        baseMapper.updateById(modelDatasetPo);
        modelDatasetAttachmentDto.setFileNum(0);
        modelDatasetAttachmentDto.setReason(e.getMessage());
        messageHandler.sendMessage(MessageTypeEnum.MODEL_DATASET_ANALYSIS_NOTICE,
            modelDatasetPo.getName(), userId, Type.FAILURE,
            JSONUtil.toJsonStr(modelDatasetAttachmentDto), StrUtil.EMPTY, true);
      }
    });
    return CommonRespDto.success(modelDatasetPo.getId());
  }

  @Override
  public CommonRespDto<Boolean> update(ModelDatasetDto modelDatasetDto) {
    Long selectCount = baseMapper.selectCount(Wrappers.<ModelDatasetPo>lambdaQuery()
        .eq(ModelDatasetPo::getName, modelDatasetDto.getName())
        .ne(ModelDatasetPo::getId, modelDatasetDto.getId()));
    if (selectCount > 0) {
      return CommonRespDto.error("名称重复");
    }
    ModelDatasetPo modelDatasetPo = modelDatasetConverter.dtoToPo(modelDatasetDto);
    baseMapper.updateById(modelDatasetPo);
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> delete(Integer id) {
    ModelDatasetPo modelDatasetPo = baseMapper.selectById(id);
    if (ObjectUtil.isNull(modelDatasetPo)) {
      return CommonRespDto.error("数据不存在");
    }
    baseMapper.deleteById(id);
    modelDatasetFileMapper.delete(
        Wrappers.<ModelDatasetFilePo>lambdaQuery().eq(ModelDatasetFilePo::getDatasetId, id));
    if (CharSequenceUtil.isNotBlank(modelDatasetPo.getPath())) {
      FileReadUtil.deleteBatchFile(CollUtil.newArrayList(modelDatasetPo.getPath()));
    }
    return CommonRespDto.success(Boolean.TRUE);
  }

  @Override
  public Long getModelDataSetNo(Integer userId) {
    List<Integer> userIdList = backendUserService.getUserIdsByUserId(UserAuthUtil.getUserId())
        .getData();
    return count(Wrappers.<ModelDatasetPo>lambdaQuery().in(BaseAuditPo::getCreateBy, userIdList));

  }

}