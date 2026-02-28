package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.start;

import cn.voicecomm.ai.voicesagex.console.api.dto.application.workflow.Variable;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class StartNode extends BaseNode implements Serializable {


  /**
   * 变量
   */
  private List<Variable> variables;

}
