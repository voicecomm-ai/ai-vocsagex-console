package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 段基类
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public abstract class Segment {

  private SegmentType value_type;
  private Object value;

  public Segment(SegmentType value_type, Object value) {
    this.value_type = value_type;
    this.value = value;
  }

  @JsonIgnore
  public String getText() {
    return value == null ? "" : value.toString();
  }

  @JsonIgnore
  public String getLog() {
    return value == null ? "" : value.toString();
  }

  @JsonIgnore
  public String getMarkdown() {
    return value == null ? "" : value.toString();
  }

  @JsonIgnore
  public int getSize() {
    return value != null ? value.toString().getBytes().length : 0;
  }

  public Object toObject() {
    return value;
  }

  public List<String> toList() {
    if (value instanceof List<?> rawList) {

      // 检查列表中的每个元素是否是 String 类型
      if (rawList.stream().allMatch(item -> item instanceof String)) {
        @SuppressWarnings("unchecked")
        List<String> stringList = (List<String>) value;

        return stringList;
      } else {
        return new ArrayList<>();
      }
    } else {
      return new ArrayList<>();
    }
  }
}
