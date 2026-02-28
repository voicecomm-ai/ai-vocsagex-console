package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BackendRoleMenuRelationDto extends BaseDto {

  @Serial
  private static final long serialVersionUID = -1041494738245591781L;

  private Integer id;

  /**
   * 角色id
   */
  private Integer roleId;

  /**
   * 菜单id
   */
  private Integer menuId;

}
