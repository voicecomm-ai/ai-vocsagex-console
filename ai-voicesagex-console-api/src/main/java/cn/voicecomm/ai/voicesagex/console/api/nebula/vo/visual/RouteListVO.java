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
@Schema(name = "路线列表",description = "路线列表")
public class RouteListVO {


    @Schema(description = "路线列表")
    private List<RouteVO> routeVOList;

    @Schema(description = "路线总数",example = "5")
    private  int total;

    @Schema(description = "全部图结构")
    List<VertexEdgeVO> vertexEdge;
}
