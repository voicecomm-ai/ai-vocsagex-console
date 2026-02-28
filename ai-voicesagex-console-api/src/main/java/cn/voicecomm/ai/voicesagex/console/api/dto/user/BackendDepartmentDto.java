package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseAuditDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * ·组织架构
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BackendDepartmentDto extends BaseAuditDto implements Serializable {

  /**
   * id
   */
  private Integer id;

  /**
   * 部门名称
   */
  private String departmentName;

  /**
   * 部门级别
   */
  private Integer level;

  /**
   * 备注
   */
  private String remark;

  /**
   * 父级id
   */
  private Integer parentId;


  /**
   * 父级id
   */
  private String parentDepartmentName;

  /**
   * 下级部门
   */
  private List<BackendDepartmentDto> children;

  /**
   * 修改人
   */
  private String updateUsername;


  /**
   * 包含成员数
   */
  private Integer userNo;


  /**
   * 包含成员数(仅当前部门)
   */
  private Integer currentDeptUserNo;


  /**
   * 是否获取全部（数据权限）
   */
  private Boolean getAllFlag;


}
