package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "子图点信息",description = "子图点信息")
public class SubVertexVO {

    @Schema(description = "节点名称",example = "5")
    @NotBlank(message = "vertexName不能为空")
    private String vertexName;


    @Schema(description = "节点本体名称",example = "5")
    @NotBlank(message = "vertexName不能为空")
    private List<String> vertexTagName;
}
