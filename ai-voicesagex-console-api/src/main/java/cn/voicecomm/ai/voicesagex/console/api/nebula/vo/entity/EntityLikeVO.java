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
@Schema(name = "模糊搜索实体",description = "模糊搜索实体")
public class EntityLikeVO {

    @Schema(description = "图空间id",example = "34323423")
    private Long spaceId;


    @Schema(description = "tagName",example = "test")
    private String tagName;


    @Schema(description = "实体名称",example = "test")
    private String entityName;
}
