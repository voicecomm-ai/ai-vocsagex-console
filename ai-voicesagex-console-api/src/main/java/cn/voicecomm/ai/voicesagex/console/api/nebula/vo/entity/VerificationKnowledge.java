package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationKnowledge {

  @Schema(description = "知识校验id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  private Integer verificationId;
  @Schema(description = "schema状态", example = "0 符合，1不符合")
  private Integer status;
}
