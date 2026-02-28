package cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * @author: gaox
 * @date: 2025/7/30 11:02
 */
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseNode implements Serializable {

  /**
   * 描述
   */
  private String desc;
  /**
   * 是否选中
   */
  private Boolean selected;
  /**
   * 标题
   */
  private String title;
  /**
   * 类型
   *
   * @see cn.voicecomm.ai.voicesagex.console.api.enums.application.workflow.NodeType
   */
  private String type;

  /**
   * 是否在循环中
   */
  private Boolean isInLoop;

  /**
   * 循环ID
   */
  private String loop_id;
  /**
   * 是否在迭代中
   */
  private Boolean isInIteration;

  /**
   * 迭代ID
   */
  private String iteration_id;

}
