package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "删除所有关系信息",description = "删除所有关系信息")
public class RelationDropAllVO {

    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;


    @Schema(description = "关系Id",example = "34323423")
    private Long edgeId;


    @Schema(description = "关系名称",example = "test")
    private String edgeName;


    @Schema(description = "主体名称",example = "demo")
    private String subjectName;


    @Schema(description = "主体类型",example = "demo")
    private String subjectTagName;


    @Schema(description = "客体类型",example = "demo")
    private String objectTagName;

}
