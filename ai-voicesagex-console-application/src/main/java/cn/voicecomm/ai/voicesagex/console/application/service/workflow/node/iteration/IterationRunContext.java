package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.iteration;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.graphengine.GraphRun;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: gaox
 * @date: 2025/12/18 13:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IterationRunContext {
  private Boolean isParallel;

  private VariablePool variablePool;
  private JSONObject nodeCanvas;
  private String workflowRunId;
  private Integer appId;

  private Integer index;
  private String nodeId;
  private String currentNodeId;

  private List<?> iteratorListValue;
  private GraphRun graphRun;
  private JSONObject filteredGraph;
  private IterationNode nodeData;

  // 迭代输出
  private List<Object> outputList;
  private Map<String, Double> loopDurationMap;

  private Map<String, Object> inputs;
  // 总输出
  private Map<String, Object> outputs;
}
