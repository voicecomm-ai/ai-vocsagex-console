package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 模型分页列表请求
 *
 * @author ryc
 * @date 2025/6/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelPageReq extends PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -3077886410445561958L;

  /**
   * 模型名称
   */
  private String name;
  /**
   * 模型类型 0：算法模型；1：预训练模型
   */
  private Integer type;
  /**
   * 模型分类集合
   */
  private List<Integer> classificationIdList;
  /**
   * 标签id集合
   */
  private List<Integer> tagIdList;
  /**
   * 是否上架 0：否；1：是
   */
  private Boolean isShelf;
  /**
   * 是否需要权限
   */
  private Boolean isAuth;

}
