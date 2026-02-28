package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.variableaggregator;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
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

/**
 * @author: gaox
 * @date: 2025/12/2 9:39
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VariableAggregatorNode extends BaseNode implements Serializable {

  /**
   * 节点类型
   */
  private String type = "variable-aggregator";

  /**
   * 输出类型
   */
  private String output_type;

  /**
   * 变量选择器列表
   */
  private List<List<String>> variables;

  /**
   * 高级设置
   */
  private AdvancedSettings advanced_settings;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class AdvancedSettings {

    /**
     * 是否启用分组
     */
    private boolean group_enabled;

    /**
     * 分组列表
     */
    private List<Group> groups;

  }

  /**
   * 分组
   *
   * @author gaox
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class Group {

    /**
     * 输出类型
     */
    private SegmentType output_type;

    /**
     * 变量选择器列表
     */
    private List<List<String>> variables;

    /**
     * 分组名称
     */
    private String group_name;
  }

}
