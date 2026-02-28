package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 变量相关类定义
 *
 * @author gaox
 */
public class Variables {


  /**
   * 字符串变量 继承 Variable 类并添加 StringSegment 的功能
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class StringVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 StringSegment 特有的属性
    private SegmentType value_type = SegmentType.STRING;
    private String value;
  }

  /**
   * 浮点数变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class FloatVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 FloatSegment 特有的属性
    private SegmentType value_type = SegmentType.FLOAT;
    private Double value;
  }

  /**
   * 整数变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class IntegerVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 IntegerSegment 特有的属性
    private SegmentType value_type = SegmentType.INTEGER;
    private Integer value;
  }

  /**
   * 对象变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class ObjectVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 ObjectSegment 特有的属性
    private SegmentType value_type = SegmentType.OBJECT;
    private Map<String, Object> value;
  }

  /**
   * 数组变量基类
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public abstract static class ArrayVariable extends Variable {
    // 从 Variable 继承 id, name, description, selector 属性
    // 数组类型变量的基类
  }

  /**
   * 任意类型数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ArrayAnyVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayAnySegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_ANY;
    private List<Object> value;
  }

  /**
   * 字符串数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class ArrayStringVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayStringSegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_STRING;
    private List<String> value;
  }

  /**
   * 数值数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor

  @Accessors(chain = true)
  public static class ArrayNumberVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayNumberSegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_NUMBER;
    private List<Number> value;
  }

  /**
   * 对象数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ArrayObjectVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayObjectSegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_OBJECT;
    private List<Map<String, Object>> value;
  }

//  /**
//   * 密钥变量
//   */
//  @EqualsAndHashCode(callSuper = true)
//  @Data
//  @AllArgsConstructor
//  @Accessors(chain = true)
//  public static class SecretVariable extends StringVariable {
//
//    // 从 StringVariable 继承所有属性
//    // 重写 value_type 为 SECRET
//    private SegmentType value_type = SegmentType.SECRET;
//
//  }

  /**
   * 空值变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class NoneVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 NoneSegment 特有的属性
    private SegmentType value_type = SegmentType.NONE;
    private Object value = null;
  }

  /**
   * 文件变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class FileVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 FileSegment 特有的属性
    private SegmentType value_type = SegmentType.FILE;
    private File value;
  }

  /**
   * 布尔变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class BooleanVariable extends Variable {

    // 从 Variable 继承 id, name, description, selector 属性
    // 添加 BooleanSegment 特有的属性
    private SegmentType value_type = SegmentType.BOOLEAN;
    private Boolean value;
  }

  /**
   * 文件数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ArrayFileVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayFileSegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_FILE;
    private List<File> value;
  }

  /**
   * 布尔数组变量
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  @AllArgsConstructor
  @Accessors(chain = true)
  public static class ArrayBooleanVariable extends ArrayVariable {

    // 从 ArrayVariable 继承 Variable 的所有属性
    // 添加 ArrayBooleanSegment 特有的属性
    private SegmentType value_type = SegmentType.ARRAY_BOOLEAN;
    private List<Boolean> value;
  }
}
