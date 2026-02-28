package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数组段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ArrayBooleanSegment extends ArraySegment {

  public ArrayBooleanSegment(List<Boolean> value) {
    super(SegmentType.ARRAY_BOOLEAN, value);
  }
}
