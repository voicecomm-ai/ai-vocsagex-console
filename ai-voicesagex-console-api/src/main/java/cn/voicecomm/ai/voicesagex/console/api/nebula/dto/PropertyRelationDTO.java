package cn.voicecomm.ai.voicesagex.console.api.nebula.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRelationDTO {


    @Schema(description = "属性类型")
    private String propertyType ;


    @Schema(description = "附加设置",example = "demo")
    private String extra ;
}
