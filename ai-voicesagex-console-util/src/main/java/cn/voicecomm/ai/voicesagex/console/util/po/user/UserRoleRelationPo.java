package cn.voicecomm.ai.voicesagex.console.util.po.user;


import cn.voicecomm.ai.voicesagex.console.util.po.BaseAuditPo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user_role_relation")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleRelationPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = 2205538283408993251L;


  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 用户id
   */
  @TableField(value = "user_id")
  private Integer userId;

  /**
   * 角色id
   */
  @TableField(value = "role_id")
  private Integer roleId;

}
