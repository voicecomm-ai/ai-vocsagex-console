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
@Schema(name = "抽取文档配置信息", description = "抽取文档配置信息")
public class DocumentConfigInfoVO {

  @Schema(description = "模型", example = "deepseek")
  private String extractEntityModel;


  @Schema(description = "要求", example = "要求")

  private String entityPromptRequire;


  @Schema(description = "实体其他要求", example = "要求")
  private String entityPromptOtherRequire;


  @Schema(description = "实体输出", example = "输出")
  private String entityPromptOutput;


  @Schema(description = "模型", example = "deepseek")
  private String extractRelationModel;


  @Schema(description = "要求", example = "要求")
  private String relationPromptRequire;


  @Schema(description = "关系其他要求", example = "要求")
  private String relationPromptOtherRequire;


  @Schema(description = "关系输出", example = "输出")
  private String relationPromptOutput;


}
