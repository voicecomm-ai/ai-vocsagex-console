package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "编辑抽取任务",description = "编辑抽取任务")
public class KnowledgeUpdateExtractionDto implements Serializable {

    @Schema(description = "图空间id",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
    @NotNull(message = "图空间id不能为空")
    private Integer spaceId;


    @Schema(description = "入图空间",example = "demo" )
    @NotBlank(message = "图空间名称不能为空")
    private String spaceName;


    @Schema(description = "本体名称,以,分割",example = "tag" )
    private List<String> tags;


    @Schema(description = "任务id",example = "4545343434" )
    private Integer jobId;


    @Schema(description = "任务名称",example = "4545343434" )
    private String jobName;


    @Schema(description = "关系名称,以,分割",example = "edge" )
    private List<String> edges;

}
