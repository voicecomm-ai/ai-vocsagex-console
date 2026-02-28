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
public class FloatSegment extends Segment {

  public FloatSegment(Float value) {
    super(SegmentType.FLOAT, value);
  }
}
