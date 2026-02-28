package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/11/13 11:39
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class LoopStartNodeHandler extends BaseNodeHandler {

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
    // 运行节点逻辑
    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .build();
  }
}
