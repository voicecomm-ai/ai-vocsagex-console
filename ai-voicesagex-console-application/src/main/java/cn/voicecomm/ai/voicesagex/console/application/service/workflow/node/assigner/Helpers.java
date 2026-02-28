package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.Operation;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: gaox
 * @date: 2025/11/19 14:02
 */
public class Helpers {

  public static boolean isOperationSupported(SegmentType variableType, Operation operation) {
    return switch (operation) {
      case OVER_WRITE, CLEAR -> true;
      case SET -> Set.of(
          SegmentType.OBJECT,
          SegmentType.STRING,
          SegmentType.NUMBER,
          SegmentType.INTEGER,
          SegmentType.FLOAT,
          SegmentType.BOOLEAN
      ).contains(variableType);
      case ADD, SUBTRACT, MULTIPLY, DIVIDE ->
        // Only number variable can be added, subtracted, multiplied or divided
          Set.of(SegmentType.NUMBER, SegmentType.INTEGER, SegmentType.FLOAT).contains(variableType);
      case APPEND, EXTEND, REMOVE_FIRST, REMOVE_LAST ->
        // Only array variable can be appended or extended
        // Only array variable can have elements removed
          variableType.is_array_type();
    };
  }

  public static boolean isVariableInputSupported(Operation operation) {
    return !Set.of(Operation.SET, Operation.ADD, Operation.SUBTRACT, Operation.MULTIPLY,
            Operation.DIVIDE)
        .contains(operation);
  }

  public static boolean isConstantInputSupported(SegmentType variableType, Operation operation) {
    return switch (variableType) {
      case STRING, OBJECT, BOOLEAN ->
          Set.of(Operation.OVER_WRITE, Operation.SET).contains(operation);
      case NUMBER, INTEGER, FLOAT -> Set.of(
          Operation.OVER_WRITE,
          Operation.SET,
          Operation.ADD,
          Operation.SUBTRACT,
          Operation.MULTIPLY,
          Operation.DIVIDE
      ).contains(operation);
      default -> false;
    };
  }

  /**
   * 检查输入值是否有效
   * <ol>
   *   <li>对于清除和移除操作，始终返回true</li>
   *   <li>根据变量类型和操作类型验证值的有效性</li>
   *   <li>对数字类型除法操作检查除零情况</li>
   *   <li>对数组操作检查元素类型匹配</li>
   * </ol>
   *
   * @param variableType 变量类型
   * @param operation    操作类型
   * @param value        输入值
   * @return 输入值是否有效
   */
  public static boolean isInputValueValid(SegmentType variableType, Operation operation,
      Object value) {
    // 处理清除和移除操作
    if (Set.of(Operation.CLEAR, Operation.REMOVE_FIRST, Operation.REMOVE_LAST)
        .contains(operation)) {
      return true;
    }

    // 根据变量类型和操作类型进行判断
    return switch (variableType) {
      case STRING -> value instanceof String;
      case BOOLEAN -> value instanceof Boolean;
      case NUMBER, INTEGER, FLOAT -> {
        if (!(value instanceof Number)) {
          yield false;
        }
        yield operation != Operation.DIVIDE || ((Number) value).doubleValue() != 0.0;
      }
      case OBJECT -> value instanceof Map;
      default -> {
        if (variableType.is_array_type()) {
          yield handleArrayValidation(variableType, operation, value);
        }
        yield false;
        // 处理数组类型
      }
    };
  }

  private static boolean handleArrayValidation(SegmentType variableType, Operation operation,
      Object value) {
    // 根据操作类型进行不同的验证
    if (operation == Operation.APPEND) {
      // 处理APPEND操作
      return switch (variableType) {
        case ARRAY_ANY ->
            value instanceof String || value instanceof Number || value instanceof Map;
        case ARRAY_STRING -> value instanceof String;
        case ARRAY_NUMBER -> value instanceof Number;
        case ARRAY_OBJECT -> value instanceof Map;
        case ARRAY_BOOLEAN -> value instanceof Boolean;
        default -> false;
      };
    } else if (operation == Operation.EXTEND || operation == Operation.OVER_WRITE) {
      // 处理EXTEND和OVER_WRITE操作
      if (!(value instanceof List<?> listValue)) {
        return false;
      }

      return switch (variableType) {
        case ARRAY_ANY -> listValue.stream()
            .allMatch(
                item -> item instanceof String || item instanceof Number || item instanceof Map);
        case ARRAY_STRING -> listValue.stream().allMatch(item -> item instanceof String);
        case ARRAY_NUMBER -> listValue.stream().allMatch(item -> item instanceof Number);
        case ARRAY_OBJECT -> listValue.stream().allMatch(item -> item instanceof Map);
        case ARRAY_BOOLEAN -> listValue.stream().allMatch(item -> item instanceof Boolean);
        default -> false;
      };
    }

    return false;
  }

}
