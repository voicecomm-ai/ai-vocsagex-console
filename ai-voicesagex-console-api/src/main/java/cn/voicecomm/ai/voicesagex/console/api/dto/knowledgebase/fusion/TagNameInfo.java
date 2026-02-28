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
public class TagNameInfo {

  @Schema(description = "本体名称", example = "demo")
  private String tagName;


  @Schema(description = "属性值集合")
  private List<FusionEntityProperties> vertexPropertiesVOS;

}
