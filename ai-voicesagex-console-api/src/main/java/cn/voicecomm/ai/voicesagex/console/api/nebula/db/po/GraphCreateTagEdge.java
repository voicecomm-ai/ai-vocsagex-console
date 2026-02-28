package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphCreateTagEdge {

  /**
   * 空间名称
   **/
  private String space;

  private Long spaceId;

  /**
   * tag /edge name
   */
  private String tagName;

  private String index;

  /**
   * 属性 集合
   */
  private String properties;

  @Builder.Default
  private boolean filedTime = false;


  /**
   * 过期时间
   */
  private String ttlCol;

  @Builder.Default
  private Integer ttlDuration = 0;

  /**
   * 0 tag  1 edge
   */
  @Builder.Default
  private Integer type = 0;

  private TagType tagTypes;


  public boolean getIsFiledTime() {
    return filedTime;
  }
}
