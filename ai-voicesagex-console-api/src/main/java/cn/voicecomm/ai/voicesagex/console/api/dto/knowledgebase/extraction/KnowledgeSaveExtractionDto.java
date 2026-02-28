package cn.voicecomm.ai.voicesagex.console.api.dto.knowledgebase.extraction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName KnowledgeSaveExtractionJobDto
 * @Author wangyang
 * @Date 2025/9/15 14:13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class KnowledgeSaveExtractionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = -6221335191716038894L;

    private Integer extractionId;

    @Schema(description = "图空间id",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
    @NotNull(message = "图空间id不能为空")
    private Integer spaceId;


    @Schema(description = "入图空间",example = "demo" )
    @NotBlank(message = "图空间名称不能为空")
    private String spaceName;


    @Schema(description = "任务名称",example = "demo" )
    @NotBlank(message = "任务名称不能为空")
    private String jobName;


    @Schema(description = "本体名称",example = "tag" )
    private List<String> tags;


    @Schema(description = "关系名称",example = "edge" )
    private List<String> edges;


    @Schema(description = "文件路径 非必填",example = "edge" )
    private String filePath;


    @Schema(description = "0 实体类型  1 文档类型",example = "0" )
    private Integer type;

}
