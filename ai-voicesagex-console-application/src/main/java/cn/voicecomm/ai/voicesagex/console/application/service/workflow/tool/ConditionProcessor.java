package cn.voicecomm.ai.voicesagex.console.application.service.workflow.tool;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.FileAttribute;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.FileManager;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.ifelse.IfElseNode.Condition;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * 条件处理器类 用于处理工作流中的条件判断逻辑
 */
@Slf4j
public class ConditionProcessor {

  /**
   * 处理条件列表
   *
   * @param variablePool 变量池，用于获取条件中引用的变量值
   * @param conditions   条件列表
   * @param operator     逻辑操作符，"and" 或 "or"
   * @return 处理结果封装对象
   */
  public ProcessConditionsResult processConditions(VariablePool variablePool,
      List<Condition> conditions, String operator) {

    // 初始化输入条件列表和组结果列表
    List<Map<String, Object>> inputConditions = new ArrayList<>();
    List<Boolean> groupResults = new ArrayList<>();

    // 遍历所有条件
    for (int i = 0; i < conditions.size(); i++) {
      Condition condition = conditions.get(i);

      // 从变量池中获取条件引用的变量
      Segment variable = variablePool.get(condition.getVariable_selector());
      log.info("----> 处理第{}个条件: variable_selector={}, comparison_operator={}, value={}",
          i + 1, condition.getVariable_selector(), condition.getComparison_operator(),
          condition.getValue());
      boolean result;
      // 检查是否为数组文件段且比较操作符为特定值
      if (variable instanceof ArrayFileSegment && (
          condition.getComparison_operator().equals("contains")
              || condition.getComparison_operator().equals("not contains")
              || condition.getComparison_operator().equals("all of"))) {

        log.info("检测到数组文件段和特定比较操作符，处理子条件");
        // 检查子条件
        if (condition.getSub_variable_condition() == null) {
          log.error("--< 子变量缺失，抛出异常");
          throw new IllegalArgumentException("子变量缺失");
        }

        // 处理子条件
        result = processSubConditions((ArrayFileSegment) variable,
            condition.getSub_variable_condition().getConditions(),
            condition.getSub_variable_condition().getLogical_operator(), variablePool,
            condition.getComparison_operator());
        log.info("----< 第{}个条件[数组文件]评估完成，结果: {}", i + 1, result);
      }
      // 检查是否存在性检查操作符
      else if (condition.getComparison_operator().equals("exists")
          || condition.getComparison_operator().equals("not exists")) {

        log.info("检测到存在性检查操作符: {}", condition.getComparison_operator());
        result = evaluateCondition(condition.getComparison_operator(),
            Optional.ofNullable(variable).map(Segment::getValue).orElse(null), null);
        log.info("----< 第{}个条件[存在性检查]评估完成，结果: {}", i + 1, result);
      }
      // 处理其他所有情况
      else {
        Object actualValue = Optional.ofNullable(variable).map(Segment::getValue).orElse(null);
        String expectedValue = condition.getValue();

        // 如果期望值是字符串，则进行模板转换
        if (expectedValue != null) {
          expectedValue = variablePool.convertTemplate(expectedValue).getText();
        }

        // 记录输入条件信息
        Map<String, Object> inputCondition = new LinkedHashMap<>();
        inputCondition.put("actual_value", actualValue);
        inputCondition.put("expected_value", expectedValue);
        inputCondition.put("comparison_operator", condition.getComparison_operator());
        inputConditions.add(inputCondition);

        // 评估条件
        result = evaluateCondition(condition.getComparison_operator(), actualValue, expectedValue);
        log.info("----< 第{}个条件评估完成，结果: {}", i + 1, result);
      }

      // 将结果添加到组结果列表
      groupResults.add(result);

      // 如果只有一个条件，直接返回当前result
      if (conditions.size() == 1) {
        log.info("仅有一个条件，直接返回结果: {}", result);
        return new ProcessConditionsResult(inputConditions, groupResults, result);
      }

      // 实现逻辑条件的短路求值
      // 对于AND操作，如果有一个条件为false，则整个结果为false
      // 对于OR操作，如果有一个条件为true，则整个结果为true
      if ((operator.equals("and") && !result) || (operator.equals("or") && result)) {
        log.info("根据操作符 {} 和当前结果 {} 触发短路求值，最终结果: {}", operator, result, result);
        return new ProcessConditionsResult(inputConditions, groupResults, result);
      }
    }

    // 根据操作符计算最终结果
    // AND操作需要所有条件都为true，OR操作只需要有一个条件为true
    boolean finalResult =
        operator.equals("and") ? groupResults.stream().allMatch(Boolean::booleanValue)
            : groupResults.stream().anyMatch(Boolean::booleanValue);

    log.info("所有条件处理完成，操作符: {}, 最终结果: {}", operator, finalResult);

    return new ProcessConditionsResult(inputConditions, groupResults, finalResult);
  }

