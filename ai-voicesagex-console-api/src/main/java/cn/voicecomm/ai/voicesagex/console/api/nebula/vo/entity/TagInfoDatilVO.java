package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

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
@Schema(name = "本体属性信息",description = "本体属性信息")
public class TagInfoDatilVO {

    /**
     * 本体id
     */
    @Schema(description = "本体Id",example = "34323423")
    private Long tagId;
    /**
     * 本体名称
     */
    @Schema(description = "本体名称",example = "test")
    private String tagName;



    @Schema(description = "属性值集合",example = "test1")
    private List<EntityPropertiesVO> entityProperties;

}
