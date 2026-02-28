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
@Schema(name = "获取抽取文档配置", description = "获取抽取文档配置")
public class ConfigInfoDto {


  @Schema(description = "id", example = "id")
  @NotNull(message = "文档id不能为空")
  private Integer documentId;

}
