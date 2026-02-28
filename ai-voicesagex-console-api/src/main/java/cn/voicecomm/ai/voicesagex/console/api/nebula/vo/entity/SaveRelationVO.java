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
@Schema(name = "新增关系",description = "新增关系")
public class SaveRelationVO {

    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;

    @Schema(description = "主体id",example = "test")
    private String subjectId;

    @Schema(description = "id",example = "test")
    private String objectId;


    @Schema(description = "rank",example = "0")
    private Long rank;


    /**
     * 本体id
     */
    @Schema(description = "关系Id",example = "34323423")
    private Long edgeId;


    @Schema(description = "主体所属本体",example = "test")
    private String subjectTagName;

    @Schema(description = "主体所属本体id",example = "test")
    private Long subjectTagId;

    /**
     * 主体名称
     */
    @Schema(description = "主体名称",example = "test")
    private String subjectName;



    @Schema(description = "关系名称",example = "test")
    private String edgeName;





    /**
     * 客体名称
     */
    @Schema(description = "客体名称",example = "test")
    private String objectName;




    @Schema(description = "客体所属本体名称",example = "test")
    private String objectTagName;

    @Schema(description = "客体所属本体id",example = "test")
    private Long objectTagId;


    @Schema(description = "属性值集合",example = "test1")
    private List<EntityPropertiesVO> entityProperties;










}
