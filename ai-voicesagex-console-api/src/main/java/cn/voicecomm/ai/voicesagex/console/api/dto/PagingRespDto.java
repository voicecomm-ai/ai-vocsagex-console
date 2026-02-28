package cn.voicecomm.ai.voicesagex.console.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingRespDto<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 310794251458536451L;

  /**
   * 当前页
   */
  private long current;

  /**
   * 每页数量
   */
  private long size;

  /**
   * 总记录数
   */
  @Builder.Default
  private long total = 0;

  /**
   * 结果集
   */
  private List<T> records;
}
