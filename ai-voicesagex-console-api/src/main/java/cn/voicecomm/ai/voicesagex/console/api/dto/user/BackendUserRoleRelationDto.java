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
public class BackendUserRoleRelationDto extends BaseDto {

  @Serial
  private static final long serialVersionUID = -8222042183003345957L;

  private Integer id;

  /**
   * 用户id
   */
  private Integer userId;

  /**
   * 角色id
   */
  private Integer roleId;

}
