package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.end.EndNode.Output;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 结束节点
 *
 * @author wangf
 * @date 2025/8/1 下午 4:27
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class EndNodeHandler extends BaseNodeHandler {

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    // 获取用户输入
    log.info("开始执行结束节点处理，节点数据: {}", JSONUtil.toJsonStr(nodeCanvas));
    EndNode endNode = getNode(nodeCanvas, EndNode.class);
    List<Output> output_vars = endNode.getOutputs();
    Map<String, Object> outputs = new LinkedHashMap<>();
    if (CollUtil.isNotEmpty(output_vars)) {
      for (Output output : output_vars) {
        String variable = output.getVariable();
        if (StrUtil.isNotEmpty(variable) && CollUtil.isNotEmpty(output.getValue_selector())) {
          List<String> value_selector = output.getValue_selector();
          Segment object = variablePool.get(value_selector);
          outputs.put(variable, Optional.ofNullable(object).map(Segment::toObject).orElse(null));
        }
      }
    }
    return NodeRunResult.builder().status(WorkflowNodeExecutionStatus.SUCCEEDED).inputs(outputs)
        .outputs(outputs).build();
  }
}
