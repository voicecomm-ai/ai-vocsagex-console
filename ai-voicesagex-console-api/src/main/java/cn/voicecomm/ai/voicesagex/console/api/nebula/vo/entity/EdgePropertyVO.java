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
@Schema(name = "本体属性列表",description = "本体属性列表")
public class EdgePropertyVO {

    @Schema(description = "主体名称",example = "demo")
    private String subjectName;


    @Schema(description = "客体名称",example = "demo")
    private String objectName;


    @Schema(description = "主体类型",example = "demo")
    private String subjectTagName;


    @Schema(description = "客体类型",example = "demo")
    private String objectTagName;

    @Schema(description = "edge名称",example = "demo")
    private String edgeName;

    /**
     * 空间名称
     **/
    @Schema(description = "图空间id",example = "23434232323" )
    private Long spaceId;

    /**
     * 空间名称
     **/
    @Schema(description = "本体ID",example = "4323232323" )
    private Long edgeId;

    @Schema(description = "pageSize",example = "10")
    private  int pageSize;

    @Schema(description = "current",example = "1")
    private  int current;

}
