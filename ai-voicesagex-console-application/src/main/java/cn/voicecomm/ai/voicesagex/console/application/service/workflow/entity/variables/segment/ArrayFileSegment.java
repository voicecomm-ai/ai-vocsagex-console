package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
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
public class ArrayFileSegment extends ArraySegment {

  public ArrayFileSegment(List<File> value) {
    super(SegmentType.ARRAY_FILE, value);
  }
}
