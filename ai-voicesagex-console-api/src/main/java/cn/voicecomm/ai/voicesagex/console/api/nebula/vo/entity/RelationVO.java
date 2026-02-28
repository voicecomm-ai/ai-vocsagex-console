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
@Schema(name = "关系对象",description = "关系对象")
public class RelationVO {
    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;


    @Schema(description = "关系名称",example = "test")
    private String edgeame;


    @Schema(description = "主体id")
    private  int subjectId;


    @Schema(description = "客体id")
    private  int objectId;
}
