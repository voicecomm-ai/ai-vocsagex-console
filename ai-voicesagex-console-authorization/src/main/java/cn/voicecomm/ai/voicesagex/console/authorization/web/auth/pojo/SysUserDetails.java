package cn.voicecomm.ai.voicesagex.console.authorization.web.auth.pojo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserDetails implements UserDetails, CredentialsContainer {

  @Serial
  private static final long serialVersionUID = -1614257555549367897L;

  /**
   * 扩展字段：用户ID
   */
  private Integer id;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 账号
   */
  private String account;

  /**
   * 状态
   */
  private Integer status;

  /**
   * 默认字段
   */
  private String username;

  private String password;

  private boolean isAccountNonExpired;

  private boolean isAccountNonLocked;

  private boolean isCredentialsNonExpired;

  private boolean isEnabled;

  private String deviceType;

  private Integer dataPermission;

  private Collection<GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void eraseCredentials() {
    password = null;
  }
}
