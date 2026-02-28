package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.code;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.ErrorDefaultValue;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.RetryConfig;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CodeNode extends BaseNode implements Serializable {


  /**
   * 输入变量列表
   */
  private List<CodeVariable> variables;

  /**
   * 代码语言
   */
  private String code_language;

  /**
   * 代码内容
   */
  private String code;

  /**
   * 输出定义
   */
  private Map<String, Outputs> outputs;

  /**
   * 是否选中
   */
  private Boolean selected;

  /**
   * 重试配置
   */
  private RetryConfig retry_config;

  /**
   * 错误处理策略
   */
  private String error_strategy;

  /**
   * 默认返回值（错误时使用）
   */
  private List<ErrorDefaultValue> default_value;


  /**
   * 输出定义
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Outputs {
    /**
     * 类型
     * @see OutputsType
     */
    private String type;

    /**
     * 子节点（嵌套结构，可为空）
     */
    private Object children; // 可根据实际结构进一步定义，当前为通用 Object
  }


  @Getter
  public enum OutputsType {
    STRING("string"),
    NUMBER("number"),
    OBJECT("object"),
    ARRAY_STRING("array[string]"),
    ARRAY_NUMBER("array[number]"),
    ARRAY_OBJECT("array[object]"),
    ;

    private final String value;

    OutputsType(String value) {
      this.value = value;
    }

    public static OutputsType fromValue(String value) {
      for (OutputsType type : OutputsType.values()) {
        if (type.value.equals(value)) {
          return type;
        }
      }
      return null;
    }
  }



  /**
   * 变量映射项
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CodeVariable {
    /**
     * 变量名
     */
    private String variable;

    /**
     * 值的选择器路径
     */
    private List<String> value_selector;

    /**
     * 值的类型
     */
    private String value_type;
  }
}
