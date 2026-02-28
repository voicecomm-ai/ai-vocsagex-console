package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendDepartmentService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserRoleRelationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendDepartmentDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.DataPermissionEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.UserStatusEnum;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendDepartmentConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendDepartmentMapper;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendUserMapper;
import cn.voicecomm.ai.voicesagex.console.util.po.user.DepartmentPo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.SysUserPo;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class BackendDepartmentServiceImpl extends
  ServiceImpl<BackendDepartmentMapper, DepartmentPo> implements BackendDepartmentService {

  private final BackendDepartmentConverter backendDepartmentConverter;

  private final BackendUserMapper userMapper;

  @Autowired
  private BackendUserService backendUserService;

  @Autowired
  private BackendUserRoleRelationService backendUserRoleRelationService;


  @Override
  public CommonRespDto<Void> add(BackendDepartmentDto dto) {
    List<BackendUserDto> userDtoList = backendUserService.getUserListByUserIdWithAdmin();
    List<Integer> userIds = userDtoList.stream().map(BackendUserDto::getId).toList();
    if (CollUtil.isEmpty(userIds)) {
      return CommonRespDto.error("获取权限数据失败！");
    }
    log.info("数据权限userIds：{}", userIds);
    if (!deptNameUnique(dto.getParentId(), null, dto.getDepartmentName(), dto.getLevel())) {
      return CommonRespDto.error("部门名称已存在，请修改");
    }

    DepartmentPo departmentPo = backendDepartmentConverter.dtoToPo(dto);
    boolean save = save(departmentPo);
    log.info("新增部门：{}", departmentPo);
    if (!save) {
      return CommonRespDto.error();
    }
    return CommonRespDto.success();
  }

  @Override
  @Transactional
  public CommonRespDto<Void> delete(Integer id) {
    // 递归查询所有下级部门
    List<DepartmentPo> listChildDept = baseMapper.listChildDept(id, 1);

    // 获取部门id list
    List<Integer> deptIds = listChildDept.stream().map(DepartmentPo::getId).toList();
    // 检查部门id下是否有用户
    if (CollUtil.isNotEmpty(deptIds) && userMapper.selectCount(
      Wrappers.<SysUserPo>lambdaQuery().in(SysUserPo::getDeptId, deptIds)
        .ne(SysUserPo::getStatus, 2)) > 0) {
      return CommonRespDto.error("该组织（含下级）存在内容，无法删除");
    }
    // 删除部门
    boolean b = removeByIds(deptIds);
    log.info("删除部门：{}", deptIds);
    if (!b) {
      return CommonRespDto.error("删除失败！");
    }
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<BackendDepartmentDto> getInfo(Integer id) {
    // 查询部门
    DepartmentPo departmentPo = getById(id);
    if (departmentPo == null) {
      return CommonRespDto.error("部门不存在");
    }
    DepartmentPo parentDepartmentPo = getById(departmentPo.getParentId());
    BackendDepartmentDto backendDepartmentDto = backendDepartmentConverter.poToDto(
      departmentPo);
    backendDepartmentDto.setParentDepartmentName(
      Optional.ofNullable(parentDepartmentPo).map(DepartmentPo::getDepartmentName)
        .orElse(StrUtil.EMPTY));
    return CommonRespDto.success(backendDepartmentDto);
  }

  @Override
  public CommonRespDto<List<BackendDepartmentDto>> getDepartmentTreeById(Integer id) {
    List<DepartmentPo> list = baseMapper.listChildDept(id, 1);
    List<BackendDepartmentDto> departmentDtoList = backendDepartmentConverter.poToDtoList(list);
    List<BackendDepartmentDto> rootList = departmentDtoList.stream()
      .filter(e -> Objects.equals(e.getId(), id)).toList();
    // 设置更新人
    setUpdateUserAccount(departmentDtoList);
    // 设置子项
    findChild(rootList, departmentDtoList);
    return CommonRespDto.success(rootList);
  }


  @Override
  public CommonRespDto<List<BackendDepartmentDto>> getDepartmentTree(BackendDepartmentDto dto) {
    List<DepartmentPo> list;
    if (Boolean.TRUE.equals(dto.getGetAllFlag())) {
      list = list(
        Wrappers.<DepartmentPo>lambdaQuery().orderByAsc(DepartmentPo::getId));
    } else {
      Integer dataPermissionType = UserAuthUtil.getDataPermissionType();
      log.info("获取部门树，数据权限：{}", dataPermissionType);
      SysUserPo sysUserPo = userMapper.selectById(UserAuthUtil.getUserId());
      if (DataPermissionEnum.DEPARTMENT_AND_SUBORDINATE.getKey().equals(dataPermissionType)) {
        list = baseMapper.listChildDept(sysUserPo.getDeptId(), 1);
      } else if (DataPermissionEnum.DEPARTMENT.getKey().equals(dataPermissionType)
        || DataPermissionEnum.ONLY_SELF.getKey().equals(dataPermissionType)) {
        list = List.of(getById(sysUserPo.getDeptId()));
      } else {
        list = list(
          Wrappers.<DepartmentPo>lambdaQuery().orderByAsc(DepartmentPo::getId));
      }
    }
    List<BackendDepartmentDto> departmentDtoList = backendDepartmentConverter.poToDtoList(list);

    // 计算每个部门人数（含下级）
    handleDeptUserNo(departmentDtoList);

    // 部门名称模糊查询
    String deptName = SpecialCharUtil.replaceSpecialWord(dto.getDepartmentName());
    if (StrUtil.isNotBlank(deptName)) {
      departmentDtoList = departmentDtoList.stream()
        .filter(e -> StrUtil.contains(e.getDepartmentName(), deptName)).toList();
    }
    // 筛选出根节点
    // 名称筛选出存在的id list，如果该id的parentId不在筛选出的id list中，说明为根节点
    List<Integer> existIds = departmentDtoList.stream().map(BackendDepartmentDto::getId).toList();
    List<BackendDepartmentDto> rootList = departmentDtoList.stream()
      .filter(e -> !existIds.contains(e.getParentId())).toList();
    setUpdateUserAccount(departmentDtoList);
    // 设置子项
    findChild(rootList, departmentDtoList);
    return CommonRespDto.success(rootList);
  }

  /**
   * 处理部门用户数量 根据部门列表和用户列表，计算每个部门的用户数量，并设置到部门对象中
   *
   * @param departmentDtoList 部门列表，用于展示后台部门信息
   */
  private void handleDeptUserNo(List<BackendDepartmentDto> departmentDtoList) {
    // 查询所有后台用户
    List<SysUserPo> sysUserPos = userMapper.selectList(Wrappers.<SysUserPo>lambdaQuery()
      .ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode()));
    // 统计每个部门的用户数量，使用Map存储，键为部门ID，值为用户数量
    Map<Integer, Integer> deptUserCountMap = sysUserPos.stream()
      .collect(Collectors.groupingBy(SysUserPo::getDeptId, Collectors.summingInt(e -> 1)));

    // 遍历部门列表，设置每个部门的当前用户数量
    departmentDtoList.forEach(
      e -> e.setCurrentDeptUserNo(deptUserCountMap.getOrDefault(e.getId(), 0)));

    // 遍历每个部门，处理子部门的用户数量统计
    for (BackendDepartmentDto backendDepartmentDto : departmentDtoList) {
      // 过滤出当前部门的子部门
      List<BackendDepartmentDto> childDtoList = departmentDtoList.stream()
        .filter(el -> el.getParentId().equals(backendDepartmentDto.getId())).toList();
      // 如果找到子部门，将其设置为当前部门的子部门，并继续递归
      AtomicInteger sumNo = new AtomicInteger(0);
      if (CollUtil.isNotEmpty(childDtoList)) {
        sumNo.addAndGet(
          childDtoList.stream().mapToInt(BackendDepartmentDto::getCurrentDeptUserNo).sum());
        sumChildUserNo(sumNo, childDtoList, departmentDtoList);
      }
      backendDepartmentDto.setUserNo(backendDepartmentDto.getCurrentDeptUserNo() + sumNo.get());
    }
  }

  /**
   * 设置更新用户账户信息 此方法用于将部门DTO列表中的更新人ID映射为对应的用户账户
   *
   * @param departmentDtoList 部门DTO列表，用于设置更新人账户信息
   */
  private void setUpdateUserAccount(List<BackendDepartmentDto> departmentDtoList) {
    if (CollUtil.isEmpty(departmentDtoList)) {
      return;
    }
    // 设置更新人
    List<Integer> updateIdList = departmentDtoList.stream().map(BaseAuditDto::getUpdateBy).toList();
    // 通过更新人ID列表查询用户信息，并创建ID到账户的映射
    Map<Integer, String> map = userMapper.selectBatchIds(updateIdList).stream()
      .collect(Collectors.toMap(SysUserPo::getId, SysUserPo::getAccount));
    // 查询部门父级表
    List<Integer> parentIds = departmentDtoList.stream().map(BackendDepartmentDto::getParentId)
      .toList();
    List<DepartmentPo> departmentPos = baseMapper.selectBatchIds(parentIds);
    Map<Integer, String> parentNameMap = departmentPos.stream().collect(
      Collectors.toMap(DepartmentPo::getId, DepartmentPo::getDepartmentName));

    // 遍历部门DTO列表，设置更新人账户信息,父级部门名称
    departmentDtoList.forEach(departmentDto -> {
      departmentDto.setUpdateUsername(map.get(departmentDto.getUpdateBy()));
      departmentDto.setParentDepartmentName(
        parentNameMap.getOrDefault(departmentDto.getParentId(), StrUtil.EMPTY));
    });
  }

  /**
   * 递归查找并设置部门的子部门 此方法通过遍历部门列表，构建树形结构的部门关系 它首先检查根部门列表是否为空，如果不为空，则遍历每个部门，寻找其子部门
   * 如果找到子部门，则将其设置为当前部门的子部门，并继续递归查找子部门的子部门
   *
   * @param rootList 根部门列表，用于构建树形结构的起点
   * @param dtoList  包含所有部门的列表，用于查找子部门
   */
  private static void findChild(List<BackendDepartmentDto> rootList,
    List<BackendDepartmentDto> dtoList) {
    // 检查根部门列表是否为空，为空则直接返回
    if (CollUtil.isEmpty(rootList)) {
      return;
    }
    // 遍历根部门列表，为每个部门查找并设置子部门
    rootList.forEach(dto -> {
      // 过滤出当前部门的子部门
      List<BackendDepartmentDto> childDtoList = dtoList.stream()
        .filter(el -> el.getParentId().equals(dto.getId())).toList();
      // 如果找到子部门，将其设置为当前部门的子部门，并继续递归
      if (CollUtil.isNotEmpty(childDtoList)) {
        dto.setChildren(childDtoList);
        findChild(dto.getChildren(), dtoList);
      }
    });
  }

  /**
   * 计算并设置部门及其子部门的用户数量总和 该方法通过递归遍历部门树结构，累加每个部门及其所有子部门的用户数量之和
   *
   * @param sum      用于累加用户数量总和的原子整数
   * @param rootList 根部门列表，表示部门树的起始点
   * @param dtoList  包含所有部门详细信息的列表，用于查找和设置子部门
   */
  private static void sumChildUserNo(AtomicInteger sum, List<BackendDepartmentDto> rootList,
    List<BackendDepartmentDto> dtoList) {
    // 检查根部门列表是否为空，为空则直接返回
    if (CollUtil.isEmpty(rootList)) {
      return;
    }
    // 遍历根部门列表，为每个部门查找并设置子部门
    rootList.forEach(dto -> {
      // 过滤出当前部门的子部门
      List<BackendDepartmentDto> childDtoList = dtoList.stream()
        .filter(el -> el.getParentId().equals(dto.getId())).toList();
      // 如果找到子部门，将其设置为当前部门的子部门，并继续递归
      if (CollUtil.isNotEmpty(childDtoList)) {
        // 计算总和
        sum.addAndGet(
          childDtoList.stream().mapToInt(BackendDepartmentDto::getCurrentDeptUserNo).sum());
        sumChildUserNo(sum, childDtoList, dtoList);
      }
    });
  }

  @Override
  public CommonRespDto<Void> update(BackendDepartmentDto dto) {
    List<BackendUserDto> userDtoList = backendUserService.getUserListByUserIdWithAdmin();
    List<Integer> userIds = userDtoList.stream().map(BackendUserDto::getId).toList();
    if (CollUtil.isEmpty(userIds)) {
      return CommonRespDto.error("获取权限数据失败！");
    }
    log.info("数据权限userIds：{}", userIds);
    DepartmentPo departmentPo = baseMapper.selectById(dto.getId());
    if (!deptNameUnique(departmentPo.getParentId(), dto.getId(), dto.getDepartmentName(),
      dto.getLevel())) {
      return CommonRespDto.error("部门名称已存在，请修改");
    }
    updateById(backendDepartmentConverter.dtoToPo(dto));
    log.info("更新部门：{}", dto);
    return CommonRespDto.success();
  }


  @Override
  public Map<Integer, String> getDeptIdNameMap() {
    return list().stream().collect(
      Collectors.toMap(DepartmentPo::getId, DepartmentPo::getDepartmentName));
  }

  @Override
  public List<Integer> getChildDeptIdsById(Integer deptId) {
    return baseMapper.listChildDept(deptId, 1).stream().map(DepartmentPo::getId).toList();
  }


  private List<Integer> getChildDeptIds(Integer deptId) {
    return baseMapper.listChildDept(deptId, 1).stream().map(DepartmentPo::getId).toList();
  }

  @Override
  public List<Integer> getParentDeptIdsById(Integer deptId, Integer userId) {
    List<Integer> list = baseMapper.listParentDept(deptId, 1).stream()
      .map(DepartmentPo::getId).toList();

    List<Integer> resultList = new ArrayList<>();
    CommonRespDto<BackendRoleDto> roleDtoCommonRespDto;
    if (userId == null) {
      roleDtoCommonRespDto = backendUserRoleRelationService.getRoleByUserId(
        UserAuthUtil.getUserId());
    } else {
      roleDtoCommonRespDto = backendUserRoleRelationService.getRoleByUserId(userId);
    }

    List<Integer> authDeptList = List.of();
    if (roleDtoCommonRespDto.isOk() && Objects.nonNull(roleDtoCommonRespDto.getData())) {
      BackendRoleDto data = roleDtoCommonRespDto.getData();
      if (DataPermissionEnum.DEPARTMENT_AND_SUBORDINATE.getKey().equals(data.getDataPermission())) {
        authDeptList = baseMapper.listChildDept(data.getDeptId(), 1).stream()
          .map(DepartmentPo::getId).toList();
      } else {
        authDeptList = List.of(data.getDeptId());
      }
    }
    if (CollUtil.isNotEmpty(authDeptList)) {
      authDeptList.forEach(e -> {
        if (list.contains(e)) {
          resultList.add(e);
        }
      });
    }
    return resultList;
  }

  @Override
  public List<Integer> getParentLevelTwoChildDeptIdsById(Integer deptId) {
    List<Integer> list = baseMapper.listParentDept(deptId, 1).stream()
      .filter(d -> Objects.equals(2, d.getLevel())).map(DepartmentPo::getId).toList();
    if (CollUtil.isNotEmpty(list)) {
      return getChildDeptIdsById(list.getFirst());
    }
    return Collections.emptyList();
  }

  /**
   * 校验直属部门下部门名称是否唯一
   *
   * @param parentDeptId 直属部门id
   * @param deptId       部门id
   * @param deptName     部门名称
   * @return
   */
  private boolean deptNameUnique(Integer parentDeptId, Integer deptId, String deptName,
    Integer level) {
    log.info("父部门id:{},当前部门id:{},部门名称:{},当前部门level:{}", parentDeptId, deptId,
      deptName, level);
    List<Integer> deptIds = getChildDeptIdsById(parentDeptId);
    log.info("直属部门下所有部门id：{}", deptIds);
    long count = count(Wrappers.<DepartmentPo>lambdaQuery()
      .ne(Objects.nonNull(deptId), DepartmentPo::getId, deptId)
      .eq(DepartmentPo::getDepartmentName, deptName)
      .eq(DepartmentPo::getLevel, level)
      .in(CollUtil.isNotEmpty(deptIds), DepartmentPo::getId, deptIds));
    return count == 0;
  }
}
