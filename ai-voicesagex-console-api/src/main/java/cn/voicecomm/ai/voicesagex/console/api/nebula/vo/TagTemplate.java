package cn.voicecomm.ai.voicesagex.console.api.nebula.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagTemplate {

    @Schema(description = "本体id")
    private Long tagId;
    @Schema(description = "本体名称")
    private String tagName;
}
