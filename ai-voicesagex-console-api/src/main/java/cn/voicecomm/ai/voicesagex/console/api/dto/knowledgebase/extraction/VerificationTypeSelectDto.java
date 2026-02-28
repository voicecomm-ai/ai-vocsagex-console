package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

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
@Schema(name = "知识校验主客体类型", description = "知识校验主客体类型")
public class VerificationTypeSelectDto {

  @Schema(description = "图空间", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  @NotNull(message = "图空间id不能为空")
  private Integer spaceId;
}
