package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import cn.voicecomm.ai.voicesagex.console.api.dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author xiaoyan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(name = "本体列表", description = "本体列表")
public class TagInfosVO extends BaseDto {

  @Serial
  private static final long serialVersionUID = 4781317149258642234L;
  @Schema(description = "本体名称", example = "demo")
  private String tagName;

  /**
   * 空间名称
   **/
  @Schema(description = "图空间id", example = "23434232323")
  private Long spaceId;


  /**
   * 空间名称
   **/
  @Schema(description = "本体ID", example = "4323232323")
  private Long tagId;


  @Schema(description = "包含实体数量", example = "20")
  private int tagNumber;


}
