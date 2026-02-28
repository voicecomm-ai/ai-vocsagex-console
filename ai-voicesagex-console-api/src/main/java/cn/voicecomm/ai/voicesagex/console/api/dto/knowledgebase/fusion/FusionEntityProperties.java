package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.fusion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "确认融合实体信息", description = "确认融合实体信息")
public class FusionEntityProperties {

  @Schema(description = "属性名称", example = "demo")
  private String propertyName;


  @Schema(description = "属性值", example = "value")
  private String propertyValue;


  @Schema(description = "属性类型")
  private String propertyType;
}


