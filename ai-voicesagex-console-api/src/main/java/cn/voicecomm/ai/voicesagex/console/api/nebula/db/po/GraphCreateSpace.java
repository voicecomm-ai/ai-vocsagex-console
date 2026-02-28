package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author adminst
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphCreateSpace implements Serializable {


  @Serial
  private static final long serialVersionUID = 2042293613802918411L;
  /**
   * 空间名称
   **/

  private String space;

  private Integer labelId;


  /**
   * 分片数量
   **/
  @Builder.Default
  private Integer partitionNum = 0;

  /**
   * 分片数量
   **/
  @Builder.Default
  private Integer replicaFactor = 0;
  /**
   * 类型
   **/
  @Builder.Default
  private String fixedType = "FIXED_STRING(32)";

  private String comment;


  private String model;


  private Integer type;

  @Builder.Default
  private String size = "";

}
