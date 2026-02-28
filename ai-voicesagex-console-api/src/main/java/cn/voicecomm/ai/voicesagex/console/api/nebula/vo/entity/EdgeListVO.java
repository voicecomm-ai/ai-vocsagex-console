package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "关系列表",description = "关系列表")
public class EdgeListVO {
    /**
     * 实体id
     */
    @Schema(description = "ID",example = "34323423")
    private Long relationId;
    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;

    @Schema(description = "本体ID",example = "4323232323" )
    private Long edgeId;
    /**
     * 主体id
     */
    @Schema(description = "主体id",example = "43343")
    private List<Long> subjectTagId;


    @Schema(description = "rank",example = "0")
    private Long rank;
    /**
     * 本体id
     */
    @Schema(description = "关系Id",example = "34323423")
    private Long tagEdgeId;
    /**
     * 本体名称
     */
    @Schema(description = "关系名称",example = "test")
    private String edgeName;

    /**
     * 主体id
     */
    @Schema(description = "主体id",example = "43343")
    private String subjectId;


    /**
     * 主体名称
     */
    @Schema(description = "主体名称",example = "test")
    private String subjectName;



    @Schema(description = "主体类型",example = "test")
    private String subjectTagName;

    /**
     * 客体名称
     */
    @Schema(description = "客体id",example = "test")
    private String objectId;

    /**
     * 客体名称
     */
    @Schema(description = "客体id",example = "test")
    private List<Long>  objectTagId;
    /**
     * 客体名称
     */
    @Schema(description = "客体名称",example = "test")
    private String objectName;

    @Schema(description = "客体类型",example = "test")
    private String objectTagName;



    @Schema(description = "来源",example = "test1")
    private String origin;


    @Schema(description = "最新操作时间",example = "2012-12-04")
    private Date processByDate;

    @Schema(description = "",example = "2012-12-04")
    private String processByName;


    private Map<String, Object> dynamicProperties;

}
