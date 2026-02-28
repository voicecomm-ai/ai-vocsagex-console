package cn.voicecomm.ai.voicesagex.console.api.dto.application;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 应用标签
 *
 * @author wangf
 * @date 2025/5/19 下午 1:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTagDto extends BaseDto {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  private Integer id;

  /**
   * 标签名称
   */
  @Size(max = 50, message = "标签名称长度不能超过50")
  private String name;

  /**
   * 使用改标签的应用数量
   */
  private Integer tagUsedNumber = 0;

}