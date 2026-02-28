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
@Schema(name = "实体详情",description = "实体详情")
public class EntityDetailsVO {
    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;

    @Schema(description = "实体id",example = "34323423")
    private String entityId;


    /**
     * 实体名称
     */
    @Schema(description = "实体名称",example = "test1")
    private String entityName;


    @Schema(description = "实体名称",example = "test1")
    private List<TagInfoDatilVO> tagInfoDetailVOS;
}
