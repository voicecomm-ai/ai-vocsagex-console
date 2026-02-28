package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "本体属性列表",description = "本体属性列表")
public class TagPropertyResultVO {


    @Schema(description = "本体名称",example = "demo")
    private String tagName;

    /**
     * 空间名称
     **/
    @Schema(description = "图空间id",example = "23434232323" )
    @NotBlank(message = "spaceId不能为空")
    private Long spaceId;


    /**
     * 空间名称
     **/
    @Schema(description = "本体ID",example = "4323232323" )
    @NotBlank(message = "本体id不能为空")
    private Long tagId;


    @Schema(description = "属性名称",example = "demo")
    private String propertyName;


    @Schema(description = "属性类型",example = "demo")
    private String propertyType ;

    @Schema(description = "是否必填",example = "0 必填  1 非必填")
    private Integer tagRequired;

    @Schema(description = "如果默认值tagRequired 为 0 必须设置",example = "test")
    private String  defaultValueAsString;

    @Schema(description = "附加设置",example = "demo")
    private String extra ;

}
