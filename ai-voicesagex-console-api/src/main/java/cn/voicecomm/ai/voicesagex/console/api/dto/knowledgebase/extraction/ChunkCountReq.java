package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "获取有效片段数", description = "获取有效片段数")
public class ChunkCountReq {


  @Schema(description = "文档抽取id", example = "demo")
  @NotBlank(message = "文档抽取id不能为空")
  private String documentId;
}
