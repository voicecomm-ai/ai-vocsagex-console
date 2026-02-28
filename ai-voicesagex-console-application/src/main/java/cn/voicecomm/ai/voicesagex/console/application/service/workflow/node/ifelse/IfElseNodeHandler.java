package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.CaseItem;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.Condition;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ConditionProcessor;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.ProcessConditionsResult;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * if-else处理
 *
 * @author wangf
 * @date 2025/8/1 下午 4:28
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IfElseNodeHandler extends BaseNodeHandler {

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    log.info("开始执行if-else处理，节点数据: {}", JSONUtil.toJsonStr(nodeCanvas));
    IfElseNode ifElseNode = getNode(nodeCanvas, IfElseNode.class);
    Map<String, Object> node_inputs = new LinkedHashMap<>(Map.of("conditions", List.of()));
    // 使用可变Map替代不可变Map，避免后续修改异常
    Map<String, Object> process_data = new LinkedHashMap<>();
    List<Object> conditionResultsList = new ArrayList<>();
    process_data.put("condition_results", conditionResultsList);

    List<?> input_conditions = new ArrayList<>();
    boolean final_result = false;
    String selected_case_id = null;
    int selectedIndex = 0;
    ConditionProcessor condition_processor = new ConditionProcessor();

    try {
      assert CollUtil.isNotEmpty(ifElseNode.getCases());
      log.info("检测到cases结构，共{}个case需要处理", ifElseNode.getCases().size());

      for (int i = 0; i < ifElseNode.getCases().size(); i++) {
        CaseItem caseItem = ifElseNode.getCases().get(i);
        log.info(">>>>>>>> 第{}个case[{}]的条件列表开始处理，条件数量: {}, 操作符: {}", i + 1,
            caseItem.getCase_id(), caseItem.getConditions().size(), caseItem.getLogical_operator());
        ProcessConditionsResult result = condition_processor.processConditions(variablePool,
            caseItem.getConditions(),
            caseItem.getLogical_operator()
        );

        input_conditions = result.inputConditions();
        List<Boolean> group_result = result.groupResults();
        boolean case_final_result = result.finalResult();

        Map<String, Object> condition_result = new LinkedHashMap<>();
        condition_result.put("group", caseItem);
        condition_result.put("results", group_result);
        condition_result.put("final_result", case_final_result);

        // 修复：直接使用已声明的List变量，避免类型转换
        conditionResultsList.add(condition_result);

        log.info("<<<<<<<< 第{}个case[{}]处理完成，结果: {} ", i + 1,
            caseItem.getCase_id(), case_final_result);

        // 如果case通过 break
        if (case_final_result) {
          selected_case_id = caseItem.getCase_id(); // Capture the ID of the passing case
          final_result = true;
          log.info("找到匹配的case: {}", selected_case_id);
          selectedIndex = i + 1;
          break;
        }
      }
      log.info("所有cases处理完成，最终结果: {}, 选中case: 第{}个，case_id:{}", final_result,
          selectedIndex, selected_case_id);
      node_inputs.put("conditions", input_conditions);

    } catch (Exception e) {
      log.error("处理if-else节点时发生异常: {}", e.getMessage(), e);
      return new NodeRunResult()
          .setStatus(WorkflowNodeExecutionStatus.FAILED)
          .setInputs(node_inputs)
          .setProcess_data(process_data)
          .setError(e.getMessage());
    }

    Map<String, Object> outputs = new LinkedHashMap<>();
    outputs.put("result", final_result);
    outputs.put("selected_case_id", selected_case_id);

    log.info("if-else节点执行成功，最终结果: {}, 选中case: {}", final_result, selected_case_id);
    return new NodeRunResult()
        .setStatus(WorkflowNodeExecutionStatus.SUCCEEDED)
        .setInputs(node_inputs)
        .setProcess_data(process_data)
        .setEdge_source_handle(
            selected_case_id != null ? selected_case_id : "false")
        .setOutputs(outputs);
  }


  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {
    IfElseNode ifElseNode = getNode(node, IfElseNode.class);
    if (CollUtil.isEmpty(ifElseNode.getCases())) {
      return new LinkedHashMap<>();
    }
    LinkedHashMap<String, List<String>> variableSelectorToVariableMapping = new LinkedHashMap<>();
    for (CaseItem caseItem : ifElseNode.getCases()) {
      if (CollUtil.isEmpty(caseItem.getConditions())) {
        continue;
      }
      for (Condition condition : caseItem.getConditions()) {
        addToVariableMapping(variableSelectorToVariableMapping, node, condition, false);

        // 处理子条件
        if (ObjUtil.isNotNull(condition.getSub_variable_condition()) && CollUtil.isNotEmpty(
            condition.getSub_variable_condition().getConditions())) {
          for (Condition subCondition : condition.getSub_variable_condition().getConditions()) {
            addToVariableMapping(variableSelectorToVariableMapping, node, subCondition, true);
          }
        }
      }
    }
    return variableSelectorToVariableMapping;
  }

  /**
   * 将条件中的变量选择器和值中提取的变量名添加到变量映射中
   * <ol>
   *   <li>从节点中获取nodeId</li>
   *   <li>将condition中的variable_selector构造成key并存入映射</li>
   *   <li>从condition的value中提取变量名</li>
   *   <li>如果提取到变量名，则将其构造成key并存入映射</li>
   * </ol>
   *
   * @param variableSelectorToVariableMapping 变量选择器到变量列表的映射关系
   * @param node                              当前处理的JSON节点对象
   * @param condition                         条件对象，包含变量选择器和值信息
   * @param isSubcondition                    是否为子条件标识，用于判断是否需要添加variable_selector映射
   */
  private void addToVariableMapping(
      LinkedHashMap<String, List<String>> variableSelectorToVariableMapping,
      JSONObject node, Condition condition, boolean isSubcondition) {
    // 获取当前节点的nodeId
    String nodeId = JSONUtil.getByPath(node, "id").toString();

    // 如果不是子条件，则添加 variable_selector 映射
    if (!isSubcondition) {
      variableSelectorToVariableMapping.put(
          nodeId + ".#" + StrUtil.join(".", condition.getVariable_selector()) + "#",
          condition.getVariable_selector());
    }

    // 从condition的value中提取变量名，并添加到映射中
    List<String> extractVariableNames = VariablePool.extractVariableNames(condition.getValue());
    if (CollUtil.isNotEmpty(extractVariableNames)) {
      for (String variableName : extractVariableNames) {
        variableSelectorToVariableMapping.put(
            nodeId + ".#" + variableName + "#",
            StrUtil.split(variableName, "."));
      }
    }
  }


}


