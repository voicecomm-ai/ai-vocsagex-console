package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

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
@Schema(name = "修改校验状态", description = "修改校验状态")
public class StatusVerificationClear {

  @Schema(description = "知识校验id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  private List<Integer> verificationIds;
}
