package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

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
@Schema(name = "获取关系列表详细信息",description = "获取关系列表详细信息")
public class TotalEdgeVO {

    @Schema(description = "关系列表",example = "demo")
    private List<EdgeInfosVO> edgeInfosVOList;

    @Schema(description = "标识",example = "false")
    private  boolean identity ;

}
