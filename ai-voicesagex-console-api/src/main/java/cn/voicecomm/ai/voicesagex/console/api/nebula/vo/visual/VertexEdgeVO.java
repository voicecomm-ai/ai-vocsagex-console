package cn.voicecomm.ai.voicesagex.console.api.nebula.vo.visual;

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
@Schema(name = "节点-边三元组信息",description = "节点-边三元组信息")
public class VertexEdgeVO {

    @Schema(description = "主体节点名称",example = "5")
    private String subjectName;




    @Schema(description = "主体所属节点名称",example = "5")
    private List<String> subjectTagName;


    @Schema(description = "主体节点Id",example = "1234434343")
    private String subjectId;


    @Schema(description = "客体节点名称",example = "5")
    private String objectName;

    @Schema(description = "客体本体名称",example = "5")
    private List<String> objectTagName;




    @Schema(description = "客体节点Id",example = "1234434343")
    private String objectId;


    @Schema(description = "rank",example = "0")
    private long rank;


    @Schema(description = "边名称",example = "5")
    private String edgeName;


    public VertexEdgeVO(String subjectId, String objectId, String edgeName) {
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.edgeName = edgeName;
    }

    public VertexEdgeVO(String subjectId, String objectId, String edgeName,long rank) {
        this.subjectId = subjectId;
        this.objectId = objectId;
        this.edgeName = edgeName;
        this.rank = rank;
    }
}
