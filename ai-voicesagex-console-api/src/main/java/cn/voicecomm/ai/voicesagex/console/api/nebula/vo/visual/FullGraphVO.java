package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "全图查询参数",description = "全图查询参数")
public class FullGraphVO {
    @Schema(description = "图空间id",example = "23434232323" )
    private Long spaceId;

    @Schema(description = "起点id",example = "4323232323" )
    @NotNull(message = "startId不能为空")
    private String startId;


    @Schema(description = "勾选边",example = "勾选边" )
    private List<String> edges;



}
