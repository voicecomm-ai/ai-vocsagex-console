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
@Schema(name = "本体关联属性信息",description = "本体关联属性信息")
public class TagPropertyVO {


    @Schema(description = "本体名称",example = "demo")
    private String tagName;


    @Schema(description = "属性值集合")
    private List<VertexPropertiesVO> vertexPropertiesVOS;



}
