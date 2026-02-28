package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

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
@Schema(name = "编辑文档校验", description = "编辑文档校验")
public class InsertVerificationVO {

  @Schema(description = "图空间id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1719736987010")
  @NotNull(message = "图空间id不能为空")
  private Integer spaceId;

  @Schema(description = "chunkId", example = "xxxxxx")
  private Integer chunkId;

  @Schema(description = "文档抽取id", example = "demo")
  @NotNull(message = "文档抽取id不能为空")
  private Integer documentId;


  @Schema(description = "主体类型", example = "tag")
  @NotBlank(message = "主体类型不能为空")
  private String subjectTag;


  @Schema(description = "主体名称", example = "tagName")
  @NotBlank(message = "主体名称不能为空")
  private String subjectName;


  @Schema(description = "关系/属性", example = "edge/property")
  @NotBlank(message = "关系/属性不能为空")
  private String edgeProperty;

  @Schema(description = "客体类型", example = "tag")
  private String objectTag;


  @Schema(description = "客体名称/属性值", example = "tagName")
  @NotBlank(message = "客体名称/属性值不能为空")
  private String objectNameValue;

  @Schema(description = "类型", example = "0 关系 1属性")
  @NotNull(message = "类型不能为空")
  private Integer type;

  @Schema(description = "属性类型", example = "INT")
  private String propertyType;


}
