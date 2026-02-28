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
@Schema(name = "编辑关系信息",description = "编辑关系信息")
public class UpdateRelationVO {

    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;


    @Schema(description = "主体所属本体id",example = "test")
    private Long subjectTagId;




    @Schema(description = "客体所属本体id",example = "test")
    private Long objectTagId;


    @Schema(description = "关系名称",example = "test")
    private String edgeName;



    @Schema(description = "属性值集合",example = "test1")
    private List<EntityPropertiesVO> entityProperties;

}
