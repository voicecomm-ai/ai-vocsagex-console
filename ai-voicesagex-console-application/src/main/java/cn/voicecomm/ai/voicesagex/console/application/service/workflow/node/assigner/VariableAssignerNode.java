package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author: gaox
 * @date: 2025/11/14 16:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class VariableAssignerNode extends BaseNode implements Serializable {

  /**
   * 变量操作项列表
   */
  private List<VariableOperationItem> items;
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class VariableOperationItem {

    /**
     * 变量选择器
     */
    private List<String> variable_selector;

    /**
     * 输入类型
     */
    private String input_type;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 值字段，根据上下文有不同的用途：
     * 1. 对于CONSTANT输入类型：包含要在操作中使用的文字值
     * 2. 对于VARIABLE输入类型：最初包含源变量的选择器
     * 3. 在变量更新过程中：重新分配以保存将应用于目标变量的解析实际值
     */
    private Object value;
  }
}
