package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion;

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
@Schema(name = "融合结果对象", description = "融合结果对象")
public class FusionResultVO {

  @Schema(description = "实体id", example = "1234434343")
  private String vertexId;


  @Schema(description = "实体Name", example = "demo")
  private String vertexName;


  @Schema(description = "实体信息", example = "demo")
  private List<TagNameInfo> tagNameInfo;
}
