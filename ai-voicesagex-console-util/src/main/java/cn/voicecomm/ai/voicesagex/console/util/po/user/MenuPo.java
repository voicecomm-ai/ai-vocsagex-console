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
@TableName("menu")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MenuPo extends BaseAuditPo {

  @Serial
  private static final long serialVersionUID = 1939455453986477651L;

  @TableId(type = IdType.AUTO)
  private Integer id;

  /**
   * 菜单名称
   */
  @TableField(value = "menu_name")
  private String menuName;

  /**
   * 标志
   */
  @TableField(value = "sign")
  private String sign;

  /**
   * 上级id
   */
  @TableField(value = "superior_id")
  private Integer superiorId;

  /**
   * 图标
   */
  @TableField(value = "icon")
  private String icon;

  /**
   * 路径
   */
  @TableField(value = "uri")
  private String uri;

  /**
   * 类别（0 菜单 1 按钮）
   */
  @TableField(value = "type")
  private Integer type;

  /**
   * 排序
   */
  @TableField(value = "sort")
  private Integer sort;

}
