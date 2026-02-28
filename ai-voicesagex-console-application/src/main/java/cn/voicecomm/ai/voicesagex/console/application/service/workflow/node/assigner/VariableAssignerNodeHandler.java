package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner;

import static cn.voicecomm.ai.voicesagex.console.api.constant.application.workflow.WorkflowConstants.CONVERSATION_VARIABLE_NODE_ID;
import static cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner.Constants.EMPTY_VALUE_MAPPING;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.node.run.NodeRunResult;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.InputType;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.Operation;
import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.WorkflowNodeExecutionStatus;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner.VariableAssignerNode.VariableOperationItem;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop.LoopNodeHandler;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool.VariablePool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: gaox
 * @date: 2025/11/14 16:44
 */
@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class VariableAssignerNodeHandler extends BaseNodeHandler {

  @Override
  public NodeRunResult run(VariablePool variablePool, JSONObject nodeCanvas, JSONObject graph,
      String workflowRunId, Integer appId) {
    JSONObject data = JSONUtil.getByPath(nodeCanvas, "data", null);

    VariableAssignerNode nodeData = JSONUtil.toBean(data, VariableAssignerNode.class);
    Map<String, Object> inputs = BeanUtil.beanToMap(nodeData); // 相当于model_dump()
    Map<String, Object> processData = new HashMap<>();

    try {
      for (VariableOperationItem item : nodeData.getItems()) {
        Segment segment = variablePool.get(item.getVariable_selector());

        Variable variableCast = LoopNodeHandler.segment_to_variable(segment,
            item.getVariable_selector(), null, null, null);
        // Check if operation is supported
        if (!Helpers.isOperationSupported(variableCast.getValue_type(),
            Objects.requireNonNull(Operation.getByValue(item.getOperation())))) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("操作类型不支持")
              .error_type("VariableAssignerNodeError")
              .build();
        }

        // Check if variable input is supported
        if (Objects.equals(item.getInput_type(), InputType.VARIABLE.getValue()) &&
            !Helpers.isVariableInputSupported(
                Objects.requireNonNull(Operation.getByValue(item.getOperation())))) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("输入变量类型不支持")
              .error_type("VariableAssignerNodeError")
              .build();
        }

        // Check if constant input is supported
        if (Objects.equals(item.getInput_type(), InputType.CONSTANT.getValue()) &&
            !Helpers.isConstantInputSupported(variableCast.getValue_type(),
                Objects.requireNonNull(Operation.getByValue(item.getOperation())))) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("输入变量类型不支持")
              .error_type("VariableAssignerNodeError")
              .build();
        }

        // Get value from variable pool
        if (Objects.equals(item.getInput_type(), InputType.VARIABLE.getValue()) &&
            !EnumSet.of(Operation.CLEAR, Operation.REMOVE_FIRST, Operation.REMOVE_LAST)
                .contains(Objects.requireNonNull(Operation.getByValue(item.getOperation())))
            && item.getValue() != null) {
          Segment value = variablePool.get((List<String>) item.getValue());
          if (value == null) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("未找到变量")
                .error_type("VariableAssignerNodeError")
                .build();
          }
          // Skip if value is NoneSegment
          if (value.getValue_type() == SegmentType.NONE) {
            continue;
          }
          item.setValue(value.getValue());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        // If set string / bytes / bytearray to object, try convert string to object.
        if (Objects.requireNonNull(Operation.getByValue(item.getOperation())) == Operation.SET &&
            variableCast.getValue_type() == SegmentType.OBJECT &&
            (item.getValue() instanceof String || item.getValue() instanceof byte[])) {
          try {
            Object parsedValue;

            if (item.getValue() instanceof String str) {
              parsedValue = objectMapper.readValue(str, Object.class);
            } else {
              // byte[] 的情况
              parsedValue = objectMapper.readValue((byte[]) item.getValue(), Object.class);
            }

            item.setValue(parsedValue);
          } catch (IOException e) {
            return NodeRunResult.builder()
                .status(WorkflowNodeExecutionStatus.FAILED)
                .inputs(inputs)
                .error("输入的值不正确")
                .error_type("VariableAssignerNodeError")
                .build();
          }
        }

        // Check if input value is valid
        if (!Helpers.isInputValueValid(variableCast.getValue_type(),
            Objects.requireNonNull(Operation.getByValue(item.getOperation())),
            item.getValue())) {
          return NodeRunResult.builder()
              .status(WorkflowNodeExecutionStatus.FAILED)
              .inputs(inputs)
              .error("输入的值不正确")
              .error_type("VariableAssignerNodeError")
              .build();
        }

        // ==================== Execution Part

        Object updatedValue = handleItem(variableCast,
            Objects.requireNonNull(Operation.getByValue(item.getOperation())), item.getValue());
        variableCast.setValue(updatedValue);
        variablePool.add(item.getVariable_selector(), variableCast);
      }
    } catch (RuntimeException e) {
      log.error("VariableAssignerNodeHandler.run error", e);
      return NodeRunResult.builder()
          .status(WorkflowNodeExecutionStatus.FAILED)
          .inputs(inputs)
          .error(e.getMessage())
          .error_type("VariableAssignerNodeError")
          .build();
    }

    return NodeRunResult.builder()
        .status(WorkflowNodeExecutionStatus.SUCCEEDED)
        .inputs(inputs)
        .process_data(processData)
        .outputs(new HashMap<>())
        .build();
  }

  public Object handleItem(Segment variable, Operation operation, Object value) {
    switch (operation) {
      case OVER_WRITE, SET:
        return value;

      case CLEAR:
        return EMPTY_VALUE_MAPPING.get(variable.getValue_type());

      case APPEND:
        List<Object> appendList = new ArrayList<>();
        if (variable.getValue() instanceof List<?>) {
          appendList.addAll((List<?>) variable.getValue());
        }
        appendList.add(value);
        return appendList;

      case EXTEND:
        List<Object> merged = new ArrayList<>();
        if (variable.getValue() instanceof List<?>) {
          merged.addAll((List<?>) variable.getValue());
        }
        if (value instanceof List<?>) {
          merged.addAll((List<?>) value);
        }
        return merged;

      case ADD:
        if (variable.getValue() instanceof Number varValue && value instanceof Number inputValue) {
          // 保持类型一致性
          if (varValue instanceof Double || inputValue instanceof Double) {
            return varValue.doubleValue() + inputValue.doubleValue();
          } else if (varValue instanceof Float || inputValue instanceof Float) {
            return varValue.floatValue() + inputValue.floatValue();
          } else {
            return varValue.longValue() + inputValue.longValue();
          }
        }
        break;

      case SUBTRACT:
        if (variable.getValue() instanceof Number varValue && value instanceof Number inputValue) {
          if (varValue instanceof Double || inputValue instanceof Double) {
            return varValue.doubleValue() - inputValue.doubleValue();
          } else if (varValue instanceof Float || inputValue instanceof Float) {
            return varValue.floatValue() - inputValue.floatValue();
          } else {
            return varValue.longValue() - inputValue.longValue();
          }
        }
        break;

      case MULTIPLY:
        if (variable.getValue() instanceof Number varValue && value instanceof Number inputValue) {
          if (varValue instanceof Double || inputValue instanceof Double) {
            return varValue.doubleValue() * inputValue.doubleValue();
          } else if (varValue instanceof Float || inputValue instanceof Float) {
            return varValue.floatValue() * inputValue.floatValue();
          } else {
            return varValue.longValue() * inputValue.longValue();
          }
        }
        break;

      case DIVIDE:
        if (variable.getValue() instanceof Number varValue && value instanceof Number inputValue) {
          if (varValue instanceof Double || inputValue instanceof Double) {
            return varValue.doubleValue() / inputValue.doubleValue();
          } else if (varValue instanceof Float || inputValue instanceof Float) {
            return varValue.floatValue() / inputValue.floatValue();
          } else {
            return varValue.longValue() / inputValue.longValue();
          }
        }
        break;

      case REMOVE_FIRST:
        // If array is empty, do nothing
        if (variable.getValue() == null || ((List<?>) variable.getValue()).isEmpty()) {
          return variable.getValue();
        }
        List<Object> removeFirstList = new ArrayList<>((List<?>) variable.getValue());
        removeFirstList.removeFirst();
        return removeFirstList;

      case REMOVE_LAST:
        // If array is empty, do nothing
        if (variable.getValue() == null || ((List<?>) variable.getValue()).isEmpty()) {
          return variable.getValue();
        }
        List<Object> removeLastList = new ArrayList<>((List<?>) variable.getValue());
        removeLastList.removeLast();
        return removeLastList;
    }
    return value;
  }

  @Override
  public Map<String, List<String>> _extractVariableSelectorToVariableMapping(JSONObject node,
      JSONObject graph, String nodeId) {

    // 创建类型化的NodeData从字典数据
    JSONObject jsonObject = JSONUtil.getByPath(node, "data", null);
    VariableAssignerNode typedNodeData = JSONUtil.toBean(jsonObject, VariableAssignerNode.class);

    Map<String, List<String>> varMapping = new HashMap<>();
    for (VariableOperationItem item : typedNodeData.getItems()) {
      addTargetMappingFromItem(varMapping, nodeId, item);
      sourceMappingFromItem(varMapping, nodeId, item);
    }
    return varMapping;
  }

  private static void addTargetMappingFromItem(Map<String, List<String>> mapping, String nodeId,
      VariableOperationItem item) {
    String selectorNodeId = item.getVariable_selector().getFirst();
    if (!CONVERSATION_VARIABLE_NODE_ID.equals(selectorNodeId)) {
      return;
    }
    String selectorStr = String.join(".", item.getVariable_selector());
    String key = String.format("%s.#%s#", nodeId, selectorStr);
    mapping.put(key, item.getVariable_selector());
  }

  private static void sourceMappingFromItem(Map<String, List<String>> mapping, String nodeId,
      VariableOperationItem item) {
    // Keep this in sync with the logic in run methods...
    if (!Objects.equals(item.getInput_type(), InputType.VARIABLE.getValue())) {
      return;
    }
    if (Operation.CLEAR.getValue().equals(item.getOperation())) {
      return;
    }
    Object selector = item.getValue();
    if (!(selector instanceof List)) {
      throw new RuntimeException("selector is not a list, nodeId=" + nodeId + ", item=" + item);
    }
    if (((List<?>) selector).size() < 2) {
      throw new RuntimeException("selector too short, nodeId=" + nodeId + ", item=" + item);
    }
    String selectorStr = String.join(".", (List<String>) selector);
    String key = String.format("%s.#%s#", nodeId, selectorStr);
    mapping.put(key, (List<String>) selector);
  }

}
