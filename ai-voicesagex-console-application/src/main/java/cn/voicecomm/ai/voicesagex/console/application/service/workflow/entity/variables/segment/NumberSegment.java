package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数字段
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NumberSegment extends Segment {

  public NumberSegment(double value) {
    super(SegmentType.NUMBER, value);
  }

  @Override
  public String getText() {
    Object value = this.getValue();
    BigDecimal bigDecimal = new BigDecimal(value.toString());
    return bigDecimal.stripTrailingZeros().toPlainString();
  }
}
