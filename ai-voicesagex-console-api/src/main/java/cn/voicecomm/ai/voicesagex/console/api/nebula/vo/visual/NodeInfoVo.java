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
@Schema(name = "节点数信息",description = "节点数信息")
public class NodeInfoVo {


    @Schema(description = "空间包含节点数",example = "10")
    private  int nodeNum;



    @Schema(description = "空间包含边数",example = "5")
    private  int edgeNum;

}
