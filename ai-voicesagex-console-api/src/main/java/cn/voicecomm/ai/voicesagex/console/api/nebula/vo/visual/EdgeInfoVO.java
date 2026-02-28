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
@Schema(name = "边属性信息",description = "边属性信息")
public class EdgeInfoVO {


    @Schema(description = "边名称",example = "5")
    private String edgeName;

    @Schema(description = "属性集合",example = "5")
    private List<EdgePropertyVO> edgePropertyVOList;
}
