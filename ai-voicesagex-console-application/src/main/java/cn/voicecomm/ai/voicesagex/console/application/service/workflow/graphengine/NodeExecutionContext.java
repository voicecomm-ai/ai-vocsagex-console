package cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 节点执行上下文类
 *
 * @author wangfan
 * @date 2025/5/19 下午 4:29
 */
@Data
@AllArgsConstructor
public class NodeExecutionContext {

  private String nodeId;
  private GraphRun graphRun;
  private VariablePool variablePool;
  private String workflowRunId;
  private JSONObject graphJson;
  private Integer appId;

}