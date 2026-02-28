package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseNodeHandler {


  public static  <T> T getNode(JSONObject node, Class<T> clazz) {
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    return JSONUtil.toBean(jsonObject, clazz);
  }


  /**
   * 运行
   *
   * @return 运行结果
   */
  public abstract NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas,
      JSONObject graph, String workflowRunId, Integer appId);

  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    return new HashMap<>();
  }
}
