package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.documentextractor;

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
public class DocumentExtractorNode extends BaseNode implements Serializable {

  /**
   * 查询变量选择器。
   */
  private List<String> variable_selector;

}
