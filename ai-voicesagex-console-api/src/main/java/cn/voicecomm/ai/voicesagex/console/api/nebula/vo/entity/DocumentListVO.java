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
@Schema(name = "文档列表", description = "文档列表")
public class DocumentListVO {


  @Schema(description = "任务id", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
  @NotNull(message = "任务id不能为空")
  private Integer jobId;


  @Schema(description = "pageSize", example = "10")
  private int pageSize;

  @Schema(description = "current", example = "1")
  private int current;

}
