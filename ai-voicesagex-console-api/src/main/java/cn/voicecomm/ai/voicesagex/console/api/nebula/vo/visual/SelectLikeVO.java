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
@Schema(name = "模糊搜索节点参数",description = "模糊搜索节点参数")
public class SelectLikeVO {


    @Schema(description = "图空间id",example = "23434232323" )
    private Long spaceId;

    @Schema(description = "节点名称",example = "5")
    private String vertexName;
}
