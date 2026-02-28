package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "模糊搜索节点信息",description = "模糊搜索节点信息")
public class VertexInfoVO {


    @Schema(description = "节点名称",example = "5")
    @NotBlank(message = "vertexName不能为空")
    private String vertexName;


    @Schema(description = "节点Id",example = "1234434343")
    @NotNull(message = "vertexId不能为空")
    private String vertexId;

    @Schema(description = "节点本体名称",example = "5")
    @NotBlank(message = "vertexName不能为空")
    private List<String> vertexTagName;





}
