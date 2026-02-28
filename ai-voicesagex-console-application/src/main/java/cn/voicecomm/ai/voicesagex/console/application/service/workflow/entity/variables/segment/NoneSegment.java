package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 空段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NoneSegment extends Segment {

  public NoneSegment() {
    super(SegmentType.NONE, null);
  }

  @Override
  public String getText() {
    return "";
  }

  @Override
  public String getLog() {
    return "";
  }

  @Override
  public String getMarkdown() {
    return "";
  }
}
