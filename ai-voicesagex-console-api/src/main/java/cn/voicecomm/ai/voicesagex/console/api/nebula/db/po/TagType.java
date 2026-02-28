package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author adminst
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TagType<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = -9082011368694437413L;
  @Schema(description = "属性名称", example = "name")
  private String propertyName;
  @Schema(description = "属性类型", example = "string")
  private String propertyType;
  // 是否必填
  @Schema(description = "是否必填", example = "0 必填  1 非必填")
  private Integer tagRequired;

  @Schema(description = "如果默认值tagRequired 为 0 必须设置", example = "test")
  private T defaultValueAsString;

  @Schema(description = "额外信息", example = "图片")
  private String extra;


  public String getDefaultValue() {
    return defaultValueAsString == null ? null : String.valueOf(defaultValueAsString);
  }


  public void setDefaultValue(String defaultValue) {
    this.defaultValueAsString = (T) String.valueOf(defaultValue);
  }
}
