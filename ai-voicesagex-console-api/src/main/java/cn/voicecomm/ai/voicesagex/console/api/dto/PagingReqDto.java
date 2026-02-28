package cn.voicecomm.ai.voicesagex.console.api.dto;

import cn.voicecomm.ai.voicesagex.console.api.constant.PaginationConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PagingReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1054820432755218440L;

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
    if (Objects.isNull(current) || current == 0) {
      return PaginationConstant.DEFAULT_PAGE_NUM;
    } else {
      return current;
    }
  }
}
