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
@Schema(name = "知识校验入图", description = "知识校验入图")
public class KnowledgeEntryMapVO {

  @Schema(description = "文档抽取id", example = "demo")
  @NotNull(message = "文档抽取id不能为空")
  private Integer documentId;


  @Schema(description = "图空间", requiredMode = Schema.RequiredMode.REQUIRED, example = "test")
  @NotNull(message = "图空间id不能为空")
  private Integer spaceId;
}
