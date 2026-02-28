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
@Schema(name = "知识校验列表", description = "知识校验列表")
public class VerificationListVO {


  @Schema(description = "知识校验id", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
  private Integer verificationId;


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


  @Schema(description = "状态", example = "0初始状态，1表示已校验,2已入图")
  private Integer status;


  @Schema(description = "类型", example = "0 pdf抽取，1手动新增")
  private Integer type;

}
