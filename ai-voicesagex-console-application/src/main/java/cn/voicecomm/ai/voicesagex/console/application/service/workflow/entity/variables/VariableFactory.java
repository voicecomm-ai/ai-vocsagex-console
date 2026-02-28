package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayAnyVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayBooleanVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayFileVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayNumberVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayObjectVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ArrayStringVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.BooleanVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.FileVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.FloatVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.IntegerVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.NoneVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.ObjectVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.Variables.StringVariable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayAnySegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayBooleanSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayFileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayNumberSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayObjectSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ArrayStringSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.BooleanSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.FileSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.FloatSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.IntegerSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.NoneSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.NumberSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.ObjectSegment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.StringSegment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 变量工厂类
 */
public class VariableFactory {

  /**
   * 段类型到段类的映射关系 包含基本类型和数组类型的所有映射
   */
  public static final Map<SegmentType, Class<? extends Segment>> _segment_factory = new HashMap<>();

  public static final Map<Class<? extends Segment>, Class<? extends Variable>> SEGMENT_TO_VARIABLE_MAP = new HashMap<>();

  static {
    // 初始化段到变量的映射关系
    SEGMENT_TO_VARIABLE_MAP.put(ArrayAnySegment.class, ArrayAnyVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ArrayBooleanSegment.class, ArrayBooleanVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ArrayFileSegment.class, ArrayFileVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ArrayNumberSegment.class, ArrayNumberVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ArrayObjectSegment.class, ArrayObjectVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ArrayStringSegment.class, ArrayStringVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(BooleanSegment.class, BooleanVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(FileSegment.class, FileVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(FloatSegment.class, FloatVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(IntegerSegment.class, IntegerVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(NoneSegment.class, NoneVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(ObjectSegment.class, ObjectVariable.class);
    SEGMENT_TO_VARIABLE_MAP.put(StringSegment.class, StringVariable.class);
  }

  public static Segment segmentToVariable(Segment segment, List<String> selector) {
    // 实现段到变量的转换逻辑
    return segment;
  }

  static {
    // 基本类型映射
    _segment_factory.put(SegmentType.NONE, NoneSegment.class);
    _segment_factory.put(SegmentType.STRING, StringSegment.class);
    _segment_factory.put(SegmentType.INTEGER, IntegerSegment.class);
    _segment_factory.put(SegmentType.FLOAT, FloatSegment.class);
    _segment_factory.put(SegmentType.FILE, FileSegment.class);
    _segment_factory.put(SegmentType.BOOLEAN, BooleanSegment.class);
    _segment_factory.put(SegmentType.OBJECT, ObjectSegment.class);

    // 数组类型映射
    _segment_factory.put(SegmentType.ARRAY_ANY, ArrayAnySegment.class);
    _segment_factory.put(SegmentType.ARRAY_STRING, ArrayStringSegment.class);
    _segment_factory.put(SegmentType.ARRAY_NUMBER, ArrayNumberSegment.class);
    _segment_factory.put(SegmentType.ARRAY_OBJECT, ArrayObjectSegment.class);
    _segment_factory.put(SegmentType.ARRAY_FILE, ArrayFileSegment.class);
    _segment_factory.put(SegmentType.ARRAY_BOOLEAN, ArrayBooleanSegment.class);
  }

  public static Segment buildSegment(Object value) {
    // 实现从值构建段的逻辑
    return switch (value) {
      case null -> new NoneSegment();
      case String s -> new StringSegment(s);
      case Number number -> new NumberSegment(number.doubleValue());
      case Map<?, ?> map -> new ObjectSegment((Map<String, Object>) map);
      case List<?> list -> {
        if (list.isEmpty() || !(list.getFirst() instanceof File)) {
          yield new ArrayAnySegment(list);
        }
        yield new ArrayFileSegment((List<File>) list);
      }
      case File file -> new FileSegment(file);
      default -> new StringSegment(value.toString());
    };
  }

  /**
   * 根据指定的段类型构建段对象
   *
   * <p>此方法通过显式类型检查创建段对象，相比标准的build_segment方法提供更严格的类型验证。</p>
   *
   * <ol>
   *   <li>处理null值情况，如果值为null但段类型不是NONE则抛出类型不匹配异常</li>
   *   <li>处理空列表特殊情况，对于数组类型返回对应的空数组段</li>
   *   <li>推断值的段类型并进行类型兼容性检查</li>
   *   <li>如果类型匹配则创建对应的段对象</li>
   *   <li>处理NUMBER类型与INTEGER/FLOAT的兼容性</li>
   * </ol>
   *
   * @param segment_type 期望的段类型
   * @param value        要转换为段的值
   * @return 对应类型的段对象
   * @author gaox
   */
  @SuppressWarnings("unchecked")
  public static Segment build_segment_with_type(SegmentType segment_type, Object value) {
    // 处理null值
    if (value == null) {
      if (segment_type == SegmentType.NONE) {
        return new NoneSegment();
      } else {
        throw new RuntimeException("类型不匹配: 期望 " + segment_type + ", 但得到 null");
      }
    }

    // 处理空列表特殊情况

    if (value instanceof List && ((List<?>) value).isEmpty()) {
      return switch (segment_type) {
        case ARRAY_ANY -> new ArrayAnySegment((List<?>) value);
        case ARRAY_STRING -> new ArrayStringSegment((List<String>) value);
        case ARRAY_BOOLEAN -> new ArrayBooleanSegment((List<Boolean>) value);
        case ARRAY_NUMBER -> new ArrayNumberSegment((List<Number>) value);
        case ARRAY_OBJECT -> new ArrayObjectSegment((List<Object>) value);
        case ARRAY_FILE -> new ArrayFileSegment((List<File>) value);
        default ->
            throw new RuntimeException("类型不匹配: 期望 " + segment_type + ", 但得到空列表");
      };
    }

    // 推断值的段类型
    SegmentType inferred_type = SegmentType.infer_segment_type(value);

    // 类型兼容性检查
    if (inferred_type == null) {
      throw new RuntimeException(
          "类型不匹配: 期望 " + segment_type + ", 但得到python对象, 类型=" + value.getClass()
              + ", 值=" + value
      );
    }

    if (inferred_type == segment_type) {
      Class<? extends Segment> segment_class = _segment_factory.get(segment_type);
      if (segment_class != null) {
        try {
          return createSegmentByType(segment_type, value);
        } catch (Exception e) {
          throw new RuntimeException("创建段对象失败: " + e.getMessage());
        }
      }
    } else if (segment_type == SegmentType.NUMBER &&
        (inferred_type == SegmentType.INTEGER || inferred_type == SegmentType.FLOAT)) {
      Class<? extends Segment> segment_class = _segment_factory.get(inferred_type);
      if (segment_class != null) {
        try {
          return createSegmentByType(inferred_type, value);
        } catch (Exception e) {
          throw new RuntimeException("创建段对象失败: " + e.getMessage());
        }
      }
    } else {
      throw new RuntimeException(
          "类型不匹配: 期望 " + segment_type + ", 但得到 " + inferred_type + ", 值=" + value);
    }

    throw new RuntimeException("无法创建段对象: 未知错误");
  }

  /**
   * 根据段类型和值直接创建段对象
   *
   * @param segment_type 段类型
   * @param value        值
   * @return 对应的段对象
   */
  private static Segment createSegmentByType(SegmentType segment_type, Object value) {
    switch (segment_type) {
      case NONE:
        return new NoneSegment();
      case STRING:
        return new StringSegment((String) value);
      case INTEGER:
        if (value instanceof Number) {
          return new IntegerSegment(((Number) value).intValue());
        }
        break;
      case FLOAT:
        if (value instanceof Number) {
          return new FloatSegment(((Number) value).floatValue());
        }
        break;
      case FILE:
        return new FileSegment((File) value);
      case BOOLEAN:
        return new BooleanSegment((Boolean) value);
      case OBJECT:
        return new ObjectSegment((Map<String, Object>) value);
      case ARRAY_ANY:
        return new ArrayAnySegment((List<?>) value);
      case ARRAY_STRING:
        return new ArrayStringSegment((List<String>) value);
      case ARRAY_BOOLEAN:
        return new ArrayBooleanSegment((List<Boolean>) value);
      case ARRAY_NUMBER:
        return new ArrayNumberSegment((List<Number>) value);
      case ARRAY_OBJECT:
        return new ArrayObjectSegment((List<Object>) value);
      case ARRAY_FILE:
        return new ArrayFileSegment((List<File>) value);
      default:
        throw new RuntimeException("不支持的段类型: " + segment_type);
    }
    throw new RuntimeException("无法创建段对象: 类型=" + segment_type + ", 值=" + value);
  }
}
