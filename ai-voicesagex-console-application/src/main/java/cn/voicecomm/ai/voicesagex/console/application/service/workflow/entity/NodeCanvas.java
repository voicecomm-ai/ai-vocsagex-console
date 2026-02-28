package cn.voicecomm.ai.voicesagex.console.application.service.workflow.entity;

import cn.voicecomm.ai.voicesagex.console.application.service.workflow.node.base.BaseNode;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 节点的对象模型类。
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCanvas implements Serializable {

  /**
   * 节点ID。
   */
  private String id;

  /**
   * 节点类型。
   */
  private String type;

  /**
   * 节点位置信息。
   */
  private Position position;

  /**
   * 目标连接位置。
   */
  private String targetPosition;

  /**
   * 源连接位置。
   */
  private String sourcePosition;

  /**
   * 前一个节点id
   */
  private String previousNodeId;

  /**
   * 绝对位置信息。
   */
  private Position positionAbsolute;

  /**
   * 节点宽度。
   */
  private double width;

  /**
   * 节点高度。
   */
  private double height;

  /**
   * 是否被选中。
   */
  private boolean selected;

  /**
   * 节点数据
   */
  private BaseNode data;

  /**
   * 内部类，表示节点的位置信息。
   */
  @Data
  @Accessors(chain = true)
  public static class Position {

    /**
     * X坐标。
     */
    private double x;

    /**
     * Y坐标。
     */
    private double y;
  }
}



