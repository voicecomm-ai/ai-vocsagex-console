package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 模型分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 3618720989207355349L;

  /**
   * 模型名称
   */
  private String name;
  /**
   * 模型类型 0：算法模型；1：预训练模型
   */
  private Integer type;
  /**
   * 标签id集合
   */
  private List<Integer> tagIdList;
  /**
   * 是否上架
   */
  private Boolean isShelf;
  /**
   * 是否支持视觉
   */
  private Boolean isSupportVisual;
  /**
   * 是否支持微调
   */
  private Boolean isSupportAdjust;
  /**
   * 生成状态 0：生成中；1：生成成功；2：生成失败
   */
  private Integer generateStatus;
  /**
   * 加载方式
   */
  private String loadingMode;
  /**
   * 是否需要权限
   */
  private Boolean isAuth;

}
