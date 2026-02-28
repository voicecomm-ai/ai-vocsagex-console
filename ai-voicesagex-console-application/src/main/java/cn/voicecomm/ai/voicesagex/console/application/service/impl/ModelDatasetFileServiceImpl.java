package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.model.ModelDatasetFileService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFileDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.model.ModelDatasetFilePageReq;
import cn.voicecomm.ai.voicesagex.console.application.converter.ModelDatasetFileConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.ModelDatasetFileMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.model.ModelDatasetFilePo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 数据集文件接口实现类
 *
 * @author ryc
 * @date 2025-08-06 13:17:29
 */
@DubboService
@Service
@Slf4j
@RequiredArgsConstructor
public class ModelDatasetFileServiceImpl extends
    ServiceImpl<ModelDatasetFileMapper, ModelDatasetFilePo> implements ModelDatasetFileService {

  private final ModelDatasetFileConverter modelDatasetFileConverter;

  @Override
  public CommonRespDto<PagingRespDto<ModelDatasetFileDto>> getPageList(
      ModelDatasetFilePageReq pageReq) {
    Page<ModelDatasetFilePo> page = Page.of(pageReq.getCurrent(), pageReq.getSize());
    LambdaQueryWrapper<ModelDatasetFilePo> lambdaQuery = Wrappers.<ModelDatasetFilePo>lambdaQuery()
        .apply(CharSequenceUtil.isNotBlank(pageReq.getName()), "name ILIKE {0}",
            "%" + SpecialCharUtil.transfer(pageReq.getName()) + "%")
        .eq(ModelDatasetFilePo::getDatasetId, pageReq.getDatasetId())
        .orderByDesc(BasePo::getCreateTime).orderByDesc(ModelDatasetFilePo::getId);
    Page<ModelDatasetFilePo> modelPoPage = baseMapper.selectPage(page, lambdaQuery);
    return CommonRespDto.success(modelDatasetFileConverter.pagePoToDto(modelPoPage));
  }

}