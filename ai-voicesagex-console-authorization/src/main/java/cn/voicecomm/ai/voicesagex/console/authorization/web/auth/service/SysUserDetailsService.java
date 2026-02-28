package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.service;

import cn.hutool.core.util.ObjectUtil;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendRoleService;
import cn.voicecomm.ai.voicesagex.console.api.api.user.BackendUserService;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendRoleDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.user.BackendUserDto;
import cn.voicecomm.ai.voicesagex.console.api.enums.role.RoleDataPermissionEnum;
import cn.voicecomm.ai.voicesagex.console.api.enums.user.MessageTypeEnum;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.converter.UserConverter;
import cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo.SysUserDetails;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
@DubboService
@RequiredArgsConstructor
@RefreshScope
public class SysUserDetailsService implements UserDetailsService {

  @Value("${voicesagex.console.single:0}")
  private Integer single;

  @DubboReference
  private BackendUserService backendUserService;

  @DubboReference
  private BackendRoleService backendRoleService;

  private final UserConverter userConverter;

  @Override
  public UserDetails loadUserByUsername(String username) {
    BackendUserDto backendUserDto = backendUserService.getInfoByAccount(username).getData();
    if (Objects.isNull(backendUserDto)) {
      return null;
    }

    return userConverter.userDtoToSysUserDetails(backendUserDto);
  }

  public SysUserDetails loadUserByAccount(String account) {
    BackendUserDto backendUserDto = backendUserService.getInfoByAccount(account).getData();
    SysUserDetails sysUserDetails = userConverter.userDtoToSysUserDetails(backendUserDto);
    if (ObjectUtil.isNotNull(sysUserDetails)) {
      BackendRoleDto backendRoleDto = backendRoleService.getRoleInfoByUserId(
        backendUserDto.getId());
      sysUserDetails.setDataPermission(
        Optional.ofNullable(backendRoleDto).map(BackendRoleDto::getDataPermission)
          .orElse(RoleDataPermissionEnum.ONLY_SELF.getKey()));
    }
    return sysUserDetails;
  }

  public void deleteLoginUser(Integer userId) {
    // 单用户登录需要踢出之前登录的用户
    if (Objects.equals(single, 1)) {
      backendUserService.deleteLogin(userId, MessageTypeEnum.SINGLE_LOGIN_LOGOUT);
    }
  }
}
