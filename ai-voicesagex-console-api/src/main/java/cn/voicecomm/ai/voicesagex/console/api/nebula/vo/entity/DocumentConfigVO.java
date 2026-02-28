package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;


import cn.voicecomm.ai.voicesagex.console.api.nebula.constant.SpaceConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "抽取文档配置", description = "抽取文档配置")
public class DocumentConfigVO {


  @Schema(description = "模型", example = "deepseek")
  @NotBlank(message = "实体模型不能为空")
  private String extractEntityModel = "Extractor";


  @Schema(description = "要求", example = "要求")
  @NotBlank(message = "实体要求不能为空")
  private String entityPromptRequire = SpaceConstant.ENTITY_PROMPT;


  @Schema(description = "实体其他要求", example = "要求")
  private String entityPromptOtherRequire;


  @Schema(description = "实体输出", example = "输出")
  private String entityPromptOutput;


  @Schema(description = "模型", example = "deepseek")
  @NotBlank(message = "关系模型不能为空")
  private String extractRelationModel = "Extractor";


  @Schema(description = "要求", example = "要求")
  @NotBlank(message = "关系要求不能为空")
  private String relationPromptRequire = SpaceConstant.RELATION_PROMPT;


  @Schema(description = "关系其他要求", example = "要求")
  private String relationPromptOtherRequire;


  @Schema(description = "关系输出", example = "输出")
  private String relationPromptOutput;

  @Schema(description = "文档id", example = "文档id")
  @NotNull(message = "文档id不能为空")
  private Integer documentId;

}
