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
@Schema(name = "获取原文信息", description = "获取原文信息")
public class OriginalInformationVO {

  @Schema(description = "校验数据", example = "demo")
  private List<Integer> verificationIds;


  @Schema(description = "文档名称", example = "demo")
  private String documentName;


  @Schema(description = "上文信息", example = "xxxx")
  private ChunkInfoVO aboveInfo;


  @Schema(description = "原文信息", example = "xxxx")
  private ChunkInfoVO originalInfo;


  @Schema(description = "下文信息", example = "xxxx")
  private ChunkInfoVO belowInfo;


}
