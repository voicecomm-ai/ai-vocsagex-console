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
@Schema(name = "新增实体",description = "新增实体")
public class EntityPropertiesVO {

    @Schema(description = "属性名称",example = "demo")
    private String propertyName;


    @Schema(description = "属性值",example = "value")
    private String propertyValue;



    @Schema(description = "属性类型")
    private String propertyType ;


    @Schema(description = "是否必填",example = "0 必填  1 非必填")
    private Integer tagRequired;

    @Schema(description = "如果默认值tagRequired 为 0 必须设置",example = "test")
    private String  defaultValueAsString;

    @Schema(description = "附加设置",example = "demo")
    private String extra ;


}
