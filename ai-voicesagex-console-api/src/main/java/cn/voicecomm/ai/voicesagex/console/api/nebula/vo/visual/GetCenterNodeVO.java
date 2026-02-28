package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "获取中心节点",description = "获取中心节点")
public class GetCenterNodeVO {

    @Schema(description = "图空间id",example = "23434232323" )
    @NotNull(message = "spaceId不能为空")
    private Long spaceId;
}
