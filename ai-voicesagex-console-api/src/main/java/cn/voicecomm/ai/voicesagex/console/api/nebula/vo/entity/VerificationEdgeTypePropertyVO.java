package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "关系属性选择", description = "关系属性选择")
public class VerificationEdgeTypePropertyVO {

  @Schema(description = "图空间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  @NotNull(message = "图空间id不能为空")
  private Integer spaceId;


  @Schema(description = "主体类型", example = "demo")
  private String subjectType;
}
