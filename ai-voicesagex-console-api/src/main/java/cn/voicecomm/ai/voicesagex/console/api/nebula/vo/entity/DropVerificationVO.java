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
@Schema(name = "删除知识校验", description = "删除知识校验")
public class DropVerificationVO {


  @Schema(description = "知识校验id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  private List<Integer> verificationId;


}
