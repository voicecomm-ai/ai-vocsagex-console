package cn.voicecomm.ai.voicesagex.console.api.dto.user;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BackendMenuDto extends BaseDto {

  @Serial
  private static final long serialVersionUID = -5375776271003656713L;

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
  private List<BackendMenuDto> children;
}
