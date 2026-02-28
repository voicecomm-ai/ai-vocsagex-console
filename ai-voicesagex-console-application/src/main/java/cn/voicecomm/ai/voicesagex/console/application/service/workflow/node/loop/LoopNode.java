package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.Condition;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 循环节点实体
 *
 * @author: gaox
 * @date: 2025/11/13 9:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoopNode extends BaseNode implements Serializable {

  /**
   * 循环开始节点的ID
   */
  private String start_node_id;

  /**
   * 最大循环次数
   */
  private Integer loop_count;

  /**
   * 循环中断条件列表
   */
  private List<Condition> break_conditions;

  /**
   * 条件逻辑操作符，支持 "and" 或 "or"
   */
  private String logical_operator;

  /**
   * 循环变量列表
   */
  private List<LoopVariableData> loop_variables;

  /**
   * 输出数据映射
   */
  private Map<String, Object> outputs;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class LoopVariableData {

    /**
     * 变量标签名称
     */
    private String label;

    /**
     * 变量类型
     */
    private String var_type;

    /**
     * 值类型，支持 "variable" 或 "constant"
     */
    private String value_type;

    /**
     * 变量值，可以是任意类型或字符串列表
     */
    private Object value;
  }
}
