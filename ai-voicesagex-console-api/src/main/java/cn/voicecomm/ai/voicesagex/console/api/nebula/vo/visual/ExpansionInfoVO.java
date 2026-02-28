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
@Schema(name = "节点扩展参数",description = "节点扩展参数")
public class ExpansionInfoVO {



    @Schema(description = "三元组信息")
    private List<VertexEdgeVO> vertexVOList;
}