  /**
   * 评估条件
   *
   * @param operator      比较操作符
   * @param value         实际值
   * @param expectedValue 期望值
   * @return 评估结果
   */
  private boolean evaluateCondition(String operator, Object value, Object expectedValue) {
    switch (operator) {
      case "contains":
        return assertContains(value, expectedValue);
      case "not contains":
        return assertNotContains(value, expectedValue);
      case "start with":
        return assertStartWith(value, expectedValue);
      case "end with":
        return assertEndWith(value, expectedValue);
      case "is":
        return assertIs(value, expectedValue);
      case "is not":
        return assertIsNot(value, expectedValue);
      case "empty":
        return assertEmpty(value);
      case "not empty":
        return assertNotEmpty(value);
      case "=":
        return assertEqual(value, expectedValue);
      case "≠":
        return assertNotEqual(value, expectedValue);
      case ">":
        return assertGreaterThan(value, expectedValue);
      case "<":
        return assertLessThan(value, expectedValue);
      case "≥":
        return assertGreaterThanOrEqual(value, expectedValue);
      case "≤":
        return assertLessThanOrEqual(value, expectedValue);
      case "null":
        return assertNull(value);
      case "not null":
        return assertNotNull(value);
      case "in":
        return assertIn(value, expectedValue);
      case "not in":
        return assertNotIn(value, expectedValue);
      case "all of":
        if (expectedValue instanceof List) {
          return assertAllOf(value, (List<?>) expectedValue);
        }
        break;
      case "exists":
        return assertExists(value);
      case "not exists":
        return assertNotExists(value);
    }

    log.error("不支持的操作符: {}", operator);
    throw new IllegalArgumentException("不支持的操作符: " + operator);
  }

  /**
   * 断言包含关系
   */
  private boolean assertContains(Object value, Object expected) {
    log.info("断言[contains]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[contains]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof String || value instanceof List)) {
      log.error("在断言[contains]中，实际值类型无效：应为字符串或数组");
      throw new IllegalArgumentException("在断言[contains]中，实际值类型无效：应为字符串或数组");
    }

    if (value instanceof List<?> list) {
      if (CollUtil.isEmpty(list)) {
        log.info("列表为空，返回false");
        return false;
      }
      Class<?> aClass = list.getFirst().getClass();
      boolean result = list.contains(convertToType(expected, aClass));
      log.info("[contains]列表包含检查结果: {}", result);
      return result;
    } else {
      boolean result = ((String) value).contains((String) expected);
      log.info("[contains]字符串包含检查结果: {}", result);
      return result;
    }
  }

  /**
   * 断言不包含关系
   */
  private boolean assertNotContains(Object value, Object expected) {
    log.info("断言[not contains]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[not contains]中，值为null，返回true");
      return true;
    }

    if (!(value instanceof String || value instanceof List)) {
      log.error("在断言[not contains]中，实际值类型无效：应为字符串或数组");
      throw new IllegalArgumentException("在断言[not contains]中，实际值类型无效：应为字符串或数组");
    }

