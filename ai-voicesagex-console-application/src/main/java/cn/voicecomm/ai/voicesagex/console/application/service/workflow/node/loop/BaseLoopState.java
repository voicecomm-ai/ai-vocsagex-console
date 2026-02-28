package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.loop;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author: gaox
 * @date: 2025/11/13 9:59
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseLoopState {

  /**
   * 循环节点ID
   */
  private String loop_node_id;

  /**
   * 当前循环索引
   */
  private Integer index;

  /**
   * 输入数据
   */
  private Map<String, Object> inputs;

  /**
   * 元数据信息
   */
  private MetaData metadata;

  @Data
  @SuperBuilder
  @Accessors(chain = true)
  public static class MetaData {
  }
}
