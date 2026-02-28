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
@Schema(name = "中心节点信息",description = "中心节点信息")
public class VisualmanagerVO {


    @Schema(description = "节点名称",example = "5")
    private String vertexName;



    @Schema(description = "节点Id",example = "1234434343")
    private String vertexId;

}
