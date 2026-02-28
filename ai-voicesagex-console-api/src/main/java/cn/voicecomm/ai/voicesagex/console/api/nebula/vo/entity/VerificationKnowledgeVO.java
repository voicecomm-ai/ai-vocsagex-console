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
@Schema(name = "知识校验", description = "知识校验")
public class VerificationKnowledgeVO {

  @Schema(description = "知识校验id", example = "1719736987010")
  private List<VerificationKnowledge> verificationKnowledges;


  @Schema(description = "图空间", example = "1719736987010")
  private Integer spaceId;

}
