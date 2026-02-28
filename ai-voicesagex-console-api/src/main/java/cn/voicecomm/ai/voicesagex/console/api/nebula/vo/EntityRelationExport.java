package cn.voicecomm.ai.voicesagex.console.api.nebula.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityRelationExport {
    @Schema(description = "实体id")
    private  String entityId;

    @Schema(description = "主体id")
    private  String  subjectId;


    @Schema(description = "客体id")
    private  String  objectId;

    @Schema(description = "rank",example = "0")
    private Long   rank;




    @Schema(description = "关系名称")
    private  String edgeName;
}
