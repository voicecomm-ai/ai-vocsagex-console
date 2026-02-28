package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.llm;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.ErrorDefaultValue;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Model;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.RetryConfig;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.general.Vision;
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
 * LLM节点的所有配置信息
 *
 * @author wangf
 * @date 2025/7/30 下午 2:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LLMNode extends BaseNode implements Serializable {


  /**
   * 变量列表
   */
  private List<String> variables;

  /**
   * 模型配置
   */
  private Model model;

  /**
   * 提示模板列表
   */
  private List<PromptTemplate> prompt_template;

  /**
   * 上下文配置
   */
  private Context context;

  /**
   * 视觉配置
   */
  private Vision vision;

  /**
   * 提示配置
   */
  private PromptConfig prompt_config;

  /**
   * 结构化输出是否启用
   */
  private boolean structured_output_enabled;

  /**
   * 结构化输出配置（json schema格式）
   */
  private StructuredOutput structured_output;

  /**
   * 重试配置
   */
  private RetryConfig retry_config;

  /**
   * 错误策略
   */
  private String error_strategy;


  /**
   * 错误策略为默认值时 默认值的配置
   */
  private List<ErrorDefaultValue> default_value;


  /**
   * 提示模板类
   */
  @Data
  @Accessors(chain = true)
  public static class PromptTemplate implements Serializable {

    /**
     * 角色
     */
    private String role;

    /**
     * 文本
     */
    private String text;

    /**
     * ID
     */
    private String id;

    /**
     * 编辑类型
     */
    private String edition_type;

    /**
     * Jinja2文本
     */
    private String jinja2_text;
  }

  /**
   * 上下文配置类
   */
  @Data
  @Accessors(chain = true)
  public static class Context implements Serializable {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 变量选择器列表
     */
    private List<String> variable_selector;
  }



  /**
   * 提示配置类
   */
  @Data
  @Accessors(chain = true)
  public static class PromptConfig implements Serializable {

    /**
     * Jinja2变量列表
     */
    private List<String> jinja2_variables;
  }

  /**
   * 提示配置类
   */
  @Data
  @Accessors(chain = true)
  public static class StructuredOutput implements Serializable {

    /**
     * schema
     */
    private JSONObject schema;
  }




}




