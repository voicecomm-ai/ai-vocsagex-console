package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.DO;

import cn.voicecomm.ai.voicesagex.console.api.nebula.db.po.Ralation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraphEdgeDropDO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6024376287655651644L;
    /**
     * 图空间id
     */
    @Schema(description = "图空间id",example = "34323423")
    private String spaceId;


    /**
     * edge Name
     */
    @Schema(description = "edge type",example = "34323423")
    private String  edgeName;




    @Schema(description = "关系id对应关系",example = "34323423")
    private  List<Ralation> ralations;


    @Schema(description = "主体名称",example = "demo")
    private String subjectName;


    @Schema(description = "主体类型",example = "demo")
    private String subjectTagName;


    @Schema(description = "客体类型",example = "demo")
    private String objectTagName;



}
