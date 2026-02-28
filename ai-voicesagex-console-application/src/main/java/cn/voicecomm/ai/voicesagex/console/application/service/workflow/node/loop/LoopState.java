package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author: gaox
 * @date: 2025/11/13 10:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoopState extends BaseLoopState {

  /**
   * 输出结果列表
   */
  private List<Object> outputs = new ArrayList<>();

  /**
   * 当前输出结果
   */
  private Object current_output;

  /**
   * 获取最后一次输出结果
   *
   * @return 最后一次输出结果，如果列表为空则返回null
   */
  public Object getLastOutput() {
    if (outputs != null && !outputs.isEmpty()) {
      return outputs.getLast();
    }
    return null;
  }


  /**
   * 元数据信息
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @SuperBuilder
  @Accessors(chain = true)
  public static class MetaData extends BaseLoopState.MetaData {

    /**
     * 循环长度
     */
    private Integer loop_length;
  }
}
