package cn.voicecomm.ai.voicesagex.console.api.nebula.db.po;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class Ralation implements Serializable {

    @Serial
    private static final long serialVersionUID = 9091076406023370112L;
    private  Long relationId;

    /**
     * 主体id
     */
    private String sourceId;

    /**
     *客体id
     */
    private String   objectId;


    /**
     * edge Name
     */
    @Schema(description = "edge type",example = "34323423")
    private String  edgeName;


    @Schema(description = "rank",example = "0")
    private Long   rank;
}
