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
@Schema(name = "属性名称类型", description = "属性名称类型")
public class PropertyInfoVO {

  @Schema(description = "property name", example = "age")
  private String propertyName;

  @Schema(description = "property type", example = "int32")
  private String propertyType;

  @Schema(description = "附加设置", example = "demo")
  private String extra;


  @Schema(description = "如果默认值tagRequired 为 0 必须设置", example = "test")
  private String defaultValueAsString;
}
