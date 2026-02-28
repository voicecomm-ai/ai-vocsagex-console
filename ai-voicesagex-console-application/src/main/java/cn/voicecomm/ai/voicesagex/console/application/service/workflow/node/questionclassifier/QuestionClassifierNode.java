package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.questionclassifier;

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
public class QuestionClassifierNode extends BaseNode implements Serializable {

  /**
   * 查询变量选择器。
   */
  private List<String> query_variable_selector;

  /**
   * 主题列表。
   */
  private List<String> topics;

  /**
   * 模型配置。
   */
  private Integer model_id;

  /**
   * 分类列表。
   */
  private List<ClassItem> classes;

  /**
   * 指令。
   */
  private String instructions;

  /**
   * 指令模板。
   */
  private String instruction;

  /**
   * 内部类，表示完成参数。
   */
  @Data
  public static class CompletionParams {
    /**
     * 温度。
     */
    private double temperature;

    /**
     * 最大令牌数。
     */
    private int maxTokens;

    /**
     * Top K值。
     */
    private int topK;

    /**
     * 随机种子。
     */
    private int seed;

    /**
     * 停止词列表。
     */
    private List<String> stop;
  }

  /**
   * 内部类，表示分类项。
   */
  @Data
  @Accessors(chain = true)
  @Builder
  public static class ClassItem {
    /**
     * ID。
     */
    private String id;

    /**
     * 名称。
     */
    private String name;
  }

}
