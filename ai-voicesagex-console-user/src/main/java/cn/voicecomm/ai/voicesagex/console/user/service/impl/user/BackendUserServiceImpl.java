package cn.voicecomm.ai.voicesagex.console.user.service.impl.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendDepartmentService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMenuService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendMessageService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserInfoService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserRoleRelationService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.CommonConstants;
import cn.voicecomm.ai.voicesagex.console.api.constant.user.RedisConstants;
import cn.voicecomm.ai.voicesagex.console.api.dto.CommonRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.message.BackendMessageDto.Type;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.UserPageReqDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.role.RoleDataPermissionEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.UserStatusEnum;
import cn.voicecomm.ai.voicesagex.console.user.config.GlusterProperties;
import cn.voicecomm.ai.voicesagex.console.user.converter.BackendUserConverter;
import cn.voicecomm.ai.voicesagex.console.user.dao.mapper.user.BackendUserMapper;
import cn.voicecomm.ai.voicesagex.console.user.util.BatchImportUserTemplateUtil;
import cn.voicecomm.ai.voicesagex.console.user.util.BatchImportUserTemplateUtil.TemplateSheetCell;
import cn.voicecomm.ai.voicesagex.console.user.util.UserValidationUtil;
import cn.voicecomm.ai.voicesagex.console.user.util.UserValidationUtil.ValidationField;
import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import cn.voicecomm.ai.voicesagex.console.util.po.user.SysUserPo;
import cn.voicecomm.ai.voicesagex.console.util.util.RSAUtils;
import cn.voicecomm.ai.voicesagex.console.util.util.SpecialCharUtil;
import cn.voicecomm.ai.voicesagex.console.util.util.UserAuthUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.voicecomm.ai.voicesagex.console.api.constant.user.UserConstant.ACCOUNT_MANAGE;
import static cn.voicecomm.ai.voicesagex.console.api.constant.user.UserConstant.NO_ACCOUNT_PERMISSION;
import static cn.voicecomm.ai.voicesagex.console.user.util.UserValidationUtil.isValidAccount;
import static cn.voicecomm.ai.voicesagex.console.user.util.UserValidationUtil.isValidPassword;
import static cn.voicecomm.ai.voicesagex.console.user.util.UserValidationUtil.isValidUsername;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class BackendUserServiceImpl extends ServiceImpl<BackendUserMapper, SysUserPo> implements
  BackendUserService {

  private final PasswordEncoder passwordEncoder;

  private final BackendUserConverter backendUserConverter;

  private final GlusterProperties glusterProperties;

  private final BackendMessageService backendMessageService;

  private final BackendUserRoleRelationService backendUserRoleRelationService;

  private final BackendRoleService backendRoleService;

  private final BackendMenuService backendMenuService;

  private final BackendDepartmentService backendDepartmentService;

  private final RedissonClient redissonClient;

  private final BackendUserInfoService backendUserInfoService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> add(BackendUserDto dto) {

    boolean verified = verifyPermission(UserAuthUtil.getUserId());
    if (!verified) {
      return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
    }

    // 唯一
    if (isAccountUnique(dto.getAccount())) {
      return CommonRespDto.error("账号名称已存在");
    }

    // 用户信息校验
    List<String> errorMsg = UserValidationUtil.isValidUser(dto, (ValidationField) null).getData();
    if (CollUtil.isNotEmpty(errorMsg)) {
      return CommonRespDto.error(errorMsg.getFirst());
    }

    dto.setPassword(passwordEncoder.encode(dto.getPassword()));

    SysUserPo sysUserPo = backendUserConverter.dtoToPo(dto);
    baseMapper.insert(sysUserPo);

    backendUserRoleRelationService.batchSave(sysUserPo.getId(), dto.getRoleId());

    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<BackendUserDto> getInfo(Integer userId) {
    BackendUserDto backendUserDto = backendUserConverter.poToDto(baseMapper.selectById(userId));
    Integer userId1 = UserAuthUtil.getUserId();
    List<Integer> parentDeptIdsById = backendDepartmentService.getParentDeptIdsById(
      backendUserDto.getDeptId(), userId1);
    backendUserDto.setDeptIdList(parentDeptIdsById);

    // 添加角色ids
    BackendRoleDto data = backendUserRoleRelationService.getRoleByUserId(userId).getData();
    Map<Integer, String> map = backendRoleService.getRoleNamesMap(
      Collections.singletonList(userId)).getData();
    backendUserDto.setRoleId(data.getId());
    backendUserDto.setRoleName(map.get(userId));
    backendUserDto.setDataPermission(data.getDataPermission());
    return CommonRespDto.success(backendUserDto);
  }

  @Override
  public CommonRespDto<BackendUserDto> getUserInfo(Integer userId) {
    BackendUserDto backendUserDto = backendUserConverter.poToDto(baseMapper.selectById(userId));
    return CommonRespDto.success(backendUserDto);
  }

  @Override
  public CommonRespDto<PagingRespDto<BackendUserDto>> getUserPage(UserPageReqDto dto) {
    Integer userId = UserAuthUtil.getUserId();
    String accountOrName = SpecialCharUtil.replaceSpecialWord(dto.getAccountOrName());
    Page<SysUserPo> page = Page.of(dto.getCurrent(), dto.getSize());
    PagingRespDto<BackendUserDto> pagingRespDto = new PagingRespDto<>();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    //已删除过滤
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    wrapper.eq(Objects.nonNull(dto.getStatus()), SysUserPo::getStatus, dto.getStatus())
      .and(StrUtil.isNotBlank(accountOrName),
        wp -> wp.like(SysUserPo::getAccount, accountOrName).or()
          .like(SysUserPo::getUsername, accountOrName))
      .orderByDesc(SysUserPo::getCreateTime);

    if (dto.getDeptId() == null || dto.getDeptId() == 0) {
      return CommonRespDto.success(new PagingRespDto<>());
    } else {
      CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
        userId);
      BackendRoleDto role = roleByUserId.getData();
      log.info("role: {}", role);
      if (role != null) {
        if (Objects.equals(role.getDataPermission(),
          RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
          List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(
            dto.getDeptId());
          wrapper.in(SysUserPo::getDeptId, childDeptIds);
        } else if (Objects.equals(role.getDataPermission(),
          RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
          wrapper.eq(SysUserPo::getDeptId, dto.getDeptId());
        } else {
          wrapper.and(
            wp -> wp.eq(BaseAuditPo::getCreateBy, userId).or().eq(SysUserPo::getId, userId));
        }
      }
    }
    if (Objects.nonNull(dto.getRoleId())) {
      List<Integer> userIds = backendRoleService.getUserIdsByRoleId(dto.getRoleId()).getData();
      // 当前角色下不存在用户，返回空
      if (CollUtil.isNotEmpty(userIds)) {
        wrapper.in(SysUserPo::getId, userIds);
      } else {
        return CommonRespDto.success(pagingRespDto);
      }
    }

    pagingRespDto = backendUserConverter.pagePoToDto(baseMapper.selectPage(page, wrapper));

    log.info("userId: {}, page: {}, deptId: {}", userId, pagingRespDto.getRecords().size(),
      dto.getDeptId());

    List<BackendUserDto> records = pagingRespDto.getRecords();
    if (CollUtil.isNotEmpty(records)) {
      List<Integer> userIdList = records.stream().map(BackendUserDto::getId).toList();
      List<Integer> updateByIdList = records.stream().map(BackendUserDto::getUpdateBy).toList();
      Map<Integer, String> roleNamesMap = backendRoleService.getRoleNamesMap(
        userIdList).getData();
      Map<Integer, String> updateByNameMap = backendUserInfoService.getUserNameMapByUserIds(
          updateByIdList)
        .getData();
      Map<Integer, Integer> userRoleMap = backendUserRoleRelationService.getRoleIdsByUserIds(
        userIdList).getData();

      Map<Integer, String> deptIdNameMap = backendDepartmentService.getDeptIdNameMap();
      pagingRespDto.getRecords()
        .forEach(userDto -> {
          userDto.setDeptName(deptIdNameMap.get(userDto.getDeptId()));
          userDto.setRoleName(roleNamesMap.get(userDto.getId()));
          userDto.setUpdateByName(updateByNameMap.get(userDto.getUpdateBy()));
          userDto.setRoleId(userRoleMap.get(userDto.getId()));
        });
    }

    return CommonRespDto.success(pagingRespDto);
  }

  @Override
  public CommonRespDto<List<BackendUserDto>> getUserListByUserId(Integer type) {
    Integer userId = UserAuthUtil.getUserId();
    SysUserPo userPo = getById(userId);
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
      userId);
    BackendRoleDto role = roleByUserId.getData();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    wrapper.ne(SysUserPo::getId, 1);
    // 筛选
    if (Objects.equals(role.getDataPermission(),
      RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
      List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(userPo.getDeptId());
      wrapper.in(SysUserPo::getDeptId, childDeptIds);
    } else if (role.getDataPermission().equals(RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
      wrapper.eq(SysUserPo::getDeptId, userPo.getDeptId());
    } else {
      wrapper.eq(SysUserPo::getId, userId);
    }
    List<SysUserPo> list = list(wrapper);
    List<BackendUserDto> backendUserDtos = backendUserConverter.poToDtoList(list);
    return CommonRespDto.success(backendUserDtos);
  }


  @Override
  public List<BackendUserDto> getUserListByUserIdWithAdmin() {
    Integer userId = UserAuthUtil.getUserId();
    SysUserPo userPo = getById(userId);
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
      userId);
    BackendRoleDto role = roleByUserId.getData();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    if (Objects.equals(role.getDataPermission(),
      RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
      List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(userPo.getDeptId());
      wrapper.in(SysUserPo::getDeptId, childDeptIds);
    } else if (role.getDataPermission().equals(RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
      wrapper.eq(SysUserPo::getDeptId, userPo.getDeptId());
    } else {
      return List.of(backendUserConverter.poToDto(userPo));
    }
    List<SysUserPo> list = list(wrapper);
    return backendUserConverter.poToDtoList(list);
  }

  @Override
  public CommonRespDto<List<Integer>> getUserIdsByUserId(Integer userId) {
    SysUserPo userPo = getById(userId);
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
      userId);
    BackendRoleDto role = roleByUserId.getData();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    if (Objects.equals(role.getDataPermission(),
      RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
      List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(userPo.getDeptId());
      wrapper.in(SysUserPo::getDeptId, childDeptIds);
    } else if (role.getDataPermission().equals(RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
      wrapper.eq(SysUserPo::getDeptId, userPo.getDeptId());
    } else {
      return CommonRespDto.success(List.of(userPo.getId()));
    }
    List<SysUserPo> list = list(wrapper);
    List<Integer> ids = list.stream().map(SysUserPo::getId).toList();
    return CommonRespDto.success(ids);
  }

  @Override
  public CommonRespDto<List<Integer>> getUserIdsByCurrentUserAndDeviceType(Integer userId,
    Integer type) {
    SysUserPo userPo = getById(userId);
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
      userId);
    BackendRoleDto role = roleByUserId.getData();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    wrapper.ne(SysUserPo::getId, 1);
    if (Objects.equals(role.getDataPermission(),
      RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
      List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(userPo.getDeptId());
      wrapper.in(SysUserPo::getDeptId, childDeptIds);
    } else if (role.getDataPermission().equals(RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
      wrapper.eq(SysUserPo::getDeptId, userPo.getDeptId());
    } else {
      wrapper.eq(SysUserPo::getId, userId);
    }
    List<SysUserPo> list = list(wrapper);
    List<BackendUserDto> backendUserDtos = backendUserConverter.poToDtoList(list);
    List<Integer> result = backendUserDtos.stream().map(BackendUserDto::getId).toList();
    return CommonRespDto.success(result);
  }

  @Override
  public CommonRespDto<List<BackendUserDto>> getUserByUserId(Integer userId) {
    SysUserPo userPo = getById(userId);
    CommonRespDto<BackendRoleDto> roleByUserId = backendUserRoleRelationService.getRoleByUserId(
      userId);
    BackendRoleDto role = roleByUserId.getData();
    LambdaQueryWrapper<SysUserPo> wrapper = Wrappers.lambdaQuery();
    wrapper.ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode());
    if (Objects.equals(role.getDataPermission(),
      RoleDataPermissionEnum.DEPARTMENT_AND_SUB.getKey())) {
      List<Integer> childDeptIds = backendDepartmentService.getChildDeptIdsById(userPo.getDeptId());
      wrapper.in(SysUserPo::getDeptId, childDeptIds);
    } else if (role.getDataPermission().equals(RoleDataPermissionEnum.ONLY_DEPARTMENT.getKey())) {
      wrapper.eq(SysUserPo::getDeptId, userPo.getDeptId());
    } else {
      return CommonRespDto.success(List.of(backendUserConverter.poToDto(userPo)));
    }
    List<SysUserPo> list = list(wrapper);
    return CommonRespDto.success(backendUserConverter.poToDtoList(list));
  }

  @Override
  public CommonRespDto<List<BackendUserDto>> getUserByIds(List<Integer> userIds) {
    if (CollUtil.isEmpty(userIds)) {
      return CommonRespDto.success(new ArrayList<>());
    }
    List<SysUserPo> list = baseMapper.selectBatchIds(userIds);
    return CommonRespDto.success(backendUserConverter.poToDtoList(list));
  }

  @Override
  public CommonRespDto<BackendUserDto> getInfoByAccount(String account) {
    return CommonRespDto.success(backendUserConverter.poToDto(baseMapper.selectOne(
      Wrappers.<SysUserPo>lambdaQuery().eq(SysUserPo::getAccount, account)
        .ne(SysUserPo::getStatus,
          UserStatusEnum.DELETED.getCode()))));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CommonRespDto<Void> update(BackendUserDto dto) {

    // 不是修改自己的用户信息需要判断权限
    if (!Objects.equals(dto.getId(), UserAuthUtil.getUserId())) {
      boolean verified = verifyPermission(UserAuthUtil.getUserId());
      if (!verified) {
        return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
      }
    }

    SysUserPo oldPo = baseMapper.selectById(dto.getId());
    if (Objects.isNull(oldPo)) {
      return CommonRespDto.error("用户不存在");
    }

    // 用户信息校验
    List<String> errorMsg = UserValidationUtil.isValidUser(dto, ValidationField.ACCOUNT,
      ValidationField.PASSWORD).getData();
    if (CollUtil.isNotEmpty(errorMsg)) {
      return CommonRespDto.error(errorMsg.getFirst());
    }

    BackendRoleDto oldRole = backendUserRoleRelationService.getRoleByUserId(dto.getId()).getData();
    //角色变更，需要踢出用户
    Integer oldRoleId = oldRole.getId();
    Integer newRoleId = dto.getRoleId();

    // 密码不更新
    dto.setPassword(null);

    baseMapper.updateById(backendUserConverter.dtoToPo(dto));
    //更新用户角色关系
    backendUserRoleRelationService.deleteByUserId(dto.getId());
    backendUserRoleRelationService.batchSave(dto.getId(), newRoleId);

    if (!oldRoleId.equals(newRoleId) || !Objects.equals(oldPo.getDeptId(), dto.getDeptId())) {
      log.info("用户角色变更，踢出");
      deleteLogin(dto.getId(), MessageTypeEnum.ROLE_AUTHORITY_MODIFY_MSG);
    } else if (oldPo.getStatus() == 0 && dto.getStatus() == 1) {
      log.info("用户状态由正常变为禁用，踢出");
      deleteLogin(dto.getId(), MessageTypeEnum.ACCOUNT_DISABLE_LOGOUT);
    }

    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> disable(Integer userId) {

    boolean verified = verifyPermission(UserAuthUtil.getUserId());
    if (!verified) {
      return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
    }

    baseMapper.updateById(
      SysUserPo.builder().id(userId).status(UserStatusEnum.DISABLED.getCode()).build());
    // 踢出
    deleteLogin(userId, MessageTypeEnum.ACCOUNT_DISABLE_LOGOUT);

    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> enable(Integer userId) {
    boolean verified = verifyPermission(UserAuthUtil.getUserId());
    if (!verified) {
      return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
    }

    baseMapper.updateById(
      SysUserPo.builder().id(userId).status(UserStatusEnum.NORMAL.getCode()).build());
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> delete(Integer userId) {

    boolean verified = verifyPermission(UserAuthUtil.getUserId());
    if (!verified) {
      return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
    }

    baseMapper.updateById(
      SysUserPo.builder().id(userId).status(UserStatusEnum.DELETED.getCode()).build());
    backendUserRoleRelationService.deleteByUserId(userId);
    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> updatePassword(Integer userId, String oldPassword,
    String firstPassword,
    String secondPassword) {
    try {
      if (!UserAuthUtil.getUserId().equals(userId)) {
        boolean verified = verifyPermission(UserAuthUtil.getUserId());
        if (!verified) {
          return CommonRespDto.error(NO_ACCOUNT_PERMISSION);
        }
      }
      SysUserPo sysUserPo = baseMapper.selectById(userId);
      if (Objects.isNull(sysUserPo)) {
        return CommonRespDto.error("用户不存在");
      }

      if (StrUtil.isNotBlank(oldPassword)) {
        boolean matches = passwordEncoder.matches(
          RSAUtils.decryptByPrivateKey(oldPassword, RSAUtils.getPrivateKey()),
          sysUserPo.getPassword());
        if (!matches) {
          return CommonRespDto.error("旧密码不正确");
        }
      }

      // 解密，验证两场密码是否一致并符合格式
      String firstPasswordD = RSAUtils.decryptByPrivateKey(firstPassword, RSAUtils.getPrivateKey());
      String secondPasswordD =
        RSAUtils.decryptByPrivateKey(secondPassword, RSAUtils.getPrivateKey());
      if (!CharSequenceUtil.equals(firstPasswordD, secondPasswordD)) {
        return CommonRespDto.error("输入的密码不一致");
      }
      boolean validPassword = isValidPassword(firstPasswordD);
      if (!validPassword) {
        return CommonRespDto.error("密码格式不正确");
      }

      boolean matches = passwordEncoder.matches(firstPasswordD, sysUserPo.getPassword());
      if (matches) {
        return CommonRespDto.error("新密码不能和旧密码相同");
      }

      // 更新
      SysUserPo userPo =
        SysUserPo.builder().id(userId).password(passwordEncoder.encode(firstPasswordD))
          .build();
      baseMapper.updateById(userPo);
    } catch (Exception e) {
      log.error("RSA 解密异常!", e);
      return CommonRespDto.error("重置密码失败");
    }
    // 踢出
    deleteLogin(userId, MessageTypeEnum.PASSWORD_UPDATE_LOGOUT);

    return new CommonRespDto<>();
  }

  @Override
  public CommonRespDto<Void> checkPassword(String password) {
    if (StrUtil.isBlank(password)) {
      return CommonRespDto.error("输入密码为空");
    }

    Integer userId = UserAuthUtil.getUserId();
    SysUserPo sysUserPo = baseMapper.selectById(userId);
    if (Objects.isNull(sysUserPo)) {
      return CommonRespDto.error("用户不存在");
    }

    try {
      boolean matches = passwordEncoder.matches(
        RSAUtils.decryptByPrivateKey(password, RSAUtils.getPrivateKey()),
        sysUserPo.getPassword());
      if (matches) {
        return CommonRespDto.success();
      }
      return CommonRespDto.error("密码错误");

    } catch (Exception e) {
      log.error("check password error: {}", userId, e);
      return CommonRespDto.error("校验密码异常");
    }
  }

  @Override
  public CommonRespDto<String> downloadBatchImportUserTemplate() {
    String downloadPath =
      Paths.get(
          glusterProperties.getExport(),
          "批量导入用户模板下载",
          "后台用户-导入模板.xlsx")
        .toString();
    log.info("模板下载路径：{}", downloadPath);
    BatchImportUserTemplateUtil.getDownloadTemplate(downloadPath);
    return CommonRespDto.success(downloadPath.replaceFirst(glusterProperties.getBaseDir(), ""));
  }

  @Override
  public CommonRespDto<Void> batchImportUser(Integer roleId, byte[] fileBytes) {
    Integer userId = UserAuthUtil.getUserId();
    try {
      Path filePath =
        Paths.get(
          glusterProperties.getUpload(),
          "批量导入用户",
          roleId.toString(),
          "后台用户-导入模板.xlsx");
      Files.createDirectories(filePath.getParent());
      Path excelFilePath = Files.write(filePath, fileBytes);
      List<TemplateSheetCell> cells;
      try {
        cells = BatchImportUserTemplateUtil.readTemplate(excelFilePath.toString());
      } catch (Exception e) {
        log.error("模板格式错误", e);
        return CommonRespDto.error("批量导入用户模板格式错误，请重新下载模板导入再次尝试");
      }

      if (cells.size() > 20000) {
        return CommonRespDto.error("单次导入用户超过2W条");
      }

      log.info("批量导入用户模板上传成功，文件路径：{}，开始异步批量导入", excelFilePath);

      batchImportUserAsync(roleId, userId, cells);
    } catch (IOException e) {
      return CommonRespDto.error("批量导入用户模板上传失败，请重试");
    }

    return CommonRespDto.success();
  }

  @Override
  public CommonRespDto<Void> deleteLogin(Integer userId, MessageTypeEnum messageTypeEnum) {
    List<String> keys = redissonClient
      .getKeys()
      .getKeysStreamByPattern(RedisConstants.TOKEN_WHITELIST_PREFIX
        + userId + ":" + CommonConstants.LOGIN_TYPE_PC + ":*").toList();

    if (CollUtil.isEmpty(keys)) {
      return CommonRespDto.success();
    }
    keys.forEach(
      keyElement -> {
        // 设置两秒过期时间
        redissonClient.getBucket(keyElement).expire(Instant.now().plusSeconds(2));
      });

    return CommonRespDto.success();
  }

  /**
   * 根据userId 验证是否拥有增删改账号的权限
   */
  private boolean verifyPermission(Integer userId) {

    return backendMenuService.getMenuListByUserId(userId).getData().stream()
      .anyMatch(b -> CharSequenceUtil.equals(b.getSign(), ACCOUNT_MANAGE));

  }


  private void batchImportUserAsync(
    Integer roleId, Integer userId, List<TemplateSheetCell> cells) {
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    singleThreadExecutor.execute(
      () -> {
        UserAuthUtil.setAttachmentUserId(userId);
        try {
          List<TemplateSheetCell> failures = new ArrayList<>();

          String errorMsg = "批量导入用户失败，失败原因：[{}]";

          log.info("批量导入用户数量：[{}]", cells.size());

          for (TemplateSheetCell cell : cells) {

            SysUserPo sysUserPo = SysUserPo.builder().username(cell.getUsername())
              .account(cell.getAccount()).password(cell.getPassword())
              .status(UserStatusEnum.NORMAL.getCode())
              .build();

            log.info("批量导入用户，用户信息：[{}]", sysUserPo);
            List<String> errors = new ArrayList<>();

            // 1. 用户名校验：规范、敏感词
            boolean validUsername = isValidUsername(sysUserPo.getUsername());
            if (Boolean.FALSE.equals(validUsername)) {
              errors.add("用户名不符规范");
            }

            // 2. 登录账号校验：规范、敏感词、唯一
            boolean validAccount = isValidAccount(sysUserPo.getAccount());
            if (Boolean.FALSE.equals(validAccount)) {
              errors.add("账号不符规范");
            }

            // 唯一
            if (isAccountUnique(sysUserPo.getAccount())) {
              errors.add("账号重复");
            }

            // 3. 密码校验：规范
            if (!isValidPassword(sysUserPo.getPassword())) {
              errors.add("密码不符规范");
            }

            if (CollectionUtils.isNotEmpty(errors)) {
              cell.setFailureReasons(errors);
              failures.add(cell);
              log.error(errorMsg, errors);
              continue;
            }

            try {
              sysUserPo.setPassword(passwordEncoder.encode(sysUserPo.getPassword()));
              baseMapper.insert(sysUserPo);
              backendUserRoleRelationService.batchSave(sysUserPo.getId(), roleId);
            } catch (DuplicateKeyException duplicateKeyException) {
              cell.setFailureReasons(Collections.singletonList("账号重复"));
              failures.add(cell);
              log.error(errorMsg, "账号重复");
            } catch (Exception e) {
              cell.setFailureReasons(Collections.singletonList("网络错误"));
              failures.add(cell);
              log.error(errorMsg, "批量导入保存用户出错");
            }
          }

          BackendMessageDto backendMessageDto = BackendMessageDto.builder().userId(userId)
            .type(Type.NOTICE.getValue()).msgType(
              MessageTypeEnum.BULK_IMPORT_USERS_COMPLETE.getCode()).build();

          log.info("批量导入用户完成，失败数量：[{}]", failures.size());

          if (CollectionUtils.isNotEmpty(failures)) {
            Path failureFilePath =
              Paths.get(
                glusterProperties.getUpload(),
                "批量导入用户",
                roleId.toString(),
                String.format(
                  "导入失败-%s.xlsx",
                  LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
            BatchImportUserTemplateUtil.generateFailedImportedUserTemplate(
              failures, failureFilePath);

            backendMessageDto.setDownloadPath(
              failureFilePath.toString().replaceFirst(glusterProperties.getBaseDir(), ""));
          }

          Integer failureNum = failures.size();
          Integer total = cells.size();
          Integer successNum = total - failureNum;

          JSONObject jsonObject = new JSONObject();
          jsonObject.set("failureNum", failureNum);
          jsonObject.set("successNum", successNum);
          jsonObject.set("total", total);
          backendMessageDto.setAttachment(JSONUtil.toJsonStr(jsonObject));
          backendMessageDto.setMsg(
            String.format(
              MessageTypeEnum.BULK_IMPORT_USERS_COMPLETE.getMessage(),
              total,
              successNum,
              failureNum));
          backendMessageService.send(backendMessageDto);
        } catch (Exception e) {
          log.error("批量导入用户出错", e);
        }
      });
    singleThreadExecutor.shutdown();
  }


  private boolean isAccountUnique(String account) {
    return count(
      new LambdaQueryWrapper<SysUserPo>()
        .eq(SysUserPo::getAccount, account)
        .ne(SysUserPo::getStatus, UserStatusEnum.DELETED.getCode())) != 0;
  }
}
