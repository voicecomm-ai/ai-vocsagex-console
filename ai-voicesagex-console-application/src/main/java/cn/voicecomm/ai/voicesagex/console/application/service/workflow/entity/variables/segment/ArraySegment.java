package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数组段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ArraySegment extends Segment {

  public ArraySegment(SegmentType segmentType, List<?> value) {
    super(segmentType, value);
  }

  @Override
  public String getMarkdown() {
    List<String> value = new ArrayList<>();
    for (Object item : (List<?>) this.getValue()) {
      value.add(item.toString());
    }
    return StrUtil.join("\n", value);
  }
}
