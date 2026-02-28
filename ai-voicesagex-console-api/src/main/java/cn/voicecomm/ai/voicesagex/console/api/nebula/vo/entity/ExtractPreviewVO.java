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
@Schema(name = "抽取预览响应", description = "抽取预览响应")
public class ExtractPreviewVO {

  @Schema(description = "主体类型", example = "tag")
  private String subjectTag;


  @Schema(description = "主体名称", example = "tagName")
  private String subjectName;


  @Schema(description = "关系/属性", example = "edge/property")
  private String edgeProperty;

  @Schema(description = "客体类型", example = "tag")
  private String objectTag;


  @Schema(description = "客体名称/属性值", example = "tagName")
  private String objectNameValue;


  @Schema(description = "客体名称/属性值", example = "tagName")
  private String chunkContent;


}
