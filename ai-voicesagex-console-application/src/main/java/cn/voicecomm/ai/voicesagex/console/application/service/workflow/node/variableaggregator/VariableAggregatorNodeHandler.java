package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.variableaggregator;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.variableaggregator.VariableAggregatorNode.Group;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/12/2 9:42
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class VariableAggregatorNodeHandler extends BaseNodeHandler {

  /**
   * 运行
   *
   * @param variablePool
   * @param nodeCanvas
   * @param graph
   * @param workflowRunId
   * @param appId
   * @return 运行结果
   */
  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject jsonObject = JSONUtil.getByPath(nodeCanvas, "data", null);

    VariableAggregatorNode nodeData = JSONUtil.toBean(jsonObject, VariableAggregatorNode.class);
    // Get variables
    Map<String, Object> outputs = new HashMap<>();
    Map<String, Object> inputs = new HashMap<>();

    for (Group group : nodeData.getAdvanced_settings().getGroups()) {
      for (List<String> selector : group.getVariables()) {
        Segment variable = variablePool.get(selector);

        if (variable != null && variable.getValue() != null) {
          Map<String, Object> groupOutput = new HashMap<>();
          groupOutput.put("output", variable.getValue());
          outputs.put(group.getGroup_name(), groupOutput);
          inputs.put(String.join(".", selector.subList(1, selector.size())), variable.toObject());
          break;
        }
      }
    }
    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .outputs(outputs)
        .inputs(inputs)
        .build();
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    Map<String, List<String>> map = new HashMap<>();
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    VariableAggregatorNode nodeData = JSONUtil.toBean(jsonObject,
        VariableAggregatorNode.class);
    for (Group group : nodeData.getAdvanced_settings().getGroups()) {
      for (List<String> selector : group.getVariables()) {
        String key = node.getStr("id") + "." + group.getGroup_name() + "." + String.join(".",
            selector);
        // nodeId.groupName.nodeId.variableName
        map.put(key, selector);
      }
    }
    return map;
  }
}
