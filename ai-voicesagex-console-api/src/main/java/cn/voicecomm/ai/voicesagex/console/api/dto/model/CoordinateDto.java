package cn.voicecomm.ai.voicesagex.console.api.dto.model;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 坐标Dto
 *
 * @author ryc
 * @date 2025-07-31 16:22:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CoordinateDto implements Serializable {


  @Serial
  private static final long serialVersionUID = -8518321306533075373L;

  /**
   * 横坐标
   */
  private Long xaxis;

  /**
   * 纵坐标
   */
  private Double yaxis;
}