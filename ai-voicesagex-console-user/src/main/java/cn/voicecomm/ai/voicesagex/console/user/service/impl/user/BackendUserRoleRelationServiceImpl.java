package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserRoleRelationService;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserRoleRelationDto;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendRoleConverter;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendUserRoleRelationConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendMenuMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendRoleMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendRoleMenuRelationMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendUserRoleRelationMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.user.MenuPo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RoleMenuRelationPo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RolePo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.UserRoleRelationPo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@DubboService
@RequiredArgsConstructor
public class BackendUserRoleRelationServiceImpl extends
  ServiceImpl<BackendUserRoleRelationMapper, UserRoleRelationPo> implements
  BackendUserRoleRelationService {

  private final BackendUserRoleRelationConverter backendUserRoleRelationConverter;

  private final BackendRoleMapper backendRoleMapper;

  private final BackendRoleConverter backendRoleConverter;
  private final BackendMenuMapper backendMenuMapper;
  private final BackendRoleMenuRelationMapper backendRoleMenuRelationMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> batchSave(Integer userId, Integer roleId) {
    UserRoleRelationPo build = UserRoleRelationPo.builder().roleId(roleId)
      .userId(userId).build();
    save(build);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> deleteByUserId(Integer userId) {
    remove(Wrappers.<UserRoleRelationPo>lambdaQuery()
      .eq(UserRoleRelationPo::getUserId, userId));
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Long> getCountByRoleId(Integer roleId) {
    Long count = baseMapper.selectCount(Wrappers.<UserRoleRelationPo>lambdaQuery()
      .eq(UserRoleRelationPo::getRoleId, roleId));
    return CommonRespDto.success(count);
  }

  /**
   * 根据用户id查询 角色信息
   *
   * @param userId 用户id
   * @return
   */
  @Override
  public CommonRespDto<BackendRoleDto> getRoleByUserId(Integer userId) {
    UserRoleRelationPo userRoleRelationPo = baseMapper.selectOne(
      Wrappers.<UserRoleRelationPo>lambdaQuery()
        .eq(UserRoleRelationPo::getUserId, userId));
    if (userRoleRelationPo == null) {
      return CommonRespDto.success();
    }
    RolePo rolePo = backendRoleMapper.selectById(
      userRoleRelationPo.getRoleId());
    return CommonRespDto.success(backendRoleConverter.poToDto(rolePo));
  }

  @Override
  public CommonRespDto<Map<Integer, Integer>> getRoleIdsByUserIds(List<Integer> userIds) {
    Map<Integer, Integer> collect = baseMapper.selectList(
        Wrappers.<UserRoleRelationPo>lambdaQuery()
          .in(UserRoleRelationPo::getUserId, userIds)).stream()
      .collect(Collectors.toMap(UserRoleRelationPo::getUserId,
        UserRoleRelationPo::getRoleId));
    return CommonRespDto.success(collect);
  }

  @Override
  public CommonRespDto<List<BackendUserRoleRelationDto>> getRelationByUserIds(
    List<Integer> userIds) {
    return CommonRespDto.success(
      backendUserRoleRelationConverter.poListToDtoList(baseMapper.selectList(
        Wrappers.<UserRoleRelationPo>lambdaQuery()
          .in(CollUtil.isNotEmpty(userIds), UserRoleRelationPo::getUserId, userIds))));
  }

  @Override
  public CommonRespDto<List<Integer>> getUserIdsByRoleIds(Integer roleId) {
    List<Integer> userIds = baseMapper.selectList(Wrappers.<UserRoleRelationPo>lambdaQuery()
        .eq(UserRoleRelationPo::getRoleId, roleId)).stream()
      .map(UserRoleRelationPo::getUserId).toList();
    return CommonRespDto.success(userIds);
  }

  @Override
  public CommonRespDto<List<Integer>> getUserIdsByRoleIdList(List<Integer> roleIdList) {
    if (CollUtil.isEmpty(roleIdList)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    List<Integer> userIds = baseMapper.selectList(Wrappers.<UserRoleRelationPo>lambdaQuery()
        .in(UserRoleRelationPo::getRoleId, roleIdList)).stream()
      .map(UserRoleRelationPo::getUserId).toList();
    return CommonRespDto.success(userIds);
  }

  @Override
  public CommonRespDto<List<Integer>> filterTemplateRoleIds(List<Integer> roleIdList) {
    if (CollUtil.isEmpty(roleIdList)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    List<Integer> templateLibraryIds = backendMenuMapper.selectList(
        Wrappers.<MenuPo>lambdaQuery().likeRight(MenuPo::getSign, "templateLibrary"))
      .stream().map(MenuPo::getId).toList();
    List<Integer> list = backendRoleMenuRelationMapper.selectList(
        Wrappers.<RoleMenuRelationPo>lambdaQuery()
          .in(RoleMenuRelationPo::getMenuId, templateLibraryIds)
          .in(RoleMenuRelationPo::getRoleId, roleIdList)).stream()
      .map(RoleMenuRelationPo::getRoleId).toList();

    return CommonRespDto.success(list);
  }

  @Override
  public CommonRespDto<Map<Integer, Long>> getUserCountMap(List<Integer> roleIds) {
    Map<Integer, Long> countMap = baseMapper.selectList(
      Wrappers.<UserRoleRelationPo>lambdaQuery()
        .in(UserRoleRelationPo::getRoleId, roleIds)).stream().collect(
      Collectors.groupingBy(UserRoleRelationPo::getRoleId, Collectors.counting()));
    return CommonRespDto.success(countMap);
  }

}
