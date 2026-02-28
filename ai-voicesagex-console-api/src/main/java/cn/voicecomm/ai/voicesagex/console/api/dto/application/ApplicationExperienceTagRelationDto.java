package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 应用与标签关联
 *
 * @author wangf
 * @date 2025/5/19 下午 1:42
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationExperienceTagRelationDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  private Integer id;

  /**
   * 标签id
   */
  private Integer tagId;

  /**
   * 应用id
   */
  private Integer experienceApplicationId;
}