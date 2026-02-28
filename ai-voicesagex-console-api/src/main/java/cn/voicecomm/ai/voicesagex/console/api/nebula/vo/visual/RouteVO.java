package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "路线信息",description = "路线信息")
public class RouteVO {

    @Schema(description = "三元组信息")
    private List<VertexEdgeVO> vertexVOList;


    @Schema(description = "跳数")
    private Integer hopCount;

}
