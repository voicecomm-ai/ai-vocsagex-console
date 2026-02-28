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
@Schema(name = "原文chunk信息", description = "原文chunk信息")
public class ChunkInfoVO {

  @Schema(description = "文档内容", example = "xxxxxx")
  private String chunkContent;

  @Schema(description = "chunkId", example = "xxxxxx")
  private Integer chunkId;


}
