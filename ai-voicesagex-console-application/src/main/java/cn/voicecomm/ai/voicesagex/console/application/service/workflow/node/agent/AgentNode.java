package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.agent;

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
 * AgentNode
 *
 * @author wangfan
 * @date 2026/1/6 上午 9:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AgentNode extends BaseNode implements Serializable {

  private Integer appId;

  /**
   * 字段值
   */
  private String queryValue;

  /**
   * 值类型 "Variable" "Constant"
   */
  private String query_value_type;


  private List<McpParam> param;
}
