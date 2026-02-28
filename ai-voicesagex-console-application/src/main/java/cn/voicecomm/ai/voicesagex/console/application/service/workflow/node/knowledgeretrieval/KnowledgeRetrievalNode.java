package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.knowledgeretrieval;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Model;
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
public class KnowledgeRetrievalNode extends BaseNode implements Serializable {

  /**
   * 查询变量选择器。
   */
  private List<String> query_variable_selector;

  /**
   * 数据集ID列表。
   */
  private List<DataSet> dataSet_list;

  /**
   * 检索模式。
   */
  private String retrieval_mode;

  /**
   * 多重检索配置。
   */
  private MultipleRetrievalConfig multiple_retrieval_config;

  /**
   * 元数据过滤模式。
   */
  private String metadata_filtering_mode;

  /**
   * 元数据过滤模型
   */
  private Model metadata_model;

  private MetadataFilteringCondition metadata_filtering_condition;

  @Data
  public static class MetadataFilteringCondition {

    /**
     * 逻辑操作符，用于组合多个条件。 可选值："and", "or" 默认为 "and"。
     */
    private String logical_operator = "and";

    /**
     * 具体的过滤条件列表。
     */
    private List<Condition> conditions;
  }

  @Data
  public static class DataSet {

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库id
     */
    private Integer id;
  }

  /**
   * 单个元数据过滤条件。 表示对某个元数据字段的过滤规则。
   */
  @Data
  public static class Condition {

    /**
     * 条件唯一标识ID。
     */
    private String id;

    /**
     * 元数据字段名称。
     */
    private String name;

    /**
     * 元数据类型：String;Number;Time
     */
    private String type;

    /**
     * 比较操作符，如：is, contains, empty, in 等。
     */
    private String comparison_operator;

    /**
     * 比较值，支持字符串、数字、列表等类型。 运行时会进行变量替换和类型处理。
     */
    private Object value;
  }

  /**
   * 内部类，表示多重检索配置。
   */
  @Data
  @Accessors(chain = true)
  public static class MultipleRetrievalConfig {

    /**
     * 顶部K值。
     */
    private int topK;

    /**
     * 启用Score 阈值
     */
    private Boolean enableScore;

    /**
     * 分数阈值。
     */
    private Double score_threshold;

    /**
     * 重新排序模式。
     */
    private String reranking_mode;

    /**
     * 重新排序模型。
     */
    private Model reranking_model;

    /**
     * 权重设置。
     */
    private Weights weights;

    /**
     * 是否启用重新排序。
     */
    private boolean reranking_enable;
  }

  /**
   * 内部类，表示权重设置。
   */
  @Data
  @Accessors(chain = true)
  public static class Weights {

    /**
     * 向量设置。
     */
    private VectorSetting vector_setting;

    /**
     * 关键字设置。
     */
    private KeywordSetting keyword_setting;

    /**
     * 模型
     */
    private Model model;
  }

  /**
   * 内部类，表示向量设置。
   */
  @Data
  @Accessors(chain = true)
  public static class VectorSetting {

    /**
     * 向量权重。
     */
    private int vector_weight;

    /**
     * 嵌入提供者名称。
     */
    private String embedding_provider_name;

    /**
     * 嵌入模型名称。
     */
    private String embedding_model_name;
  }

  /**
   * 内部类，表示关键字设置。
   */
  @Data
  @Accessors(chain = true)
  public static class KeywordSetting {

    /**
     * 关键字权重。
     */
    private int keyword_weight;
  }
}
