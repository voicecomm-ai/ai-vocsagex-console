package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "节点拓展信息",description = "节点拓展信息")
public class ExtendVO {

    @Schema(description = "可扩展边",example = "生成药品" )
    private String extendEdge;

    @Schema(description = "向下扩展边跳数",example = "10" )
    private Integer edgeExtendNumber;

}
