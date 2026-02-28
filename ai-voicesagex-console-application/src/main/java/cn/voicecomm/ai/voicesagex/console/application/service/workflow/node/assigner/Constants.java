package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.assigner;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: gaox
 * @date: 2025/11/19 14:34
 */
public class Constants {

  /**
   * 空值映射表
   * <p>
   * Note: This mapping is duplicated with
   * [get_zero_value](file://D:\code\dify\api\core\workflow\nodes\variable_assigner\v1\node.py#L147-L167).
   * Consider refactoring to avoid redundancy.
   */
  public static final Map<SegmentType, Object> EMPTY_VALUE_MAPPING = new HashMap<>();

  static {
    EMPTY_VALUE_MAPPING.put(SegmentType.STRING, "");
    EMPTY_VALUE_MAPPING.put(SegmentType.NUMBER, 0);
    EMPTY_VALUE_MAPPING.put(SegmentType.BOOLEAN, false);
    EMPTY_VALUE_MAPPING.put(SegmentType.OBJECT, new HashMap<>());
    EMPTY_VALUE_MAPPING.put(SegmentType.ARRAY_ANY, new ArrayList<>());
    EMPTY_VALUE_MAPPING.put(SegmentType.ARRAY_STRING, new ArrayList<>());
    EMPTY_VALUE_MAPPING.put(SegmentType.ARRAY_NUMBER, new ArrayList<>());
    EMPTY_VALUE_MAPPING.put(SegmentType.ARRAY_OBJECT, new ArrayList<>());
    EMPTY_VALUE_MAPPING.put(SegmentType.ARRAY_BOOLEAN, new ArrayList<>());
  }
}
