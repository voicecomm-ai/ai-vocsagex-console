package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleMenuRelationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleMenuRelationDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendRoleMenuRelationConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendRoleMenuRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RoleMenuRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class BackendRoleMenuRelationServiceImpl extends
  ServiceImpl<BackendRoleMenuRelationMapper, RoleMenuRelationPo> implements
  BackendRoleMenuRelationService {

  private final BackendRoleMenuRelationConverter backendRoleMenuRelationConverter;

  @Override
  public CommonRespDto<List<BackendRoleMenuRelationDto>> getRelationsByRoleId(Integer roleId) {
    List<RoleMenuRelationPo> relationPoList = baseMapper.selectList(
      Wrappers.<RoleMenuRelationPo>lambdaQuery()
        .eq(RoleMenuRelationPo::getRoleId, roleId));
    return CommonRespDto.success(backendRoleMenuRelationConverter.poListToDtoList(relationPoList));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> batchSave(Integer roleId, List<Integer> menuIds) {
    List<RoleMenuRelationPo> relationPoList = CollUtil.newArrayList();
    menuIds.forEach(menuId -> relationPoList.add(RoleMenuRelationPo.builder().roleId(roleId)
      .menuId(menuId).build()));
    saveBatch(relationPoList);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> deleteByRoleId(Integer roleId) {

    remove(Wrappers.<RoleMenuRelationPo>lambdaQuery()
      .eq(RoleMenuRelationPo::getRoleId, roleId));

    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Boolean> hasMenu(List<Integer> roleIds, Integer menuId) {
    Long count = baseMapper.selectCount(Wrappers.<RoleMenuRelationPo>lambdaQuery()
      .in(RoleMenuRelationPo::getRoleId, roleIds)
      .eq(RoleMenuRelationPo::getMenuId, menuId));
    return CommonRespDto.success(count > 0);
  }
}
