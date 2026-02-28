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
@Schema(name = "知识校验原文信息", description = "知识校验原文信息")
public class OriginalInformationInfoVO {

  @Schema(description = "知识校验id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  @NotNull(message = "知识校验id不能为空")
  private Integer verificationId;

}
