package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 模型分类列表请求
 *
 * @author wangf
 * @date 2025/5/19 下午 2:08
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelCategoryPageReq implements Serializable {

  @Serial
  private static final long serialVersionUID = 7246228626053352967L;

  /**
   * 是否广场查询
   */
  private Boolean isSquare;
  /**
   * 是否预设 0：否；1：是
   */
  private Boolean isBuilt;
}
