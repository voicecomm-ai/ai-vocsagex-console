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
@Schema(name = "勾选实体关系导出VO",description = "勾选实体关系导出VO")
public class ExportDataVO {
    @Schema(description = "图空间",requiredMode = Schema.RequiredMode.REQUIRED,example = "1719736987010" )
    private Long spaceId;

    private List<EntityRelationExport> entityRelationExportList;

    @Schema(description = "type  0 实体  1 关系")
    private int type;

    @Schema(description = "本体关系名称")
    private String tagEdgeName;


   private List<String> DynamicKeys;

}
