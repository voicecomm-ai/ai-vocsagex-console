package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "关系列表",description = "关系列表")
public class EdgeInfoVO {
    /**
     * 本体id
     */
    @Schema(description = "关系Id",example = "34323423")
    private Long edgeId;


    @Schema(description = "关系名称",example = "test")
    private String edgeName;

}
