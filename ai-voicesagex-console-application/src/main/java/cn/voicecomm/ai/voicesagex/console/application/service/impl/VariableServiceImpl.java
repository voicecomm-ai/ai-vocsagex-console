package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.application.VariableService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.VariableDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.VariableOptionRespDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.VariableConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.VariableMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.application.workflow.VariablePo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
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
public class VariableServiceImpl extends ServiceImpl<VariableMapper, VariablePo> implements
    VariableService {

  private final VariableConverter variableConverter;

  @Override
  public CommonRespDto<VariableDto> add(VariableDto dto) {
    VariablePo po = variableConverter.dtoToPo(dto);
    int i = baseMapper.insert(po);
    if (i <= 0) {
      return CommonRespDto.error();
    }
    VariablePo variablePo = baseMapper.selectById(po.getId());

    return CommonRespDto.success(variableConverter.poToDto(variablePo));
  }

  @Override
  public CommonRespDto<Void> update(VariableDto dto) {
    VariablePo po = variableConverter.dtoToPo(dto);
    int i = baseMapper.updateById(po);
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<VariableDto> getInfo(Integer id) {
    VariablePo po = baseMapper.selectById(id);
    if (ObjUtil.isNull(po)) {
      return CommonRespDto.error("数据不存在");
    }
    return CommonRespDto.success(variableConverter.poToDto(po));
  }

  @Override
  public CommonRespDto<Void> delete(Integer id) {
    return null;
  }

  @Override
  public CommonRespDto<List<VariableOptionRespDto>> variableOptions(Integer id) {
    VariablePo po = baseMapper.selectById(id);
    // 循环内部节点（内部流程向前递归，当前所属的循环节点向前递归）

    // 变量赋值节点用于向可写入变量进行变量赋值，已支持以下可写入变量：
    //会话变量
    //循环变量
    // 手动测试发现迭代变量也可以（文档未给出）


    // 普通节点
    // 根据所属nodeId递归向上查询
    return null;
  }
}
