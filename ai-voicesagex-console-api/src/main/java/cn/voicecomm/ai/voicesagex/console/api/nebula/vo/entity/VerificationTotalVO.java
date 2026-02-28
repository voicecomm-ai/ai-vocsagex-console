package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "知识校验统计值", description = "知识校验统计值")
public class VerificationTotalVO {


  @Schema(description = "共计", example = "125")
  private Long total;


  @Schema(description = "已校验", example = "25")
  private Long verification;


  @Schema(description = "已入图", example = "25")
  private Long loadedMap;
}
