package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType;
import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: gaox
 * @date: 2025/11/13 11:32
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LoopStartNode extends BaseNode implements Serializable {

  /**
   * 节点类型
   */
  private NodeType nodeType = NodeType.LOOP_START;

}
