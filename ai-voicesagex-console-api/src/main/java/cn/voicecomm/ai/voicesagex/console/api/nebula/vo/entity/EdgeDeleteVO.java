package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Ralation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "删除关系记录", description = "删除关系记录")
public class EdgeDeleteVO {

  /**
   * 图空间id
   */
  @Schema(description = "图空间id", example = "34323423")
  private Long spaceId;


  @Schema(description = "关系id对应关系", example = "34323423")
  private List<Ralation> ralationVOS;

}
