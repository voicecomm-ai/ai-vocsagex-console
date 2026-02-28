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
@Schema(name = "删除所有实体信息",description = "删除所有实体信息")
public class EntityDropAllVO {
    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;


    @Schema(description = "本体Id",example = "34323423")
    private Long tagId;


    @Schema(description = "本体名称",example = "test")
    private String tagName;


    @Schema(description = "实体名称",example = "test1")
    private String entityName;




}
