package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "节点拓展",description = "节点拓展")
public class ExpansionVO {

    @Schema(description = "图空间id",example = "23434232323" )
    @NotNull(message = "spaceId不能为空")
    private Long spaceId;


    private List<VertexsInfoVO> vertexInfoVOList;

    @Schema(description = "选择边")
    @NotNull(message = "edgeNameList不能为空")
    private List<String> edgeNameList;

    @Schema(description = "扩展方向" ,example = "0 流入  1 流出  2 双向")
    private Integer  direction;


    @Schema(description = "扩展步数" ,example = "2")
    private Integer stepNumber;


}
