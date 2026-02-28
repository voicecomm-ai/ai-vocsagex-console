package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class IfElseNode extends BaseNode implements Serializable {

  /**
   * 条件分支的各个 case
   */
  private List<CaseItem> cases;


  /**
   * Case 项实体类
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CaseItem {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 用例ID
     */
    private String case_id;

    /**
     * 逻辑操作符（如 and）
     */
    private String logical_operator;

    /**
     * 条件列表
     */
    private List<Condition> conditions;
  }

  /**
   * 条件实体类
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Condition {

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 变量类型
     */
    private String key;

    /**
     * 变量类型
     */
    private String varType;

    /**
     * 变量选择器
     */
    private List<String> variable_selector;

    /**
     * 比较操作符（如 ≠, >, ≥, not empty）
     */
    private String comparison_operator;

    /**
     * 比较值，支持变量引用（如 {{#...}}）或常量
     */
    private String value;

    /**
     * 数值变量类型（可选）
     */
    private String numberVarType;


    /**
     * 子变量条件
     */
    private SubVariableCondition sub_variable_condition;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SubVariableCondition {

    /**
     * 用例ID
     */
    private String case_id;

    /**
     * 逻辑操作符（如 and）
     */
    private String logical_operator;

    /**
     * 条件列表
     */
    private List<Condition> conditions;
  }


}
