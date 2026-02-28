package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "执行抽取任务Dto", description = "执行抽取任务Dto")
public class KnowledgeDoExtractionDto implements Serializable {


  @Schema(description = "documentId", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  @NotNull(message = "documentId不能为空")
  private Integer documentId;

}
