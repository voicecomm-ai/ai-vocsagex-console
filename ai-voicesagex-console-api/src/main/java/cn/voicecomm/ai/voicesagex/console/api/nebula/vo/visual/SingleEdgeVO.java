package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

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
@Schema(name = "单条边属性获取",description = "单条边属性获取")
public class  SingleEdgeVO {


    @Schema(description = "图空间id",example = "23434232323" )
    @NotNull(message = "spaceId不能为空")
    private Long spaceId;

    @Schema(description = "边名称",example = "5")
    @NotBlank(message = "edgeName不能为空")
    private String edgeName;


    @Schema(description = "主体节点Id",example = "1234434343")
    @NotNull(message = "subjectId不能为空")
    private String subjectId;


    @Schema(description = "客体节点Id",example = "1234434343")
    @NotNull(message = "objectId不能为空")
    private String objectId;



    @Schema(description = "客体节点Id",example = "1234434343")
    @NotNull(message = "rank不能为空")
    private Long rank;

}
