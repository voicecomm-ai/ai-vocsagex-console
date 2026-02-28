package cn.voicecomm.ai.voicesagex.console.api.nebula.vo;

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
@Schema(name = "模版VO",description = "模版VO")
public class TemplateEntityVO {

    @Schema(description = "图空间",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
    private Long spaceId;

    private List<TagTemplate> templateList;

}
