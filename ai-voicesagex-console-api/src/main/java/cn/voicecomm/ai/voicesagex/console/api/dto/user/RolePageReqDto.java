package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class RolePageReqDto extends PagingReqDto {

  @Serial
  private static final long serialVersionUID = -7500305675336455801L;

  /**
   * 部门id
   */
  private Integer deptId;
}
