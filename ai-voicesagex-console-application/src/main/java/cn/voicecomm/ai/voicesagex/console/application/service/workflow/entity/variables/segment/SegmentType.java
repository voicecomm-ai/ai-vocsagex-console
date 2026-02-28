package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 段类型枚举
 */
@Getter
@AllArgsConstructor
public enum SegmentType {
  NUMBER("number"), INTEGER("integer"), FLOAT("float"), STRING("string"), OBJECT("object"), SECRET(
      "secret"),

  FILE("file"), BOOLEAN("boolean"),

  ARRAY_ANY("array[any]"), ARRAY_STRING("array[string]"), ARRAY_NUMBER(
      "array[number]"), ARRAY_OBJECT("array[object]"), ARRAY_FILE("array[file]"), ARRAY_BOOLEAN(
      "array[boolean]"),

  NONE("none"), GROUP("group");

  private final String value;
  /**
   * 段类型到段类的映射关系
   */
  public static final Map<SegmentType, Class<? extends Segment>> SEGMENT_TYPE_TO_CLASS_MAP = new HashMap<>();

  static {
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.NUMBER, NumberSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.INTEGER, IntegerSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.FLOAT, FloatSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.STRING, StringSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.OBJECT, ObjectSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.FILE, FileSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.BOOLEAN, BooleanSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_ANY, ArrayAnySegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_STRING, ArrayStringSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_NUMBER, ArrayNumberSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_OBJECT, ArrayObjectSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_FILE, ArrayFileSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.ARRAY_BOOLEAN, ArrayBooleanSegment.class);
    SEGMENT_TYPE_TO_CLASS_MAP.put(SegmentType.NONE, NoneSegment.class);
  }
  /**
   * 数组元素类型映射关系 ARRAY_ANY没有对应的元素类型
   */
  private static final Map<SegmentType, SegmentType> _ARRAY_ELEMENT_TYPES_MAPPING = new HashMap<>();

  /**
   * 数组类型集合
   */
  private static final Set<SegmentType> _ARRAY_TYPES = new HashSet<>();

  /**
   * 数值类型集合 包含NUMBER、INTEGER、FLOAT三种数值类型
   */
  private static final Set<SegmentType> _NUMERICAL_TYPES = new HashSet<>();

  static {
    // 初始化数组元素类型映射关系
    _ARRAY_ELEMENT_TYPES_MAPPING.put(ARRAY_STRING, STRING);
    _ARRAY_ELEMENT_TYPES_MAPPING.put(ARRAY_NUMBER, NUMBER);
    _ARRAY_ELEMENT_TYPES_MAPPING.put(ARRAY_OBJECT, OBJECT);
    _ARRAY_ELEMENT_TYPES_MAPPING.put(ARRAY_FILE, FILE);
    _ARRAY_ELEMENT_TYPES_MAPPING.put(ARRAY_BOOLEAN, BOOLEAN);

    // 初始化数组类型集合
    _ARRAY_TYPES.addAll(_ARRAY_ELEMENT_TYPES_MAPPING.keySet());
    _ARRAY_TYPES.add(ARRAY_ANY);

    _NUMERICAL_TYPES.addAll(Arrays.asList(NUMBER, INTEGER, FLOAT));
  }

  public static SegmentType fromValue(String value) {
    for (SegmentType type : SegmentType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No SegmentType with value: " + value);
  }

  /**
   * 尝试根据值的Java类型推断[SegmentType](file://D:\code\dify\api\core\variables\types.py#L25-L202)
   *
   * <p>如果无法为给定值确定适当的[SegmentType](file://D:\code\dify\api\core\variables\types.py#L25-L202)，则返回`null`。
   * 例如，当输入是类型为[object](file://D:\code\dify\web\app\components\workflow\types.ts#L277-L277)的通用Java对象时，可能会发生这种情况。</p>
   *
   * <ol>
   *   <li>如果值是List类型，遍历元素并推断每个元素的类型</li>
   *   <li>处理数值类型的特殊情况，如数值混合时返回ARRAY_NUMBER</li>
   *   <li>根据元素类型匹配返回对应的数组类型</li>
   *   <li>处理基本类型：null、Boolean、Integer、Double、String、Map、File等</li>
   * </ol>
   *
   * @param value 需要推断类型的值
   * @return 推断出的SegmentType，如果无法推断则返回null
   * @author gaox
   */
  public static SegmentType infer_segment_type(Object value) {
    // 处理List类型
    if (value instanceof List<?> listValue) {
      Set<SegmentType> elemTypes = new HashSet<>();

      // 遍历列表元素并推断类型
      for (Object item : listValue) {
        SegmentType segmentType = infer_segment_type(item);
        if (segmentType == null) {
          return null;
        }
        elemTypes.add(segmentType);
      }

      // 处理元素类型集合
      if (elemTypes.size() != 1) {
        if (_NUMERICAL_TYPES.containsAll(elemTypes)) {
          return SegmentType.ARRAY_NUMBER;
        }
        return SegmentType.ARRAY_ANY;
      } else if (elemTypes.stream().allMatch(SegmentType::is_array_type)) {
        return SegmentType.ARRAY_ANY;
      }

      SegmentType elementType = elemTypes.iterator().next();
      return switch (elementType) {
        case STRING -> SegmentType.ARRAY_STRING;
        case NUMBER, INTEGER, FLOAT -> SegmentType.ARRAY_NUMBER;
        case OBJECT -> SegmentType.ARRAY_OBJECT;
        case FILE -> SegmentType.ARRAY_FILE;
        case NONE -> SegmentType.ARRAY_ANY;
        case BOOLEAN -> SegmentType.ARRAY_BOOLEAN;
        default -> throw new IllegalArgumentException("不支持的值: " + value);
      };
    }

    // 处理null值
    if (value == null) {
      return SegmentType.NONE;
    }

    // 重要：对[Boolean](file://D:\code\dify\web\app\components\workflow\panel\chat-variable-panel\type.ts#L3-L3)的检查必须在对`Integer`的检查之前，
    // 因为在Java类型系统中，[Boolean](file://D:\code\dify\web\app\components\workflow\panel\chat-variable-panel\type.ts#L3-L3)不是`Integer`的子类，
    // 但在Python中bool是int的子类，这里保持一致性
    if (value instanceof Boolean) {
      return SegmentType.BOOLEAN;
    } else if (value instanceof Integer) {
      return SegmentType.INTEGER;
    } else if (value instanceof Double || value instanceof Float) {
      return SegmentType.FLOAT;
    } else if (value instanceof String) {
      return SegmentType.STRING;
    } else if (value instanceof Map) {
      return SegmentType.OBJECT;
    } else if (value instanceof File) {
      return SegmentType.FILE;
    } else {
      return null;
    }
  }

  /**
   * 检查是否为数组类型
   *
   * @return 如果是数组类型返回true，否则返回false
   * @author gaox
   */
  public boolean is_array_type() {
    return _ARRAY_TYPES.contains(this);
  }
}
