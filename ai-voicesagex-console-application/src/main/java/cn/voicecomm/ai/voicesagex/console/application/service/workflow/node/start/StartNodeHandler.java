package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start;

import cn.hutool.json.JSONObject;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 开始节点
 *
 * @author wangf
 * @date 2025/8/1 下午 4:27
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class StartNodeHandler extends BaseNodeHandler {

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    log.info("开始执行start节点处理，节点数据: {}", nodeCanvas);
    // 获取用户输入
    Map<String, Object> nodeInputs = new LinkedHashMap<>(variablePool.getUser_inputs());

//    // 获取系统变量
//    Map<String, Object> systemInputs = BeanUtil.beanToMap(variablePool.getSystem_variables());
//
//    systemInputs.put("workflow_run_id", variablePool.getSystem_variables().getWorkflow_execution_id());
//    systemInputs.remove("workflow_execution_id");
//    systemInputs.remove("files");
//    nodeInputs.put("workflow_run_id", variablePool.getSystem_variables().getWorkflow_execution_id());

//    // 系统变量应该可以直接访问，不需要特殊处理
//    // 将系统变量设置为节点输出
//    for (Map.Entry<String, Object> entry : systemInputs.entrySet()) {
//      String systemVariableKey =
//          WorkflowConstants.SYSTEM_VARIABLE_NODE_ID + "." + entry.getKey();
//      nodeInputs.put(systemVariableKey, entry.getValue());
//    }

    return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.SUCCEEDED).inputs(nodeInputs)
        .outputs(nodeInputs).build();
  }
}
