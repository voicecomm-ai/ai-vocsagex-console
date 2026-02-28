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
@Schema(name = "获取知识校验列表", description = "获取知识校验列表")
public class VerificationInfoDto {

  @Schema(description = "文档抽取id", example = "demo")
  @NotNull(message = "文档抽取id不能为空")
  private Integer documentId;


  @Schema(description = "pageSize", example = "10")
  private int pageSize;

  @Schema(description = "current", example = "1")
  private int current;


  @Schema(description = "type", example = "true")
  private boolean type;


  public boolean isType() {
    return type;
  }

  public void setType(boolean type) {
    this.type = type;
  }
}
