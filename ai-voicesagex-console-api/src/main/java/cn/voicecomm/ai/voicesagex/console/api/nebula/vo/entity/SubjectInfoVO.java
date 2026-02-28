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
@Schema(name = "主体客体列表",description = "主体客体列表")
public class SubjectInfoVO {


    /**
     * 本体id
     */
    @Schema(description = "实体Id",example = "34323423")
    private String entityId;


    @Schema(description = "实体名称",example = "test")
    private String entityName;

    @Schema(description = "实体所属本体id",example = "34323423")
    private Long tagId;

    @Schema(description = "实体所属本体Name",example = "name")
    private String  tagName;



}
