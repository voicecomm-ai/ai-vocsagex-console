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
@Schema(name = "多个点集合",description = "多个点集合")
public class VertexsInfoVO {


    @Schema(description = "节点Id",example = "1234434343")
    @NotNull(message = "vertexId不能为空")
    private String vertexId;
}
