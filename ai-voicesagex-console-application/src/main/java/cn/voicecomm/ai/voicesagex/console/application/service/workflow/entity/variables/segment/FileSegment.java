package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.File;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 文件段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FileSegment extends Segment {

  public FileSegment(File file) {
    super(SegmentType.FILE, file);
  }
}
