package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphTagEdgeProperty implements Serializable {

  @Serial
  private static final long serialVersionUID = -5043196571488638913L;

  private String space;
  /**
   * 空间名称
   **/
  private Long tagEdgeId;

  @Builder.Default
  private Integer type = 0;

  private String tagName;


  private String propertyName;

  private Long propertyId;
}
