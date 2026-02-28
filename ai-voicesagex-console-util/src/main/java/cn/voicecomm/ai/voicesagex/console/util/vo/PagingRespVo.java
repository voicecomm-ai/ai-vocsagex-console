package cn.voicecomm.ai.voicesagex.console.util.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 响应分页
 *
 * @author jiwh
 * @date 2024/5/30 14:10
 */
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingRespVo<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -3461347355483145274L;


  /**
   * 当前页
   */
  private long current;

  /**
   * 当前页大小
   */
  private long size;


  /**
   * 总条数
   */
  private long total;


  /**
   * 结果集
   */
  private List<T> records;
}
