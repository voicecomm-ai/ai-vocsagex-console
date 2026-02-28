package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 字符串段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IntegerSegment extends Segment {

  public IntegerSegment(Integer value) {
    super(SegmentType.INTEGER, value);
  }
}
