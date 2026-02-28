package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 对象段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ObjectSegment extends Segment {

  public ObjectSegment(Map<String, Object> value) {
    super(SegmentType.OBJECT, value);
  }
}
