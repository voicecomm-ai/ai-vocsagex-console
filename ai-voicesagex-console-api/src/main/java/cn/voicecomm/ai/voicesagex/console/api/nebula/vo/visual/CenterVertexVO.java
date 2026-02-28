package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "中心节点设置",description = "中心节点设置")
public class CenterVertexVO {

    @Schema(description = "图空间id",example = "23434232323" )
    @NotNull(message = "spaceId不能为空")
    private Long spaceId;

    @Schema(description = "节点Id",example = "1234434343")
    private String vertexId;

    @Schema(description = "节名称点",example = "1234434343")
    private String vertexName;



}