    if (value instanceof List<?> list) {
      if (CollUtil.isEmpty(list)) {
        log.info("[not contains]列表为空，返回true");
        return true;
      }
      Class<?> aClass = list.getFirst().getClass();
      boolean result = !list.contains(convertToType(expected, aClass));
      log.info("[not contains]列表不包含检查结果: {}", result);
      return result;
    } else {
      boolean result = !((String) value).contains((String) expected);
      log.info("[not contains]字符串不包含检查结果: {}", result);
      return result;
    }
  }

  /**
   * 断言以某字符串开头
   */
  private boolean assertStartWith(Object value, Object expected) {
    log.info("断言[start with]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[start with]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof String)) {
      log.error("在断言[start with]中，实际值类型无效：应为字符串");
      throw new IllegalArgumentException("实际值类型无效：应为字符串");
    }

    boolean result = ((String) value).startsWith((String) expected);
    log.info("[start with]开头匹配检查结果: {}", result);
    return result;
  }

  /**
   * 断言以某字符串结尾
   */
  private boolean assertEndWith(Object value, Object expected) {
    log.info("断言[end with]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[end with]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof String)) {
      log.error("在断言[end with]中，实际值类型无效：应为字符串");
      throw new IllegalArgumentException("实际值类型无效：应为字符串");
    }

    boolean result = ((String) value).endsWith((String) expected);
    log.info("[end with]结尾匹配检查结果: {}", result);
    return result;
  }

  /**
   * 断言相等关系
   */
  private boolean assertIs(Object value, Object expected) {
    log.info("断言[is]: value={}, expectedValue={}", value, expected);

    if (value == null || expected == null) {
      log.info("在断言[is]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof String) || !(expected instanceof String)) {
      log.error("在断言[is]中，实际值类型无效：应为字符串");
      throw new IllegalArgumentException("实际值类型无效：应为字符串");
    }

    boolean result = value.toString().equals(expected.toString());
    log.info("[is]相等检查结果: {}", result);
    return result;
  }

  /**
   * 断言不相等关系
   */
  private boolean assertIsNot(Object value, Object expected) {
    log.info("断言[is not]: value={}, expectedValue={}", value, expected);

    if (value == null || expected == null) {
      log.info("在断言[is not]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof String) || !(expected instanceof String)) {
      log.error("在断言[is not]中，实际值类型无效：应为字符串");
      throw new IllegalArgumentException("实际值类型无效：应为字符串");
    }

    boolean result = !value.toString().equals(expected.toString());
    log.info("[is not]不相等检查结果: {}", result);
    return result;
  }

  /**
   * 断言为空
   */
  private boolean assertEmpty(Object value) {
    log.info("断言[empty]: value={}", value);

    boolean result = switch (value) {
      case null -> true;
      case String s -> s.isEmpty();
      case List<?> list -> list.isEmpty();
      default -> false;
    };

    log.info("[empty]为空检查结果: {}", result);
    return result;
  }

  /**
   * 断言不为空
   */
  private boolean assertNotEmpty(Object value) {
    log.info("断言[not empty]: value={}", value);
    boolean result = !assertEmpty(value);
    log.info("[not empty]不为空检查结果: {}", result);
    return result;
  }

  /**
   * 断言数值相等
   */
  private boolean assertEqual(Object value, Object expected) {
    log.info("断言[=]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[=]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof Number)) {
      log.error("在断言[=]中，实际值类型无效：应为数值");
      throw new IllegalArgumentException("实际值类型无效：应为数值");
    }
    if (StrUtil.isEmpty(expected.toString())) {
      log.error("在断言[=]中，期望值为空");
      throw new IllegalArgumentException("期望值为空");
    }

    boolean result = switch (value) {
      case Integer i -> i.equals(Integer.valueOf(expected.toString()));
      case Double v -> v.equals(Double.valueOf(expected.toString()));
      case Float v -> v.equals(Float.valueOf(expected.toString()));
      case Long l -> l.equals(Long.valueOf(expected.toString()));
      default -> false;
    };

    log.info("[=]数值相等检查结果: {}", result);
    return result;
  }

  /**
   * 断言数值不相等
   */
  private boolean assertNotEqual(Object value, Object expected) {
    log.info("断言[≠]: value={}, expectedValue={}", value, expected);
    boolean result = !assertEqual(value, expected);
    log.info("[≠]数值不相等检查结果: {}", result);
    return result;
  }

  /**
   * 断言大于关系
   */
  private boolean assertGreaterThan(Object value, Object expected) {
    log.info("断言[>]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[>]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof Number)) {
      log.error("在断言[>]中，实际值类型无效：应为数值");
      throw new IllegalArgumentException("实际值类型无效：应为数值");
    }

    double val = ((Number) value).doubleValue();
    double exp = Double.parseDouble(expected.toString());
    boolean result = val > exp;

    log.info("[>]大于检查结果: {} > {} = {}", val, exp, result);
    return result;
  }

  /**
   * 断言小于关系
   */
  private boolean assertLessThan(Object value, Object expected) {
    log.info("断言[<]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[<]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof Number)) {
      log.error("在断言[<]中，实际值类型无效：应为数值");
      throw new IllegalArgumentException("实际值类型无效：应为数值");
    }

    double val = ((Number) value).doubleValue();
    double exp = Double.parseDouble(expected.toString());
    boolean result = val < exp;

    log.info("[<]小于检查结果: {} < {} = {}", val, exp, result);
    return result;
  }

  /**
   * 断言大于等于关系
   */
  private boolean assertGreaterThanOrEqual(Object value, Object expected) {
    log.info("断言[≥]: value={}, expectedValue={}", value, expected);
    if (value == null) {
      log.info("在断言[≥]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof Number)) {
      log.error("在断言[≥]中，实际值类型无效：应为数值");
      throw new IllegalArgumentException("实际值类型无效：应为数值");
    }

    double val = ((Number) value).doubleValue();
    double exp = Double.parseDouble(expected.toString());
    boolean result = val >= exp;

    log.info("[≥]大于等于检查结果: {} >= {} = {}", val, exp, result);
    return result;
  }

  /**
   * 断言小于等于关系
   */
  private boolean assertLessThanOrEqual(Object value, Object expected) {
    log.info("断言[≤]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[≤]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof Number)) {
      log.error("在断言[≤]中，实际值类型无效：应为数值");
      throw new IllegalArgumentException("实际值类型无效：应为数值");
    }

    double val = ((Number) value).doubleValue();
    double exp = Double.parseDouble(expected.toString());
    boolean result = val <= exp;

    log.info("[≤]小于等于检查结果: {} <= {} = {}", val, exp, result);
    return result;
  }

  /**
   * 断言为空值
   */
  private boolean assertNull(Object value) {
    log.info("断言[null]: value={}", value);
    boolean result = value == null;
    log.info("[null]空值检查结果: {}", result);
    return result;
  }

  /**
   * 断言不为空值
   */
  private boolean assertNotNull(Object value) {
    log.info("断言[not null]: value={}", value);
    boolean result = value != null;
    log.info("[not null]非空值检查结果: {}", result);
    return result;
  }

  /**
   * 断言在列表中
   */
  private boolean assertIn(Object value, Object expected) {
    log.info("断言[in]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[in]中，值为null，返回false");
      return false;
    }

    if (!(expected instanceof List)) {
      log.error("在断言[in]中，期望值类型无效：应为数组");
      throw new IllegalArgumentException("期望值类型无效：应为数组");
    }

    boolean result = ((List<?>) expected).contains(value);
    log.info("[in]在列表中检查结果: {}", result);
    return result;
  }

  /**
   * 断言不在列表中
   */
  private boolean assertNotIn(Object value, Object expected) {
    log.info("断言[not in]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("值为null，返回true");
      return true;
    }

    if (!(expected instanceof List)) {
      log.error("在断言[not in]中，期望值类型无效：应为数组");
      throw new IllegalArgumentException("期望值类型无效：应为数组");
    }

    boolean result = !((List<?>) expected).contains(value);
    log.info("[not in]不在列表中检查结果: {}", result);
    return result;
  }

  /**
   * 断言全部是
   */
  private boolean assertAllOf(Object value, List<?> expected) {
    log.info("断言[all of]: value={}, expectedValue={}", value, expected);

    if (value == null) {
      log.info("在断言[all of]中，值为null，返回false");
      return false;
    }

    if (!(value instanceof List<?> valueList)) {
      log.error("在断言[all of]中，实际值类型无效：应为数组");
      throw new IllegalArgumentException("实际值类型无效：应为数组");
    }

    boolean result = new HashSet<Object>(valueList).containsAll(expected);
    log.info("[all of]全部是检查结果: {}", result);
    return result;
  }

  /**
   * 断言存在
   */
  private boolean assertExists(Object value) {
    log.info("断言[exists]: value={}", value);
    boolean result = value != null;
    log.info("[exists]存在检查结果: {}", result);
    return result;
  }

  /**
   * 断言不存在
   */
  private boolean assertNotExists(Object value) {
    log.info("断言[not exists]: value={}", value);
    boolean result = value == null;
    log.info("[not exists]不存在检查结果: {}", result);
    return result;
  }

  /**
   * 处理子条件
   *
   * @param variable                          数组文件段变量
   * @param subConditions                     子条件列表
   * @param operator                          逻辑操作符
   * @param parentConditionComparisonOperator 父条件比较操作符
   * @return 处理结果
   */
  private boolean processSubConditions(ArrayFileSegment variable, List<Condition> subConditions,
      String operator, VariablePool variablePool, String parentConditionComparisonOperator) {

    log.info("处理子条件: 数量={}, 操作符={}", subConditions.size(), operator);

    List<File> files =
        variable.getValue() instanceof List<?> ? ((List<?>) variable.getValue()).stream()
            .filter(File.class::isInstance).map(File.class::cast).toList() : List.of();
    List<Boolean> groupResults = new ArrayList<>();

    for (int i = 0; i < subConditions.size(); i++) {
      Condition condition = subConditions.get(i);
      FileAttribute key = FileAttribute.fromValue(condition.getKey());
      List<Object> values = files.stream().map(file -> FileManager.getAttr(file, key)).toList();

      Object expectedValue = variablePool.convertTemplate(condition.getValue()).getText();
      log.info("处理第{}个子条件: key={}, values数量={}, 期望值：{}", i + 1, condition.getKey(),
          values.size(), expectedValue);

      // 特殊处理文件扩展名
      if (key == FileAttribute.EXTENSION) {
        if (expectedValue == null) {
          log.error("当键为FileAttribute.EXTENSION时，扩展名缺失");
          throw new IllegalArgumentException("当键为FileAttribute.EXTENSION时，扩展名缺失");
        }
        List<Object> normalized_values = new ArrayList<>();
        for (Object value : values) {
          if (value instanceof String) {
            if (!value.toString().startsWith(".")) {
              value = "." + value;
            }
          }
          normalized_values.add(value);
        }
        values = normalized_values;
      }

      // 评估子条件
      List<Boolean> subGroupResults = values.stream()
          .map(value -> evaluateCondition(condition.getComparison_operator(), value, expectedValue))
          .toList();

      boolean result;
      if ("all of".equals(parentConditionComparisonOperator)) {
        result = subGroupResults.stream().allMatch(Boolean::booleanValue);
      } else if ("not contains".equals(parentConditionComparisonOperator)) {
        // "not contains"时, 所有子条件都不能满足
        result = subGroupResults.stream().noneMatch(Boolean::booleanValue);
      } else {
        // "contains"时，只需满足一个
        result = subGroupResults.stream().anyMatch(Boolean::booleanValue);
      }

      log.info("第{}个子条件评估完成，父级比较符：{}，结果: {}，最终结果: {}", i + 1,
          parentConditionComparisonOperator, subGroupResults, result);

      groupResults.add(result);
    }

    // 根据操作符返回最终结果
    boolean finalResult =
        operator.equals("and") ? groupResults.stream().allMatch(Boolean::booleanValue)
            : groupResults.stream().anyMatch(Boolean::booleanValue);

    log.info("所有子条件处理完成，操作符: {}, 最终结果: {}", operator, finalResult);
    return finalResult;
  }

  /**
   * 将对象转换为指定类型
   *
   * @param value      需要转换的对象
   * @param targetType 目标类型
   * @return 转换后的对象
   */
  private Object convertToType(Object value, Class<?> targetType) {
    log.info("转换类型: value={}, targetType={}", value, targetType);

    if (value == null || targetType == null) {
      log.info("值或目标类型为null，直接返回");
      return value;
    }

    // 如果类型已经匹配，直接返回
    if (targetType.isInstance(value)) {
      log.info("类型已匹配，无需转换");
      return value;
    }

    // 根据目标类型进行转换
    try {
      Object result;
      if (targetType == String.class) {
        result = String.valueOf(value);
      } else if (targetType == Integer.class || targetType == int.class) {
        result = Integer.valueOf(value.toString());
      } else if (targetType == Long.class || targetType == long.class) {
        result = Long.valueOf(value.toString());
      } else if (targetType == Double.class || targetType == double.class) {
        result = Double.valueOf(value.toString());
      } else if (targetType == Float.class || targetType == float.class) {
        result = Float.valueOf(value.toString());
      } else if (targetType == Boolean.class || targetType == boolean.class) {
        result = Boolean.valueOf(value.toString());
      } else if (targetType == BigDecimal.class) {
        result = new BigDecimal(value.toString());
      } else {
        // 其他类型直接使用toString
        result = value.toString();
      }

      log.info("类型转换成功，结果: {}", result);
      return result;
    } catch (NumberFormatException e) {
      log.error("无法将值 {} 转换为类型 {}", value, targetType.getSimpleName(), e);
      throw new IllegalArgumentException(
          "无法将值 " + value + " 转换为类型 " + targetType.getSimpleName());
    }
  }
}