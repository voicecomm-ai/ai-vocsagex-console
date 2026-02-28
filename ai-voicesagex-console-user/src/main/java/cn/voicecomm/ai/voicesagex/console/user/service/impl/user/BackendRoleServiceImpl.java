package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendDepartmentService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMessageService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleMenuRelationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserRoleRelationService;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleMenuRelationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserRoleRelationDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.RolePageReqDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.role.RoleDataPermissionEnum;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendRoleConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendMenuMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendRoleMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendRoleMenuRelationMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendUserMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.RolePo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.SysUserPo;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class BackendRoleServiceImpl extends ServiceImpl<BackendRoleMapper, RolePo> implements
    BackendRoleService {

  private final BackendRoleConverter backendRoleConverter;

  private final BackendRoleMenuRelationService backendRoleMenuRelationService;

  private final BackendUserRoleRelationService backendUserRoleRelationService;

  private final RedissonClient redissonClient;

  private final BackendMessageService backendMessageService;

  private final BackendUserInfoService backendUserInfoService;

  private final BackendDepartmentService backendDepartmentService;

  private final BackendUserMapper backendUserMapper;
  private final BackendRoleMenuRelationMapper backendRoleMenuRelationMapper;
  private final BackendMenuMapper backendMenuMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> addRole(BackendRoleDto roleDto) {
    boolean roleUnique = isRoleUnique(roleDto.getRoleName(), null, roleDto.getDeptId()
    );
    if (!roleUnique) {
      return CommonRespDto.error("角色名称重复");
    }
    //保存角色信息
    RolePo rolePo = backendRoleConverter.dtoToPo(roleDto);
    baseMapper.insert(rolePo);
    // 更新菜单关联关系
    backendRoleMenuRelationService.batchSave(rolePo.getId(), roleDto.getMenuIds());

    return CommonRespDto.success();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Boolean> updateRole(BackendRoleDto roleDto) {
    boolean roleUnique = isRoleUnique(roleDto.getRoleName(), roleDto.getId(), roleDto.getDeptId());
    if (!roleUnique) {
      return CommonRespDto.error("角色名称重复");
    }
    RolePo oldRole = getById(roleDto.getId());
    // 旧的菜单
    CommonRespDto<List<BackendRoleMenuRelationDto>> relationsByRoleId = backendRoleMenuRelationService.getRelationsByRoleId(
        roleDto.getId());
    List<Integer> oldMenuIdList = relationsByRoleId.getData().stream()
        .map(BackendRoleMenuRelationDto::getMenuId).sorted().toList();
    //保存角色信息
    RolePo rolePo = backendRoleConverter.dtoToPo(roleDto);
    baseMapper.updateById(rolePo);
    // 更新菜单关联关系
    backendRoleMenuRelationService.deleteByRoleId(rolePo.getId());
    backendRoleMenuRelationService.batchSave(rolePo.getId(), roleDto.getMenuIds());
    // 比较权限变化
    List<Integer> newMenuIdList = roleDto.getMenuIds().stream().sorted().toList();
    // 踢出用户
    List<Integer> userIds = backendUserRoleRelationService.getUserIdsByRoleIds(
        rolePo.getId()).getData();
    // 修改用户部门
    if (CollUtil.isNotEmpty(userIds) && !Objects.equals(oldRole.getDeptId(), roleDto.getDeptId())) {
      backendUserMapper.update(
          Wrappers.<SysUserPo>lambdaUpdate().set(SysUserPo::getDeptId, roleDto.getDeptId())
              .in(SysUserPo::getId, userIds));
    }
    if (CollUtil.isNotEmpty(userIds)) {
      boolean menuBool = !CollUtil.isEqualList(oldMenuIdList, newMenuIdList);
      boolean deptBool = !Objects.equals(oldRole.getDeptId(), rolePo.getDeptId());
      boolean dateBool = !Objects.equals(oldRole.getDataPermission(),
          rolePo.getDataPermission());
      if (menuBool || deptBool || dateBool) {
        deleteLogin(userIds);
        return CommonRespDto.success(true);
      }
    }
    return CommonRespDto.success();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> deleteRole(Integer roleId) {
    //校验是否存在用户
    Long data = backendUserRoleRelationService.getCountByRoleId(roleId).getData();
    if (data > 0) {
      return CommonRespDto.error("存在用户，无法删除");
    }
    //删除角色信息
    baseMapper.deleteById(roleId);
    // 删除菜单关联关系
    backendRoleMenuRelationService.deleteByRoleId(roleId);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<BackendRoleDto> getRoleDetail(Integer roleId) {
    RolePo rolePo = baseMapper.selectById(roleId);
    BackendRoleDto backendRoleDto = backendRoleConverter.poToDto(rolePo);

    List<Integer> parentDeptIdsById = backendDepartmentService.getParentDeptIdsById(
        backendRoleDto.getDeptId(), null);
    backendRoleDto.setDeptIdList(parentDeptIdsById);
    //添加菜单
    List<Integer> menuIds = backendRoleMenuRelationService.getRelationsByRoleId(roleId).getData()
        .stream()
        .map(BackendRoleMenuRelationDto::getMenuId).toList();
    backendRoleDto.setMenuIds(menuIds);
    return CommonRespDto.success(backendRoleDto);
  }

  @Override
  public CommonRespDto<PagingRespDto<BackendRoleDto>> getRolePage(RolePageReqDto dto) {
    Integer userId = UserAuthUtil.getUserId();
    Page<RolePo> page = Page.of(dto.getCurrent(), dto.getSize());
    LambdaQueryWrapper<RolePo> wrapper = Wrappers.lambdaQuery();
    wrapper.orderByDesc(RolePo::getCreateTime);

    if (dto.getDeptId() == null || dto.getDeptId() == 0) {
      return CommonRespDto.success(new PagingRespDto<>());
    } else {
      CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
          userId);
      BackendRoleDto role = roleByUserId.getData();
      if (role != null) {
        if (Objects.equals(role.getDataPermission(),
            RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
          List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(
              dto.getDeptId());
          wrapper.in(RolePo::getDeptId, childDeptIds);
        } else if (Objects.equals(role.getDataPermission(),
            RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
          wrapper.eq(RolePo::getDeptId, dto.getDeptId());
        } else {
          wrapper.and(wp -> wp.eq(BaseAuditPo::getCreateBy, userId)
                  .eq(RolePo::getDeptId, dto.getDeptId()))
              .or().eq(RolePo::getId, role.getId());
        }
      }
    }
    Page<RolePo> poPage = baseMapper.selectPage(page, wrapper);
    PagingRespDto<BackendRoleDto> respDto = backendRoleConverter.pagingPoToDto(poPage);

    List<BackendRoleDto> records = respDto.getRecords();
    if (CollUtil.isNotEmpty(records)) {
      List<Integer> roleIds = records.stream().map(BackendRoleDto::getId).toList();
      Map<Integer, Long> countMap = backendUserRoleRelationService.getUserCountMap(roleIds)
          .getData();
      List<Integer> updateByIds = records.stream().map(BackendRoleDto::getUpdateBy).toList();
      Map<Integer, String> userNameMap = backendUserInfoService.getUserNameMapByUserIds(updateByIds)
          .getData();

      Map<Integer, String> deptIdNameMap = backendDepartmentService.getDeptIdNameMap();
      records.forEach(backendRoleDto -> {
        backendRoleDto.setDeptName(deptIdNameMap.get(backendRoleDto.getDeptId()));
        backendRoleDto.setUpdateByName(userNameMap.get(backendRoleDto.getUpdateBy()));
        int count = 0;
        if (Objects.nonNull(countMap.get(backendRoleDto.getId()))) {
          count = Math.toIntExact(countMap.get(backendRoleDto.getId()));
        }
        backendRoleDto.setUserCount(count);
      });
    }

    return CommonRespDto.success(respDto);
  }

  @Override
  public CommonRespDto<List<Integer>> getUserIdsByRoleId(Integer roleId) {
    List<Integer> userIds = backendUserRoleRelationService.getUserIdsByRoleIds(roleId).getData();
    return CommonRespDto.success(userIds);
  }

  @Override
  public CommonRespDto<Map<Integer, String>> getRoleNamesMap(List<Integer> userIds) {

    Map<Integer, String> result = new HashMap<>();
    List<BackendUserRoleRelationDto> relationDtoList = backendUserRoleRelationService.getRelationByUserIds(
        userIds).getData();

    List<Integer> roleIds = relationDtoList.stream().map(BackendUserRoleRelationDto::getRoleId)
        .toList();

    Map<Integer, String> roleNameMap = baseMapper.selectBatchIds(roleIds).stream()
        .collect(Collectors.toMap(RolePo::getId, RolePo::getRoleName));

    Map<Integer, List<Integer>> relationMap = relationDtoList.stream().collect(
        Collectors.groupingBy(BackendUserRoleRelationDto::getUserId,
            Collectors.mapping(BackendUserRoleRelationDto::getRoleId, Collectors.toList())));

    relationMap.forEach((key, value) -> {
      String name = value.stream().map(roleNameMap::get).collect(Collectors.joining("/"));
      result.put(key, name);
    });

    return CommonRespDto.success(result);
  }

  @Override
  public BackendRoleDto getRoleInfoByUserId(Integer userId) {
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
        userId);
    if (roleByUserId.isOk() && ObjUtil.isNotNull(roleByUserId.getData())) {
      return roleByUserId.getData();
    }
    return null;
  }

  @Override
  public CommonRespDto<List<BackendRoleDto>> getRolesByDeptId(Integer deptId) {
    Integer userId = UserAuthUtil.getUserId();
    LambdaQueryWrapper<RolePo> wrapper = Wrappers.lambdaQuery();
    if (deptId == null || deptId == 0) {
      return CommonRespDto.success(new ArrayList<>());
    } else {
      CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
          userId);
      BackendRoleDto role = roleByUserId.getData();
      if (role != null) {
        // 仅本人权限只能看自己创建的角色，其他看这个部门下的角色
        if (Objects.equals(role.getDataPermission(), RoleDataPermissionEnum.ONLY_SELF.getKey())) {
          wrapper.eq(BaseAuditPo::getCreateBy, userId);
        } else {
          wrapper.eq(RolePo::getDeptId, deptId);
        }
      }
    }
    wrapper.orderByDesc(RolePo::getCreateTime);
    List<RolePo> list = list(wrapper);
    return CommonRespDto.success(backendRoleConverter.poListToDtoList(list));
  }

  /**
   * 根据部门id获取角色列表（包含自己）
   *
   * @param deptId
   * @return
   */
  @Override
  public CommonRespDto<List<BackendRoleDto>> getRolesByDeptIdWithSelf(Integer deptId) {
    Integer userId = UserAuthUtil.getUserId();
    LambdaQueryWrapper<RolePo> wrapper = Wrappers.lambdaQuery();
    if (deptId == null || deptId == 0) {
      return CommonRespDto.success(new ArrayList<>());
    } else {
      CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
          userId);
      BackendRoleDto role = roleByUserId.getData();
      if (role != null) {
        // 仅本人权限只能看自己创建的角色，其他看这个部门下的角色
        if (Objects.equals(role.getDataPermission(), RoleDataPermissionEnum.ONLY_SELF.getKey())) {
          wrapper.and(wp -> wp.eq(BaseAuditPo::getCreateBy, userId)
                  .eq(RolePo::getDeptId, deptId))
              .or().eq(RolePo::getId, role.getId());
        } else {
          wrapper.eq(RolePo::getDeptId, deptId);
        }
      }
    }
    List<RolePo> list = list(wrapper);
    return CommonRespDto.success(backendRoleConverter.poListToDtoList(list));
  }

  @Override
  public CommonRespDto<List<BackendRoleDto>> getAllRoles() {
    List<RolePo> list = list();
    return CommonRespDto.success(backendRoleConverter.poListToDtoList(list));
  }

  public void deleteLogin(List<Integer> userIds) {
    Set<String> userIdTokenSet = new HashSet<>();
    List<String> keys = redissonClient.getKeys()
        .getKeysStreamByPattern(RedisConstants.TOKEN_WHITELIST_PREFIX + "*").toList();
    userIds.forEach(userId -> {
      List<String> currentKeys = keys.stream().filter(key -> key.startsWith(
          RedisConstants.TOKEN_WHITELIST_PREFIX + userId + ":" + CommonConstants.LOGIN_TYPE_PC
              + ":")).collect(Collectors.toList());
      if (CollUtil.isNotEmpty(currentKeys)) {
        userIdTokenSet.addAll(currentKeys);
        // 发送消息
//        BackendMessageDto messageDto = BackendMessageDto.builder().userId(userId)
//          .msg(MessageTypeEnum.ROLE_AUTHORITY_MODIFY_MSG.getMessage())
//          .msgType(MessageTypeEnum.ROLE_AUTHORITY_MODIFY_MSG.getCode()).build();
//        backendMessageService.send(messageDto);
      }
    });
    if (CollUtil.isNotEmpty(userIdTokenSet)) {
      userIdTokenSet.forEach(currentKeys -> {
        // 设置两秒过期时间
        redissonClient.getBucket(currentKeys).expire(Instant.now().plusSeconds(2));
      });
    }

    CommonRespDto.success();
  }

  private boolean isRoleUnique(String roleName, Integer roleId, Integer deptId) {
    // 获取二级部门子部门ids
    List<Integer> deptIds = backendDepartmentService.getParentLevelTwoChildDeptIdsById(
        deptId);

    LambdaQueryWrapper<RolePo> queryWrapper = new LambdaQueryWrapper<RolePo>().eq(
        RolePo::getRoleName, roleName);
    if (Objects.nonNull(roleId)) {
      queryWrapper.ne(RolePo::getId, roleId);
    }

    // 只校验所在二级部门下是否重复
    if (CollUtil.isNotEmpty(deptIds)) {
      queryWrapper.in(RolePo::getDeptId, deptIds);
    }
    return count(queryWrapper) == 0;
  }
}
