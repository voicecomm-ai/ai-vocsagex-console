package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 段组
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SegmentGroup {

  private List<Segment> value;

  private SegmentType value_type = SegmentType.GROUP;

  /**
   * 拼接所有子段的 text 属性
   */
  public String getText() {
    if (value == null) {
      return "";
    }
    return value.stream()
        .filter(Objects::nonNull)
        .map(Segment::getText)
        .collect(Collectors.joining());
  }

  /**
   * 拼接所有子段的 log 属性
   */
  public String getLog() {
    if (value == null) {
      return "";
    }
    return value.stream()
        .filter(Objects::nonNull)
        .map(Segment::getLog)
        .collect(Collectors.joining());
  }

  /**
   * 拼接所有子段的 markdown 属性
   */
  public String getMarkdown() {
    if (value == null) {
      return "";
    }
    return value.stream()
        .filter(Objects::nonNull)
        .map(Segment::getMarkdown)
        .collect(Collectors.joining());
  }

  /**
   * 递归转换为原始对象（通常是 Map/List 结构） 对应 Python 的 to_object()
   *
   * @return List<Object>
   */
  public List<Object> toObject() {
    if (value == null) {
      return List.of();
    }
    return value.stream()
        .filter(Objects::nonNull)
        .map(Segment::toObject)
        .collect(Collectors.toList());
  }

}
