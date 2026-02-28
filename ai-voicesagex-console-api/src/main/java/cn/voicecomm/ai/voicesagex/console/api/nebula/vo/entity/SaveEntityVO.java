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
@Schema(name = "新增实体",description = "新增实体")
public class SaveEntityVO {

    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;

    @Schema(description = "实体id",example = "34323423")
    private String entityId;
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


    @Schema(description = "属性值集合",example = "test1")
    private List<EntityPropertiesVO> entityProperties;










}
