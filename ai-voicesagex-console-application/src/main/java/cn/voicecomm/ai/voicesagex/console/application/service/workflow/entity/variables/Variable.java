package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.Segment;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity.variables.segment.SegmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 变量基类
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Variable extends Segment {

  private String id = UUID.randomUUID().toString();
  private String name;
  private String description = "";
  private List<String> selector = new ArrayList<>();


  public Variable(SegmentType valueType, Object value, String name) {
    super(valueType, value);
    this.name = name;
  }
}
