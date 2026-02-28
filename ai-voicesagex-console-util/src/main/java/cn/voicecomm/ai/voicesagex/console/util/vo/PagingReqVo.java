package cn.voicecomm.ai.voicesagex.console.util.vo;

import cn.voicecomm.ai.voicesagex.console.util.constant.PaginationConstant;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jiwh
 * @date 2024/5/30 14:05
 */
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingReqVo implements Serializable {

  @Serial
  private static final long serialVersionUID = 5048281326621853827L;

  /**
   * 当前页
   */
  protected Integer current = PaginationConstant.DEFAULT_PAGE_NUM;

  /**
   * 每页数量
   */
  protected Integer size = PaginationConstant.DEFAULT_PAGE_SIZE;

  /**
   * 排序字段
   */
  protected String orderBy;

  /**
   * 升序或降序
   */
  protected String descOrAsc;

  public Integer getCurrent() {
    if (current == null || current == 0) {
      return PaginationConstant.DEFAULT_PAGE_NUM;
    } else {
      return current;
    }
  }
}
