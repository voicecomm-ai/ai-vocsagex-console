package cn.voicecomm.ai.voicesagex.console.user.vo.user;

import lombok.Data;

import java.util.List;

@Data
public class BackendMenuVo {

  /**
   * id
   */
  private Integer id;

  /**
   * 菜单名称
   */
  private String menuName;

  /**
   * 标志
   */
  private String sign;

  /**
   * 上级id
   */
  private Integer superiorId;

  /**
   * 图标
   */
  private String icon;

  /**
   * 路径
   */
  private String uri;

  /**
   * 类别（0 菜单 1 按钮）
   */
  private Integer type;

  /**
   * 排序
   */
  private Integer sort;

  /**
   * 子菜单
   */
  private List<BackendMenuVo> children;
}
