package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "实体列表",description = "实体")
public class EntityInfosVO {
    /**
     * 实体id
     */
    @Schema(description = "VID",example = "34323423")
    private String  entityId;
    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;
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
    /**
     * 实体名称
     */
    @Schema(description = "实体名称",example = "test1")
    private String entityName;


    @Schema(description = "来源",example = "test1")
    private String origin;


    @Schema(description = "最新操作时间",example = "2012-12-04")
    private Date processByDate;

    @Schema(description = "",example = "2012-12-04")
    private String processByName;

}
