package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "确认融合信息", description = "确认融合信息")
public class AffirmFusionVO {

  @Schema(description = "图空间", example = "1719736987010")
  @NotNull(message = "spaceId 不能为空")
  private Integer spaceId;


  @Schema(description = "融合实体id集合", example = "[]")
  @NotNull(message = "融合实体id集合 不能为空")
  private List<String> vertexIds;


  @Schema(description = "融合结果对象", example = "{}")
  @NotNull(message = "融合结果对象 不能为空")
  private FusionResultVO fusionResultVO;

}
