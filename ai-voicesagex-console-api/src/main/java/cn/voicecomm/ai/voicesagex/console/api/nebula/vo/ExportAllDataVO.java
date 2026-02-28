package cn.voicecomm.ai.voicesagex.console.api.nebula.vo;

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
@Schema(name = "实体关系导出VO",description = "实体关系导出VO")
public class ExportAllDataVO {
    @Schema(description = "图空间",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
    private Long spaceId;
    @Schema(description = "本体关系id")
    private Long tagEdgeId;
    @Schema(description = "本体关系名称")
    private String tagEdgeName;

    @Schema(description = "type  0 实体  1 关系")
    private int type;

    @Schema(description = "实体名称")
    private String entityName;

    @Schema(description = "")
    private String subjectName;




    @Schema(description = "主体类型名称")
    private String subjectTagName;


    @Schema(description = "客体类型名称")
    private String objectTagName;


    private List<String> DynamicKeys;

}
