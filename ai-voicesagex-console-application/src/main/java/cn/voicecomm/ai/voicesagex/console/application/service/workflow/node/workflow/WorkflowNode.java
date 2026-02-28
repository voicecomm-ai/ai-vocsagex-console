package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.workflow;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.mcp.McpNode.McpParam;
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
 * @date: 2025/9/10 14:12
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowNode extends BaseNode implements Serializable {

  /**
   * 查询变量选择器。
   */
  private List<String> query_variable_selector;

  private Integer appId;

  private List<McpParam> param;
}
