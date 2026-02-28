
package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "实体属性",description = "实体属性")
public class VertexPropertiesVO {

    @Schema(description = "属性名称",example = "demo")
    private String propertyName;


    @Schema(description = "属性值",example = "value")
    private String propertyValue;


    @Schema(description = "属性类型")
    private String propertyType ;



    @Schema(description = "是否必填",example = "0 必填  1 非必填")
    private Integer tagRequired;


    @Schema(description = "附加设置",example = "demo")
    private String extra ;

}
