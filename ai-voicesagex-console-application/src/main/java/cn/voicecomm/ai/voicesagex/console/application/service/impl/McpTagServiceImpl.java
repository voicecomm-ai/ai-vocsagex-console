package cn.voicecomm.ai.voicesagex.console.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.mcp.McpTagService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.mcp.McpTagDto;
import cn.voicecomm.ai.voicesagex.console.application.converter.McpTagConverter;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpTagMapper;
import cn.voicecomm.ai.voicesagex.console.application.dao.mapper.McpTagRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BasePo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagPo;
import cn.voicecomm.ai.voicesagex.console.util.po.mcp.McpTagRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
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
public class McpTagServiceImpl extends ServiceImpl<McpTagMapper, McpTagPo> implements
    McpTagService {

  private final McpTagConverter mcpTagConverter;

  private final McpTagRelationMapper mcpTagRelationMapper;

  private final McpMapper mcpMapper;

  @Override
  public CommonRespDto<List<McpTagDto>> getList(String tagName) {

    List<McpTagPo> list = baseMapper.selectList(Wrappers.<McpTagPo>lambdaQuery()
        .like(StrUtil.isNotBlank(tagName), McpTagPo::getName, tagName)
        .orderByDesc(BasePo::getCreateTime));

    List<McpTagDto> dtoList = mcpTagConverter.poListToDtoList(list);
    if (CollUtil.isNotEmpty(dtoList)) {
      List<Integer> tagIdList = dtoList.stream().map(McpTagDto::getId).toList();
      List<McpTagRelationPo> relationPoList = mcpTagRelationMapper.selectList(
          Wrappers.<McpTagRelationPo>lambdaQuery().in(McpTagRelationPo::getTagId, tagIdList));
      if (CollUtil.isNotEmpty(relationPoList)) {
        // 根据标签id分组成map，key为标签id，value为应用id的list的size的map
        Map<Integer, Long> tagIdMap = relationPoList.stream()
            .collect(Collectors.groupingBy(McpTagRelationPo::getTagId, Collectors.counting()));
        dtoList.forEach(dto -> dto.setTagUsedNumber(tagIdMap.getOrDefault(dto.getId(), 0L)));
      }
    }
    return CommonRespDto.success(dtoList);
  }


  @Override
  public CommonRespDto<List<McpTagDto>> getListByShelfMcp() {

    // 获取已上架的mcp ids
    List<Integer> mcpIds = mcpMapper.selectObjs(
        Wrappers.<McpPo>lambdaQuery().select(McpPo::getId).eq(McpPo::getIsShelf, true));
    if (CollUtil.isEmpty(mcpIds)) {
      return CommonRespDto.success(new ArrayList<>());
    }

    // 根据关联表查询标签ids
    List<Integer> tagIds = mcpTagRelationMapper.selectObjs(
        Wrappers.<McpTagRelationPo>lambdaQuery().select(McpTagRelationPo::getTagId)
            .in(McpTagRelationPo::getMcpId, mcpIds));
    if (CollUtil.isEmpty(tagIds)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    List<McpTagPo> list = baseMapper.selectList(
        Wrappers.<McpTagPo>lambdaQuery().in(McpTagPo::getId, tagIds)
            .orderByDesc(BasePo::getCreateTime));
    return CommonRespDto.success(mcpTagConverter.poListToDtoList(list));
  }

  @Override
  public CommonRespDto<McpTagDto> getById(Integer id) {
    McpTagPo po = baseMapper.selectById(id);
    if (po != null) {
      McpTagDto dto = mcpTagConverter.poToDto(po);
      dto.setTagUsedNumber(mcpTagRelationMapper.selectCount(
          Wrappers.<McpTagRelationPo>lambdaQuery().eq(McpTagRelationPo::getTagId, id)));
      return CommonRespDto.success(dto);
    }
    return CommonRespDto.error();
  }

  @Override
  public CommonRespDto<Integer> add(McpTagDto dto) {
    McpTagPo po = mcpTagConverter.dtoToPo(dto);
    // 查询所有数量
    Long l = baseMapper.selectCount(Wrappers.lambdaQuery());
    if (l >= 30) {
      return CommonRespDto.error("最多添加30个标签");
    }
    // 重名校验
    if (baseMapper.selectCount(Wrappers.<McpTagPo>lambdaQuery().eq(McpTagPo::getName, po.getName()))
        > 0) {
      return CommonRespDto.error("名称已存在");
    }
    baseMapper.insert(po);
    return CommonRespDto.success(po.getId());
  }

  @Override
  public CommonRespDto<Void> update(McpTagDto dto) {
    McpTagPo po = mcpTagConverter.dtoToPo(dto);
    // 重名校验
    if (baseMapper.selectCount(Wrappers.<McpTagPo>lambdaQuery().eq(McpTagPo::getName, po.getName())
        .ne(McpTagPo::getId, po.getId())) > 0) {
      return CommonRespDto.error("名称已存在");
    }
    int i = baseMapper.updateById(po);
    return CommonRespDto.of(i > 0);
  }

  @Override
  public CommonRespDto<Void> delete(Integer id) {
    int i = baseMapper.deleteById(id);
    mcpTagRelationMapper.delete(
        Wrappers.<McpTagRelationPo>lambdaQuery().eq(McpTagRelationPo::getTagId, id));
    return CommonRespDto.of(i > 0);
  }

}
