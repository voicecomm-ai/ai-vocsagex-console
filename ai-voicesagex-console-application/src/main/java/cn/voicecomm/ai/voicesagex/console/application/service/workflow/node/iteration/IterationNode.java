package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration;

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
 * @date: 2025/12/3 9:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class IterationNode extends BaseNode implements Serializable {

  /**
   * 迭代开始节点ID
   */
  private String start_node_id;

  /**
   * 父循环ID（冗余字段，当前未使用）
   */
  private String parent_loop_id;

  /**
   * 迭代器输入类型
   */
  private String iterator_input_type;

  /**
   * 迭代器选择器（变量选择器）
   */
  private List<String> iterator_selector;

  /**
   * 输出选择器
   */
  private List<String> output_selector;

  /**
   * 是否开启并行模式
   */
  private boolean is_parallel = false;

  /**
   * 并行数量
   */
  private int parallel_nums = 10;
}
