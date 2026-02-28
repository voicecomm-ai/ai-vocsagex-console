package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import com.fasterxml.jackson.annotation.JsonInclude;
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
 * @date: 2025/9/10 14:12
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class McpNode extends BaseNode implements Serializable {

  /**
   * 查询变量选择器。
   */
  private List<String> query_variable_selector;

  private Integer mcp_id;

  private String tool_name;

  private List<McpParam> param;

  @Data
  @Accessors(chain = true)
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class McpParam {

    /**
     * 字段名称。(如果是子智能体，使用"子智能体appid&变量名"方式  例：1&name  2&age)
     */
    private String name;

    /**
     * 变量名
     */
    private String nameText;

    /**
     * 类型：String;Number;Time
     */
    private String type;

    /**
     * 字段值
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object value;

    /**
     * 值类型 "Variable" "Constant"
     */
    private String value_type;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 是否必填
     */
    private Boolean required;
  }
}
