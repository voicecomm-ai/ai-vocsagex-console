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
@Schema(name = "节点信息",description = "节点信息")
public class VertexTagInfoVO {


    @Schema(description = "节点名称",example = "5")
    private String vertexName;



    @Schema(description = "节点Id",example = "1234434343")
    private String vertexId;



    @Schema(description = "本体关联属性")
    private List<TagPropertyVO> tagName;



}
