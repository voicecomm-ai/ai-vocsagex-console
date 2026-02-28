package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class EndNode extends BaseNode implements Serializable {


  /**
   * 变量
   */
  private List<Output> outputs;

  /**
   * 表示变量与值选择器映射关系的Java类。 该类用于定义如何从流程的特定节点中选取数据并赋值给当前变量。
   */
  @Data
  @Accessors(chain = true)
  public static class Output implements Serializable {

    /**
     * 目标变量名。 数据将被选取并赋值给此变量。
     */
    private String variable;

    /**
     * 值选择器数组。 数组第一个元素通常是节点的唯一标识符（如时间戳）， 第二个元素是该节点内要提取的具体字段名。
     */
    private List<String> value_selector;


  }

}



